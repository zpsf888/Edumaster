<template>
  <div class="my-courses-container">
    <div class="action-buttons">
      <button class="action-btn add-btn" @click="showAddCourseModal = true">
        <i class="fas fa-plus"></i>
        发布新课程
      </button>
    </div>
    <div v-if="isLoading" class="loading-state">
      <i class="fas fa-spinner fa-spin"></i>
      加载中...
    </div>
    <div v-else-if="publishedCourses.length === 0" class="empty-state">
      暂无发布的课程
    </div>
    <div v-else class="courses-grid">
      <div v-for="course in publishedCourses" :key="course.id" class="course-card">
        <div class="course-info">
          <h3>{{ course.name }}</h3>
          <div class="course-meta">
            <span>课号：{{ course.courseNumber }}</span>
          </div>
          <p class="course-description">{{ course.description }}</p>
          <div class="card-actions">
            <button class="card-btn edit-btn" @click="handleEditCourse(course.id)">
              <i class="fas fa-edit"></i>
              编辑课程
            </button>
            <button 
              class="card-btn delete-btn" 
              @click="handleDeleteSingleCourse(course.id)"
              :disabled="isDeleting === course.id"
            >
              <i class="fas" :class="isDeleting === course.id ? 'fa-spinner fa-spin' : 'fa-trash'"></i>
              {{ isDeleting === course.id ? '删除中...' : '删除课程' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加课程弹窗 -->
    <div v-if="showAddCourseModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>发布新课程</h2>
          <button class="close-btn" @click="closeModal">&times;</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="submitNewCourse">
            <div class="form-group">
              <label for="courseName">课程名称</label>
              <input
                type="text"
                id="courseName"
                v-model="newCourse.name"
                placeholder="请输入课程名称"
                required
              >
            </div>
            <div class="form-group">
              <label for="courseDescription">课程简介</label>
              <textarea
                id="courseDescription"
                v-model="newCourse.description"
                placeholder="请输入课程简介"
                rows="4"
                required
              ></textarea>
            </div>
            <div class="form-actions">
              <button type="button" class="cancel-btn" @click="closeModal">取消</button>
              <button type="submit" class="submit-btn" :disabled="isSubmitting">
                <i class="fas" :class="isSubmitting ? 'fa-spinner fa-spin' : 'fa-check'"></i>
                {{ isSubmitting ? '发布中...' : '发布课程' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted } from 'vue'
import axios from 'axios'

interface Course {
  id: number;
  name: string;
  instructor: string;
  description: string;
  courseNumber: string;
}

interface NewCourse {
  name: string;
  description: string;
}

interface ApiResponse {
  code: number;
  message: string;
  courseId: number;
}

interface CourseListResponse {
  code: number;
  message: string;
  data: {
    courseId: number;
    title: string;
    description: string;
    creatorId: number;
  }[];
}

export default defineComponent({
  name: 'PublishCourses',
  setup() {
    const showAddCourseModal = ref(false)
    const isSubmitting = ref(false)
    const isDeleting = ref<number | null>(null)
    const isLoading = ref(true)
    const newCourse = ref<NewCourse>({
      name: '',
      description: ''
    })

    const publishedCourses = ref<Course[]>([])

    const fetchPublishedCourses = async () => {
      try {
        isLoading.value = true
        const response = await axios.get<CourseListResponse>('http://localhost:8081/users/1/courses')

        if (response.data.code === 200) {
          publishedCourses.value = response.data.data.map(course => ({
            id: course.courseId,
            name: course.title,
            instructor: '当前用户',
            description: course.description,
            courseNumber: `CS${course.courseId.toString().padStart(3, '0')}`
          }))
        } else {
          alert('获取课程列表失败：' + response.data.message)
        }
      } catch (error) {
        console.error('获取课程列表时出错：', error)
        alert('获取课程列表失败，请稍后重试')
      } finally {
        isLoading.value = false
      }
    }

    const generateCourseNumber = () => {
      const lastCourse = publishedCourses.value[publishedCourses.value.length - 1]
      const lastNumber = parseInt(lastCourse.courseNumber.replace('CS', ''))
      return `CS${(lastNumber + 1).toString().padStart(3, '0')}`
    }

    const closeModal = () => {
      showAddCourseModal.value = false
      newCourse.value = {
        name: '',
        description: ''
      }
      isSubmitting.value = false
    }

    const submitNewCourse = async () => {
      try {
        isSubmitting.value = true
        
        const response = await axios.post<ApiResponse>('http://localhost:8081/courses', {
          title: newCourse.value.name,
          description: newCourse.value.description,
          creatorId: 1 // 这里应该使用实际的用户ID
        })

        if (response.data.code === 201) {
          const course: Course = {
            id: response.data.courseId,
            name: newCourse.value.name,
            instructor: '当前用户',
            description: newCourse.value.description,
            courseNumber: generateCourseNumber()
          }
          
          publishedCourses.value.push(course)
          alert('课程发布成功！')
          closeModal()
        } else {
          alert('课程发布失败：' + response.data.message)
        }
      } catch (error) {
        console.error('发布课程时出错：', error)
        alert('发布课程失败，请稍后重试')
      } finally {
        isSubmitting.value = false
      }
    }

    const handleEditCourse = (courseId: number) => {
      console.log('编辑课程:', courseId)
    }

    const handleDeleteSingleCourse = async (courseId: number) => {
      if (!confirm('确定要删除这个课程吗？')) {
        return
      }

      try {
        isDeleting.value = courseId
        
        const response = await axios.delete<ApiResponse>(`http://localhost:8081/courses/${courseId}`)

        if (response.data.code === 201) {
          publishedCourses.value = publishedCourses.value.filter(course => course.id !== courseId)
          alert('课程删除成功！')
        } else {
          alert('课程删除失败：' + response.data.message)
        }
      } catch (error: any) {
        if (error.response?.status === 404) {
          alert('课程不存在')
        } else {
          console.error('删除课程时出错：', error)
          alert('删除课程失败，请稍后重试')
        }
      } finally {
        isDeleting.value = null
      }
    }

    onMounted(() => {
      fetchPublishedCourses()
    })

    return {
      publishedCourses,
      showAddCourseModal,
      isSubmitting,
      isDeleting,
      isLoading,
      newCourse,
      closeModal,
      submitNewCourse,
      handleEditCourse,
      handleDeleteSingleCourse
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

.action-buttons {
  display: flex;
  gap: 1rem;
  padding: 0 1rem;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
}

.action-btn {
  padding: 0.8rem 1.5rem;
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

.action-btn i {
  font-size: 1.1rem;
}

.add-btn {
  background-color: #4CAF50;
  color: white;
}

.add-btn:hover {
  background-color: #45a049;
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

.edit-btn {
  background-color: #2196F3;
  color: white;
}

.edit-btn:hover {
  background-color: #1976D2;
}

.delete-btn {
  background-color: #f44336;
  color: white;
}

.delete-btn:hover {
  background-color: #da190b;
}

.delete-btn:disabled {
  background-color: #ffcdd2;
  cursor: not-allowed;
}

.delete-btn i {
  margin-right: 0.5rem;
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

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  border-radius: 12px;
  width: 90%;
  max-width: 600px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.modal-header {
  padding: 1.5rem;
  border-bottom: 1px solid #e6f3ff;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h2 {
  margin: 0;
  color: #2c5282;
  font-size: 1.5rem;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  color: #718096;
  cursor: pointer;
  padding: 0.5rem;
}

.modal-body {
  padding: 1.5rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #4a5568;
  font-weight: 500;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 0.8rem;
  border: 1px solid #e6f3ff;
  border-radius: 6px;
  font-size: 1rem;
  transition: border-color 0.3s ease;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #2196F3;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 2rem;
}

.cancel-btn,
.submit-btn {
  padding: 0.8rem 1.5rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 500;
  transition: all 0.3s ease;
}

.cancel-btn {
  background-color: #e2e8f0;
  color: #4a5568;
}

.cancel-btn:hover {
  background-color: #cbd5e0;
}

.submit-btn {
  background-color: #2196F3;
  color: white;
}

.submit-btn:hover {
  background-color: #1976D2;
}

.submit-btn:disabled {
  background-color: #90caf9;
  cursor: not-allowed;
}

.submit-btn i {
  margin-right: 0.5rem;
}

.loading-state,
.empty-state {
  text-align: center;
  padding: 2rem;
  color: #666;
  font-size: 1.1rem;
}

.loading-state i {
  margin-right: 0.5rem;
  font-size: 1.5rem;
}

.empty-state {
  color: #999;
}
</style> 