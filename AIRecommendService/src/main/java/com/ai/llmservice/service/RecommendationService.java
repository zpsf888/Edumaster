package com.ai.llmservice.service;

import com.ai.llmservice.entity.Course;
import com.ai.llmservice.entity.UserCourse;
import com.ai.llmservice.entity.Recommend;
import com.ai.llmservice.repository.CourseRepository;
import com.ai.llmservice.repository.UserCourseRepository;
import com.ai.llmservice.repository.RecommendRepository;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 一定要导入这个包

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final CourseRepository courseRepository;
    private final UserCourseRepository userCourseRepository;
    private final AiKimiService aiKimiService;
    private final RecommendRepository recommendRepository;

    @Autowired
    public RecommendationService(CourseRepository courseRepository,
                                 UserCourseRepository userCourseRepository,
                                 AiKimiService aiKimiService,
                                 RecommendRepository recommendRepository) {
        this.courseRepository = courseRepository;
        this.userCourseRepository = userCourseRepository;
        this.aiKimiService = aiKimiService;
        this.recommendRepository = recommendRepository;
    }

    /**
     * 根据 userId 获取推荐课程 ID 并写入 recommend 表。
     * 注意：增加 @Transactional 确保删除/写入操作有事务支持。
     */
    @Transactional
    public List<Integer> recommendCourses(Integer userId) throws Exception {
        // 1. 获取所有课程
        List<Course> allCourses = courseRepository.findAll();
        // 2. 获取该用户历史选课记录
        List<UserCourse> userHistory = userCourseRepository.findByUserId(userId);

        // 3. 构造 prompt
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("以下是全部课程信息：\n");
        for (Course c : allCourses) {
            promptBuilder.append("课程ID: ").append(c.getCourseId())
                    .append(", 标题: ").append(c.getTitle())
                    .append(", 描述: ").append(c.getDescription())
                    .append("; \n");
        }
        promptBuilder.append("\n用户已选课程:\n");
        for (UserCourse uc : userHistory) {
            promptBuilder.append("user_id: ").append(uc.getUserId())
                    .append(", course_id: ").append(uc.getCourseId())
                    .append("\n");
        }
        promptBuilder.append("请根据这些信息，给出用户推荐的课程ID列表，以JSON数组返回，例如 [28,29,30],这是铁律，同时不要再推荐已经加入的课程，推荐数量不要超过5个。");

        // 4. 调用大模型
        String responseStr = aiKimiService.completions(promptBuilder.toString());

        // 5. 解析返回结果 (假设豆包返回的就是一个JSON数组字符串，如: [28, 30, 31])
        JSONArray jsonArray = new JSONArray(responseStr);
        // 把JSON数组转成List<Integer>
        List<Integer> recommendedCourseIds = jsonArray
                .toList()
                .stream()
                .map(o -> Integer.valueOf(o.toString()))
                .collect(Collectors.toList());

        // 6. 删除旧的推荐数据 (需要事务)
        recommendRepository.deleteByUserId(userId);

        // 7. 根据 AI 返回的课程 ID，从 course 表拿到详细信息并插入 recommend 表
        List<Course> recommendedCourses = courseRepository.findAllById(recommendedCourseIds);
        for (Course course : recommendedCourses) {
            Recommend rec = new Recommend();
            rec.setUserId(userId);
            rec.setCourseId(course.getCourseId());
            rec.setTitle(course.getTitle());
            rec.setDescription(course.getDescription());
            rec.setCreatorId(course.getCreatorId());
            recommendRepository.save(rec);
        }

        return recommendedCourseIds;
    }
}
