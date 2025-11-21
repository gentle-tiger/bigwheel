<template>
  <div v-if="show" class="spin-modal-overlay">
    <div class="spin-modal-content">
      <div class="modal-header">
        <h2 class="modal-title">🎰 Big Wheel</h2>
        <p class="modal-subtitle">행운의 돌림판이 돌아갑니다!</p>
      </div>

      <div class="wheel-wrapper" :class="{ spinning: isSpinning }">
        <svg
          class="wheel-svg"
          width="400"
          height="400"
          viewBox="0 0 300 300"
          :style="{ transform: `rotate(${rotation}deg)` }"
        >
          <!-- 배경 원 -->
          <circle cx="150" cy="150" r="145" fill="#fff" stroke="#333" stroke-width="3"/>

          <!-- 구역별 색상 섹터 -->
          <g v-for="(sector, index) in sectors" :key="index">
            <path
              :d="createArcPath(sector.startAngle, sector.endAngle)"
              :fill="sector.color"
              stroke="#fff"
              stroke-width="2"
            />
            <!-- 구역 텍스트 -->
            <text
              :x="getTextX(sector.middleAngle)"
              :y="getTextY(sector.middleAngle)"
              text-anchor="middle"
              font-size="16"
              font-weight="bold"
              fill="#fff"
              :transform="`rotate(${sector.middleAngle}, ${getTextX(sector.middleAngle)}, ${getTextY(sector.middleAngle)})`"
            >
              {{ sector.name }}
            </text>
          </g>

          <!-- 중앙 원 -->
          <circle cx="150" cy="150" r="30" fill="#333"/>
          <circle cx="150" cy="150" r="25" fill="#fff"/>
        </svg>

        <!-- 포인터 (위쪽 고정) -->
        <div class="pointer"></div>
      </div>

      <!-- 스핀 버튼 -->
      <button
        v-if="!isSpinning && !hasSpun"
        class="spin-button"
        @click="startSpin"
      >
        돌리기
      </button>

      <!-- 스피닝 상태 표시 -->
      <div v-if="isSpinning" class="spinning-text">
        <div class="spinner-icon">🎯</div>
        <p>돌아가는 중...</p>
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

// 섹터 데이터 생성
const sectors = computed(() => {
  const totalSlots = props.zones.reduce((sum, zone) => sum + zone.slots, 0)
  let currentAngle = 0
  const colors = {
    'silver': '#c0c0c0',
    'gold': '#ffd700',
    'emerald': '#10b981',
    'diamond': '#60a5fa',
    'crystal': '#a78bfa',
    'joker': '#ef4444',
    'mega': '#f59e0b'
  }

  return props.zones.map(zone => {
    const slotAngle = (zone.slots / totalSlots) * 360
    const sector = {
      id: zone.id,
      name: zone.name,
      startAngle: currentAngle,
      endAngle: currentAngle + slotAngle,
      middleAngle: currentAngle + slotAngle / 2,
      color: colors[zone.id] || '#999'
    }
    currentAngle += slotAngle
    return sector
  })
})

// 호(arc) 경로 생성
const createArcPath = (startAngle, endAngle) => {
  const start = polarToCartesian(150, 150, 145, startAngle)
  const end = polarToCartesian(150, 150, 145, endAngle)
  const largeArcFlag = endAngle - startAngle <= 180 ? '0' : '1'

  return [
    `M 150 150`,
    `L ${start.x} ${start.y}`,
    `A 145 145 0 ${largeArcFlag} 1 ${end.x} ${end.y}`,
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
const getTextX = (angle) => {
  const rad = (angle - 90) * Math.PI / 180
  return 150 + 100 * Math.cos(rad)
}

const getTextY = (angle) => {
  const rad = (angle - 90) * Math.PI / 180
  return 150 + 100 * Math.sin(rad)
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

    // 최종 회전 각도 (0~360)
    const finalAngle = rotation.value % 360

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
  background: rgba(0, 0, 0, 0.85);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
  animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.spin-modal-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
  animation: scaleIn 0.4s ease-out;
}

@keyframes scaleIn {
  from {
    transform: scale(0.8);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}

.modal-header {
  text-align: center;
  color: white;
}

.modal-title {
  font-size: 2rem;
  font-weight: 800;
  margin-bottom: 8px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.modal-subtitle {
  font-size: 1.125rem;
  opacity: 0.9;
}

.wheel-wrapper {
  position: relative;
  width: 400px;
  height: 400px;
  filter: drop-shadow(0 8px 32px rgba(0, 0, 0, 0.5));
}

.wheel-svg {
  width: 100%;
  height: 100%;
  transition: transform 3s cubic-bezier(0.17, 0.67, 0.12, 0.99);
}

.wheel-wrapper.spinning .wheel-svg {
  transition: transform 3s cubic-bezier(0.17, 0.67, 0.12, 0.99);
}

.pointer {
  position: absolute;
  top: -15px;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 0;
  border-left: 20px solid transparent;
  border-right: 20px solid transparent;
  border-top: 35px solid #ef4444;
  z-index: 10;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.4));
}

.spin-button {
  padding: 16px 48px;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: white;
  border: none;
  border-radius: 16px;
  font-size: 1.5rem;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 8px 24px rgba(99, 102, 241, 0.5);
  transition: all 0.3s;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

.spin-button:hover {
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 12px 32px rgba(99, 102, 241, 0.6);
}

.spin-button:active {
  transform: translateY(0) scale(0.98);
}

.spinning-text {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  color: white;
  font-size: 1.25rem;
  font-weight: 600;
}

.spinner-icon {
  font-size: 3rem;
  animation: rotate 2s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* 반응형 - 모바일 */
@media (max-width: 640px) {
  .wheel-wrapper {
    width: 320px;
    height: 320px;
  }

  .modal-title {
    font-size: 1.5rem;
  }

  .modal-subtitle {
    font-size: 1rem;
  }

  .spin-button {
    padding: 12px 36px;
    font-size: 1.25rem;
  }
}
</style>
