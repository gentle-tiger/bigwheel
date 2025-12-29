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
          <svg width="26" height="26" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
            <circle cx="12" cy="12" r="6" stroke="currentColor" stroke-width="1.5" opacity="0.7"/>
            <circle cx="12" cy="12" r="2" fill="currentColor"/>
            <path d="M12 2 L12 6 M12 18 L12 22 M2 12 L6 12 M18 12 L22 12" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
          </svg>
          <span class="logo-text">BigWheel</span>
        </router-link>
      </div>
    </header>

    <!-- Main Content -->
    <RouterView />

    <!-- Bottom Navbar - Floating Style -->
    <nav class="bottom-navbar">
      <router-link to="/" class="nav-item">
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M3 9L12 2L21 9V20C21 20.5304 20.7893 21.0391 20.4142 21.4142C20.0391 21.7893 19.5304 22 19 22H5C4.46957 22 3.96086 21.7893 3.58579 21.4142C3.21071 21.0391 3 20.5304 3 20V9Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M9 22V12H15V22" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <span>홈</span>
      </router-link>
      <router-link to="/play" class="nav-item">
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
          <circle cx="12" cy="12" r="3" fill="currentColor" opacity="0.5"/>
          <path d="M12 2 L12 6 M12 18 L12 22 M2 12 L6 12 M18 12 L22 12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
        </svg>
        <span>플레이</span>
      </router-link>
      <router-link to="/statistics" class="nav-item">
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M18 20V10M12 20V4M6 20V14" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <span>통계</span>
      </router-link>
      <router-link to="/chips" class="nav-item">
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <circle cx="12" cy="12" r="9" stroke="currentColor" stroke-width="2"/>
          <circle cx="12" cy="12" r="5" stroke="currentColor" stroke-width="1.5" opacity="0.6"/>
          <circle cx="12" cy="12" r="2" fill="currentColor"/>
        </svg>
        <span>칩</span>
      </router-link>
      <router-link to="/profile" class="nav-item">
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <circle cx="12" cy="8" r="4" stroke="currentColor" stroke-width="2"/>
          <path d="M4 20C4 17 7 14 12 14C17 14 20 17 20 20" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
        </svg>
        <span>내 정보</span>
      </router-link>
    </nav>
  </div>
</template>

<style>
@import './assets/style.css';
</style>

<style scoped>
.header {
  background: rgba(13, 17, 23, 0.95);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.header-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 0.75rem 1rem;
}

.logo {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  text-decoration: none;
}

.logo svg {
  color: var(--neon-pink, #ff2d7b);
  filter: drop-shadow(0 0 10px rgba(255, 45, 123, 0.6));
  animation: logo-pulse 3s ease-in-out infinite;
}

@keyframes logo-pulse {
  0%, 100% {
    filter: drop-shadow(0 0 8px rgba(255, 45, 123, 0.5));
  }
  50% {
    filter: drop-shadow(0 0 15px rgba(255, 45, 123, 0.8)) drop-shadow(0 0 25px rgba(255, 45, 123, 0.4));
  }
}

.logo-text {
  font-weight: 800;
  font-size: 1.125rem;
  background: linear-gradient(135deg, #ff2d7b 0%, #9945ff 50%, #00d9ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.02em;
}

/* Button styles */
button.btn-login {
  border: none;
  background: linear-gradient(135deg, #ff2d7b 0%, #9945ff 100%);
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  transition: all 0.3s ease;
  box-shadow: 0 0 15px rgba(255, 45, 123, 0.4);
}

button.btn-login:hover {
  transform: translateY(-1px);
  box-shadow: 0 0 25px rgba(255, 45, 123, 0.6);
}
</style>
