<template>
  <div class="home">
    <!-- Hero Section -->
    <section class="hero-section">
      <!-- Background Effects -->
      <div class="bg-effects">
        <div class="glow-orb orb-1"></div>
        <div class="glow-orb orb-2"></div>
        <div class="glow-orb orb-3"></div>
      </div>

      <div class="hero-content">
        <h1 class="hero-title">
          <span class="title-glow">행운의 빅휠</span>
        </h1>
        <p class="hero-subtitle">당신의 행운을 시험해보세요</p>

        <div class="hero-wheel">
          <svg width="180" height="180" viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
            <defs>
              <linearGradient id="wheelGradient" x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%" style="stop-color:#ff2d7b;stop-opacity:1" />
                <stop offset="50%" style="stop-color:#9945ff;stop-opacity:1" />
                <stop offset="100%" style="stop-color:#00d9ff;stop-opacity:1" />
              </linearGradient>
              <filter id="glow">
                <feGaussianBlur stdDeviation="3" result="coloredBlur"/>
                <feMerge>
                  <feMergeNode in="coloredBlur"/>
                  <feMergeNode in="SourceGraphic"/>
                </feMerge>
              </filter>
            </defs>
            <circle cx="100" cy="100" r="95" fill="none" stroke="url(#wheelGradient)" stroke-width="4" filter="url(#glow)"/>
            <circle cx="100" cy="100" r="75" fill="none" stroke="url(#wheelGradient)" stroke-width="2" opacity="0.6"/>
            <circle cx="100" cy="100" r="55" fill="none" stroke="url(#wheelGradient)" stroke-width="1.5" opacity="0.4"/>
            <line x1="100" y1="5" x2="100" y2="195" stroke="#00d9ff" stroke-width="1.5" opacity="0.8"/>
            <line x1="5" y1="100" x2="195" y2="100" stroke="#ff2d7b" stroke-width="1.5" opacity="0.8"/>
            <line x1="30" y1="30" x2="170" y2="170" stroke="#9945ff" stroke-width="1" opacity="0.6"/>
            <line x1="170" y1="30" x2="30" y2="170" stroke="#9945ff" stroke-width="1" opacity="0.6"/>
            <circle cx="100" cy="100" r="12" fill="url(#wheelGradient)" filter="url(#glow)"/>
            <circle cx="100" cy="100" r="5" fill="#fff"/>
          </svg>
        </div>

        <div v-if="!isLoggedIn" class="button-container">
          <router-link to="/login" class="btn-play">
            <span>로그인</span>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
              <path d="M5 12H19M19 12L12 5M19 12L12 19" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </router-link>
          <router-link to="/register" class="btn-secondary">회원가입</router-link>
        </div>
        <div v-else class="button-container">
          <button @click="showModeModal = true" class="btn-play">
            <span>게임 선택하기</span>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
              <path d="M5 12H19M19 12L12 5M19 12L12 19" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
          <router-link to="/chips" class="btn-secondary">칩 입력하기</router-link>
        </div>
      </div>
    </section>

    <!-- 모드 선택 모달 -->
    <ModeSelectionModal
      :show="showModeModal"
      @close="showModeModal = false"
      @modeSelected="handleModeSelected"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import ModeSelectionModal from '../components/ModeSelectionModal.vue'

const router = useRouter()
const isLoggedIn = ref(false)
const showModeModal = ref(false)

onMounted(() => {
  isLoggedIn.value = localStorage.getItem('user') !== null
})

// 모드 선택 완료 시 플레이 페이지로 이동
const handleModeSelected = (mode) => {
  showModeModal.value = false
  router.push('/play')
}
</script>

<style scoped>
.home {
  min-height: calc(100vh - 130px);
  overflow: hidden;
}

.hero-section {
  min-height: calc(100vh - 130px);
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(180deg, #1a0a2e 0%, #0d1117 40%, #0a1628 100%);
  padding: 2rem 1.5rem;
  position: relative;
  overflow: hidden;
}

/* Background Glow Effects */
.bg-effects {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  overflow: hidden;
}

.glow-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
  opacity: 0.5;
}

