<template>
  <div class="course-management">
    <button @click="$router.push('/home')" class="back-btn">返回主页</button>
    <div class="tabs">
      <div class="tabs-left">
        <button 
          v-for="tab in tabs" 
          :key="tab.id"
          :class="['tab-button', { active: currentTab === tab.id }]"
          @click="handleTabClick(tab.id)"
        >
          {{ tab.name }}
        </button>
      </div>
      <button class="ai-support-btn" @click="goToAISupport">
        <i class="fas fa-robot"></i>
        AI智能答疑
      </button>
    </div>

    <div class="tab-content">
      <!-- 搜索课程 -->
      <div v-if="currentTab === 'search-courses'" class="search-courses-container">
        <div class="search-box">
          <input 
            type="text" 
            v-model="searchQuery" 
            placeholder="输入课程名称、讲师或关键词搜索..."
            class="search-input"
            @input="handleSearch"
          >
          <button class="search-btn">
            <i class="fas fa-search"></i>
          </button>
        </div>
        
        <div class="courses-grid">
          <div v-for="course in filteredCourses" :key="course.id" class="course-card">
            <img :src="course.image" :alt="course.name" class="course-image">
            <div class="course-info">
              <h3>{{ course.name }}</h3>
              <p class="course-instructor">讲师: {{ course.instructor }}</p>
              <p class="course-description">{{ course.description }}</p>
              <div class="course-meta">
                <span>{{ course.duration }}</span>
                <span>{{ course.level }}</span>
              </div>
              <button class="join-course-btn" @click="joinCourse(course)" v-if="!course.isJoined">
                <i class="fas fa-plus"></i>
                加入课程
              </button>
              <button class="quit-btn" @click="quitCourse(course)" v-else>
                <i class="fas fa-sign-out-alt"></i>
                退出课程
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 发布课程 -->
      <div v-if="currentTab === 'publish-courses'" class="my-courses-container">
        <div class="action-buttons">
          <button class="action-btn add-btn" @click="handleAddCourse">
            <i class="fas fa-plus"></i>
            发布新课程
          </button>
        </div>
        <div class="courses-grid">
          <div v-for="course in publishedCourses" :key="course.id" class="course-card">
            <img :src="course.image" :alt="course.name" class="course-image">
            <div class="course-info">
              <h3>{{ course.name }}</h3>
              <div class="course-meta">
                <span>{{ course.duration }}</span>
                <span>{{ course.level }}</span>
              </div>
              <p class="course-description">{{ course.description }}</p>
              <div class="card-actions">
                <button class="card-btn edit-btn" @click="handleEditCourse(course.id)">
                  <i class="fas fa-edit"></i>
                  编辑课程
                </button>
                <button class="card-btn delete-btn" @click="handleDeleteSingleCourse(course.id)">
                  <i class="fas fa-trash"></i>
                  删除课程
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 我的课程 -->
      <div v-if="currentTab === 'my-courses'" class="courses-grid">
        <div v-for="course in myCourses" :key="course.id" class="course-card">
          <img :src="course.image" :alt="course.name" class="course-image">
          <div class="course-info">
            <h3>{{ course.name }}</h3>
            <p class="course-instructor">讲师: {{ course.instructor }}</p>
            <p class="course-progress">进度: {{ course.progress }}%</p>
            <div class="progress-bar">
              <div class="progress" :style="{ width: course.progress + '%' }"></div>
            </div>
            <p class="course-description">{{ course.description }}</p>
            <div class="course-meta">
              <span>{{ course.duration }}</span>
              <span>{{ course.level }}</span>
            </div>
            <div class="card-actions">
              <button class="card-btn continue-btn" @click="continueCourse(course)">
                <i class="fas fa-play"></i>
                继续学习
              </button>
              <button class="card-btn quit-btn" @click="quitCourse(course)">
                <i class="fas fa-sign-out-alt"></i>
                退出课程
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 课程推荐 -->
      <div v-if="currentTab === 'recommended'" class="recommendation-container">
        <div class="recommendation-header">
          <h2>个性化课程推荐</h2>
          <p class="recommendation-desc">基于您的学习历史和兴趣，为您推荐最适合的课程</p>
          <button class="generate-btn" @click="generateRecommendations" :disabled="isGenerating">
            <i class="fas fa-magic"></i>
            {{ isGenerating ? '生成中...' : '生成推荐课程' }}
          </button>
        </div>
        
        <div v-if="recommendedCourses.length === 0" class="empty-state">
          <i class="fas fa-lightbulb empty-icon"></i>
          <p>点击上方按钮，获取为您精心推荐的课程</p>
        </div>

        <div v-else class="courses-grid">
          <div v-for="course in recommendedCourses" :key="course.id" class="course-card">
            <div class="match-rate">
              <i class="fas fa-star"></i>
              匹配度 {{ course.matchRate }}%
            </div>
            <img :src="course.image" :alt="course.name" class="course-image">
            <div class="course-info">
              <h3>{{ course.name }}</h3>
              <p class="course-instructor">讲师: {{ course.instructor }}</p>
              <p class="course-description">{{ course.description }}</p>
              <div class="course-meta">
                <span>{{ course.level }}</span>
                <span>{{ course.duration }}</span>
              </div>
              <button class="join-btn" @click="joinCourse(course.id)">
                <i class="fas fa-plus"></i>
                加入课程
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 文件上传对话框 -->
    <div class="upload-dialog" v-if="showUploadDialog">
      <div class="upload-dialog-content">
        <h3>上传课程文件</h3>
        <div class="upload-area" @click="triggerFileInput" @drop.prevent="handleFileDrop" @dragover.prevent>
          <i class="fas fa-cloud-upload-alt"></i>
          <p>点击或拖拽文件到此处上传</p>
          <input type="file" ref="fileInput" @change="handleFileSelect" style="display: none">
        </div>
        <div class="dialog-buttons">
          <button class="cancel-btn" @click="showUploadDialog = false">取消</button>
          <button class="confirm-btn" @click="confirmUpload">确认上传</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

