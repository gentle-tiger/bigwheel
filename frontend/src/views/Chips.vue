<template>
  <div class="chips-page">
    <!-- 상단 헤더 -->
    <div class="page-header">
      <h1>💰 칩 관리</h1>
      <div class="total-balance-header">
        <span>총액</span>
        <span class="amount">{{ totalBalance.toLocaleString() }}원</span>
      </div>
    </div>

    <div class="content">
      <!-- 칩 입력/조회 통합 섹션 -->
      <section class="chips-section">
        <div class="chips-grid">
          <div v-for="chip in chipsList" :key="chip.key" class="chip-item">
            <!-- 칩 아이콘 (SVG) -->
            <svg class="chip-svg" width="60" height="60" viewBox="0 0 120 120" xmlns="http://www.w3.org/2000/svg">
              <circle cx="60" cy="60" r="58" :fill="chip.color" :stroke="chip.strokeColor" stroke-width="2"/>
              <circle cx="60" cy="60" r="54" fill="none" :stroke="chip.strokeColor" stroke-width="8" stroke-dasharray="8 4"/>
              <circle cx="60" cy="60" r="38" fill="#FFFFFF"/>
              <text x="60" y="63" font-family="Arial, sans-serif" font-size="16" font-weight="bold" :fill="chip.strokeColor" text-anchor="middle">{{ chip.displayValue }}</text>
            </svg>

            <!-- 입력 필드 -->
            <input
              v-model.number="chips[chip.key]"
              type="number"
              min="0"
              class="chip-input"
              :placeholder="chip.label"
            />

            <!-- 금액 표시 -->
            <div class="chip-value">
              {{ (chip.value * chips[chip.key]).toLocaleString() }}원
            </div>
          </div>
        </div>

        <button class="btn-save" @click="saveChips">저장</button>
      </section>
    </div>

    <!-- Toast 알림 -->
    <Toast
      :show="showToast"
      :message="toastMessage"
      :type="toastType"
      @close="showToast = false"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Toast from '../components/Toast.vue'

const router = useRouter()

// Toast 상태
const showToast = ref(false)
const toastMessage = ref('')
const toastType = ref('success')

// 칩 데이터
const chips = ref({
  chip1000: 0,
  chip5000: 0,
  chip10000: 0,
  chip100000: 0,
  chip1000000: 0
})

// 칩 정보 (BigWheel.vue와 동일한 디자인)
const chipsList = [
  { key: 'chip1000', label: '1천원', value: 1000, color: '#E8749B', strokeColor: '#C94A7A', displayValue: '1천' },
  { key: 'chip5000', label: '5천원', value: 5000, color: '#E89F5A', strokeColor: '#C87B3A', displayValue: '5천' },
  { key: 'chip10000', label: '1만원', value: 10000, color: '#5FB57C', strokeColor: '#3D8B5C', displayValue: '1만' },
  { key: 'chip100000', label: '10만원', value: 100000, color: '#2B4B7C', strokeColor: '#1A3A5C', displayValue: '10만' },
  { key: 'chip1000000', label: '100만원', value: 1000000, color: '#4A6B9C', strokeColor: '#3A5A8C', displayValue: '100만' }
]

// 총 보유 금액 계산
const totalBalance = computed(() => {
  return Object.entries(chips.value).reduce((sum, [key, count]) => {
    const chipInfo = chipsList.find(c => c.key === key)
    return sum + (chipInfo ? chipInfo.value * count : 0)
  }, 0)
})

// 칩 저장
const saveChips = () => {
  localStorage.setItem('userChips', JSON.stringify(chips.value))

  // Toast 알림 표시
  toastMessage.value = '칩 정보가 저장되었습니다!'
  toastType.value = 'success'
  showToast.value = true
}

// 페이지 로드 시 칩 정보 불러오기
onMounted(() => {
  const savedChips = localStorage.getItem('userChips')
  if (savedChips) {
    chips.value = JSON.parse(savedChips)
  }
})
</script>

<style scoped>
.chips-page {
  min-height: calc(100vh - 130px);
  padding-bottom: 80px;
  background: linear-gradient(180deg, #1a0a2e 0%, #0d1117 50%, #0a1628 100%);
}

.page-header {
  background: linear-gradient(135deg, rgba(255, 204, 0, 0.15) 0%, rgba(255, 170, 0, 0.1) 100%);
  border-bottom: 1px solid rgba(255, 204, 0, 0.2);
  padding: 1rem;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-header h1 {
  font-size: 1.25rem;
  font-weight: 700;
  margin: 0;
  background: linear-gradient(135deg, #ffcc00 0%, #ffaa00 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.total-balance-header {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.6);
}

.total-balance-header .amount {
  font-size: 1.125rem;
  font-weight: 700;
  color: #ffcc00;
  text-shadow: 0 0 15px rgba(255, 204, 0, 0.5);
}

.content {
  padding: 0.75rem;
}

/* 칩 섹션 */
.chips-section {
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 16px;
  padding: 1rem;
}

.chips-grid {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  margin-bottom: 1rem;
}

.chip-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem;
  background: rgba(10, 10, 15, 0.5);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  transition: all 0.3s;
}

.chip-item:hover {
  border-color: rgba(255, 204, 0, 0.3);
  box-shadow: 0 0 15px rgba(255, 204, 0, 0.1);
}

.chip-svg {
  flex-shrink: 0;
  filter: drop-shadow(0 0 8px rgba(255, 255, 255, 0.2));
}

.chip-input {
  flex: 1;
  padding: 0.5rem 0.75rem;
  background: rgba(10, 10, 15, 0.6);
  border: 2px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  text-align: center;
  color: #fff;
  transition: all 0.3s;
  min-width: 0;
}

.chip-input:focus {
  outline: none;
  border-color: #ffcc00;
  box-shadow: 0 0 15px rgba(255, 204, 0, 0.3);
}

.chip-input::placeholder {
  color: rgba(255, 255, 255, 0.3);
}

.chip-value {
  font-size: 0.875rem;
  font-weight: 700;
  color: #ffcc00;
  white-space: nowrap;
  min-width: 70px;
  text-align: right;
  text-shadow: 0 0 10px rgba(255, 204, 0, 0.4);
}

.btn-save {
  width: 100%;
  padding: 0.875rem;
  background: linear-gradient(135deg, #ffcc00 0%, #ffaa00 100%);
  color: #0a0a0f;
  border: none;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 0 25px rgba(255, 204, 0, 0.4);
}

.btn-save:hover {
  transform: translateY(-2px);
  box-shadow: 0 0 35px rgba(255, 204, 0, 0.6), 0 10px 30px rgba(0, 0, 0, 0.3);
}

.btn-save:active {
  transform: translateY(0);
}
</style>
