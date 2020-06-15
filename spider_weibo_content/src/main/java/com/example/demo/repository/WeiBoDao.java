package com.example.demo.repository;

import com.example.demo.domain.WeiBo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WeiBoDao extends JpaRepository<WeiBo, Long> {
    @Query(nativeQuery = true, value = "SELECT w_timestamp from wei_bo_record where type=0 order by w_timestamp desc limit 1")
    long getLastedTime();
}
