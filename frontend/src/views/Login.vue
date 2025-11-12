<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-header">
        <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
          <path d="M12 2 L12 22 M2 12 L22 12" stroke="currentColor" stroke-width="1.5"/>
          <path d="M12 2 L18 12 L12 22 L6 12 Z" fill="currentColor" opacity="0.3"/>
        </svg>
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
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M20 21V19C20 17.9391 19.5786 16.9217 18.8284 16.1716C18.0783 15.4214 17.0609 15 16 15H8C6.93913 15 5.92172 15.4214 5.17157 16.1716C4.42143 16.9217 4 17.9391 4 19V21M16 7C16 9.20914 14.2091 11 12 11C9.79086 11 8 9.20914 8 7C8 4.79086 9.79086 3 12 3C14.2091 3 16 4.79086 16 7Z" stroke="currentColor" stroke-width="2"/>
            </svg>
            아이디
          </label>
          <input type="text" id="username" v-model="form.username" required placeholder="아이디를 입력하세요">
        </div>

        <div class="form-group">
          <label for="password">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect x="5" y="11" width="14" height="10" rx="2" stroke="currentColor" stroke-width="2"/>
              <path d="M7 11V7C7 5.67392 7.52678 4.40215 8.46447 3.46447C9.40215 2.52678 10.6739 2 12 2C13.3261 2 14.5979 2.52678 15.5355 3.46447C16.4732 4.40215 17 5.67392 17 7V11" stroke="currentColor" stroke-width="2"/>
            </svg>
            비밀번호
          </label>
          <input type="password" id="password" v-model="form.password" required placeholder="비밀번호를 입력하세요">
        </div>

        <button type="submit" class="btn-submit">로그인</button>
      </form>

      <div class="login-footer">
        <p>아직 회원이 아니신가요?</p>
        <router-link to="/register" class="btn-register">회원가입</router-link>
      </div>

      <router-link to="/" class="back-home">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M19 12H5M5 12L12 19M5 12L12 5" stroke="currentColor" stroke-width="2"/>
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
