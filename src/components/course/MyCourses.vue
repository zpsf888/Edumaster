<template>
  <div class="my-courses-container">
    <div v-if="isLoading" class="loading-state">
      <i class="fas fa-spinner fa-spin"></i>
      加载中...
    </div>

    <div v-else class="courses-grid">
      <div v-for="course in myCourses" :key="course.courseId" class="course-card">
        <div class="course-info">
          <h3>{{ course.title }}</h3>
          <div class="course-meta">
            <span>课号：{{ course.courseNumber }}</span>
          </div>
          <p class="course-description">{{ course.description }}</p>
          <div class="card-actions">
            <button class="card-btn continue-btn" @click="handleContinueCourse(course.courseId)">
              继续学习
            </button>
            <button 
              class="card-btn quit-btn" 
              @click="handleQuitCourse(course.courseId)"
              :disabled="quittingCourseId === course.courseId"
            >
              {{ quittingCourseId === course.courseId ? '退出中...' : '退出课程' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

interface Course {
  courseId: number;
  title: string;
  description: string;
  creatorId: number;
  courseNumber: string;
}

interface CourseResponse {
  code: number;
  data: Course[];
  message: string;
}

interface QuitCourseResponse {
  code: number;
  message: string;
}

export default defineComponent({
  name: 'MyCourses',
  setup() {
    const router = useRouter()
    const myCourses = ref<Course[]>([])
    const isLoading = ref(true)
    const quittingCourseId = ref<number | null>(null)

    const fetchMyCourses = async () => {
      try {
        isLoading.value = true
        // TODO: 从用户状态获取userId
        const userId = 1 // 临时使用固定值，后续需要从用户状态获取
        const response = await axios.get<CourseResponse>(`http://localhost:8081/students/${userId}/courses`)
        
        if (response.data.code === 200) {
          // 为每个课程生成课号
          myCourses.value = response.data.data.map(course => ({
            ...course,
            courseNumber: `CS${course.courseId.toString().padStart(3, '0')}`
          }))
        } else {
          throw new Error(response.data.message || '获取课程列表失败')
        }
      } catch (error) {
        console.error('获取我的课程列表时出错：', error)
        alert(error instanceof Error ? error.message : '获取课程列表失败，请稍后重试')
      } finally {
        isLoading.value = false
      }
    }

    const handleContinueCourse = (courseId: number) => {
      router.push({
        name: 'CourseDetail',
        params: { id: courseId.toString() }
      })
    }

    const handleQuitCourse = async (courseId: number) => {
      if (quittingCourseId.value === courseId) return
      
      if (!confirm('确定要退出这个课程吗？')) {
        return
      }

      try {
        quittingCourseId.value = courseId
        // TODO: 从用户状态获取userId
        const userId = 1 // 临时使用固定值，后续需要从用户状态获取
        const response = await axios.delete<QuitCourseResponse>(`http://localhost:8081/students/${userId}/courses/${courseId}`)
        
        if (response.data.code === 200) {
          alert('成功退出课程')
          // 刷新课程列表
          await fetchMyCourses()
        } else {
          throw new Error(response.data.message || '退出课程失败')
        }
      } catch (error) {
        console.error('退出课程时出错：', error)
        alert(error instanceof Error ? error.message : '退出课程失败，请稍后重试')
      } finally {
        quittingCourseId.value = null
      }
    }

    onMounted(() => {
      fetchMyCourses()
    })

    return {
      myCourses,
      isLoading,
      quittingCourseId,
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

.quit-btn:hover:not(:disabled) {
  background-color: #da190b;
}

.quit-btn:disabled {
  background-color: #ffcdd2;
  cursor: not-allowed;
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