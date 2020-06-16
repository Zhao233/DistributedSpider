package com.example.demo.WeiBoDao;

import com.example.demo.domain.WeiBo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WeiBoDao extends JpaRepository<WeiBo, Long> {
//    @Query(nativeQuery = true, value = "SELECT SUM()")
//    List<>

    @Query(nativeQuery = true, value = "select w_timestamp from wei_bo_record order by w_timestamp desc limit 1")
    long getMaxWTimestamp();

    @Query(nativeQuery = true, value = "select w_timestamp from wei_bo_record order by w_timestamp asc limit 1")
    long getMinWTimestamp();



    @Query(nativeQuery = true, value = "select sum(score) from (select avg(score) as score\n" +
            "                        from wei_bo_record\n" +
            "                        where score > 80\n" +
            "                        group by w_timestamp\n" +
            "                        having w_timestamp > ?1\n" +
            "                           and w_timestamp < ?2)u;")
    Integer getGoodAverageScoreByTIme(long start, long end);

    @Query(nativeQuery = true, value = "select sum(score) from (select avg(score) as score, w_timestamp as time\n" +
            "                        from wei_bo_record\n" +
            "                        where score < 50\n" +
            "                        group by w_timestamp\n" +
            "                        having w_timestamp > ?1\n" +
            "                           and w_timestamp < ?2)u;")
    Integer getBadAverageScoreByTIme(long start, long end);

    @Query(nativeQuery = true, value = "select sum(score) from (select avg(score) as score, w_timestamp as time\n" +
            "                        from wei_bo_record\n" +
            "                        where score > 50 and score < 80\n" +
            "                        group by w_timestamp\n" +
            "                        having w_timestamp > ?1\n" +
            "                           and w_timestamp < ?2)u;")
    Integer getMiddleAverageScoreByTIme(long start, long end);




    @Query(nativeQuery = true, value="select sum(time) from (select count(w_timestamp) as time\n" +
            "                      from wei_bo_record\n" +
            "                      group by w_timestamp\n" +
            "                      having w_timestamp > ?1\n" +
            "                         and w_timestamp < ?2)u")
    Integer getAttentionByDate(long start, long end);

    @Query(nativeQuery = true, value="select count(id) from wei_bo_record where score > 80")
    Integer getGoodNumber();

    @Query(nativeQuery = true, value="select count(id) from wei_bo_record where score > 50 and score < 80")
    Integer getMiddleNumber();

    @Query(nativeQuery = true, value="select count(id) from wei_bo_record where score < 50")
    Integer getBadNumber();
}
