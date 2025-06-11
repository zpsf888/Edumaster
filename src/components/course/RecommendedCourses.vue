<template>
  <div class="recommended-courses-container">
    <div class="header">
      <h2>为你推荐的课程</h2>
      <p>基于你的学习历史和兴趣，我们为你精选了以下课程</p>
      <button 
        class="generate-btn" 
        @click="generateRecommendations"
        :disabled="isGenerating"
      >
        {{ isGenerating ? '生成中...' : '重新生成推荐' }}
      </button>
    </div>

    <div class="courses-grid" v-if="recommendedCourses.length > 0">
      <div v-for="course in recommendedCourses" :key="course.id" class="course-card">
        <div class="course-info">
          <div class="match-rate">
            <span class="rate">{{ course.matchRate }}%</span>
            <span class="label">匹配度</span>
          </div>
          <h3>{{ course.name }}</h3>
          <div class="course-meta">
            <span>课号：{{ course.courseNumber }}</span>
          </div>
          <p class="course-description">{{ course.description }}</p>
          <div class="card-actions">
            <button class="card-btn join-btn" @click="handleJoinCourse(course.id)">
              加入课程
            </button>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="empty-state">
      <i class="fas fa-lightbulb"></i>
      <p>点击上方按钮生成课程推荐</p>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue'

interface RecommendedCourse {
  id: number;
  name: string;
  instructor: string;
  description: string;
  courseNumber: string;
  matchRate: number;
}

export default defineComponent({
  name: 'RecommendedCourses',
  setup() {
    const isGenerating = ref(false)
    const recommendedCourses = ref<RecommendedCourse[]>([])

    const generateRecommendations = async () => {
      isGenerating.value = true
      
      // 模拟API调用延迟
      await new Promise(resolve => setTimeout(resolve, 1500))
      
      recommendedCourses.value = [
        {
          id: 1,
          name: 'Java Web开发',
          instructor: '李四',
          description: '本课程专注于Java Web开发技术，包括Servlet、JSP、Spring MVC等。学生将学习如何构建动态Web应用程序，以及如何使用Java技术处理Web请求、生成响应和与数据库交互。',
          courseNumber: 'CS101',
          matchRate: 95
        },
        {
          id: 2,
          name: 'Java基础编程',
          instructor: '王五',
          description: '本课程是Java编程的入门课程，旨在帮助初学者掌握Java语言的基本语法和编程概念。课程内容包括变量、数据类型、控制结构、方法、数组等基础知识，并通过实例讲解如何编写简单的Java程序。',
          courseNumber: 'CS102',
          matchRate: 88
        }
      ]
      
      isGenerating.value = false
    }

    const handleJoinCourse = (courseId: number) => {
      console.log('加入课程:', courseId)
    }

    return {
      isGenerating,
      recommendedCourses,
      generateRecommendations,
      handleJoinCourse
    }
  }
})
</script>

<style scoped>
.recommended-courses-container {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.header {
  text-align: center;
  padding: 2rem 1rem;
}

.header h2 {
  color: #2c5282;
  font-size: 2rem;
  margin-bottom: 1rem;
}

.header p {
  color: #718096;
  font-size: 1.1rem;
  margin-bottom: 2rem;
}

.generate-btn {
  padding: 0.8rem 1.5rem;
  background-color: #2196F3;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 500;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  transition: all 0.3s ease;
}

.generate-btn:hover:not(:disabled) {
  background-color: #1976D2;
}

.generate-btn:disabled {
  background-color: #90caf9;
  cursor: not-allowed;
}

.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 2.5rem;
  padding: 1rem;
}

.course-card {
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: all 0.3s ease;
  position: relative;
  border: 1px solid #e6f3ff;
}

.course-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.course-info {
  padding: 2rem;
}

.match-rate {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background-color: #4CAF50;
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.2rem;
}

.match-rate .rate {
  font-size: 1.2rem;
  font-weight: 600;
}

.match-rate .label {
  font-size: 0.8rem;
  opacity: 0.9;
}

.course-info h3 {
  color: #2c5282;
  margin-bottom: 1rem;
  font-size: 1.4rem;
  font-weight: 600;
  padding-right: 80px;
}

.course-description {
  color: #4a5568;
  margin: 1rem 0;
  font-size: 1rem;
  line-height: 1.6;
}

.course-meta {
  display: flex;
  justify-content: space-between;
  color: #718096;
  font-size: 1rem;
  margin: 1.5rem 0;
  padding: 0.8rem 0;
  border-top: 1px solid #e6f3ff;
}

.card-actions {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
}

.card-btn {
  flex: 1;
  padding: 0.8rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  transition: all 0.3s ease;
}

.card-btn i {
  font-size: 1rem;
}

.join-btn {
  background-color: #4CAF50;
  color: white;
}

.join-btn:hover {
  background-color: #45a049;
}

.empty-state {
  text-align: center;
  padding: 4rem 1rem;
  color: #718096;
}

.empty-state i {
  font-size: 3rem;
  margin-bottom: 1rem;
  color: #cbd5e0;
}

.empty-state p {
  font-size: 1.1rem;
}

@media (max-width: 1200px) {
  .courses-grid {
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  }
}

@media (max-width: 768px) {
  .courses-grid {
    grid-template-columns: 1fr;
    gap: 2rem;
  }
}
</style> 