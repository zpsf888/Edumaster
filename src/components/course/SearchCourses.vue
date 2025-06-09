<template>
  <div class="search-courses-container">
    <div class="search-bar">
      <input
        type="text"
        v-model="searchQuery"
        @input="handleSearch"
        placeholder="搜索课程..."
        class="search-input"
      >
      <button class="search-btn" @click="handleSearch">
        <i class="fas fa-search"></i>
        搜索
      </button>
    </div>

    <div class="courses-grid">
      <div v-for="course in filteredCourses" :key="course.id" class="course-card">
        <div class="course-info">
          <h3>{{ course.name }}</h3>
          <div class="course-meta">
            <span>课号：{{ course.courseNumber }}</span>
          </div>
          <p class="course-description">{{ course.description }}</p>
          <div class="card-actions">
            <button class="card-btn join-btn" @click="handleJoinCourse(course.id)">
              <i class="fas fa-sign-in-alt"></i>
              加入课程
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
}

export default defineComponent({
  name: 'SearchCourses',
  setup() {
    const searchQuery = ref('')
    const filteredCourses = ref<Course[]>([
      {
        id: 1,
        name: 'Vue.js 高级开发',
        instructor: '张老师',
        description: '深入学习 Vue.js 框架的高级特性和最佳实践',
        courseNumber: 'CS101'
      },
      {
        id: 2,
        name: 'TypeScript 实战',
        instructor: '李老师',
        description: '使用 TypeScript 开发大型应用的完整指南',
        courseNumber: 'CS102'
      }
    ])

    const handleSearch = () => {
      console.log('搜索课程:', searchQuery.value)
    }

    const handleJoinCourse = (courseId: number) => {
      console.log('加入课程:', courseId)
    }

    return {
      searchQuery,
      filteredCourses,
      handleSearch,
      handleJoinCourse
    }
  }
})
</script>

<style scoped>
.search-courses-container {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.search-bar {
  display: flex;
  gap: 1rem;
  padding: 0 1rem;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
}

.search-input {
  flex: 1;
  padding: 0.8rem 1.2rem;
  border: 1px solid #e6f3ff;
  border-radius: 8px;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.search-input:focus {
  outline: none;
  border-color: #2196F3;
  box-shadow: 0 0 0 2px rgba(33, 150, 243, 0.1);
}

.search-btn {
  padding: 0.8rem 1.5rem;
  background-color: #2196F3;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  transition: all 0.3s ease;
}

.search-btn:hover {
  background-color: #1976D2;
}

.search-btn i {
  font-size: 1.1rem;
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