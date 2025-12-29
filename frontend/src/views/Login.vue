<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-header">
        <div class="logo-icon">
          <svg width="56" height="56" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
            <circle cx="12" cy="12" r="6" stroke="currentColor" stroke-width="1.5" opacity="0.6"/>
            <circle cx="12" cy="12" r="2" fill="currentColor"/>
            <path d="M12 2 L12 6 M12 18 L12 22 M2 12 L6 12 M18 12 L22 12" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
          </svg>
        </div>
        <h1>BigWheel</h1>
        <p>행운의 빅휠에 오신 것을 환영합니다</p>
      </div>

      <div v-if="error" class="alert-error">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
          <path d="M12 8V12M12 16H12.01" stroke="currentColor" stroke-width="2"/>
        </svg>
        <span>{{ error }}</span>
      </div>

      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label for="username">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="12" cy="8" r="4" stroke="currentColor" stroke-width="2"/>
              <path d="M4 20C4 17 7 14 12 14C17 14 20 17 20 20" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
            아이디
          </label>
          <input type="text" id="username" v-model="form.username" required placeholder="아이디를 입력하세요">
        </div>

        <div class="form-group">
          <label for="password">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect x="5" y="11" width="14" height="10" rx="2" stroke="currentColor" stroke-width="2"/>
              <path d="M7 11V7C7 4.23858 9.23858 2 12 2C14.7614 2 17 4.23858 17 7V11" stroke="currentColor" stroke-width="2"/>
              <circle cx="12" cy="16" r="1.5" fill="currentColor"/>
            </svg>
            비밀번호
          </label>
          <input type="password" id="password" v-model="form.password" required placeholder="비밀번호를 입력하세요">
        </div>

        <button type="submit" class="btn-submit">
          <span>로그인</span>
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
            <path d="M5 12H19M19 12L12 5M19 12L12 19" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
      </form>

      <div class="login-footer">
        <p>아직 회원이 아니신가요?</p>
        <router-link to="/register" class="btn-register">회원가입</router-link>
      </div>

      <router-link to="/" class="back-home">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M19 12H5M5 12L12 19M5 12L12 5" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
        </svg>
        홈으로 돌아가기
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { userApi } from '../api/api'

const router = useRouter()
const error = ref('')

const form = ref({
  username: '',
  password: ''
})

onMounted(() => {
  if (localStorage.getItem('user')) {
    window.location.href = '/'
  }
})

const handleLogin = async () => {
  try {
    error.value = ''
    const response = await userApi.login(form.value)
    localStorage.setItem('user', form.value.username)
    // 서버에서 nickname을 받아서 저장 (응답에 nickname이 있을 경우)
    if (response.data && response.data.nickName) {
      localStorage.setItem('nickname', response.data.nickName)
    }
    window.location.href = '/'
  } catch (err) {
    error.value = '로그인 실패'
  }
}
</script>

<style scoped>
.login-page {
  min-height: calc(100vh - 130px);
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(180deg, #1a0a2e 0%, #0d1117 50%, #0a1628 100%);
  padding: 1.5rem;
  position: relative;
  overflow: hidden;
}

.login-page::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image:
    radial-gradient(ellipse at 30% 20%, rgba(153, 69, 255, 0.3) 0%, transparent 50%),
    radial-gradient(ellipse at 70% 80%, rgba(255, 45, 123, 0.25) 0%, transparent 50%);
  pointer-events: none;
}

.login-container {
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 20px;
  padding: 2rem 1.5rem;
  width: 100%;
  max-width: 360px;
  position: relative;
  z-index: 1;
  box-shadow: 0 25px 80px rgba(0, 0, 0, 0.4);
}

.login-header {
  text-align: center;
  margin-bottom: 1.5rem;
}

.logo-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 0.75rem;
}

.logo-icon svg {
  color: #ff2d7b;
  filter: drop-shadow(0 0 20px rgba(255, 45, 123, 0.6));
}

.login-header h1 {
  font-size: 1.5rem;
  font-weight: 800;
  background: linear-gradient(135deg, #ff2d7b 0%, #9945ff 50%, #00d9ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 0.375rem;
}

.login-header p {
  color: rgba(255, 255, 255, 0.5);
  font-size: 0.8rem;
}

.alert-error {
  display: flex;
  align-items: center;
  gap: 0.625rem;
  padding: 0.75rem 1rem;
  background: rgba(255, 51, 102, 0.12);
  border: 1px solid rgba(255, 51, 102, 0.4);
  border-radius: 10px;
  color: #ff3366;
  margin-bottom: 1.25rem;
  font-size: 0.85rem;
}

.alert-error svg {
  flex-shrink: 0;
  filter: drop-shadow(0 0 6px rgba(255, 51, 102, 0.5));
}

.login-form {
  margin-bottom: 1.25rem;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.85rem;
}

.form-group label svg {
  color: #00d9ff;
}

.form-group input {
  width: 100%;
  padding: 0.875rem 1rem;
  background: rgba(10, 10, 15, 0.6);
  border: 2px solid rgba(255, 255, 255, 0.08);
  border-radius: 10px;
  font-size: 0.95rem;
  color: #fff;
  transition: all 0.3s ease;
}

.form-group input::placeholder {
  color: rgba(255, 255, 255, 0.25);
}

.form-group input:focus {
  outline: none;
  border-color: #00d9ff;
  box-shadow: 0 0 20px rgba(0, 217, 255, 0.25);
  background: rgba(10, 10, 15, 0.8);
}

.btn-submit {
  width: 100%;
  padding: 0.875rem;
  background: linear-gradient(135deg, #ff2d7b 0%, #9945ff 100%);
  color: #fff;
  border: none;
  border-radius: 10px;
  font-size: 1rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 0 25px rgba(255, 45, 123, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.btn-submit:hover {
  transform: translateY(-2px);
  box-shadow: 0 0 35px rgba(255, 45, 123, 0.6), 0 10px 30px rgba(0, 0, 0, 0.3);
}

.btn-submit svg {
  transition: transform 0.3s ease;
}

.btn-submit:hover svg {
  transform: translateX(4px);
}

.login-footer {
  text-align: center;
  padding-top: 1.25rem;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.login-footer p {
  color: rgba(255, 255, 255, 0.5);
  margin-bottom: 0.75rem;
  font-size: 0.8rem;
}

.btn-register {
  display: inline-block;
  padding: 0.625rem 1.75rem;
  border: 2px solid #00d9ff;
  color: #00d9ff;
  background: transparent;
  border-radius: 10px;
  font-weight: 600;
  font-size: 0.9rem;
  transition: all 0.3s ease;
  text-decoration: none;
}

.btn-register:hover {
  background: #00d9ff;
  color: #0a0a0f;
  box-shadow: 0 0 25px rgba(0, 217, 255, 0.5);
}

.back-home {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.375rem;
  margin-top: 1.25rem;
  color: rgba(255, 255, 255, 0.4);
  font-size: 0.8rem;
  transition: all 0.3s ease;
  text-decoration: none;
}

.back-home:hover {
  color: #ff2d7b;
}

.back-home svg {
  transition: transform 0.3s ease;
}

.back-home:hover svg {
  transform: translateX(-4px);
}
</style>
