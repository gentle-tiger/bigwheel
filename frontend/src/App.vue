<script setup>
import { RouterView } from 'vue-router'
import { ref, onMounted, onUnmounted } from 'vue'

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

// 커서 따라다니는 글로우 효과 (잔상)
const isOutsideApp = ref(false)
const mousePos = ref({ x: 0, y: 0 })
const prevMousePos = ref({ x: 0, y: 0 })  // 이전 마우스 위치 (보간용)

// 떠다니는 동그라미들
const orbs = ref([])
const orbElements = ref([])
let animationId = null

// 동그라미 색상들
const orbColors = [
  { bg: 'rgba(255, 45, 123, 0.6)', shadow: 'rgba(255, 45, 123, 0.8)' },   // 핑크
  { bg: 'rgba(0, 217, 255, 0.6)', shadow: 'rgba(0, 217, 255, 0.8)' },     // 시안
  { bg: 'rgba(153, 69, 255, 0.6)', shadow: 'rgba(153, 69, 255, 0.8)' },   // 퍼플
  { bg: 'rgba(0, 255, 136, 0.5)', shadow: 'rgba(0, 255, 136, 0.7)' },     // 그린
  { bg: 'rgba(255, 204, 0, 0.5)', shadow: 'rgba(255, 204, 0, 0.7)' },     // 옐로우
]

// 동그라미 초기화
const initOrbs = () => {
  const orbCount = 15
  orbs.value = []

  for (let i = 0; i < orbCount; i++) {
    const color = orbColors[i % orbColors.length]
    const size = 10 + Math.random() * 25
    orbs.value.push({
      id: i,
      x: Math.random() * window.innerWidth,
      y: Math.random() * window.innerHeight,
      baseX: Math.random() * window.innerWidth,
      baseY: Math.random() * window.innerHeight,
      size: size,
      color: color,
      speedX: (Math.random() - 0.5) * 0.5,
      speedY: (Math.random() - 0.5) * 0.5,
      phase: Math.random() * Math.PI * 2
    })
  }

  // DOM 요소 생성
  createOrbElements()
  // 애니메이션 시작
  animateOrbs()
}

// DOM 요소 생성
const createOrbElements = () => {
  // 기존 요소 제거
  orbElements.value.forEach(el => el.remove())
  orbElements.value = []

  orbs.value.forEach(orb => {
    const el = document.createElement('div')
    el.className = 'floating-orb'
    el.style.width = orb.size + 'px'
    el.style.height = orb.size + 'px'
    el.style.background = `radial-gradient(circle, ${orb.color.bg} 0%, transparent 70%)`
    el.style.boxShadow = `0 0 ${orb.size/2}px ${orb.color.shadow}`
    document.documentElement.appendChild(el)  // html에 붙임 (body 뒤에)
    orbElements.value.push(el)
  })
}

// 애니메이션 루프
const animateOrbs = () => {
  const time = Date.now() * 0.001
  const appWidth = 400
  const windowCenter = window.innerWidth / 2
  const appLeft = windowCenter - appWidth / 2
  const appRight = windowCenter + appWidth / 2

  orbs.value.forEach((orb, i) => {
    // 기본 떠다니는 움직임
    orb.baseX += orb.speedX
    orb.baseY += orb.speedY

    // 화면 경계 반사
    if (orb.baseX < 0 || orb.baseX > window.innerWidth) orb.speedX *= -1
    if (orb.baseY < 0 || orb.baseY > window.innerHeight) orb.speedY *= -1

    // 물결치는 움직임
    const waveX = Math.sin(time + orb.phase) * 20
    const waveY = Math.cos(time * 0.8 + orb.phase) * 20

    // 목표 위치 (기본 위치 + 물결)
    let targetX = orb.baseX + waveX
    let targetY = orb.baseY + waveY

    // 마우스가 앱 바깥에 있으면 마우스 쪽으로 끌려감
    if (isOutsideApp.value) {
      const dx = mousePos.value.x - targetX
      const dy = mousePos.value.y - targetY
      const dist = Math.sqrt(dx * dx + dy * dy)

      if (dist < 300) {
        const force = (300 - dist) / 300 * 0.3
        targetX += dx * force
        targetY += dy * force
      }
    }

    // 부드러운 이동
    orb.x += (targetX - orb.x) * 0.05
    orb.y += (targetY - orb.y) * 0.05

    // DOM 업데이트
    if (orbElements.value[i]) {
      // 앱 영역 안에 있으면 숨김
      const isInApp = orb.x > appLeft && orb.x < appRight
      orbElements.value[i].style.opacity = isInApp ? '0' : '1'
      orbElements.value[i].style.left = orb.x + 'px'
      orbElements.value[i].style.top = orb.y + 'px'
    }
  })

  animationId = requestAnimationFrame(animateOrbs)
}

