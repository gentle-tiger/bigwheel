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
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  animation: fadeIn 0.2s ease-out;
  padding: 1rem;
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
  background: rgba(13, 17, 23, 0.98);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  padding: 1.5rem;
  max-width: 400px;
  width: 100%;
  box-shadow: 0 0 60px rgba(0, 0, 0, 0.5), 0 0 30px rgba(153, 69, 255, 0.2);
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
  font-size: 1.25rem;
  font-weight: 700;
  background: linear-gradient(135deg, #ff2d7b 0%, #9945ff 50%, #00d9ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-align: center;
  margin-bottom: 6px;
}

.modal-subtitle {
  font-size: 0.8rem;
  color: rgba(255, 255, 255, 0.5);
  text-align: center;
  margin-bottom: 1.25rem;
}

.mode-options {
  display: flex;
  gap: 12px;
  margin-bottom: 1rem;
}

.mode-card {
  flex: 1;
  padding: 1.25rem 1rem;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 14px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.mode-card:hover {
  border-color: rgba(153, 69, 255, 0.5);
  transform: translateY(-4px);
  box-shadow: 0 0 25px rgba(153, 69, 255, 0.3);
}

.mode-icon {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.75rem;
  margin-bottom: 12px;
}

.mode-icon.online {
  background: linear-gradient(135deg, #9945ff 0%, #00d9ff 100%);
  box-shadow: 0 0 20px rgba(153, 69, 255, 0.4);
}

.mode-icon.offline {
  background: linear-gradient(135deg, #00ff88 0%, #00cc6a 100%);
  box-shadow: 0 0 20px rgba(0, 255, 136, 0.4);
}

.mode-info {
  flex: 1;
}

.mode-name {
  font-size: 1rem;
  font-weight: 700;
  color: #fff;
  margin-bottom: 6px;
}

.mode-description {
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.5);
  line-height: 1.4;
}

.close-button {
  width: 100%;
  padding: 0.75rem;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.6);
  border-radius: 10px;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.close-button:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
}

/* 반응형 - 모바일 */
@media (max-width: 640px) {
  .mode-options {
    flex-direction: column;
  }

  .modal-content {
    padding: 1.25rem;
  }

  .mode-card {
    padding: 1rem;
    flex-direction: row;
    text-align: left;
    gap: 1rem;
  }

  .mode-icon {
    margin-bottom: 0;
    width: 48px;
    height: 48px;
    font-size: 1.5rem;
  }
}
</style>
