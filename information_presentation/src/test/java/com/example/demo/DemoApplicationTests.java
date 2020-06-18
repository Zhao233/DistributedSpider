package com.example.demo;

import com.example.demo.WeiBoDao.WeiBoDao;
import com.example.demo.domain.WeiBo;
import com.example.demo.service.KafkaService;
import com.example.demo.service.SearchService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.*;


@SpringBootTest
class DemoApplicationTests {
    HttpGet get = new HttpGet();
    HttpClient client = HttpClientBuilder.create().build();

    String cookie = "SINAGLOBAL=4380708593471.6206.1587122404400; SCF=ArsRkvs-oW_Gx9WsvBDPnSDs0CTIdKc70XBBSJnlg2I_kIelYEnjQ6CUhpFMNmHDugUhPZYBSleq5hsBrj_tv8Q.; un=17610829296; wvr=6; login_sid_t=b9cdd9eac2b261a8d69c87edd2a11325; cross_origin_proto=SSL; _s_tentry=passport.weibo.com; UOR=tech.ifeng.com,widget.weibo.com,www.baidu.com; Apache=5446464526622.649.1590647923648; ULV=1590647923653:7:6:2:5446464526622.649.1590647923648:1590543097354; SUB=_2A25zyyzuDeRhGeFK7VIX-SnIyzuIHXVQoRkmrDV8PUJbmtANLWPRkW9NQyVU5iuR17nikwLBsfIVFhldDLXuh0EQ; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9WW34FahFbdeXNLqlH4D_lqX5NHD95QNShq7So.NSh5NWs4DqcjMi--NiK.Xi-2Ri--ciKnRi-zNeh-NeK2NS0zNe5tt; SUHB=0RyqguR8Yc7UYB; ALF=1622183997; SSOLoginState=1590647998; webim_unReadCount=%7B%22time%22%3A1590650886583%2C%22dm_pub_total%22%3A0%2C%22chat_group_client%22%3A0%2C%22chat_group_notice%22%3A0%2C%22allcountNum%22%3A5%2C%22msgbox%22%3A0%7D";
    public int getRandomTimeIn300To1000(){
        Random random = new Random(1000);
        return random.nextInt(10)*100;
    }

    public String getCommentUrl(String id, int page){
        String commentUrl = "https://m.weibo.cn/api/comments/show";

        return commentUrl+"?id="+id+"&page="+page;
    }

    public int getMaxPage(String response){
        int size = 7;
        int page = 0;

        JSONObject object = JSONObject.fromObject(response);

        int totalNum = (int) object.getJSONObject("data").get("total_number");

        page = totalNum / size;

        page = totalNum % size != 0 ? page + 1 : page;

        return page;
    }

    public void acsess() throws ClientProtocolException, IOException, URISyntaxException, InterruptedException {
        String SearchUrl = "https://s.weibo.com/weibo?q=%23%E7%BE%A4%E4%BD%93%E5%85%8D%E7%96%AB%23";

        String commentUrl = "https://m.weibo.cn/api/comments/show?id=4491600334270590&page=0";

        Thread.sleep(getRandomTimeIn300To1000());
        Document doc = Jsoup.connect("https://s.weibo.com/weibo?q=%23%E7%BE%A4%E4%BD%93%E5%85%8D%E7%96%AB%23")
                .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:45.0) Gecko/20100101 Firefox/45.0")
                .cookie("Cookie", cookie)
                .timeout(3000)
                .get();

        //Elements w_id = doc.select("a[action-type='fl_unfold']");
        Elements w_id = doc.select("div[action-type='feed_list_item']");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        long time_now =calendar.getTimeInMillis();

        int page_search = 0;

        //微博正文，id
        LinkedList<String> w_id_int = new LinkedList<>();
        LinkedList<String> w_content = new LinkedList<>();
        LinkedList<Long> time = new LinkedList<>();

        LinkedList<String> w_id_int_comment = new LinkedList<>();
        LinkedList<String> w_comment = new LinkedList<>();

        if(page_search == 0){
            String page_search_temp = doc.select("ul.s-scroll[node-type='feed_list_page_morelist']").select("li").last().text();

            page_search_temp = page_search_temp.replaceAll("[\u4e00-\u9fa5]*", "");

            page_search = Integer.parseInt(page_search_temp);
        }