const handleMouseMove = (e) => {
  const currentX = e.clientX
  const currentY = e.clientY

  // 앱 영역(400px) 바깥인지 체크
  const appWidth = 400
  const windowCenter = window.innerWidth / 2
  const appLeft = windowCenter - appWidth / 2
  const appRight = windowCenter + appWidth / 2

  // 앱 바깥 영역에서만 잔상 생성
  if (currentX < appLeft || currentX > appRight) {
    isOutsideApp.value = true

    // 이전 위치와 현재 위치 사이를 보간해서 끊김 방지
    const prevX = prevMousePos.value.x || currentX
    const prevY = prevMousePos.value.y || currentY
    const dist = Math.sqrt((currentX - prevX) ** 2 + (currentY - prevY) ** 2)

    // 거리가 멀면 중간에 점들 추가
    const steps = Math.max(1, Math.floor(dist / 5))  // 5px마다 점 하나
    for (let i = 0; i <= steps; i++) {
      const t = i / steps
      const x = prevX + (currentX - prevX) * t
      const y = prevY + (currentY - prevY) * t
      createTrailDot(x, y)
    }
  } else {
    isOutsideApp.value = false
  }

  // 현재 위치 저장
  prevMousePos.value = { x: currentX, y: currentY }
  mousePos.value = { x: currentX, y: currentY }
}

// 잔상 점 생성
const createTrailDot = (x, y) => {
  const dot = document.createElement('div')
  dot.className = 'trail-dot'
  dot.style.left = x + 'px'
  dot.style.top = y + 'px'
  document.documentElement.appendChild(dot)  // html에 붙임 (body/앱 뒤에)

  setTimeout(() => {
    dot.classList.add('fade-out')
  }, 10)

  setTimeout(() => {
    dot.remove()
  }, 700)
}

const handleMouseLeave = () => {
  isOutsideApp.value = false
}

// 창 크기 변경 시 재초기화
const handleResize = () => {
  orbs.value.forEach(orb => {
    orb.baseX = Math.min(orb.baseX, window.innerWidth)
    orb.baseY = Math.min(orb.baseY, window.innerHeight)
  })
}

onMounted(() => {
  checkLogin()
  window.addEventListener('storage', checkLogin)
  window.addEventListener('mousemove', handleMouseMove)
  document.addEventListener('mouseleave', handleMouseLeave)
  window.addEventListener('resize', handleResize)

  // 떠다니는 동그라미 초기화
  initOrbs()
})

onUnmounted(() => {
  window.removeEventListener('mousemove', handleMouseMove)
  document.removeEventListener('mouseleave', handleMouseLeave)
  window.removeEventListener('resize', handleResize)

  // 애니메이션 정리
  if (animationId) cancelAnimationFrame(animationId)
  orbElements.value.forEach(el => el.remove())
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

#app {
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden;
}

/* 붓펜 잔상 효과 */
.trail-dot {
  position: fixed;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  pointer-events: none;
  z-index: 1;  /* body(z-index:10)보다 낮게 */
  transform: translate(-50%, -50%);
  background: radial-gradient(
    circle,
    rgba(255, 45, 123, 0.9) 0%,
    rgba(153, 69, 255, 0.6) 50%,
    transparent 100%
  );
  box-shadow:
    0 0 6px rgba(255, 45, 123, 0.8),
    0 0 12px rgba(153, 69, 255, 0.5);
  opacity: 1;
  transition: opacity 0.7s ease-out, transform 0.7s ease-out;
}

.trail-dot.fade-out {
  opacity: 0;
  transform: translate(-50%, -50%) scale(0.3);
}

/* 떠다니는 동그라미 */
.floating-orb {
  position: fixed;
  border-radius: 50%;
  pointer-events: none;
  z-index: 1;  /* body(z-index:10)보다 낮게 */
  transform: translate(-50%, -50%);
  transition: opacity 0.3s ease;
  filter: blur(2px);
}
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
