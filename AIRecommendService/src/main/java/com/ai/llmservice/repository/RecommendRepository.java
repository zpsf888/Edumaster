package com.ai.llmservice.repository;

import com.ai.llmservice.entity.Recommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendRepository extends JpaRepository<Recommend, Integer> {

    // 根据 userId 删除用户之前所有推荐记录
    void deleteByUserId(Integer userId);

    // 根据 userId 查询用户推荐课程
    // Spring Data JPA 规范方法
    // 如果要自定义更多查询，可在此添加
    java.util.List<Recommend> findByUserId(Integer userId);
}
