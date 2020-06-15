package com.example.demo;

import com.example.demo.dao.WeiboDao;
import com.example.demo.domain.Weibo;
import com.example.demo.nlp.Analyser;
import com.example.demo.nlp.TokenHelper;
import com.example.demo.zookeeper.ZooTool;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class Spider {
    @Autowired
    ThreadPoolExecutor threadPool;

    public static LinkedBlockingQueue<Long> weibo_id_queue = new LinkedBlockingQueue<>();

    @Autowired
    LinkedBlockingQueue<Weibo> queue;

    @Autowired
    private WeiboDao weiboDao;

    @Autowired
    TokenHelper tokenHelper;

    @Autowired
    private Analyser analyser;

    @Autowired
    ZooTool zooTool;

    String commnet_search_url = "https://m.weibo.cn/api/comments/show";

    List<String> content_ids = new LinkedList();
    int page_content_ids = 0;

    String cookie = "_T_WM=57729684766; WEIBOCN_FROM=1110006030; XSRF-TOKEN=d3ccdb; MLOGIN=1; SSOLoginState=1591904257; ALF=1594496257; SCF=ArsRkvs-oW_Gx9WsvBDPnSDs0CTIdKc70XBBSJnlg2I_J12Ga-18oAUScg5mrXKkBv1FJJuxyEH7qePpumzqC7k.; SUB=_2A25z5vhRDeRhGeNG7FoS8yfLwz6IHXVRKJgZrDV6PUNbktANLXXZkW1NSxcGz22t1giY7H9sCvH12rDpQQOUCEkK; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9WWH8sZgENHoUiMErIEu-kal5JpX5KzhUgL.Fo-RS0n0e0.N1hz2dJLoI7UDdPDReKnc; SUHB=0vJ4HA3-rIwF3h";

    public void init(){
        tokenHelper.getAccessToken();
        content_ids = weiboDao.getAllContentIds();
    }

    public int getRandomTimeIn3000To10000(){
        Random random = new Random();
        return random.nextInt(2000) + 3000;
    }

    //获取时间
    private Long getTimestamp(String time){
        Calendar calendar = Calendar.getInstance();

        if(time.contains("-")){
            String[] time_ = time.split("-");

            int month = Integer.parseInt(time_[0])-1;
            int day = Integer.parseInt(time_[1]);


            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
        }

        if(time.contains("昨天")){
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)-1);
        }

        if(time.contains("前")){

        }

        Long res = calendar.getTimeInMillis();
        res /= 100000;
        res *= 100000;

        return res;
    }

    public void start() throws InterruptedException, IOException {
        //从队列中获取数据进行处理
        Thread thread = new Thread(() -> {
            System.out.println("获取队列数据进程已开启");

            while(true){
                try {
                    Thread.sleep(200);

                    Weibo weiBo = queue.poll();

                    if(weiBo != null) {
                        ProcessRunnable runnable1 = new ProcessRunnable(weiBo);
                        try {
                            threadPool.execute(runnable1);
                        } catch (RejectedExecutionException rejectedExecutionException){
                            rejectedExecutionException.printStackTrace();

                            //rejoin the weibo to the queue
                            queue.add(weiBo);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        });
        thread.start();


        for(;;){
            if(weibo_id_queue.size() == 0){
                continue;
            }

            String content_id = String.valueOf(weibo_id_queue.poll());

            int score_content = weiboDao.getContentScoreByWID(content_id);

            for(int page = 1; ;page ++){

                String search_url = commnet_search_url +"?id=" + content_id + "&page=" + page + "&sudaref=graph.qq.com";

                Thread.sleep(getRandomTimeIn3000To10000());

                Document doc_temp = Jsoup.connect( search_url )
                        .ignoreContentType(true)
                        .ignoreContentType(true)
                        .header("Accept", "*/*")
                        .header("Accept-Encoding", "gzip, deflate")
                        .header("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
                        .header("Content-Type", "application/json;charset=UTF-8")
                        .header("accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                        .header("accept-encoding","gzip, deflate, br")
                        .header("accept-language","zh-CN,zh;q=0.9,ru;q=0.8")
                        .header("cache-control","no-cache")
                        .header("pragma","no-cache")
                        .header("sec-fetch-dest","empty")
                        .header("sec-fetch-mode","same-origin")
                        .header("sec-fetch-site","same-origin")
                        .header("upgrade-insecure-requests","1")
                        .header("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.61 Safari/537.36")
                        .cookie("cookie", cookie)
                        .get();

                String res = doc_temp.body().text();

                System.out.println("addr: "+ search_url +"   res : " + res);

                if(res.contains("登录") || res.contains("通行证")){
                    System.out.println("无内容  微博id" + content_id + " page : "+ page + " index : " + page );

                    break;
                }

                JSONObject object = null;

                try {

                    object = JSONObject.fromObject(res);
                } catch (net.sf.json.JSONException error){
                    System.out.println("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");

                    error.printStackTrace();

                    System.out.println("error comment : " + res);
                    System.out.println("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");

                    continue;
                }

                int message = object.getInt("ok");

                if(message == 0){
                    System.out.println("无内容 微博id" + content_id + " page : "+ page + "index : " + page );

                    break;
                }

                JSONArray comments = object.getJSONObject("data").getJSONArray("data");

                for(int k = 0; k < comments.size(); k++){
                    JSONObject object1 = comments.getJSONObject(k);

                    String id = object1.getString("id");

                    if(object1.size() != 7){
                        continue;
                    }

                    String content = object1.get("text").toString();

                    if(content.equals("")){
                        continue;
                    }

                    Weibo weiBo = new Weibo();
                    weiBo.setW_content_id(content_id);
                    weiBo.setType(1);
                    weiBo.setW_content(content);
                    weiBo.setW_timestamp(getTimestamp(object1.get("created_at").toString()));
                    weiBo.setW_id(id);
                    weiBo.setScoreContent(score_content);

                    queue.add(weiBo);
                }
            }
        }
    }

    //处理获取到的数据
    class ProcessRunnable implements Runnable{
        Weibo weiBo;

        public ProcessRunnable(Weibo weiBo){
            this.weiBo = weiBo;
        }

        public void run() {
            String content = weiBo.getW_content();
            content = content.replaceAll("#|【|】","");
            content = content.replaceAll("</[a-zA-Z][^>]*>", "");
            //content = content.replace(" ","");

            if(content.equals("")){
                return;
            }

            weiBo.setW_content(content);

            int score=0;

            try {
                score = analyser.analyse(content);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(score > 90){
                if(!content.contains("群体免疫") || content.contains("群免")){
                    score = 50;
                } else {
                    if(weiBo.getScoreContent() < 50){
                        score = 100-score;
                    }
                }
            }

            weiBo.setScore(score);

            System.out.println("the weibo : "+ weiBo);

            weiboDao.save(weiBo);
        }
    }

}
