package com.example.demo.spider;

import com.example.demo.domain.WeiBo;
import com.example.demo.kafka.KafkaService;
import com.example.demo.nlp.Analyser;
import com.example.demo.nlp.TokenHelper;
import com.example.demo.repository.WeiBoDao;
import com.example.demo.zookeeper.ZooTool;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class SpiderForContent {
    @Autowired
    ThreadPoolExecutor threadPool;//线程池

    @Autowired
    LinkedBlockingQueue<WeiBo> queue;//阻塞队列

    @Autowired
    TokenHelper tokenHelper;

    @Autowired
    Analyser analyser;

    @Autowired
    WeiBoDao weiBoDao;

    @Autowired
    KafkaService kafkaService;

    @Autowired
    ZooTool zooTool;

    String cookie = "SINAGLOBAL=4380708593471.6206.1587122404400; un=17610829296; login_sid_t=b3f227f0da30de52c0eb496ee148b786; cross_origin_proto=SSL; _s_tentry=passport.weibo.com; Apache=7098047885056.769.1591864262878; ULV=1591864262884:12:4:2:7098047885056.769.1591864262878:1591682940962; WBtopGlobal_register_version=fd6b3a12bb72ffed; un=17610829296; webim_unReadCount=%7B%22time%22%3A1591892989334%2C%22dm_pub_total%22%3A0%2C%22chat_group_client%22%3A0%2C%22chat_group_notice%22%3A0%2C%22allcountNum%22%3A0%2C%22msgbox%22%3A0%7D; UOR=tech.ifeng.com,widget.weibo.com,www.baidu.com; SCF=ArsRkvs-oW_Gx9WsvBDPnSDs0CTIdKc70XBBSJnlg2I_jKmY35AnWMLMnDvrYzALtv_hBERf0VkWaRgI_0XEl_Y.; SUB=_2A25z5gmEDeRhGeFK7VIX-SnIyzuIHXVQknxMrDV8PUNbmtAKLU_EkW9NQyVU5mdYt_q8jjlknqO1QVUOcFVYhzOB; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9WW34FahFbdeXNLqlH4D_lqX5JpX5K2hUgL.FoMXSo5c1KMXehM2dJLoIp7LxKML1KBLBKnLxKqL1hnLBoM7SKMpeKMNeoM7; SUHB=0A0JONAkl9e1Jx; ALF=1592505428; SSOLoginState=1591900628";

    public int getRandomTimeIn3000To10000(){
        Random random = new Random();
        return random.nextInt(2000) + 3000;
    }

    public void start() throws InterruptedException, IOException {
        tokenHelper.getAccessToken();//获取百度 ai 的token

        //从队列中获取数据进行处理，张彤
        Thread thread = new Thread(() -> {
            System.out.println("获取队列数据进程已开启");

            while(true){
                try {
                    Thread.sleep(200);

                    WeiBo weiBo = queue.poll();
                    if(weiBo != null) {
                        ProcessRunnable runnable1 = new ProcessRunnable(weiBo);
                        threadPool.execute(runnable1);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        });
        thread.start();

        //爬虫部分，徐华磊
        for(; ; ) {
            int page = zooTool.getPageFromRemote();//从zookeeper获取page

            if(page == Integer.MIN_VALUE){
                break;
            }

            int time_delay = getRandomTimeIn3000To10000();

            Thread.sleep(time_delay);

            Document doc = Jsoup.connect("https://s.weibo.com/weibo/%25E7%25BE%25A4%25E4%25BD%2593%25E5%2585%258D%25E7%2596%25AB?topnav=1&wvr=6" + "&page=" + page)
                    .ignoreContentType(true)
                    .header("Accept", "*/*")
                    .header("Accept-Encoding", "gzip, deflate")
                    .header("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .cookie("Cookie", "SINAGLOBAL=4380708593471.6206.1587122404400; Ugrow-G0=7e0e6b57abe2c2f76f677abd9a9ed65d; login_sid_t=802e3efe53b8b7efc62ed667fc6f359a; cross_origin_proto=SSL; YF-V5-G0=8c4aa275e8793f05bfb8641c780e617b; wb_view_log=1368*9122; _s_tentry=passport.weibo.com; Apache=376261282560.74365.1590543097347; ULV=1590543097354:6:5:1:376261282560.74365.1590543097347:1590117575367; WBStorage=42212210b087ca50|undefined; appkey=; wb_view_log_7460697407=1368*9122; WBtopGlobal_register_version=fd6b3a12bb72ffed; YF-Page-G0=c3beab582124cd34995283c3a2849d9d|1590571140|1590571218; webim_unReadCount=%7B%22time%22%3A1590571246905%2C%22dm_pub_total%22%3A0%2C%22chat_group_client%22%3A0%2C%22chat_group_notice%22%3A0%2C%22allcountNum%22%3A1%2C%22msgbox%22%3A0%7D; UOR=tech.ifeng.com,widget.weibo.com,login.sina.com.cn; SCF=ArsRkvs-oW_Gx9WsvBDPnSDs0CTIdKc70XBBSJnlg2I_kIelYEnjQ6CUhpFMNmHDugUhPZYBSleq5hsBrj_tv8Q.; SUB=_2A25zykHTDeRhGeFK7VIX-SnIyzuIHXVQvjQbrDV8PUNbmtAKLRb6kW9NQyVU5iphfpNmUNl8Z_RcywNi7aLinDzt; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9WW34FahFbdeXNLqlH4D_lqX5JpX5K2hUgL.FoMXSo5c1KMXehM2dJLoIp7LxKML1KBLBKnLxKqL1hnLBoM7SKMpeKMNeoM7; SUHB=0cSn7tkZTz9Je5; ALF=1591176196; SSOLoginState=1590571395; un=17610829296")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0")
                    .cookie("Cookie", cookie)
                    .timeout(5000)
                    .get();

            Elements weibos = doc.select("div[action-type='feed_list_item']");

            if (weibos.size() == 0) {
                System.out.println("nothing");

                while(true){
                    if(queue.size() == 0) {
                        System.out.println("处理完毕！当前page： "+page);
                        System.out.println("response : "+ doc.body().toString());

                        System.exit(0);//爬完了，退出
                    }
                }
            }

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR, 0);

            long time_now = calendar.getTimeInMillis();

            //获取一个微博页内所有博文的wid 内容 时间
            for (Element temp : weibos) {
                WeiBo weiBo = new WeiBo();

                String mid;
                try {
                    mid = temp.attr("mid");//w_id
                } catch (Exception e) {
                    System.out.println("get mid error! mid = " + temp.attr("mid") + "**** content: " + temp.text());
                    //logger.error("get mid error! mid = "+temp.attr("mid")+"**** content: "+temp.text());

                    continue;
                }

                String content = temp.select("p.txt[node-type='feed_list_content_full'][nick-name]").text();

                if(content.equals("")){
                    content = temp.select("p.txt[node-type='feed_list_content'][nick-name]").text();
                }

                String time_ = temp.select("p.from").select("a[href]").text().split(" ")[0];

                //时间解析, 获取微博的时间
                if (time_.contains("分钟前") || time_.contains("今天")) {//今天
                    weiBo.setW_timestamp(time_now);
                } else {
                    int month;
                    int day;

                    String[] date = time_.split("[日|月]");

                    month = Integer.parseInt(date[0]);
                    day = Integer.parseInt(date[1]);

                    calendar = Calendar.getInstance();
                    calendar.set(Calendar.MONTH, month - 1);
                    calendar.set(Calendar.DAY_OF_MONTH, day);
                }

                //把时间戳截至到月日
                Long time = calendar.getTimeInMillis();
                time /= 100000;
                time *= 100000;

//                if( time <= lastedTime ){
//                    continue;
//                }

                weiBo.setW_timestamp(time);

                weiBo.setW_content(content);
                weiBo.setW_id(mid);

                //System.out.println(weiBo.toString());

                try {

                    queue.add(weiBo);//把数据放入阻塞队列

                } catch (Exception e) {
                    System.out.println(e);
                    System.out.println("the content of weibo : " + weiBo.toString());
                }
            }
        }
    }

    //处理获取到的数据，具体线程池处理，张彤
    class ProcessRunnable implements Runnable{
        WeiBo weiBo;

        public ProcessRunnable(WeiBo weiBo){
            this.weiBo = weiBo;
        }

        public void run() {
            //数据清洗
            String content = weiBo.getW_content();
            content = content.replaceAll("#|【|】","");
            content = content.replace("收起全文d","");
            //content = content.replace(" ","");

            weiBo.setW_content(content);


            int score = 0;

            try {
                score = analyser.analyse(content);//获取褒贬意
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            weiBo.setScore(score);

            weiBoDao.save(weiBo);//保存到数据库

            System.out.println("weibo saved : "+ weiBo.toString());

            kafkaService.sendMessageToKafka(weiBo.getW_id());//发送kafka
        }
    }

}