//package com.example.demo;
//
//import com.example.demo.domain.WeiBo;
//import com.example.demo.service.KafkaService;
//import javafx.beans.binding.LongExpression;
//import net.sf.json.JSONObject;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.io.IOException;
//import java.util.Base64;
//import java.util.Calendar;
//import java.util.LinkedList;
//import java.util.Random;
//
//public class Spider {
//    private static  Logger logger = LoggerFactory.getLogger(Spider.class);
//
//    @Autowired
//    static KafkaService kafkaService;
//
//    static String cookie = "SINAGLOBAL=4380708593471.6206.1587122404400; wvr=6; _s_tentry=login.sina.com.cn; Apache=1825815429326.0786.1590040113047; ULV=1590040113070:4:3:1:1825815429326.0786.1590040113047:1589631481850; WBtopGlobal_register_version=fd6b3a12bb72ffed; crossidccode=CODE-yf-1JBFfU-29aZh4-S6IMywuNHgDFn9C5d4052; ALF=1621580682; SSOLoginState=1590044683; SCF=ArsRkvs-oW_Gx9WsvBDPnSDs0CTIdKc70XBBSJnlg2I_4ddYgUdY7Rad6Um1-QUbchF7rIF5XysEeS_IuUBeT-U.; SUB=_2A25zwlhbDeRhGeNG7FoS8yfLwz6IHXVQts6TrDV8PUNbmtANLW-nkW9NSxcGz1yL5EE7VY34P0kDE4B02sa_Ovay; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9WWH8sZgENHoUiMErIEu-kal5JpX5KzhUgL.Fo-RS0n0e0.N1hz2dJLoI7UDdPDReKnc; SUHB=00xZM2OmPq6aT4; UOR=tech.ifeng.com,widget.weibo.com,graph.qq.com; webim_unReadCount=%7B%22time%22%3A1590044823957%2C%22dm_pub_total%22%3A0%2C%22chat_group_client%22%3A0%2C%22chat_group_notice%22%3A0%2C%22allcountNum%22%3A43%2C%22msgbox%22%3A0%7D";
//
//    static int page_size = 0;
//    static String searchUrl = "https://s.weibo.com/weibo?q=%23%E7%BE%A4%E4%BD%93%E5%85%8D%E7%96%AB%23";
//
//    String usernam = "";
//    String password = "";
//
//    public static int getRandomTimeIn3000To10000(){
//        Random random = new Random(10000);
//        return random.nextInt(10)*100;
//    }
//
//    public String getUserName(String username){
//        Base64.Encoder  base64 = Base64.getEncoder();
//        return String.valueOf(base64.encode(username.getBytes()));
//    }
//
//    public String getEncPass(){
//    }
//
//    public String[] getServerData() throws IOException {
//        String[] datas = new String[4];
//        String url_step_1 = "http://login.sina.com.cn/sso/prelogin.php?entry=weibo&callback=sinaSSOController.preloginCallBack&su=&rsakt=mod&client=ssologin.js(v1.4.18)&_="+Calendar.getInstance().getTimeInMillis();
//
//        Document doc = Jsoup.connect(url_step_1)
//                .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:45.0) Gecko/20100101 Firefox/45.0")
//                .cookie("Cookie", cookie)
//                .timeout(3000)
//                .get();
//
//        String res = doc.body().text();
//        res = res.substring(res.indexOf("("), res.indexOf(")"));
//
//        JSONObject object = JSONObject.fromObject(res);
//        datas[0] = object.getString("serverTime");
//        datas[1] = object.getString("pubkey");
//        datas[2] = object.getString("nonce");
//        datas[3] = object.getString("rsakv");
//
//        return datas;
//    }
//
//    public String login(String username, String password){
//
//
//        Document doc = Jsoup.connect("https://s.weibo.com/weibo?q=%23%E7%BE%A4%E4%BD%93%E5%85%8D%E7%96%AB%23"+"&page="+i)
//                .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:45.0) Gecko/20100101 Firefox/45.0")
//                .cookie("Cookie", cookie)
//                .timeout(3000)
//                .get();
//    }
//
//
//
//    public static void start() throws IOException, InterruptedException {
//        LinkedList<WeiBo> list_weibo = new LinkedList<>();
//        for(int i = 0; i < page_size; i++ ){
//            list_weibo.clear();
//
//            Thread.sleep(getRandomTimeIn3000To10000());
//
//            Document doc = Jsoup.connect("https://s.weibo.com/weibo?q=%23%E7%BE%A4%E4%BD%93%E5%85%8D%E7%96%AB%23"+"&page="+i)
//                    .ignoreContentType(true)
//                    .header("Accept", "*/*")
//                    .header("Accept-Encoding", "gzip, deflate")
//                    .header("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
//                    .header("Content-Type", "application/json;charset=UTF-8")
//                    .cookie("Cookie", "SINAGLOBAL=4380708593471.6206.1587122404400; Ugrow-G0=7e0e6b57abe2c2f76f677abd9a9ed65d; login_sid_t=802e3efe53b8b7efc62ed667fc6f359a; cross_origin_proto=SSL; YF-V5-G0=8c4aa275e8793f05bfb8641c780e617b; wb_view_log=1368*9122; _s_tentry=passport.weibo.com; Apache=376261282560.74365.1590543097347; ULV=1590543097354:6:5:1:376261282560.74365.1590543097347:1590117575367; WBStorage=42212210b087ca50|undefined; appkey=; wb_view_log_7460697407=1368*9122; WBtopGlobal_register_version=fd6b3a12bb72ffed; YF-Page-G0=c3beab582124cd34995283c3a2849d9d|1590571140|1590571218; webim_unReadCount=%7B%22time%22%3A1590571246905%2C%22dm_pub_total%22%3A0%2C%22chat_group_client%22%3A0%2C%22chat_group_notice%22%3A0%2C%22allcountNum%22%3A1%2C%22msgbox%22%3A0%7D; UOR=tech.ifeng.com,widget.weibo.com,login.sina.com.cn; SCF=ArsRkvs-oW_Gx9WsvBDPnSDs0CTIdKc70XBBSJnlg2I_kIelYEnjQ6CUhpFMNmHDugUhPZYBSleq5hsBrj_tv8Q.; SUB=_2A25zykHTDeRhGeFK7VIX-SnIyzuIHXVQvjQbrDV8PUNbmtAKLRb6kW9NQyVU5iphfpNmUNl8Z_RcywNi7aLinDzt; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9WW34FahFbdeXNLqlH4D_lqX5JpX5K2hUgL.FoMXSo5c1KMXehM2dJLoIp7LxKML1KBLBKnLxKqL1hnLBoM7SKMpeKMNeoM7; SUHB=0cSn7tkZTz9Je5; ALF=1591176196; SSOLoginState=1590571395; un=17610829296")
//                    .header("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0")
//                    .cookie("Cookie", cookie)
//                    .timeout(5000)
//                    .get();
//
//            Elements weibos = doc.select("div[action-type='feed_list_item']");
//
//            if(weibos.size() == 0){
//                break;
//            }
//
//            Calendar calendar = Calendar.getInstance();
//            calendar.set(Calendar.HOUR, 0);
//            calendar.set(Calendar.MINUTE, 0);
//            calendar.set(Calendar.SECOND, 0);
//
//            long time_now =calendar.getTimeInMillis();
//
//            //获取一个微博页内所有博文的wid 内容 时间
//            for(Element temp : weibos) {
//                WeiBo weiBo = new WeiBo();
//
//                long mid;
//                try {
//                    mid = Long.valueOf(temp.attr("mid"));
//                } catch (Exception e){
//                    logger.error("get mid error! mid = "+temp.attr("mid")+"**** content: "+temp.text());
//
//                    continue;
//                }
//
//                String content = temp.select("p.txt[node-type='feed_list_content_full'][nick-name]").text();
//                String time_ = temp.select("p.from").select("a[href]").text().split(" ")[0];
//
//                //时间解析
//                if(time_.contains("分钟前")){//今天
//                    weiBo.setW_timestamp(time_now);
//                }else{
//                    int month;
//                    int day;
//
//                    String[] date = time_.split("[日|月]");
//
//                    month = Integer.parseInt(date[0]);
//                    day = Integer.parseInt(date[1]);
//
//                    calendar = Calendar.getInstance();
//                    calendar.set(Calendar.MONTH, month-1);
//                    calendar.set(Calendar.HOUR, 0);
//                    calendar.set(Calendar.DAY_OF_MONTH, day);
//                    calendar.set(Calendar.HOUR_OF_DAY, 0);
//                    calendar.set(Calendar.MINUTE, 0);
//                    calendar.set(Calendar.SECOND, 0);
//
//                    Long time = calendar.getTimeInMillis();
//                    time /= 100000;
//                    time *= 100000;
//
//                    weiBo.setW_timestamp(time);
//                }
//
//                weiBo.setW_content(content);
//                weiBo.setW_id(mid);
//
//                list_weibo.add(weiBo);
//            }
//
//            //发送Weibo正文信息
//            kafkaService.sendWeiBoListToKafkaSenderServer(list_weibo);
//        }
//
//    }
//
//}
