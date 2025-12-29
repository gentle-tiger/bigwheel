<template>
  <div class="login-page">
    <Modal
      :show="modal.show"
      :type="modal.type"
      :title="modal.title"
      :message="modal.message"
      @close="modal.show = false"
      @confirm="handleModalConfirm"
    />
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
        <h1>회원가입</h1>
        <p>BigWheel 계정을 만들어보세요</p>
      </div>

      <div v-if="error" class="alert-error">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
          <path d="M12 8V12M12 16H12.01" stroke="currentColor" stroke-width="2"/>
        </svg>
        <span>{{ error }}</span>
      </div>

      <form @submit.prevent="handleRegister" class="login-form">
        <!-- 아이디 -->
        <div class="form-group">
          <label for="username">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
              <circle cx="12" cy="8" r="4" stroke="currentColor" stroke-width="2"/>
              <path d="M4 20C4 17 7 14 12 14C17 14 20 17 20 20" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
            아이디
          </label>
          <input type="text" id="username" v-model="form.username" required placeholder="아이디">
        </div>

        <!-- 비밀번호 -->
        <div class="form-group">
          <label for="password">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
              <rect x="5" y="11" width="14" height="10" rx="2" stroke="currentColor" stroke-width="2"/>
              <path d="M7 11V7C7 4.23858 9.23858 2 12 2C14.7614 2 17 4.23858 17 7V11" stroke="currentColor" stroke-width="2"/>
              <circle cx="12" cy="16" r="1.5" fill="currentColor"/>
            </svg>
            비밀번호
          </label>
          <input type="password" id="password" v-model="form.password" required placeholder="비밀번호">
        </div>

        <!-- 닉네임 -->
        <div class="form-group">
          <label for="nickName">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
              <path d="M20 21V19C20 16.7909 18.2091 15 16 15H8C5.79086 15 4 16.7909 4 19V21" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              <circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="2"/>
            </svg>
            닉네임
          </label>
          <input type="text" id="nickName" v-model="form.nickName" required placeholder="닉네임">
        </div>

        <!-- 이메일 -->
        <div class="form-group">
          <label for="email">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
              <rect x="2" y="4" width="20" height="16" rx="2" stroke="currentColor" stroke-width="2"/>
              <path d="M2 7L12 13L22 7" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
            이메일
          </label>
          <input type="email" id="email" v-model="form.email" required placeholder="이메일">
        </div>

        <button type="submit" class="btn-submit">
          <span>회원가입</span>
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
            <path d="M5 12H19M19 12L12 5M19 12L12 19" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
      </form>

      <div class="login-footer">
        <p>이미 계정이 있으신가요?</p>
        <router-link to="/login" class="btn-register">로그인하기</router-link>
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
import Modal from '../components/Modal.vue'

const router = useRouter()
const error = ref('')

const form = ref({
  username: '',
  password: '',
  nickName: '',
  email: ''
})

const modal = ref({
  show: false,
  type: 'success',
  title: '',
  message: ''
})

onMounted(() => {
  if (localStorage.getItem('user')) {
    window.location.href = '/'
  }
})

const handleRegister = async () => {
  try {
    error.value = ''
    await userApi.register(form.value)
    localStorage.setItem('user', form.value.username)
    localStorage.setItem('nickname', form.value.nickName)

    modal.value = {
      show: true,
      type: 'success',
      title: '회원가입 완료!',
      message: '환영합니다! 로그인하여 게임을 즐겨보세요.'
    }
  } catch (err) {
    error.value = '회원가입 실패'
  }
}

const handleModalConfirm = () => {
  window.location.href = '/'
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
  padding: 1.75rem 1.5rem;
  width: 100%;
  max-width: 360px;
  position: relative;
  z-index: 1;
  box-shadow: 0 25px 80px rgba(0, 0, 0, 0.4);
}

.login-header {
  text-align: center;
  margin-bottom: 1.25rem;
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
  margin-bottom: 1rem;
  font-size: 0.85rem;
}

.alert-error svg {
  flex-shrink: 0;
  filter: drop-shadow(0 0 6px rgba(255, 51, 102, 0.5));
}

.login-form {
  margin-bottom: 1rem;
}

.form-group {
  margin-bottom: 0.875rem;
}

.form-group label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 600;
  margin-bottom: 0.375rem;
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.8rem;
}

.form-group label svg {
  color: #00d9ff;
}

.form-group input {
  width: 100%;
  padding: 0.75rem 1rem;
  background: rgba(10, 10, 15, 0.6);
  border: 2px solid rgba(255, 255, 255, 0.08);
  border-radius: 10px;
  font-size: 0.9rem;
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
  margin-top: 0.5rem;
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
  padding-top: 1rem;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.login-footer p {
  color: rgba(255, 255, 255, 0.5);
  margin-bottom: 0.625rem;
  font-size: 0.8rem;
}

.btn-register {
  display: inline-block;
  padding: 0.5rem 1.5rem;
  border: 2px solid #00d9ff;
  color: #00d9ff;
  background: transparent;
  border-radius: 10px;
  font-weight: 600;
  font-size: 0.85rem;
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
  margin-top: 1rem;
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
