import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../store/user'
import CourseManagement from '../views/CourseManagement.vue'
import CourseDetail from '../views/CourseDetail.vue'
import AISupport from '../views/AISupport.vue'
import CourseLearning from '../views/CourseLearning.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/courses'
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('../views/Login.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('../views/Register.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/courses',
      name: 'courses',
      component: CourseManagement,
      meta: { requiresAuth: true }
    },
    {
      path: '/course/:id',
      name: 'CourseDetail',
      component: CourseDetail,
      meta: { requiresAuth: true }
    },
    {
      path: '/course/:id/ai-support',
      name: 'AISupport',
      component: AISupport,
      meta: { requiresAuth: true }
    },
    {
      path: '/course-learning/:id',
      name: 'CourseLearning',
      component: CourseLearning,
      meta: { requiresAuth: true }
    }
  ]
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const isAuthenticated = userStore.checkAuth()

  if (to.meta.requiresAuth && !isAuthenticated) {
    next('/login')
  } else if (!to.meta.requiresAuth && isAuthenticated) {
    next('/courses')
  } else {
    next()
  }
})

export default router
