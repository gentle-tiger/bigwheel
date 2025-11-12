<template>
  <div class="chip-input-page">
    <Modal
      :show="modal.show"
      :type="modal.type"
      :title="modal.title"
      :message="modal.message"
      @close="modal.show = false"
      @confirm="handleModalConfirm"
    />

    <!-- 상단 고정 타이틀 -->
    <div class="page-header">
      <div class="header-title">칩 입력</div>
    </div>

    <main class="main-content">

      <section class="chip-section">
        <div class="chip-list">
          <!-- 1,000원 칩 (핑크) -->
          <div class="chip-item">
            <div class="chip-icon">
              <svg width="50" height="50" viewBox="0 0 120 120" xmlns="http://www.w3.org/2000/svg">
                <circle cx="60" cy="60" r="58" fill="#E8749B" stroke="#C94A7A" stroke-width="2"/>
                <circle cx="60" cy="60" r="54" fill="none" stroke="#C94A7A" stroke-width="8" stroke-dasharray="8 4"/>
                <circle cx="60" cy="60" r="38" fill="#FFFFFF"/>
                <text x="60" y="67" font-family="Arial, sans-serif" font-size="18" font-weight="bold" fill="#C94A7A" text-anchor="middle">1천</text>
              </svg>
            </div>
            <div class="chip-label">1,000원</div>
            <input type="number" v-model.number="chips.chip1000" min="0" class="chip-input" placeholder="0">
          </div>

          <!-- 5,000원 칩 (오렌지) -->
          <div class="chip-item">
            <div class="chip-icon">
              <svg width="50" height="50" viewBox="0 0 120 120" xmlns="http://www.w3.org/2000/svg">
                <circle cx="60" cy="60" r="58" fill="#E89F5A" stroke="#C87B3A" stroke-width="2"/>
                <circle cx="60" cy="60" r="54" fill="none" stroke="#8B5A2B" stroke-width="8" stroke-dasharray="8 4"/>
                <circle cx="60" cy="60" r="38" fill="#FFFFFF"/>
                <text x="60" y="67" font-family="Arial, sans-serif" font-size="18" font-weight="bold" fill="#C87B3A" text-anchor="middle">5천</text>
              </svg>
            </div>
            <div class="chip-label">5,000원</div>
            <input type="number" v-model.number="chips.chip5000" min="0" class="chip-input" placeholder="0">
          </div>

          <!-- 10,000원 칩 (초록) -->
          <div class="chip-item">
            <div class="chip-icon">
              <svg width="50" height="50" viewBox="0 0 120 120" xmlns="http://www.w3.org/2000/svg">
                <circle cx="60" cy="60" r="58" fill="#5FB57C" stroke="#3D8B5C" stroke-width="2"/>
                <circle cx="60" cy="60" r="54" fill="none" stroke="#2A6B45" stroke-width="8" stroke-dasharray="8 4"/>
                <circle cx="60" cy="60" r="38" fill="#FFFFFF"/>
                <text x="60" y="67" font-family="Arial, sans-serif" font-size="18" font-weight="bold" fill="#3D8B5C" text-anchor="middle">1만</text>
              </svg>
            </div>
            <div class="chip-label">10,000원</div>
            <input type="number" v-model.number="chips.chip10000" min="0" class="chip-input" placeholder="0">
          </div>

          <!-- 100,000원 칩 (진한 파랑) -->
          <div class="chip-item">
            <div class="chip-icon">
              <svg width="50" height="50" viewBox="0 0 120 120" xmlns="http://www.w3.org/2000/svg">
                <circle cx="60" cy="60" r="58" fill="#2B4B7C" stroke="#1A3A5C" stroke-width="2"/>
                <circle cx="60" cy="60" r="54" fill="none" stroke="#0F2544" stroke-width="8" stroke-dasharray="8 4"/>
                <circle cx="60" cy="60" r="38" fill="#FFFFFF"/>
                <text x="60" y="67" font-family="Arial, sans-serif" font-size="18" font-weight="bold" fill="#1A3A5C" text-anchor="middle">10만</text>
              </svg>
            </div>
            <div class="chip-label">100,000원</div>
            <input type="number" v-model.number="chips.chip100000" min="0" class="chip-input" placeholder="0">
          </div>

          <!-- 1,000,000원 칩 (밝은 파랑) -->
          <div class="chip-item">
            <div class="chip-icon">
              <svg width="50" height="50" viewBox="0 0 120 120" xmlns="http://www.w3.org/2000/svg">
                <circle cx="60" cy="60" r="58" fill="#4A6B9C" stroke="#3A5A8C" stroke-width="2"/>
                <circle cx="60" cy="60" r="54" fill="none" stroke="#2A4A7C" stroke-width="8" stroke-dasharray="8 4"/>
                <circle cx="60" cy="60" r="38" fill="#FFFFFF"/>
                <text x="60" y="64" font-family="Arial, sans-serif" font-size="16" font-weight="bold" fill="#3A5A8C" text-anchor="middle">100만</text>
              </svg>
            </div>
            <div class="chip-label">1,000,000원</div>
            <input type="number" v-model.number="chips.chip1000000" min="0" class="chip-input" placeholder="0">
          </div>
        </div>

        <div class="total-amount">
          <span>총 금액:</span>
          <strong>{{ totalAmount.toLocaleString() }} 원</strong>
        </div>

        <button class="btn-save" @click="saveChips">저장하기</button>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import Modal from '../components/Modal.vue'

