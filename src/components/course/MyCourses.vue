<template>
  <div class="my-courses-container">
    <div class="courses-grid">
      <div v-for="course in myCourses" :key="course.id" class="course-card">
        <div class="course-info">
          <h3>{{ course.name }}</h3>
          <div class="course-meta">
            <span>课号：{{ course.courseNumber }}</span>
          </div>
          <div class="progress-bar">
            <div class="progress" :style="{ width: course.progress + '%' }"></div>
          </div>
          <p class="progress-text">完成度: {{ course.progress }}%</p>
          <p class="course-description">{{ course.description }}</p>
          <div class="card-actions">
            <button class="card-btn continue-btn" @click="handleContinueCourse(course.id)">
              <i class="fas fa-play"></i>
              继续学习
            </button>
            <button class="card-btn quit-btn" @click="handleQuitCourse(course.id)">
              <i class="fas fa-sign-out-alt"></i>
              退出课程
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue'

interface Course {
  id: number;
  name: string;
  instructor: string;
  description: string;
  courseNumber: string;
  progress: number;
}

export default defineComponent({
  name: 'MyCourses',
  setup() {
    const myCourses = ref<Course[]>([
      {
        id: 1,
        name: 'Vue.js 高级开发',
        instructor: '张老师',
        description: '深入学习 Vue.js 框架的高级特性和最佳实践',
        courseNumber: 'CS101',
        progress: 75
      },
      {
        id: 2,
        name: 'TypeScript 实战',
        instructor: '李老师',
        description: '使用 TypeScript 开发大型应用的完整指南',
        courseNumber: 'CS102',
        progress: 30
      }
    ])

    const handleContinueCourse = (courseId: number) => {
      console.log('继续学习课程:', courseId)
    }

    const handleQuitCourse = (courseId: number) => {
      if (confirm('确定要退出这个课程吗？')) {
        console.log('退出课程:', courseId)
      }
    }

    return {
      myCourses,
      handleContinueCourse,
      handleQuitCourse
    }
  }
})
</script>

<style scoped>
.my-courses-container {
  display: flex;
  flex-direction: column;
  gap: 2rem;
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

.course-info h3 {
  color: #2c5282;
  margin-bottom: 1rem;
  font-size: 1.4rem;
  font-weight: 600;
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

.progress-bar {
  width: 100%;
  height: 8px;
  background-color: #e6f3ff;
  border-radius: 4px;
  overflow: hidden;
  margin: 1rem 0;
}

.progress {
  height: 100%;
  background-color: #4CAF50;
  transition: width 0.3s ease;
}

.progress-text {
  color: #718096;
  font-size: 0.9rem;
  margin-bottom: 1rem;
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

.continue-btn {
  background-color: #4CAF50;
  color: white;
}

.continue-btn:hover {
  background-color: #45a049;
}

.quit-btn {
  background-color: #f44336;
  color: white;
}

.quit-btn:hover {
  background-color: #da190b;
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