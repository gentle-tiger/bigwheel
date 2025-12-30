<template>
  <div v-if="show" class="spin-modal-overlay">
    <div class="spin-modal-content">
      <div class="modal-header">
        <h2 class="modal-title">🎰 Big Wheel</h2>
        <p class="modal-subtitle">행운의 돌림판이 돌아갑니다!</p>
      </div>

      <div class="wheel-wrapper" :class="{ spinning: isSpinning }">
        <!-- 외곽 네온 링 -->
        <div class="neon-ring"></div>
        <div class="neon-ring ring-2"></div>

        <svg
          class="wheel-svg"
          width="320"
          height="320"
          viewBox="0 0 300 300"
          :style="{ transform: `rotate(${rotation}deg)` }"
        >
          <defs>
            <!-- 각 구역별 그라데이션 -->
            <linearGradient id="silverGrad" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" style="stop-color:#e8e8e8" />
              <stop offset="100%" style="stop-color:#a0a0a0" />
            </linearGradient>
            <linearGradient id="goldGrad" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" style="stop-color:#ffd700" />
              <stop offset="100%" style="stop-color:#b8860b" />
            </linearGradient>
            <linearGradient id="emeraldGrad" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" style="stop-color:#00ff88" />
              <stop offset="100%" style="stop-color:#00b359" />
            </linearGradient>
            <linearGradient id="diamondGrad" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" style="stop-color:#00d9ff" />
              <stop offset="100%" style="stop-color:#0099cc" />
            </linearGradient>
            <linearGradient id="crystalGrad" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" style="stop-color:#9945ff" />
              <stop offset="100%" style="stop-color:#6b21a8" />
            </linearGradient>
            <linearGradient id="jokerGrad" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" style="stop-color:#ff3366" />
              <stop offset="100%" style="stop-color:#cc1a4a" />
            </linearGradient>
            <linearGradient id="megaGrad" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" style="stop-color:#ffcc00" />
              <stop offset="100%" style="stop-color:#cc9900" />
            </linearGradient>
            <linearGradient id="centerGrad" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" style="stop-color:#ff2d7b" />
              <stop offset="50%" style="stop-color:#9945ff" />
              <stop offset="100%" style="stop-color:#00d9ff" />
            </linearGradient>
            <filter id="glow">
              <feGaussianBlur stdDeviation="2" result="coloredBlur"/>
              <feMerge>
                <feMergeNode in="coloredBlur"/>
                <feMergeNode in="SourceGraphic"/>
              </feMerge>
            </filter>
          </defs>

          <!-- 배경 원 (어두운 색) -->
          <circle cx="150" cy="150" r="148" fill="#0a0a0f" stroke="url(#centerGrad)" stroke-width="3"/>

          <!-- 구역별 색상 섹터 -->
          <g v-for="(sector, index) in sectors" :key="index">
            <path
              :d="createArcPath(sector.startAngle, sector.endAngle)"
              :fill="`url(#${sector.id}Grad)`"
              stroke="rgba(255,255,255,0.3)"
              stroke-width="1"
              filter="url(#glow)"
            />
            <!-- 구역 아이콘 -->
            <text
              :x="getTextX(sector.middleAngle)"
              :y="getTextY(sector.middleAngle)"
              text-anchor="middle"
              dominant-baseline="middle"
              :font-size="sector.slots >= 10 ? 24 : sector.slots >= 4 ? 20 : 16"
              :transform="`rotate(${sector.middleAngle + 90}, ${getTextX(sector.middleAngle)}, ${getTextY(sector.middleAngle)})`"
            >
              {{ sector.icon }}
            </text>
            <!-- 구역 이름 (큰 섹터에만) -->
            <text
              v-if="sector.slots >= 7"
              :x="getTextX(sector.middleAngle, 60)"
              :y="getTextY(sector.middleAngle, 60)"
              text-anchor="middle"
              dominant-baseline="middle"
              font-size="10"
              font-weight="bold"
              fill="rgba(255,255,255,0.9)"
              :transform="`rotate(${sector.middleAngle + 90}, ${getTextX(sector.middleAngle, 60)}, ${getTextY(sector.middleAngle, 60)})`"
            >
              {{ sector.name }}
            </text>
          </g>

          <!-- 내부 장식 원 -->
          <circle cx="150" cy="150" r="40" fill="none" stroke="rgba(255,255,255,0.1)" stroke-width="1"/>

          <!-- 중앙 원 -->
          <circle cx="150" cy="150" r="32" fill="url(#centerGrad)" filter="url(#glow)"/>
          <circle cx="150" cy="150" r="22" fill="#0a0a0f"/>
          <circle cx="150" cy="150" r="8" fill="#fff"/>
        </svg>

        <!-- 포인터 (위쪽 고정) -->
        <div class="pointer">
          <div class="pointer-glow"></div>
        </div>
      </div>

      <!-- 스핀 버튼 -->
      <button
        v-if="!isSpinning && !hasSpun"
        class="spin-button"
        @click="startSpin"
      >
        <span class="btn-icon">🎯</span>
        <span>돌리기</span>
      </button>

      <!-- 스피닝 상태 표시 -->
      <div v-if="isSpinning" class="spinning-text">
        <div class="spinner-icon">✨</div>
        <p>행운을 빕니다...</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

