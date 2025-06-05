<template>
  <div class="register-container">
    <h2>注册</h2>
    <form @submit.prevent="handleSubmit" class="register-form">
      <div class="form-group">
        <label for="username">用户名</label>
        <input
          type="text"
          id="username"
          v-model="form.username"
          required
          placeholder="请输入用户名"
        />
      </div>
      <div class="form-group">
        <label for="email">邮箱</label>
        <input
          type="email"
          id="email"
          v-model="form.email"
          required
          placeholder="请输入邮箱"
        />
      </div>
      <div class="form-group">
        <label for="password">密码</label>
        <input
          type="password"
          id="password"
          v-model="form.password"
          required
          minlength="6"
          placeholder="请输入密码（至少6位）"
        />
      </div>
      <div class="error-message" v-if="errorMessage">{{ errorMessage }}</div>
      <button type="submit" :disabled="isLoading">
        {{ isLoading ? '注册中...' : '注册' }}
      </button>
      <div class="login-link">
        已有账号？<router-link to="/login">立即登录</router-link>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { register } from '../api/auth';
import { useUserStore } from '../store/user';

const router = useRouter();
const userStore = useUserStore();

const form = reactive({
  username: '',
  email: '',
  password: '',
});

const isLoading = ref(false);
const errorMessage = ref('');

const handleSubmit = async () => {
  try {
    isLoading.value = true;
    errorMessage.value = '';
    
    await register(form);
    // 注册成功后自动登录
    const loginSuccess = await userStore.login(form.username, form.password);
    if (loginSuccess) {
      router.push('/home');
    } else {
      errorMessage.value = '注册成功，但自动登录失败，请手动登录';
      router.push('/login');
    }
  } catch (error: any) {
    errorMessage.value = error.response?.data?.message || '注册失败，请稍后重试';
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
.register-container {
  max-width: 400px;
  margin: 40px auto;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.register-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

label {
  font-weight: 500;
}

input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

button {
  padding: 12px;
  background-color: #3182ce;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.error-message {
  color: #ff4444;
  font-size: 14px;
}

.login-link {
  text-align: center;
  font-size: 14px;
}

.login-link a {
  color: #3182ce;
  text-decoration: none;
}
</style> 