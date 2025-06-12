package com.ai.llmservice.repository;

import com.ai.llmservice.entity.UserCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCourseRepository extends JpaRepository<UserCourse, Integer> {

    // 根据userId查询历史选课记录
    List<UserCourse> findByUserId(Integer userId);

}