// Props
const props = defineProps({
  show: {
    type: Boolean,
    default: false
  },
  zones: {
    type: Array,
    required: true
  }
})

// Emit
const emit = defineEmits(['spinComplete'])

// 상태
const isSpinning = ref(false)
const rotation = ref(0)
const hasSpun = ref(false)

// show가 true로 변경될 때 상태 초기화
watch(() => props.show, (newVal) => {
  if (newVal) {
    hasSpun.value = false
  }
})

// 아이콘 매핑
const icons = {
  'silver': '🥈',
  'gold': '🥇',
  'emerald': '💚',
  'diamond': '💎',
  'crystal': '🔮',
  'joker': '🃏',
  'mega': '⭐'
}

// 섹터 데이터 생성
const sectors = computed(() => {
  const totalSlots = props.zones.reduce((sum, zone) => sum + zone.slots, 0)
  let currentAngle = 0

  return props.zones.map(zone => {
    const slotAngle = (zone.slots / totalSlots) * 360
    const sector = {
      id: zone.id,
      name: zone.name,
      icon: icons[zone.id] || '🎯',
      slots: zone.slots,
      startAngle: currentAngle,
      endAngle: currentAngle + slotAngle,
      middleAngle: currentAngle + slotAngle / 2
    }
    currentAngle += slotAngle
    return sector
  })
})

// 호(arc) 경로 생성
const createArcPath = (startAngle, endAngle) => {
  const start = polarToCartesian(150, 150, 140, startAngle)
  const end = polarToCartesian(150, 150, 140, endAngle)
  const largeArcFlag = endAngle - startAngle <= 180 ? '0' : '1'

  return [
    `M 150 150`,
    `L ${start.x} ${start.y}`,
    `A 140 140 0 ${largeArcFlag} 1 ${end.x} ${end.y}`,
    `Z`
  ].join(' ')
}

// 극좌표 → 직교좌표 변환
const polarToCartesian = (cx, cy, radius, angle) => {
  const rad = (angle - 90) * Math.PI / 180
  return {
    x: cx + radius * Math.cos(rad),
    y: cy + radius * Math.sin(rad)
  }
}

// 텍스트 위치 계산
const getTextX = (angle, distance = 100) => {
  const rad = (angle - 90) * Math.PI / 180
  return 150 + distance * Math.cos(rad)
}

const getTextY = (angle, distance = 100) => {
  const rad = (angle - 90) * Math.PI / 180
  return 150 + distance * Math.sin(rad)
}

// 스핀 시작
const startSpin = () => {
  isSpinning.value = true
  hasSpun.value = true

  // 랜덤 회전 각도 (최소 5바퀴 + 랜덤)
  const spins = 5 + Math.random() * 3 // 5~8바퀴
  const randomAngle = Math.random() * 360
  const totalRotation = spins * 360 + randomAngle

  rotation.value += totalRotation

  // 3초 후 결과 확인
  setTimeout(() => {
    isSpinning.value = false

    // 최종 회전 각도 (0~360) - 포인터는 위쪽(0도)에 있으므로 360에서 빼기
    const finalAngle = (360 - (rotation.value % 360)) % 360

    // 어떤 섹터에 멈췄는지 찾기
    const resultSector = sectors.value.find(sector => {
      return finalAngle >= sector.startAngle && finalAngle < sector.endAngle
    })

    if (resultSector) {
      // 0.5초 후 결과 전달 (결과 확인 시간)
      setTimeout(() => {
        emit('spinComplete', resultSector.id)
      }, 500)
    }
  }, 3000)
}
</script>

<style scoped>
.spin-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(ellipse at center, rgba(26, 10, 46, 0.98) 0%, rgba(10, 10, 15, 0.99) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
  animation: fadeIn 0.2s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.spin-modal-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  animation: scaleIn 0.3s ease-out;
}

