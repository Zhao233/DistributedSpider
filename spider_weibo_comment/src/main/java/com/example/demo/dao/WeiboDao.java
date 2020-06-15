package com.example.demo.dao;

import com.example.demo.domain.Weibo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeiboDao extends JpaRepository<Weibo, Long> {
    @Query(value = "SELECT weibo.w_id FROM Weibo weibo WHERE weibo.type = 0 and weibo.isProcess = 0")
    List<String> getAllContentIds();

    @Query(value = "SELECT weibo.score FROM Weibo weibo WHERE weibo.w_id = ?1")
    int getContentScoreByWID(String id);
}