interface Course {
  id: number;
  name: string;
  instructor: string;
  description: string;
  duration: string;
  level: string;
  image: string;
  isJoined?: boolean;
  progress?: number;
}

export default defineComponent({
  name: 'CourseManagement',
  setup() {
    const currentTab = ref('publish-courses')
    const router = useRouter()
    const isGenerating = ref(false)
    const recommendedCourses = ref<any[]>([])
    const isDeleteMode = ref(false)
    const selectedCourses = ref<number[]>([])
    const showUploadDialog = ref(false)
    const fileInput = ref<HTMLInputElement | null>(null)
    const searchQuery = ref('')
    const filteredCourses = ref<Course[]>([])

    // 所有课程数据
    const allCourses = ref<Course[]>([
      {
        id: 1,
        name: 'Vue.js 高级开发',
        instructor: '张教授',
        description: '深入学习 Vue.js 框架的高级特性和最佳实践',
        duration: '30课时',
        level: '高级',
        image: '/course-images/vue-advanced.jpg',
        isJoined: false
      },
      {
        id: 2,
        name: 'TypeScript 实战',
        instructor: '李老师',
        description: '使用 TypeScript 开发大型应用的完整指南',
        duration: '24课时',
        level: '中级',
        image: '/course-images/typescript.jpg',
        isJoined: false
      },
      {
        id: 3,
        name: 'React 入门到精通',
        instructor: '王老师',
        description: '从零开始学习 React 框架开发',
        duration: '24课时',
        level: '初级到中级',
        image: '/course-images/react.jpg',
        isJoined: false
      },
      {
        id: 4,
        name: 'Node.js 后端开发',
        instructor: '赵教授',
        description: '使用 Node.js 构建可扩展的后端应用',
        duration: '36课时',
        level: '高级',
        image: '/course-images/nodejs.jpg',
        isJoined: false
      }
    ])

    const handleTabClick = (tabId: string) => {
      currentTab.value = tabId
      if (tabId === 'search-courses') {
        filteredCourses.value = allCourses.value
      }
      if (tabId === 'recommended') {
        recommendedCourses.value = []
      }
    }

    const generateRecommendations = async () => {
      isGenerating.value = true
      // 模拟API调用延迟
      await new Promise(resolve => setTimeout(resolve, 2000))
      
      // 模拟推荐课程数据
      recommendedCourses.value = [
        {
          id: 1,
          name: 'AI与机器学习实战',
          instructor: '张教授',
          description: '从零开始学习人工智能和机器学习的核心概念和实践应用',
          matchRate: 98,
          level: '中级',
          duration: '36课时',
          image: '/course-images/ai.jpg'
        },
        {
          id: 2,
          name: '区块链技术详解',
          instructor: '李博士',
          description: '深入了解区块链技术原理及其在实际中的应用',
          matchRate: 95,
          level: '高级',
          duration: '48课时',
          image: '/course-images/blockchain.jpg'
        },
        {
          id: 3,
          name: '云计算架构师课程',
          instructor: '王工程师',
          description: '掌握云计算架构设计和部署的核心技能',
          matchRate: 92,
          level: '高级',
          duration: '42课时',
          image: '/course-images/cloud.jpg'
        }
      ]
      
      isGenerating.value = false
    }

    const joinCourse = (course: Course) => {
      course.isJoined = true
      console.log('加入课程:', course.name)
    }

    const quitCourse = (course: Course) => {
      if (confirm(`确定要退出 "${course.name}" 课程吗？`)) {
        course.isJoined = false
        console.log('退出课程:', course.name)
      }
    }

    const continueCourse = (course: Course) => {
      router.push({
        name: 'CourseDetail',
        params: { id: course.id.toString() }
      })
    }

    const handleAddCourse = () => {
      // 实现添加课程的逻辑
      console.log('添加课程')
    }

    const handleDeleteCourse = () => {
      isDeleteMode.value = !isDeleteMode.value
      if (!isDeleteMode.value) {
        selectedCourses.value = []
      }
    }

    const handleDeleteSingleCourse = (courseId: number) => {
      if (confirm('确定要删除这个课程吗？')) {
        console.log('删除课程:', courseId)
        // 实现删除课程的逻辑
      }
    }

    const handleUploadFile = (courseId: number) => {
      console.log('为课程上传文件:', courseId)
      showUploadDialog.value = true
    }

    const triggerFileInput = () => {
      fileInput.value?.click()
    }

    const handleFileSelect = (event: Event) => {
      const files = (event.target as HTMLInputElement).files
      if (files && files.length > 0) {
        console.log('选择的文件:', files[0].name)
      }
    }

    const handleFileDrop = (event: DragEvent) => {
      const files = event.dataTransfer?.files
      if (files && files.length > 0) {
        console.log('拖拽的文件:', files[0].name)
      }
    }

    const confirmUpload = () => {
      // 实现文件上传逻辑
      console.log('确认上传')
      showUploadDialog.value = false
    }

    const handleSearch = () => {
      if (!searchQuery.value.trim()) {
        filteredCourses.value = allCourses.value
        return
      }
      
      const query = searchQuery.value.toLowerCase()
      filteredCourses.value = allCourses.value.filter(course => 
        course.name.toLowerCase().includes(query) ||
        course.instructor.toLowerCase().includes(query) ||
        course.description.toLowerCase().includes(query) ||
        course.level.toLowerCase().includes(query)
      )
    }

    // 初始化显示所有课程
    onMounted(() => {
      filteredCourses.value = allCourses.value
    })

    const tabs = [
      { id: 'search-courses', name: '搜索课程' },
      { id: 'publish-courses', name: '发布课程' },
      { id: 'my-courses', name: '我的课程' },
      { id: 'recommended', name: '课程推荐' }
    ]

    // 发布的课程列表
    const publishedCourses = ref<Course[]>([
      {
        id: 1,
        name: 'Vue.js 高级开发',
        instructor: '当前用户',
        description: '深入学习 Vue.js 框架的高级特性和最佳实践',
        duration: '30课时',
        level: '高级',
        image: '/course-images/vue-advanced.jpg'
      },
      {
        id: 2,
        name: 'TypeScript 实战',
        instructor: '当前用户',
        description: '使用 TypeScript 开发大型应用的完整指南',
        duration: '24课时',
        level: '中级',
        image: '/course-images/typescript.jpg'
      }
    ])

    // 我的课程（已加入的课程）列表
    const myCourses = ref<Course[]>([
      {
        id: 3,
        name: 'React 入门到精通',
        instructor: '张教授',
        description: '从零开始学习 React 框架开发',
        duration: '24课时',
        level: '中级',
        image: '/course-images/react.jpg',
        progress: 65
      },
      {
        id: 4,
        name: 'Node.js 后端开发',
        instructor: '李老师',
        description: '使用 Node.js 构建可扩展的后端应用',
        duration: '36课时',
        level: '高级',
        image: '/course-images/nodejs.jpg',
        progress: 30
      }
    ])

    const goToAISupport = () => {
      router.push({
        name: 'AISupport',
        params: { id: 'global' }  // 全局AI答疑
      })
    }

    return {
      currentTab,
      handleTabClick,
      isGenerating,
      generateRecommendations,
      recommendedCourses,
      joinCourse,
      quitCourse,
      continueCourse,
      isDeleteMode,
      selectedCourses,
      showUploadDialog,
      fileInput,
      handleAddCourse,
      handleDeleteCourse,
      handleDeleteSingleCourse,
      handleUploadFile,
      triggerFileInput,
      handleFileSelect,
      handleFileDrop,
      confirmUpload,
      tabs,
      publishedCourses,
      myCourses,
      searchQuery,
      filteredCourses,
      handleSearch,
      allCourses,
      goToAISupport
    }
  }
})
</script>

