<template>
  <nav class="navbar">
    <router-link to="/courses" class="logo">EduMaster</router-link>
    <div class="nav-right">
      <template v-if="userStore.isLoggedIn">
        <div class="user-menu" @click="toggleMenu">
          <i class="fa fa-user"></i> {{ userStore.username }}
          <div v-if="showMenu" class="dropdown">
            <button @click="logout">登出</button>
          </div>
        </div>
      </template>
      <template v-else>
        <router-link to="/login">登录</router-link>
        <router-link to="/register">注册</router-link>
      </template>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useUserStore } from '../store/user';
import { useRouter } from 'vue-router';

const userStore = useUserStore();
const router = useRouter();
const showMenu = ref(false);

function toggleMenu() {
  showMenu.value = !showMenu.value;
}

function logout() {
  userStore.logout();
  showMenu.value = false;
  router.push('/login');
}
</script>

<style scoped>
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 1rem;
  height: 56px;
  background: #fff;
  border-bottom: 1px solid #eee;
  width: 100%;
}
.logo {
  font-weight: bold;
  font-size: 20px;
  color: #3182ce;
  text-decoration: none;
}
.nav-right {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-right: 1rem;
}
.nav-right a {
  color: #3182ce;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s;
  padding: 0.5rem 1rem;
}
.nav-right a:hover {
  color: #2c5282;
}
.user-menu {
  position: relative;
  cursor: pointer;
}
.dropdown {
  position: absolute;
  right: 0;
  top: 32px;
  background: #fff;
  border: 1px solid #eee;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  z-index: 10;
}
.dropdown button {
  background: none;
  border: none;
  padding: 8px 16px;
  width: 100%;
  text-align: left;
  cursor: pointer;
}
</style> 