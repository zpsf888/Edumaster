<template>
  <div class="course-management">
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
      <SearchCourses v-if="currentTab === 'search-courses'" />
      <PublishCourses v-if="currentTab === 'publish-courses'" />
      <MyCourses v-if="currentTab === 'my-courses'" />
      <RecommendedCourses v-if="currentTab === 'recommended'" />
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue'
import { useRouter } from 'vue-router'
import SearchCourses from '@/components/course/SearchCourses.vue'
import PublishCourses from '@/components/course/PublishCourses.vue'
import MyCourses from '@/components/course/MyCourses.vue'
import RecommendedCourses from '@/components/course/RecommendedCourses.vue'

export default defineComponent({
  name: 'CourseManagement',
  components: {
    SearchCourses,
    PublishCourses,
    MyCourses,
    RecommendedCourses
  },
  setup() {
    const currentTab = ref('publish-courses')
    const router = useRouter()

    const handleTabClick = (tabId: string) => {
      currentTab.value = tabId
    }

    const goToAISupport = () => {
      router.push({
        name: 'AISupport',
        params: { id: 'global' }  // 全局AI答疑
      })
    }

    const tabs = [
      { id: 'search-courses', name: '搜索课程' },
      { id: 'publish-courses', name: '发布课程' },
      { id: 'my-courses', name: '我的课程' },
      { id: 'recommended', name: '课程推荐' }
    ]

    return {
      currentTab,
      handleTabClick,
      tabs,
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

@media (max-width: 768px) {
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

  .ai-support-btn {
    width: 100%;
    justify-content: center;
  }
}
</style> 