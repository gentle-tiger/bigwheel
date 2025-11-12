<template>
  <div class="home">
    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">행운의 빅휠</h1>
        <p class="hero-subtitle">당신의 행운을 시험해보세요</p>
        <div class="hero-wheel">
          <svg width="200" height="200" viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
            <circle cx="100" cy="100" r="95" fill="url(#wheelGradient)" stroke="#333" stroke-width="3"/>
            <defs>
              <linearGradient id="wheelGradient" x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%" style="stop-color:#6366f1;stop-opacity:1" />
                <stop offset="100%" style="stop-color:#8b5cf6;stop-opacity:1" />
              </linearGradient>
            </defs>
            <line x1="100" y1="5" x2="100" y2="195" stroke="#fff" stroke-width="2"/>
            <line x1="5" y1="100" x2="195" y2="100" stroke="#fff" stroke-width="2"/>
            <line x1="30" y1="30" x2="170" y2="170" stroke="#fff" stroke-width="1.5"/>
            <line x1="170" y1="30" x2="30" y2="170" stroke="#fff" stroke-width="1.5"/>
            <circle cx="100" cy="100" r="15" fill="#fff"/>
          </svg>
        </div>

        <div v-if="!isLoggedIn" class="button-container">
          <router-link to="/login" class="btn-play">로그인</router-link>
          <router-link to="/register" class="btn-secondary">회원가입</router-link>
        </div>
        <div v-else class="button-container">
          <router-link to="/category" class="btn-play">게임 선택하기</router-link>
          <router-link to="/chip-input" class="btn-secondary">칩 입력하기</router-link>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const isLoggedIn = ref(false)

onMounted(() => {
  isLoggedIn.value = localStorage.getItem('user') !== null
})
</script>

<style scoped>
.home {
  height: calc(100vh - 130px);
  overflow: hidden;
}

.hero-section {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--primary) 0%, var(--secondary) 100%);
  padding: 2rem 1rem;
}

.hero-content {
  text-align: center;
  color: var(--white);
  width: 100%;
  max-width: 500px;
}

.hero-title {
  font-size: 2.5rem;
  font-weight: 800;
  margin-bottom: 0.5rem;
}

.hero-subtitle {
  font-size: 1.125rem;
  opacity: 0.95;
  margin-bottom: 2rem;
}

.hero-wheel {
  margin: 2rem auto;
  display: flex;
  justify-content: center;
  animation: spin 20s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.button-container {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-top: 2rem;
}

.btn-play {
  display: inline-block;
  padding: 1rem 2.5rem;
  background: var(--white);
  color: var(--primary);
  border-radius: 12px;
  font-weight: 700;
  font-size: 1.125rem;
  transition: transform 0.3s, box-shadow 0.3s;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.btn-play:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2);
}

.btn-secondary {
  display: inline-block;
  padding: 1rem 2.5rem;
  background: transparent;
  color: var(--white);
  border: 2px solid var(--white);
  border-radius: 12px;
  font-weight: 600;
  font-size: 1rem;
  transition: all 0.3s;
}

.btn-secondary:hover {
  background: var(--white);
  color: var(--primary);
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 2rem;
  }

  .hero-subtitle {
    font-size: 1rem;
  }
}
</style>