.orb-1 {
  width: 300px;
  height: 300px;
  background: rgba(255, 45, 123, 0.3);
  top: -100px;
  left: -100px;
  animation: float 8s ease-in-out infinite;
}

.orb-2 {
  width: 250px;
  height: 250px;
  background: rgba(0, 217, 255, 0.25);
  bottom: -50px;
  right: -50px;
  animation: float 10s ease-in-out infinite reverse;
}

.orb-3 {
  width: 200px;
  height: 200px;
  background: rgba(153, 69, 255, 0.3);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation: pulse-glow 5s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0);
  }
  50% {
    transform: translate(30px, 30px);
  }
}

@keyframes pulse-glow {
  0%, 100% {
    opacity: 0.3;
    transform: translate(-50%, -50%) scale(1);
  }
  50% {
    opacity: 0.5;
    transform: translate(-50%, -50%) scale(1.2);
  }
}

.hero-content {
  text-align: center;
  color: #fff;
  width: 100%;
  max-width: 400px;
  position: relative;
  z-index: 1;
}

.hero-title {
  font-size: 2.25rem;
  font-weight: 800;
  margin-bottom: 0.5rem;
  letter-spacing: -0.02em;
}

.title-glow {
  background: linear-gradient(135deg, #ff2d7b 0%, #fff 50%, #00d9ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: none;
  filter: drop-shadow(0 0 30px rgba(255, 45, 123, 0.5));
}

.hero-subtitle {
  font-size: 1rem;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 1.5rem;
}

.hero-wheel {
  margin: 1.5rem auto;
  display: flex;
  justify-content: center;
  animation: spin 15s linear infinite;
  filter: drop-shadow(0 0 20px rgba(153, 69, 255, 0.4));
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.button-container {
  display: flex;
  flex-direction: column;
  gap: 0.875rem;
  margin-top: 1.5rem;
}

.btn-play {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 1rem 2rem;
  background: linear-gradient(135deg, #ff2d7b 0%, #9945ff 100%);
  color: #fff;
  border: none;
  border-radius: 12px;
  font-weight: 700;
  font-size: 1.0625rem;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow:
    0 0 20px rgba(255, 45, 123, 0.4),
    0 4px 15px rgba(0, 0, 0, 0.3);
  animation: neon-pulse 2s ease-in-out infinite;
  width: 100%;
  text-decoration: none;
}

@keyframes neon-pulse {
  0%, 100% {
    box-shadow:
      0 0 15px rgba(255, 45, 123, 0.4),
      0 4px 15px rgba(0, 0, 0, 0.3);
  }
  50% {
    box-shadow:
      0 0 30px rgba(255, 45, 123, 0.6),
      0 0 50px rgba(255, 45, 123, 0.3),
      0 4px 15px rgba(0, 0, 0, 0.3);
  }
}

.btn-play:hover {
  transform: translateY(-3px) scale(1.02);
  box-shadow:
    0 0 35px rgba(255, 45, 123, 0.6),
    0 0 60px rgba(255, 45, 123, 0.3),
    0 8px 25px rgba(0, 0, 0, 0.4);
}

.btn-play svg {
  transition: transform 0.3s ease;
}

.btn-play:hover svg {
  transform: translateX(4px);
}

.btn-secondary {
  display: inline-block;
  padding: 0.875rem 2rem;
  background: transparent;
  color: #00d9ff;
  border: 2px solid #00d9ff;
  border-radius: 12px;
  font-weight: 600;
  font-size: 1rem;
  transition: all 0.3s ease;
  text-decoration: none;
  width: 100%;
  text-align: center;
}

.btn-secondary:hover {
  background: #00d9ff;
  color: #0a0a0f;
  box-shadow:
    0 0 20px rgba(0, 217, 255, 0.5),
    0 0 40px rgba(0, 217, 255, 0.2);
}
</style>
