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

    <div v-if="isLoading" class="loading-state">
      <i class="fas fa-spinner fa-spin"></i>
      加载中...
    </div>

    <div v-else class="courses-grid">
      <div v-for="course in filteredCourses" :key="course.course_id" class="course-card">
        <div class="course-info">
          <h3>{{ course.title }}</h3>
          <div class="course-meta">
            <span>课号：{{ course.courseNumber }}</span>
          </div>
          <p class="course-description">{{ course.description }}</p>
          <div class="card-actions">
            <button class="card-btn join-btn" @click="handleJoinCourse(course.course_id)">
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
import { defineComponent, ref, computed, onMounted } from 'vue'
import axios from 'axios'

interface Course {
  course_id: number;
  creator_id: number;
  description: string;
  title: string;
  courseNumber: string;
}

interface CourseResponse {
  code: number;
  data: Course[];
  message: string;
}

export default defineComponent({
  name: 'SearchCourses',
  setup() {
    const searchQuery = ref('')
    const courses = ref<Course[]>([])
    const isLoading = ref(true)

    const fetchCourses = async () => {
      try {
        isLoading.value = true
        const response = await axios.get<CourseResponse>('http://localhost:8081/courses')
        
        if (response.data.code === 200) {
          courses.value = response.data.data.map(course => ({
            ...course,
            courseNumber: `CS${course.course_id.toString().padStart(3, '0')}`
          }))
        } else {
          throw new Error(response.data.message || '获取课程列表失败')
        }
      } catch (error) {
        console.error('获取课程列表时出错：', error)
        alert(error instanceof Error ? error.message : '获取课程列表失败，请稍后重试')
      } finally {
        isLoading.value = false
      }
    }

    const filteredCourses = computed(() => {
      if (!searchQuery.value) return courses.value
      
      const query = searchQuery.value.toLowerCase()
      return courses.value.filter(course => 
        course.title.toLowerCase().includes(query) ||
        course.description.toLowerCase().includes(query) ||
        course.courseNumber.toLowerCase().includes(query)
      )
    })

    const handleSearch = () => {
      // 搜索逻辑已通过计算属性实现
    }

    const handleJoinCourse = (courseId: number) => {
      console.log('加入课程:', courseId)
    }

    onMounted(() => {
      fetchCourses()
    })

    return {
      searchQuery,
      filteredCourses,
      isLoading,
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

.loading-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
  color: #4a5568;
  font-size: 1.2rem;
}

.loading-state i {
  margin-right: 0.5rem;
  font-size: 1.5rem;
}
</style> 