<style scoped>
.course-management {
  padding: 2rem;
  background-color: #f0f8ff;
  min-height: 100vh;
  max-width: 1400px;
  margin: 0 auto;
}

.tabs {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
  margin-bottom: 2rem;
  background-color: #ffffff;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.tabs-left {
  display: flex;
  gap: 1rem;
}

.tab-button {
  padding: 1rem 2.5rem;
  border: none;
  border-radius: 8px;
  background-color: transparent;
  color: #4a5568;
  cursor: pointer;
  font-size: 1.2rem;
  transition: all 0.3s ease;
  font-weight: 500;
}

.tab-button.active {
  background-color: #3182ce;
  color: white;
  box-shadow: 0 2px 4px rgba(49, 130, 206, 0.2);
}

.tab-button:hover:not(.active) {
  background-color: #e6f3ff;
  color: #3182ce;
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

.course-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-bottom: 1px solid #e6f3ff;
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

.course-progress {
  color: #3182ce;
  margin: 1rem 0;
  font-size: 1.1rem;
  font-weight: 500;
}

.progress-bar {
  height: 8px;
  background-color: #e6f3ff;
  border-radius: 4px;
  margin: 1rem 0 1.5rem;
  overflow: hidden;
}

.progress {
  height: 100%;
  background-color: #3182ce;
  border-radius: 4px;
  transition: width 0.5s ease;
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

.continue-btn, .join-btn {
  width: 100%;
  padding: 1rem;
  border: none;
  border-radius: 8px;
  background-color: #3182ce;
  color: white;
  cursor: pointer;
  font-size: 1.1rem;
  font-weight: 500;
  transition: all 0.3s ease;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.continue-btn:hover, .join-btn:hover {
  background-color: #2c5282;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(44, 82, 130, 0.2);
}

.course-instructor {
  color: #718096;
  font-size: 1rem;
  margin: 0.8rem 0;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.course-instructor::before {
  content: "👨‍🏫";
  font-size: 1.2rem;
}

.recommendation-container {
  padding: 2rem;
}

.recommendation-header {
  text-align: center;
  margin-bottom: 3rem;
}

.recommendation-header h2 {
  font-size: 2rem;
  color: #2c5282;
  margin-bottom: 1rem;
}

.recommendation-desc {
  color: #4a5568;
  font-size: 1.1rem;
  margin-bottom: 2rem;
}

.generate-btn {
  padding: 1rem 2rem;
  font-size: 1.1rem;
  background-color: #6B46C1;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin: 0 auto;
  transition: all 0.3s ease;
}

.generate-btn:hover:not(:disabled) {
  background-color: #553C9A;
  transform: translateY(-2px);
}

.generate-btn:disabled {
  background-color: #A0AEC0;
  cursor: not-allowed;
}

.generate-btn i {
  font-size: 1.2rem;
}

.empty-state {
  text-align: center;
  padding: 4rem 2rem;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.empty-icon {
  font-size: 4rem;
  color: #A0AEC0;
  margin-bottom: 1.5rem;
}

.empty-state p {
  color: #4a5568;
  font-size: 1.1rem;
}

.match-rate {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background-color: rgba(49, 130, 206, 0.9);
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  z-index: 1;
}

.match-rate i {
  color: #FFD700;
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

  .tabs {
    flex-direction: column;
    padding: 1rem;
  }

  .tabs-left {
    flex-direction: column;
    width: 100%;
  }

  .tab-button {
    width: 100%;
    padding: 1rem;
  }

  .course-info {
    padding: 1.5rem;
  }

  .ai-support-btn {
    width: 100%;
    justify-content: center;
  }
}

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

.delete-btn {
  background-color: #f44336;
  color: white;
}

.delete-btn:hover {
  background-color: #da190b;
}

.upload-btn {
  background-color: #2196F3;
  color: white;
}

.upload-btn:hover {
  background-color: #1976D2;
}

.course-select {
  position: absolute;
  top: 1rem;
  left: 1rem;
  z-index: 1;
}

.course-select input[type="checkbox"] {
  width: 20px;
  height: 20px;
  cursor: pointer;
}

.upload-dialog {
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

.upload-dialog-content {
  background-color: white;
  padding: 2rem;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
}

.upload-dialog h3 {
  margin-bottom: 1.5rem;
  color: #2c5282;
  text-align: center;
}

.upload-area {
  border: 2px dashed #3182ce;
  border-radius: 8px;
  padding: 2rem;
  text-align: center;
  cursor: pointer;
  margin-bottom: 1.5rem;
  transition: all 0.3s ease;
}

.upload-area:hover {
  border-color: #2c5282;
  background-color: #f0f8ff;
}

.upload-area i {
  font-size: 3rem;
  color: #3182ce;
  margin-bottom: 1rem;
}

.upload-area p {
  color: #4a5568;
}

.dialog-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
}

.dialog-buttons button {
  padding: 0.8rem 1.5rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.cancel-btn {
  background-color: #e2e8f0;
  color: #4a5568;
}

.cancel-btn:hover {
  background-color: #cbd5e0;
}

.confirm-btn {
  background-color: #3182ce;
  color: white;
}

.confirm-btn:hover {
  background-color: #2c5282;
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

.card-btn.upload-btn {
  background-color: #2196F3;
  color: white;
}

.card-btn.upload-btn:hover {
  background-color: #1976D2;
}

.card-btn.delete-btn {
  background-color: #f44336;
  color: white;
}

.card-btn.delete-btn:hover {
  background-color: #da190b;
}

.course-actions {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
}

.course-actions.joined {
  flex-direction: column;
}

.join-course-btn, .continue-btn, .quit-btn {
  width: 100%;
  padding: 0.8rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  transition: all 0.3s ease;
}

.join-course-btn {
  background-color: #4CAF50;
  color: white;
}

.join-course-btn:hover {
  background-color: #45a049;
}

.continue-btn {
  background-color: #2196F3;
  color: white;
}

.continue-btn:hover {
  background-color: #1976D2;
}

.quit-btn {
  background-color: #f44336;
  color: white;
}

.quit-btn:hover {
  background-color: #da190b;
}

.edit-btn {
  background-color: #2196F3;
  color: white;
}

.edit-btn:hover {
  background-color: #1976D2;
}

.search-courses-container {
  padding: 2rem;
}

.search-box {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
}

.search-input {
  flex: 1;
  padding: 0.8rem;
  border: 1px solid #e6f3ff;
  border-radius: 8px;
}

.search-btn {
  padding: 0.8rem 1.5rem;
  border: none;
  border-radius: 8px;
  background-color: #3182ce;
  color: white;
  cursor: pointer;
}

.search-btn i {
  font-size: 1.2rem;
}

.ai-support-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.8rem 1.5rem;
  background-color: #6B46C1;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
  font-size: 1rem;
}

.ai-support-btn:hover {
  background-color: #553C9A;
  transform: translateY(-2px);
  box-shadow: 0 2px 4px rgba(107, 70, 193, 0.2);
}

.ai-support-btn i {
  font-size: 1.2rem;
}

.back-btn {
  margin: 16px 0;
  padding: 6px 16px;
  background: #4caf50;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
</style> 