@keyframes scaleIn {
  from { transform: scale(0.9); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}

.modal-header {
  text-align: center;
  color: white;
}

.modal-title {
  font-size: 1.75rem;
  font-weight: 800;
  margin-bottom: 6px;
  background: linear-gradient(135deg, #ff2d7b 0%, #9945ff 50%, #00d9ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  filter: drop-shadow(0 0 20px rgba(153, 69, 255, 0.5));
}

.modal-subtitle {
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.6);
}

.wheel-wrapper {
  position: relative;
  width: 320px;
  height: 320px;
}

/* 외곽 네온 링 */
.neon-ring {
  position: absolute;
  top: -8px;
  left: -8px;
  right: -8px;
  bottom: -8px;
  border-radius: 50%;
  border: 2px solid transparent;
  background: linear-gradient(135deg, #ff2d7b, #9945ff, #00d9ff, #ff2d7b) border-box;
  -webkit-mask: linear-gradient(#fff 0 0) padding-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  animation: ringRotate 4s linear infinite;
  opacity: 0.8;
}

.neon-ring.ring-2 {
  top: -16px;
  left: -16px;
  right: -16px;
  bottom: -16px;
  opacity: 0.4;
  animation: ringRotate 6s linear infinite reverse;
}

@keyframes ringRotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.wheel-wrapper::before {
  content: '';
  position: absolute;
  top: -20px;
  left: -20px;
  right: -20px;
  bottom: -20px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(153, 69, 255, 0.3) 0%, transparent 70%);
  z-index: -1;
  animation: glowPulse 2s ease-in-out infinite;
}

@keyframes glowPulse {
  0%, 100% { opacity: 0.5; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.05); }
}

.wheel-svg {
  width: 100%;
  height: 100%;
  transition: transform 3s cubic-bezier(0.17, 0.67, 0.12, 0.99);
  filter: drop-shadow(0 0 20px rgba(153, 69, 255, 0.4));
}

.wheel-wrapper.spinning .wheel-svg {
  filter: drop-shadow(0 0 40px rgba(153, 69, 255, 0.6));
}

.wheel-wrapper.spinning .neon-ring {
  animation: ringRotate 0.5s linear infinite;
  opacity: 1;
}

.wheel-wrapper.spinning .neon-ring.ring-2 {
  animation: ringRotate 0.3s linear infinite reverse;
}

.wheel-wrapper.spinning::before {
  animation: glowSpin 0.3s linear infinite;
}

@keyframes glowSpin {
  0% { opacity: 0.5; }
  50% { opacity: 1; }
  100% { opacity: 0.5; }
}

.pointer {
  position: absolute;
  top: -12px;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 0;
  border-left: 16px solid transparent;
  border-right: 16px solid transparent;
  border-top: 30px solid #ff2d7b;
  z-index: 10;
  filter: drop-shadow(0 0 10px rgba(255, 45, 123, 0.8));
}

.pointer-glow {
  position: absolute;
  top: -35px;
  left: 50%;
  transform: translateX(-50%);
  width: 40px;
  height: 40px;
  background: radial-gradient(circle, rgba(255, 45, 123, 0.6) 0%, transparent 70%);
  animation: pointerGlow 1s ease-in-out infinite;
}

@keyframes pointerGlow {
  0%, 100% { opacity: 0.5; transform: translateX(-50%) scale(1); }
  50% { opacity: 1; transform: translateX(-50%) scale(1.3); }
}

.spin-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 36px;
  background: linear-gradient(135deg, #ff2d7b 0%, #9945ff 100%);
  color: white;
  border: none;
  border-radius: 14px;
  font-size: 1.15rem;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 0 30px rgba(255, 45, 123, 0.5);
  transition: all 0.3s;
  animation: btnPulse 2s ease-in-out infinite;
}

.btn-icon {
  font-size: 1.3rem;
}

@keyframes btnPulse {
  0%, 100% { transform: scale(1); box-shadow: 0 0 30px rgba(255, 45, 123, 0.5); }
  50% { transform: scale(1.02); box-shadow: 0 0 50px rgba(255, 45, 123, 0.7); }
}

.spin-button:hover {
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 0 50px rgba(255, 45, 123, 0.8), 0 0 80px rgba(153, 69, 255, 0.4);
}

.spin-button:active {
  transform: translateY(0) scale(0.98);
}

.spinning-text {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  color: white;
  font-size: 1rem;
  font-weight: 600;
}

.spinning-text p {
  background: linear-gradient(135deg, #ff2d7b 0%, #9945ff 50%, #00d9ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.spinner-icon {
  font-size: 2rem;
  animation: sparkle 0.8s ease-in-out infinite;
}

@keyframes sparkle {
  0%, 100% { transform: scale(1) rotate(0deg); opacity: 1; }
  50% { transform: scale(1.2) rotate(180deg); opacity: 0.8; }
}

/* 반응형 - 모바일 */
@media (max-width: 400px) {
  .wheel-wrapper {
    width: 280px;
    height: 280px;
  }

  .modal-title {
    font-size: 1.5rem;
  }

  .modal-subtitle {
    font-size: 0.85rem;
  }

  .spin-button {
    padding: 12px 28px;
    font-size: 1rem;
  }

  .pointer {
    border-left: 14px solid transparent;
    border-right: 14px solid transparent;
    border-top: 26px solid #ff2d7b;
  }
}
</style>
