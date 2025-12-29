<template>
  <div class="floating-mode-selector">
    <!-- Floating 버튼 -->
    <button class="mode-toggle-btn" @click="toggleModeMenu">
      <span class="mode-icon">{{ currentModeIcon }}</span>
    </button>

    <!-- 모드 선택 메뉴 -->
    <div v-if="showModeMenu" class="mode-menu">
      <div class="mode-option" @click="selectMode('online')">
        <span class="option-icon">🌐</span>
        <div class="option-text">
          <div class="option-title">온라인</div>
          <div class="option-desc">실제 서비스</div>
        </div>
        <span v-if="gameMode === 'online'" class="check-icon">✓</span>
      </div>
      <div class="mode-option" @click="selectMode('offline')">
        <span class="option-icon">📝</span>
        <div class="option-text">
          <div class="option-title">오프라인</div>
          <div class="option-desc">기록용</div>
        </div>
        <span v-if="gameMode === 'offline'" class="check-icon">✓</span>
      </div>
    </div>

    <!-- 배경 오버레이 -->
    <div v-if="showModeMenu" class="mode-overlay" @click="toggleModeMenu"></div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

// Props
const emit = defineEmits(['modeChange'])

// 상태
const showModeMenu = ref(false)
const gameMode = ref('offline') // 기본값: offline

// 현재 모드 아이콘
const currentModeIcon = computed(() => {
  return gameMode.value === 'online' ? '🌐' : '📝'
})

// 모드 메뉴 토글
const toggleModeMenu = () => {
  showModeMenu.value = !showModeMenu.value
}

// 모드 선택
const selectMode = (mode) => {
  gameMode.value = mode
  localStorage.setItem('bigwheelGameMode', mode)
  emit('modeChange', mode)
  showModeMenu.value = false
}

// 마운트 시 localStorage에서 모드 불러오기
onMounted(() => {
  const savedMode = localStorage.getItem('bigwheelGameMode')
  if (savedMode) {
    gameMode.value = savedMode
    emit('modeChange', savedMode)
  }
})
</script>

<style scoped>
.floating-mode-selector {
  position: absolute;
  bottom: 80px;
  right: 16px;
  z-index: 99;
}

.mode-toggle-btn {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  background: linear-gradient(135deg, #9945ff 0%, #00d9ff 100%);
  color: white;
  border: none;
  font-size: 1.35rem;
  cursor: pointer;
  box-shadow: 0 0 25px rgba(153, 69, 255, 0.5);
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.mode-toggle-btn:hover {
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 0 35px rgba(153, 69, 255, 0.7), 0 0 50px rgba(0, 217, 255, 0.3);
}

.mode-toggle-btn:active {
  transform: translateY(0) scale(0.98);
}

.mode-icon {
  font-size: 1.35rem;
}

.mode-menu {
  position: absolute;
  bottom: 65px;
  right: 0;
  width: 200px;
  background: rgba(13, 17, 23, 0.98);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 14px;
  box-shadow: 0 0 40px rgba(0, 0, 0, 0.5), 0 0 20px rgba(153, 69, 255, 0.2);
  overflow: hidden;
  animation: slideUp 0.2s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.mode-option {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  cursor: pointer;
  transition: all 0.2s;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.mode-option:last-child {
  border-bottom: none;
}

.mode-option:hover {
  background: rgba(153, 69, 255, 0.15);
}

.option-icon {
  font-size: 1.35rem;
  flex-shrink: 0;
}

.option-text {
  flex: 1;
}

.option-title {
  font-size: 0.85rem;
  font-weight: 600;
  color: #fff;
  margin-bottom: 2px;
}

.option-desc {
  font-size: 0.7rem;
  color: rgba(255, 255, 255, 0.4);
}

.check-icon {
  font-size: 1rem;
  color: #00ff88;
  font-weight: bold;
  text-shadow: 0 0 8px rgba(0, 255, 136, 0.5);
}

.mode-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: -1;
}
</style>
