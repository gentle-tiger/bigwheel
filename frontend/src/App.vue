<script setup>
import { RouterView } from 'vue-router'
import { ref, onMounted } from 'vue'

const isLoggedIn = ref(false)

const checkLogin = () => {
  isLoggedIn.value = localStorage.getItem('user') !== null
}

const logout = () => {
  localStorage.removeItem('user')
  localStorage.removeItem('nickname')
  isLoggedIn.value = false
  window.location.href = '/'
}

onMounted(() => {
  checkLogin()
  window.addEventListener('storage', checkLogin)
})
</script>

<template>
  <div id="app">
    <!-- Header -->
    <header class="header">
      <div class="header-container">
        <router-link to="/" class="logo">
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
            <path d="M12 2 L12 22 M2 12 L22 12" stroke="currentColor" stroke-width="1.5"/>
          </svg>
          BigWheel
        </router-link>
      </div>
    </header>

    <!-- Main Content -->
    <RouterView />

    <!-- Bottom Navbar -->
    <nav class="bottom-navbar">
      <router-link to="/" class="nav-item">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M3 9L12 2L21 9V20C21 20.5304 20.7893 21.0391 20.4142 21.4142C20.0391 21.7893 19.5304 22 19 22H5C4.46957 22 3.96086 21.7893 3.58579 21.4142C3.21071 21.0391 3 20.5304 3 20V9Z" stroke="currentColor" stroke-width="2"/>
        </svg>
        <span>홈</span>
      </router-link>
      <router-link to="/play" class="nav-item">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
          <path d="M12 2 L12 22 M2 12 L22 12" stroke="currentColor" stroke-width="1.5"/>
        </svg>
        <span>플레이</span>
      </router-link>
      <router-link to="/statistics" class="nav-item">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M9 3V15M15 9V21M3 11H21M5 3H19C20.1046 3 21 3.89543 21 5V19C21 20.1046 20.1046 21 19 21H5C3.89543 21 3 20.1046 3 19V5C3 3.89543 3.89543 3 5 3Z" stroke="currentColor" stroke-width="2"/>
        </svg>
        <span>통계</span>
      </router-link>
      <router-link to="/chips" class="nav-item">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <circle cx="12" cy="12" r="9" stroke="currentColor" stroke-width="2"/>
          <circle cx="12" cy="12" r="4" stroke="currentColor" stroke-width="2"/>
          <path d="M12 3V6M12 18V21M21 12H18M6 12H3" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
        </svg>
        <span>칩</span>
      </router-link>
      <router-link to="/profile" class="nav-item">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M20 21V19C20 17.9391 19.5786 16.9217 18.8284 16.1716C18.0783 15.4214 17.0609 15 16 15H8C6.93913 15 5.92172 15.4214 5.17157 16.1716C4.42143 16.9217 4 17.9391 4 19V21M16 7C16 9.20914 14.2091 11 12 11C9.79086 11 8 9.20914 8 7C8 4.79086 9.79086 3 12 3C14.2091 3 16 4.79086 16 7Z" stroke="currentColor" stroke-width="2"/>
        </svg>
        <span>내 정보</span>
      </router-link>
    </nav>
  </div>
</template>

<style>
@import './assets/style.css';

button.btn-login {
  border: none;
  background: var(--primary);
  color: var(--white);
  padding: 0.5rem 1rem;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  transition: background 0.3s;
}

button.btn-login:hover {
  background: var(--primary-dark);
}
</style>
