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
  position: fixed;
  bottom: 80px;
  right: 20px;
  z-index: 1000;
}

.mode-toggle-btn {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: white;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.4);
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.mode-toggle-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(99, 102, 241, 0.5);
}

.mode-toggle-btn:active {
  transform: translateY(0);
}

.mode-icon {
  font-size: 1.5rem;
}

.mode-menu {
  position: absolute;
  bottom: 70px;
  right: 0;
  width: 200px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
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
  padding: 12px 16px;
  cursor: pointer;
  transition: background 0.2s;
  border-bottom: 1px solid #f0f0f0;
}

.mode-option:last-child {
  border-bottom: none;
}

.mode-option:hover {
  background: #f9fafb;
}

.option-icon {
  font-size: 1.5rem;
  flex-shrink: 0;
}

.option-text {
  flex: 1;
}

.option-title {
  font-size: 0.875rem;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 2px;
}

.option-desc {
  font-size: 0.75rem;
  color: #6b7280;
}

.check-icon {
  font-size: 1.125rem;
  color: #10b981;
  font-weight: bold;
}

.mode-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  z-index: -1;
}
</style>
