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
        <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
          <path d="M12 2 L12 22 M2 12 L22 12" stroke="currentColor" stroke-width="1.5"/>
          <path d="M12 2 L18 12 L12 22 L6 12 Z" fill="currentColor" opacity="0.3"/>
        </svg>
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
          <label for="username">아이디</label>
          <input type="text" id="username" v-model="form.username" required placeholder="아이디">
        </div>

        <!-- 비밀번호 -->
        <div class="form-group">
          <label for="password">비밀번호</label>
          <input type="password" id="password" v-model="form.password" required placeholder="비밀번호">
        </div>

        <!-- 닉네임 -->
        <div class="form-group">
          <label for="nickName">닉네임</label>
          <input type="text" id="nickName" v-model="form.nickName" required placeholder="닉네임">
        </div>

        <!-- 이메일 -->
        <div class="form-group">
          <label for="email">이메일</label>
          <input type="email" id="email" v-model="form.email" required placeholder="이메일">
        </div>

        <button type="submit" class="btn-submit">회원가입</button>
      </form>

      <div class="login-footer">
        <p>이미 계정이 있으신가요?</p>
        <router-link to="/login" class="btn-register">로그인하기</router-link>
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
