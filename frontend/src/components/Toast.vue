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
  top: 70px; /* 헤더 아래 */
  left: 50%;
  transform: translateX(-50%);
  z-index: 10001;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem 1.5rem;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  min-width: 280px;
  max-width: 90vw;
}

.toast-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.25rem;
  font-weight: 700;
  color: white;
  flex-shrink: 0;
}

.toast-message {
  flex: 1;
  font-size: 0.875rem;
  font-weight: 600;
  color: #1f2937;
}

/* 타입별 색상 */
.toast-container.success .toast-icon {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.toast-container.error .toast-icon {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
}

.toast-container.warning .toast-icon {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.toast-container.info .toast-icon {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
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
    min-width: 260px;
    padding: 0.875rem 1.25rem;
  }

  .toast-icon {
    width: 28px;
    height: 28px;
    font-size: 1rem;
  }

  .toast-message {
    font-size: 0.8125rem;
  }
}
</style>
