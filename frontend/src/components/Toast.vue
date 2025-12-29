<template>
  <Transition name="toast-slide">
    <div v-if="show" class="toast-container" :class="type">
      <div class="toast-icon">{{ getIcon() }}</div>
      <div class="toast-message">{{ message }}</div>
    </div>
  </Transition>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  },
  message: {
    type: String,
    default: ''
  },
  type: {
    type: String,
    default: 'success', // success, error, info, warning
    validator: (value) => ['success', 'error', 'info', 'warning'].includes(value)
  },
  duration: {
    type: Number,
    default: 3000
  }
})

const emit = defineEmits(['close'])

const getIcon = () => {
  switch (props.type) {
    case 'success':
      return '✓'
    case 'error':
      return '✕'
    case 'warning':
      return '⚠'
    case 'info':
      return 'ℹ'
    default:
      return '✓'
  }
}

// 자동 닫기
watch(() => props.show, (newVal) => {
  if (newVal && props.duration > 0) {
    setTimeout(() => {
      emit('close')
    }, props.duration)
  }
})
</script>

<style scoped>
.toast-container {
  position: fixed;
  top: 60px; /* 헤더 아래 */
  left: 50%;
  transform: translateX(-50%);
  z-index: 10001;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.875rem 1.25rem;
  background: rgba(13, 17, 23, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  box-shadow: 0 0 30px rgba(0, 0, 0, 0.4);
  min-width: 260px;
  max-width: 90vw;
}

.toast-icon {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1rem;
  font-weight: 700;
  flex-shrink: 0;
}

.toast-message {
  flex: 1;
  font-size: 0.85rem;
  font-weight: 600;
  color: #fff;
}

/* 타입별 색상 */
.toast-container.success .toast-icon {
  background: #00ff88;
  color: #0a0a0f;
  box-shadow: 0 0 15px rgba(0, 255, 136, 0.5);
}

.toast-container.error .toast-icon {
  background: #ff3366;
  color: #fff;
  box-shadow: 0 0 15px rgba(255, 51, 102, 0.5);
}

.toast-container.warning .toast-icon {
  background: #ffcc00;
  color: #0a0a0f;
  box-shadow: 0 0 15px rgba(255, 204, 0, 0.5);
}

.toast-container.info .toast-icon {
  background: #00d9ff;
  color: #0a0a0f;
  box-shadow: 0 0 15px rgba(0, 217, 255, 0.5);
}

/* 타입별 테두리 글로우 */
.toast-container.success {
  border-color: rgba(0, 255, 136, 0.3);
  box-shadow: 0 0 20px rgba(0, 255, 136, 0.2);
}

.toast-container.error {
  border-color: rgba(255, 51, 102, 0.3);
  box-shadow: 0 0 20px rgba(255, 51, 102, 0.2);
}

.toast-container.warning {
  border-color: rgba(255, 204, 0, 0.3);
  box-shadow: 0 0 20px rgba(255, 204, 0, 0.2);
}

.toast-container.info {
  border-color: rgba(0, 217, 255, 0.3);
  box-shadow: 0 0 20px rgba(0, 217, 255, 0.2);
}

/* 애니메이션 */
.toast-slide-enter-active {
  animation: slideDown 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

.toast-slide-leave-active {
  animation: slideUp 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translate(-50%, -100%);
  }
  to {
    opacity: 1;
    transform: translate(-50%, 0);
  }
}

@keyframes slideUp {
  from {
    opacity: 1;
    transform: translate(-50%, 0);
  }
  to {
    opacity: 0;
    transform: translate(-50%, -20px);
  }
}

/* 모바일 최적화 */
@media (max-width: 640px) {
  .toast-container {
    min-width: 240px;
    padding: 0.75rem 1rem;
  }

  .toast-icon {
    width: 24px;
    height: 24px;
    font-size: 0.875rem;
  }

  .toast-message {
    font-size: 0.8rem;
  }
}
</style>