        //获取一个页的每一个博文的id
        for(Element temp : w_id){
            System.out.println(temp.toString());
            //String[] content = temp.attr("href").split("[/|?]");
            String mid = temp.attr("mid");

            if( w_id_int.contains(mid) ){
                continue;
            }

            String content = temp.select("p.txt[node-type='feed_list_content_full'][nick-name]").text();
            String time_ = temp.select("p.from").select("a[href]").text().split(" ")[0];

            //时间解析
            if(time_.contains("分钟前") || time_.contains("今天")){//今天
                time.add( time_now );
            } else{
                int month;
                int day;

                String[] date = time_.split("[日|月]");

                month = Integer.parseInt(date[0]);
                day = Integer.parseInt(date[1]);

                calendar = Calendar.getInstance();
                calendar.set(Calendar.MONTH, month-1);
                calendar.set(Calendar.HOUR, 0);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);

                time.add(calendar.getTimeInMillis());
            }
            w_id_int.add(mid);
            w_content.add(content);
            //w_id_string.add(id_string);
        }

        int page = 0;

        //获取一个微博的评论
        for(int i =0 ; i < w_id_int.size(); i++){
            /*获取page的最大值*/
            Document doc_temp = Jsoup.connect( getCommentUrl(w_id_int.get(i), 40) )
                    .ignoreContentType(true)
                    .header("Accept", "*/*")
                    .header("Accept-Encoding", "gzip, deflate")
                    .header("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .header("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0")
                    //.cookie("Cookie", cookie)
                    .timeout(3000)
                    .get();

            String res = doc_temp.body().text();

            try {
                page = getMaxPage(res);
            } catch (Exception e){
                System.out.println(e.toString());

                continue;
            }


            for(int j = 1; j < page; j++){
                Thread.sleep(getRandomTimeIn300To1000());

                Document doc_temp_ = Jsoup.connect( getCommentUrl(w_id_int.get(i), j) )
                        .ignoreContentType(true)
                        .header("Accept", "*/*")
                        .header("Accept-Encoding", "gzip, deflate")
                        .header("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
                        .header("Content-Type", "application/json;charset=UTF-8")
                        .header("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0")
                        //.cookie("Cookie", cookie)
                        .timeout(3000)
                        .get();

                JSONObject object = JSONObject.fromObject(doc_temp_.body().text());

                JSONArray comments = object.getJSONObject("data").getJSONArray("data");

                for(int k = 0; k < comments.size(); k++){
                    JSONObject object1 = comments.getJSONObject(k);

                    if(object1.size() != 7){
                        continue;
                    }

                    w_comment.add( object1.get("text").toString() );
                    w_id_int_comment.add(object1.get("id").toString());
                }

                System.out.println(object);
            }
        }

        page = 0;

        System.out.println();

    }

    @Autowired
    KafkaService kafkaService;

    public LinkedList<WeiBo> spiderForComment(String w_id) throws IOException, InterruptedException {
        LinkedList<WeiBo> list = new LinkedList<>();
        String getCommentUrl = "https://m.weibo.cn/api/comments/show";

        for(int page = 0 ; ; page++){
            Thread.sleep(getRandomTimeIn300To1000());

            String searchUrl = getCommentUrl + "?id=" + w_id + "&page=" + page;

            Document doc_temp = Jsoup.connect( searchUrl )
                    .ignoreContentType(true)
                    .header("Accept", "*/*")
                    .header("Accept-Encoding", "gzip, deflate")
                    .header("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .cookie("Cookie", "SINAGLOBAL=4380708593471.6206.1587122404400; Ugrow-G0=7e0e6b57abe2c2f76f677abd9a9ed65d; login_sid_t=802e3efe53b8b7efc62ed667fc6f359a; cross_origin_proto=SSL; YF-V5-G0=8c4aa275e8793f05bfb8641c780e617b; wb_view_log=1368*9122; _s_tentry=passport.weibo.com; Apache=376261282560.74365.1590543097347; ULV=1590543097354:6:5:1:376261282560.74365.1590543097347:1590117575367; WBStorage=42212210b087ca50|undefined; appkey=; wb_view_log_7460697407=1368*9122; WBtopGlobal_register_version=fd6b3a12bb72ffed; YF-Page-G0=c3beab582124cd34995283c3a2849d9d|1590571140|1590571218; webim_unReadCount=%7B%22time%22%3A1590571246905%2C%22dm_pub_total%22%3A0%2C%22chat_group_client%22%3A0%2C%22chat_group_notice%22%3A0%2C%22allcountNum%22%3A1%2C%22msgbox%22%3A0%7D; UOR=tech.ifeng.com,widget.weibo.com,login.sina.com.cn; SCF=ArsRkvs-oW_Gx9WsvBDPnSDs0CTIdKc70XBBSJnlg2I_kIelYEnjQ6CUhpFMNmHDugUhPZYBSleq5hsBrj_tv8Q.; SUB=_2A25zykHTDeRhGeFK7VIX-SnIyzuIHXVQvjQbrDV8PUNbmtAKLRb6kW9NQyVU5iphfpNmUNl8Z_RcywNi7aLinDzt; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9WW34FahFbdeXNLqlH4D_lqX5JpX5K2hUgL.FoMXSo5c1KMXehM2dJLoIp7LxKML1KBLBKnLxKqL1hnLBoM7SKMpeKMNeoM7; SUHB=0cSn7tkZTz9Je5; ALF=1591176196; SSOLoginState=1590571395; un=17610829296")
                    .header("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0")
                    //.cookie("Cookie", cookie)
                    .timeout(5000)
                    .get();

            String res = doc_temp.body().text();

            if(res.contains("登录")){
                System.out.println("需重新登录");

                cookie = getCookie();

                page--;
                continue;
            }

            JSONObject object = JSONObject.fromObject( res );

            JSONArray comments = object.getJSONObject("data").getJSONArray("data");

            for(int k = 0; k < comments.size(); k++){
                JSONObject object1 = comments.getJSONObject(k);

                if(object1.size() != 7){
                    continue;
                }
                System.out.println("page : " + page +"content : "+ object1.toString());
            }
        }

    }


    final String PASSPORT_URL = "https://passport.weibo.com/visitor/visitor?entry=miniblog&a=enter&url=http://weibo.com/?category=2"
            + "&domain=.weibo.com&ua=php-sso_sdk_client-0.6.23";

    final String GEN_VISITOR_URL = "https://passport.weibo.com/visitor/genvisitor";
    final String VISITOR_URL = "https://passport.weibo.com/visitor/visitor?a=incarnate";


    private String getCookie() {
        Map<String, String> map;
        while (true) {
            try {
                map = getCookieParam();

                if (map.containsKey("SUB") &&
                        map.containsKey("SUBP") &&
                        map.get("SUB") != null &&  map.get("SUBP") != null) {
                    break;
                }


            } catch (IOException e) {
                e.printStackTrace();

                return "";
            }

            //HttpClientInstance.instance().changeProxy();
        }

        return " YF-Page-G0=" + "; _s_tentry=-; SUB=" + map.get("SUB") + "; SUBP=" + map.get("SUBP");
    }

    private Map<String, String> getCookieParam() throws IOException {
        String time = System.currentTimeMillis() + "";
        time = time.substring(0, 9) + "." + time.substring(9, 13);
        String passporturl = PASSPORT_URL + "&_rand=" + time;

        String tid = "";
        String c = "";
        String w = "";
        {
            String str = postGenvisitor(passporturl);
            if (str.contains("\"retcode\":20000000")) {
                JSONObject jsonObject = JSONObject.fromObject(str).getJSONObject("data");
                tid = jsonObject.optString("tid");
                try {
                    tid = URLEncoder.encode(tid, "utf-8");
                } catch (UnsupportedEncodingException e) {
                }
                c = jsonObject.has("confidence") ? "000" + jsonObject.getInt("confidence") : "100";
                w = jsonObject.optBoolean("new_tid") ? "3" : "2";
            }
        }
        String s = "";
        String sp = "";
        {
            if ( tid!=null&& w != null && c != null ) {
                String str = getVisitor(tid, w, c, passporturl);
                str = str.substring(str.indexOf("(") + 1, str.indexOf(")"));
                if (str.contains("\"retcode\":20000000")) {
                    System.out.println(JSONObject.fromObject(str).toString(2));
                    JSONObject jsonObject = JSONObject.fromObject(str).getJSONObject("data");
                    s = jsonObject.getString("sub");
                    sp = jsonObject.getString("subp");
                }

            }
        }
        Map<String, String> map = new HashMap<>();
        map.put("SUB", s);
        map.put("SUBP", sp);
        return map;
    }

    private String postGenvisitor(String passporturl) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();

        Map<String, String> params = new HashMap<>();
        params.put("cb", "gen_callback");
        params.put("fp", fp());

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("cb", "gen_callback"));
        nvps.add(new BasicNameValuePair("fp", fp()));

        HttpPost httpPost = new HttpPost(GEN_VISITOR_URL);
        httpPost.addHeader(HttpHeaders.ACCEPT, "*/*");
        httpPost.addHeader(HttpHeaders.ORIGIN, "https://passport.weibo.com");
        httpPost.addHeader(HttpHeaders.REFERER, passporturl);
        httpPost.setEntity(new UrlEncodedFormEntity(nvps,HTTP.UTF_8));

        HttpResponse res = client.execute(httpPost);
        //return str.substring(str.indexOf("(") + 1, str.lastIndexOf(""));
        String str = EntityUtils.toString(res.getEntity(),"UTF-8");

        return str.substring(str.indexOf("(") + 1, str.lastIndexOf(")"));
    }

    private String getVisitor(String tid, String w, String c, String passporturl) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();

        String url = VISITOR_URL + "&t=" + tid + "&w=" + "&c=" + c.substring(c.length() - 3)
                + "&gc=&cb=cross_domain&from=weibo&_rand=0." + rand();

        Map<String, String> headers =new HashMap<>();
        headers.put(HttpHeaders.ACCEPT, "*/*");
        headers.put(HttpHeaders.HOST, "passport.weibo.com");
        headers.put(HttpHeaders.COOKIE, "tid=" + tid + "__0" + c);
        headers.put(HttpHeaders.REFERER, passporturl);

        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader(HttpHeaders.ACCEPT, "*/*");
        httpGet.addHeader(HttpHeaders.HOST, "passport.weibo.com");
        httpGet.addHeader(HttpHeaders.COOKIE, "tid=" + tid + "__0" + c);
        httpGet.addHeader(HttpHeaders.REFERER, passporturl);

        httpGet.setConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build());

        HttpResponse response = client.execute(httpGet);
        String res = EntityUtils.toString(response.getEntity(), "UTF-8");

        return res;
    }

    private static String fp() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("os", "1");
        jsonObject.put("browser", "Chrome59,0,3071,115");
        jsonObject.put("fonts", "undefined");
        jsonObject.put("screenInfo", "1680*1050*24");
        jsonObject.put("plugins",
                "Enables Widevine licenses for playback of HTML audio/video content. (version: 1.4.8.984)::widevinecdmadapter.dll::Widevine Content Decryption Module|Shockwave Flash 26.0 r0::pepflashplayer.dll::Shockwave Flash|::mhjfbmdgcfjbbpaeojofohoefgiehjai::Chrome PDF Viewer|::internal-nacl-plugin::Native Client|Portable Document Format::internal-pdf-viewer::Chrome PDF Viewer");
        return jsonObject.toString();
    }

    private static String rand() {
        return new BigDecimal(Math.floor(Math.random() * 10000000000000000L)).toString();
    }



    @Test
    void contextLoads() {
        try {
            spiderForComment("4508826525826464");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void start() throws IOException, InterruptedException {
        LinkedList<WeiBo> list_weibo = new LinkedList<>();

        for(int i = 0; ; i++ ) {
            list_weibo.clear();

            Thread.sleep(getRandomTimeIn300To1000());

            Document doc = Jsoup.connect("https://s.weibo.com/weibo?q=%23%E7%BE%A4%E4%BD%93%E5%85%8D%E7%96%AB%23" + "&page=" + i)
                    .ignoreContentType(true)
                    .header("Accept", "*/*")
                    .header("Accept-Encoding", "gzip, deflate")
                    .header("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .cookie("Cookie", "SINAGLOBAL=4380708593471.6206.1587122404400; Ugrow-G0=7e0e6b57abe2c2f76f677abd9a9ed65d; login_sid_t=802e3efe53b8b7efc62ed667fc6f359a; cross_origin_proto=SSL; YF-V5-G0=8c4aa275e8793f05bfb8641c780e617b; wb_view_log=1368*9122; _s_tentry=passport.weibo.com; Apache=376261282560.74365.1590543097347; ULV=1590543097354:6:5:1:376261282560.74365.1590543097347:1590117575367; WBStorage=42212210b087ca50|undefined; appkey=; wb_view_log_7460697407=1368*9122; WBtopGlobal_register_version=fd6b3a12bb72ffed; YF-Page-G0=c3beab582124cd34995283c3a2849d9d|1590571140|1590571218; webim_unReadCount=%7B%22time%22%3A1590571246905%2C%22dm_pub_total%22%3A0%2C%22chat_group_client%22%3A0%2C%22chat_group_notice%22%3A0%2C%22allcountNum%22%3A1%2C%22msgbox%22%3A0%7D; UOR=tech.ifeng.com,widget.weibo.com,login.sina.com.cn; SCF=ArsRkvs-oW_Gx9WsvBDPnSDs0CTIdKc70XBBSJnlg2I_kIelYEnjQ6CUhpFMNmHDugUhPZYBSleq5hsBrj_tv8Q.; SUB=_2A25zykHTDeRhGeFK7VIX-SnIyzuIHXVQvjQbrDV8PUNbmtAKLRb6kW9NQyVU5iphfpNmUNl8Z_RcywNi7aLinDzt; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9WW34FahFbdeXNLqlH4D_lqX5JpX5K2hUgL.FoMXSo5c1KMXehM2dJLoIp7LxKML1KBLBKnLxKqL1hnLBoM7SKMpeKMNeoM7; SUHB=0cSn7tkZTz9Je5; ALF=1591176196; SSOLoginState=1590571395; un=17610829296")
                    .header("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0")
                    .cookie("Cookie", cookie)
                    .timeout(5000)
                    .get();

            Elements weibos = doc.select("div[action-type='feed_list_item']");

            if (weibos.size() == 0) {
                continue;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            long time_now = calendar.getTimeInMillis();

            //获取一个微博页内所有博文的wid 内容 时间
            for (Element temp : weibos) {
                WeiBo weiBo = new WeiBo();

                long mid;
                try {
                    mid = Long.valueOf(temp.attr("mid"));
                } catch (Exception e) {
                    // logger.error("get mid error! mid = "+temp.attr("mid")+"**** content: "+temp.text());

                    continue;
                }

                String content = temp.select("p.txt[node-type='feed_list_content_full'][nick-name]").text();
                if(content.equals("")){
                    continue;
                }

                String time_ = temp.select("p.from").select("a[href]").text().split(" ")[0];

                //时间解析
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
                    calendar.set(Calendar.HOUR, 0);
                    calendar.set(Calendar.DAY_OF_MONTH, day);
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);

                    Long time = calendar.getTimeInMillis();
                    time /= 100000;
                    time *= 100000;

                    weiBo.setW_timestamp(time);
                }

                weiBo.setW_content(content);
                //weiBo.setW_id(mid);

                System.out.println(weiBo.toString());

                //weiBoDao.save(weiBo);

                //list_weibo.add(weiBo);
            }
        }
    }

    @Autowired
    SearchService searchService;
    @Autowired
    WeiBoDao weiBoDao;
    @Test
    void getContentAndComment() throws IOException, InterruptedException {
//        Calendar start = Calendar.getInstance();
//        Calendar end = Calendar.getInstance();
//
//        long min_w_time =  weiBoDao.getMinWTimestamp();
//
//        start.setTime(new Date(min_w_time));
//        end.setTime(new Date(min_w_time));
//
//        end.set(Calendar.DAY_OF_MONTH, start.get(Calendar.DAY_OF_MONTH)+14);
//
//        System.out.println(weiBoDao.getGoodAverageScoreByTIme(start.getTimeInMillis(),end.getTimeInMillis()));

        searchService.getFirstGraphData(false);
    }
}
