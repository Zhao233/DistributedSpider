package com.example.demo.service;

import com.example.demo.WeiBoDao.WeiBoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

@Service
public class SearchService {
    @Autowired
    WeiBoDao weiBoDao;

    public HashMap<Object, Object> getFirstGraphData(boolean isUpdate){
        if(CACHE.res_line_chart_1 != null  && isUpdate == false){
            return CACHE.res_line_chart_1;
        }

        HashMap<Object, Object> res = new HashMap<>();

        LinkedList<Integer> good = new LinkedList<>();
        LinkedList<Integer> middle = new LinkedList<>();
        LinkedList<Integer> bad = new LinkedList<>();
        LinkedList<Long> time = new LinkedList<>();

        long min_w_time =  weiBoDao.getMinWTimestamp();
        long max_w_time =  weiBoDao.getMaxWTimestamp();

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.setTime(new Date(min_w_time));
        end.setTime(new Date(max_w_time));

        Calendar calendar_start = Calendar.getInstance();
        Calendar calendar_end = Calendar.getInstance();

        int date_average = (end.get(Calendar.DAY_OF_YEAR) - start.get(Calendar.DAY_OF_YEAR))/20;


        for(int i = 0; ;i++){
            boolean isEnd = false;

            if(i == 0){
                calendar_start.setTime(new Date(min_w_time));
                calendar_end.setTime(new Date(min_w_time));
            } else {
                calendar_start.setTime( calendar_end.getTime() );
            }

            calendar_end.set(Calendar.DAY_OF_MONTH, calendar_start.get(Calendar.DAY_OF_MONTH)+date_average);

            if(calendar_end.getTimeInMillis() > max_w_time){
                calendar_end.setTime(new Date(max_w_time));

                isEnd = true;
            }

            System.out.println("start: " + calendar_start + "end: "+calendar_end);

            long start_w_time = calendar_start.getTimeInMillis();
            long end_w_time = calendar_end.getTimeInMillis();

            System.out.println("start time :" + calendar_start.getTime() + "   end time : " + calendar_end.getTime() );


            Integer good_score = weiBoDao.getGoodSumNumByTime(start_w_time, end_w_time);
            good_score = good_score == null? 0 : good_score;
            Integer middle_score = weiBoDao.getMiddleSumNumByTime(start_w_time, end_w_time);
            middle_score = middle_score == null? 0 : middle_score;
            Integer bad_score = weiBoDao.getBadSumNumByTime(start_w_time, end_w_time);
            bad_score = bad_score == null? 0 : bad_score;

            good.add(good_score);
            middle.add(middle_score);
            bad.add(bad_score);
            time.add(calendar_start.getTimeInMillis());

            if(isEnd){
                break;
            }
        }

        res.put("time", time);
        res.put("good", good);
        res.put("middle", middle);
        res.put("bad", bad);

        CACHE.res_line_chart_1 = res;

        return res;
    }

    public HashMap<Object, Object> getSecondGraphData(boolean isUpdate){
        if(CACHE.res_line_chart_2 != null && isUpdate == false){
            return CACHE.res_line_chart_2;
        }

        HashMap<Object, Object> res = new HashMap<>();

        LinkedList<Integer> attention = new LinkedList<>();
        LinkedList<Long> time = new LinkedList<>();

        long min_w_time =  weiBoDao.getMinWTimestamp();
        long max_w_time =  weiBoDao.getMaxWTimestamp();

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.setTime(new Date(min_w_time));
        end.setTime(new Date(max_w_time));

        Calendar calendar_start = Calendar.getInstance();
        Calendar calendar_end = Calendar.getInstance();

        int date_average = (end.get(Calendar.DAY_OF_YEAR) - start.get(Calendar.DAY_OF_YEAR))/20;


        for(int i = 0; ;i++){
            boolean isEnd = false;

            if(i == 0){
                calendar_start.setTime(new Date(min_w_time));
                calendar_end.setTime(new Date(min_w_time));
            } else {
                calendar_start.setTime( calendar_end.getTime() );
            }

            calendar_end.set(Calendar.DAY_OF_MONTH, calendar_start.get(Calendar.DAY_OF_MONTH)+date_average);

            if(calendar_end.getTimeInMillis() > max_w_time){
                calendar_end.setTime(new Date(max_w_time));

                isEnd = true;
            }

            System.out.println("start: " + calendar_start + "end: "+calendar_end);

            long start_w_time = calendar_start.getTimeInMillis();
            long end_w_time = calendar_end.getTimeInMillis();

            System.out.println("start time :" + calendar_start.getTime() + "   end time : " + calendar_end.getTime() );

            Integer attention_ = weiBoDao.getAttentionByDate(start_w_time, end_w_time);
            attention_ = attention_ == null? 0 : attention_;

            attention.add(attention_);

            if(isEnd){
                break;
            }
        }

        res.put("attention", attention);

        CACHE.res_line_chart_2 = res;

        return res;
    }

    public HashMap<Object, Object> getThirdGraphData(boolean isUpdate){
        if(CACHE.res_line_chart_3 != null && isUpdate == false){
            return CACHE.res_line_chart_3;
        }

        HashMap<Object, Object> res = new HashMap<>();

        int good_num = weiBoDao.getGoodNumber();
        int middle_num = weiBoDao.getMiddleNumber();
        int bad_num = weiBoDao.getBadNumber();

        res.put("good", good_num);
        res.put("middle", middle_num);
        res.put("bad", bad_num);

        CACHE.res_line_chart_3 = res;

        return res;
    }

    static class CACHE{
        public static HashMap<Object,Object> res_line_chart_1;
        public static HashMap<Object,Object> res_line_chart_2;
        public static HashMap<Object,Object> res_line_chart_3;
    }
}