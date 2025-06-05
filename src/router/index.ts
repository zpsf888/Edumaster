import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../views/HomePage.vue'
import CourseManagement from '../views/CourseManagement.vue'
import CourseDetail from '../views/CourseDetail.vue'
import AISupport from '../views/AISupport.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomePage
    },
    {
      path: '/courses',
      name: 'courses',
      component: CourseManagement
    },
    {
      path: '/course/:id',
      name: 'CourseDetail',
      component: CourseDetail
    },
    {
      path: '/course/:id/ai-support',
      name: 'AISupport',
      component: AISupport
    }
  ]
})

export default router
