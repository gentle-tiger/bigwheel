<template>
  <div v-if="show" class="modal-overlay" @click="handleOverlayClick">
    <div class="modal-content" @click.stop>
      <h2 class="modal-title">게임 모드 선택</h2>
      <p class="modal-subtitle">플레이 방식을 선택해주세요</p>

      <div class="mode-options">
        <div class="mode-card" @click="selectMode('online')">
          <div class="mode-icon online">🌐</div>
          <div class="mode-info">
            <h3 class="mode-name">온라인</h3>
            <p class="mode-description">실제 서비스<br>자동으로 돌림판이 돌아갑니다</p>
          </div>
        </div>

        <div class="mode-card" @click="selectMode('offline')">
          <div class="mode-icon offline">📝</div>
          <div class="mode-info">
            <h3 class="mode-name">오프라인</h3>
            <p class="mode-description">기록용<br>결과를 직접 입력합니다</p>
          </div>
        </div>
      </div>

      <button class="close-button" @click="close">취소</button>
    </div>
  </div>
</template>

<script setup>
// Props
const props = defineProps({
  show: {
    type: Boolean,
    default: false
  }
})

// Emit
const emit = defineEmits(['close', 'modeSelected'])

// 모드 선택
const selectMode = (mode) => {
  localStorage.setItem('bigwheelGameMode', mode)
  emit('modeSelected', mode)
}

// 닫기
const close = () => {
  emit('close')
}

// 오버레이 클릭 시 닫기
const handleOverlayClick = () => {
  close()
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  animation: fadeIn 0.2s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.modal-content {
  background: white;
  border-radius: 16px;
  padding: 32px;
  max-width: 500px;
  width: 90%;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.modal-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1f2937;
  text-align: center;
  margin-bottom: 8px;
}

.modal-subtitle {
  font-size: 0.875rem;
  color: #6b7280;
  text-align: center;
  margin-bottom: 24px;
}

.mode-options {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}

.mode-card {
  flex: 1;
  padding: 24px;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.mode-card:hover {
  border-color: #6366f1;
  background: #f9fafb;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.2);
}

.mode-icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  margin-bottom: 16px;
}

.mode-icon.online {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
}

.mode-icon.offline {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.mode-info {
  flex: 1;
}

.mode-name {
  font-size: 1.125rem;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 8px;
}

.mode-description {
  font-size: 0.875rem;
  color: #6b7280;
  line-height: 1.5;
}

.close-button {
  width: 100%;
  padding: 12px;
  background: #f3f4f6;
  color: #6b7280;
  border: none;
  border-radius: 8px;
  font-size: 0.875rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.close-button:hover {
  background: #e5e7eb;
}

/* 반응형 - 모바일 */
@media (max-width: 640px) {
  .mode-options {
    flex-direction: column;
  }

  .modal-content {
    padding: 24px;
  }

  .mode-card {
    padding: 20px;
  }
}
</style>
