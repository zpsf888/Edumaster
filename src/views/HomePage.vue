<template>
  <div class="home">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="logo">
        <h1>EduMaster在线教育平台</h1>
      </div>
      <div class="nav-buttons">
        <router-link to="/courses" class="course-btn">课程管理</router-link>
        <template v-if="!userStore.isLoggedIn">
          <router-link to="/login" class="login-btn">登录/注册</router-link>
        </template>
        <template v-else>
          <div class="user-menu" @click="toggleMenu">
            <i class="fa fa-user"></i> {{ userStore.username }}
            <div v-if="showMenu" class="dropdown">
              <button @click="logout">登出</button>
            </div>
          </div>
        </template>
      </div>
    </header>

    <!-- 主要内容区域 -->
    <main>
      <!-- 平台介绍banner -->
      <section class="banner">
        <h2>开启您的智能学习之旅</h2>
        <p>专业的在线教育平台，为您提供优质的学习体验</p>
      </section>

      <!-- 功能展示区域 -->
      <section class="features">
        <div class="feature-card">
          <i class="fas fa-book"></i>
          <h3>课程管理</h3>
          <ul>
            <li>课程发布与管理</li>
            <li>在线课程学习</li>
            <li>进度跟踪</li>
          </ul>
        </div>

        <div class="feature-card">
          <i class="fas fa-robot"></i>
          <h3>AI辅助教学</h3>
          <ul>
            <li>智能课程推荐</li>
            <li>虚拟助教答疑</li>
            <li>个性化学习方案</li>
          </ul>
        </div>

        <div class="feature-card">
          <i class="fas fa-comments"></i>
          <h3>评论与互动</h3>
          <ul>
            <li>课程评价</li>
            <li>学习社区</li>
            <li>实时讨论</li>
          </ul>
        </div>
      </section>
    </main>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue'
import { useUserStore } from '../store/user'
import { useRouter } from 'vue-router'

export default defineComponent({
  name: 'HomePage',
  setup() {
    const userStore = useUserStore()
    const router = useRouter()
    const showMenu = ref(false)

    const toggleMenu = () => {
      showMenu.value = !showMenu.value
    }

    const logout = () => {
      userStore.logout()
      showMenu.value = false
      router.push('/login')
    }

    return {
      userStore,
      showMenu,
      toggleMenu,
      logout
    }
  }
})
</script>

<style scoped>
.home {
  min-height: 100vh;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  background-color: #f0f8ff;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background-color: #ffffff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-bottom: 2rem;
  border-bottom: 2px solid #e6f3ff;
}

.banner {
  text-align: center;
  padding: 4rem 2rem;
  background-color: #e6f3ff;
  border-radius: 10px;
  margin-bottom: 3rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.banner h2 {
  font-size: 2.5rem;
  margin-bottom: 1rem;
  color: #2c5282;
}

.banner p {
  font-size: 1.2rem;
  color: #4a5568;
}

.features {
  display: flex;
  justify-content: space-between;
  padding: 2rem 0;
  flex-wrap: wrap;
  gap: 2rem;
}

.feature-card {
  flex: 1;
  min-width: 300px;
  padding: 2rem;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  text-align: center;
  background-color: #ffffff;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  border: 1px solid #e6f3ff;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(44, 82, 130, 0.15);
}

.feature-card i {
  font-size: 2.5rem;
  color: #3182ce;
  margin-bottom: 1.5rem;
}

.feature-card h3 {
  font-size: 1.5rem;
  margin-bottom: 1rem;
  color: #2c5282;
}

.feature-card ul {
  list-style: none;
  padding: 0;
  margin-top: 1rem;
}

.feature-card li {
  margin: 0.8rem 0;
  color: #4a5568;
  font-size: 1.1rem;
}

.login-btn {
  padding: 0.8rem 2rem;
  background-color: #3182ce;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1.1rem;
  transition: background-color 0.3s ease;
}

.login-btn:hover {
  background-color: #2c5282;
}

.logo h1 {
  font-size: 1.8rem;
  color: #2c5282;
}

.nav-buttons {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.course-btn {
  padding: 0.8rem 2rem;
  background-color: #e6f3ff;
  color: #3182ce;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1.1rem;
  text-decoration: none;
  transition: background-color 0.3s ease;
}

.course-btn:hover {
  background-color: #bfdbfe;
}

.user-menu {
  position: relative;
  cursor: pointer;
  padding: 0.8rem 2rem;
  background-color: #3182ce;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 1.1rem;
  transition: background-color 0.3s ease;
}

.user-menu:hover {
  background-color: #2c5282;
}

.dropdown {
  position: absolute;
  right: 0;
  top: 100%;
  background: #fff;
  border: 1px solid #eee;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  z-index: 10;
  margin-top: 0.5rem;
}

.dropdown button {
  background: none;
  border: none;
  padding: 8px 16px;
  width: 100%;
  text-align: left;
  cursor: pointer;
  color: #2c5282;
}

.dropdown button:hover {
  background-color: #e6f3ff;
}

@media (max-width: 768px) {
  .features {
    justify-content: center;
  }
  
  .feature-card {
    min-width: 100%;
  }
  
  .banner h2 {
    font-size: 2rem;
  }
  
  .logo h1 {
    font-size: 1.5rem;
  }
}
</style> 