const router = useRouter()

const chips = ref({
  chip1000: 0,
  chip5000: 0,
  chip10000: 0,
  chip100000: 0,
  chip1000000: 0
})

const modal = ref({
  show: false,
  type: 'success',
  title: '',
  message: ''
})

const totalAmount = computed(() => {
  return (
    chips.value.chip1000 * 1000 +
    chips.value.chip5000 * 5000 +
    chips.value.chip10000 * 10000 +
    chips.value.chip100000 * 100000 +
    chips.value.chip1000000 * 1000000
  )
})

const saveChips = () => {
  // localStorage에 칩 데이터 저장
  localStorage.setItem('userChips', JSON.stringify(chips.value))

  modal.value = {
    show: true,
    type: 'success',
    title: '칩 저장 완료!',
    message: `총 ${totalAmount.value.toLocaleString()}원이 저장되었습니다.`
  }
}

const handleModalConfirm = () => {
  router.push('/category')
}
</script>

<style scoped>
.chip-input-page {
  min-height: calc(100vh - 200px);
  padding-bottom: 1rem;
}

/* 상단 고정 헤더 */
.page-header {
  position: sticky;
  top: 60px;
  z-index: 100;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  padding: 0.75rem 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-title {
  text-align: center;
  font-size: 1rem;
  font-weight: 700;
  color: var(--white);
}

.main-content {
  padding: 0 0.5rem;
}

.chip-section {
  background: var(--white);
  border-radius: 12px;
  padding: 0.75rem;
  margin: 0.75rem auto;
  max-width: 500px;
  box-shadow: 0 2px 8px var(--shadow);
}

.chip-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  margin-bottom: 0.75rem;
}

.chip-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.5rem;
  background: var(--light-gray);
  border-radius: 8px;
  transition: all 0.2s;
}

.chip-item:hover {
  transform: translateX(3px);
  background: rgba(99, 102, 241, 0.05);
}

.chip-icon {
  flex-shrink: 0;
}

.chip-label {
  flex: 1;
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--dark);
}

.chip-input {
  width: 80px;
  padding: 0.5rem;
  border: 2px solid var(--border);
  border-radius: 8px;
  font-size: 1rem;
  text-align: center;
  font-weight: 600;
  transition: all 0.2s;
}

.chip-input:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
}

.total-amount {
  text-align: center;
  font-size: 0.875rem;
  padding: 0.75rem;
  background: linear-gradient(135deg, var(--primary) 0%, var(--secondary) 100%);
  color: var(--white);
  border-radius: 8px;
  margin-bottom: 0.75rem;
}

.total-amount strong {
  font-size: 1.25rem;
  margin-left: 0.5rem;
  display: block;
  margin-top: 0.25rem;
}

.btn-save {
  width: 100%;
  padding: 0.75rem;
  background: var(--primary);
  color: var(--white);
  border: none;
  border-radius: 10px;
  font-size: 1rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
}

.btn-save:hover {
  background: var(--primary-dark);
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(99, 102, 241, 0.4);
}

@media (max-width: 768px) {
  .chip-section {
    padding: 0.75rem;
  }

  .chip-item {
    gap: 0.5rem;
  }

  .chip-label {
    font-size: 0.75rem;
  }

  .chip-input {
    width: 70px;
    font-size: 0.875rem;
  }
}
</style>
