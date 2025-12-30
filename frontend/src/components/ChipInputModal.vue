<template>
  <div v-if="show" class="modal-overlay" @click="$emit('close')">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h2>칩 추가하기</h2>
        <button @click="$emit('close')" class="close-button">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
            <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2"/>
          </svg>
        </button>
      </div>

      <div class="modal-body">
        <!-- 현재 보유 칩 -->
        <div class="current-chips-section">
          <h3>현재 보유 칩</h3>
          <div v-if="loadingChips" class="loading">조회 중...</div>
          <div v-else class="chips-grid">
            <div
              v-for="chip in currentChips"
              :key="chip.chipType"
              class="chip-display"
            >
              <span class="chip-type">{{ formatChipType(chip.chipType) }}</span>
              <span class="chip-count">{{ chip.count }}개</span>
            </div>
          </div>
        </div>

        <!-- 추가할 칩 입력 -->
        <div class="add-chips-section">
          <h3>추가할 칩 입력</h3>
          <div class="chips-input-grid">
            <div v-for="chipType in chipTypes" :key="chipType" class="chip-input">
              <label>{{ formatChipType(chipType) }}</label>
              <div class="input-stepper">
                <button class="stepper-btn minus" @click="decrementChip(chipType)" :disabled="addChips[chipType] <= 0">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
                    <path d="M5 12H19" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/>
                  </svg>
                </button>
                <input
                  type="number"
                  v-model.number="addChips[chipType]"
                  min="0"
                  placeholder="0"
                  @input="validateInput(chipType)"
                />
                <button class="stepper-btn plus" @click="incrementChip(chipType)">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
                    <path d="M12 5V19M5 12H19" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/>
                  </svg>
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- 추가 후 예상 칩 -->
        <div class="preview-section">
          <h3>추가 후 예상 칩</h3>
          <div class="chips-grid">
            <div
              v-for="chip in previewChips"
              :key="chip.chipType"
              class="chip-display preview"
            >
              <span class="chip-type">{{ formatChipType(chip.chipType) }}</span>
              <span class="chip-count">{{ chip.count }}개</span>
            </div>
          </div>
        </div>
      </div>

      <div class="modal-footer">
        <button @click="$emit('close')" class="button secondary">취소</button>
        <button @click="handleSave" class="button primary">저장</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import axios from 'axios'

const props = defineProps({
  show: Boolean,
  userId: String
})

const emit = defineEmits(['close', 'saved'])

const chipTypes = ['CHIP_1000', 'CHIP_5000', 'CHIP_10000', 'CHIP_100000', 'CHIP_1000000']

const loadingChips = ref(false)
const currentChips = ref([])
const addChips = ref({
  CHIP_1000: 0,
  CHIP_5000: 0,
  CHIP_10000: 0,
  CHIP_100000: 0,
  CHIP_1000000: 0
})

// 모달 열릴 때 현재 칩 조회
watch(() => props.show, async (isShow) => {
  if (isShow) {
    await fetchCurrentChips()
    // 입력 필드 초기화
    addChips.value = {
      CHIP_1000: 0,
      CHIP_5000: 0,
      CHIP_10000: 0,
      CHIP_100000: 0,
      CHIP_1000000: 0
    }
  }
})

// 현재 보유 칩 조회
const fetchCurrentChips = async () => {
  try {
    loadingChips.value = true
    const response = await axios.get(`/api/v1/user/${props.userId}/chips`)
    currentChips.value = response.data.chips || []
  } catch (err) {
    console.error('칩 조회 실패:', err)
    currentChips.value = []
  } finally {
    loadingChips.value = false
  }
}

// 추가 후 예상 칩 계산
const previewChips = computed(() => {
  return chipTypes.map(chipType => {
    const current = currentChips.value.find(c => c.chipType === chipType)
    const currentCount = current ? current.count : 0
    const addCount = addChips.value[chipType] || 0

    return {
      chipType,
      count: currentCount + addCount
    }
  })
})

// 칩 저장
const handleSave = async () => {
  try {
    // 0보다 큰 칩만 필터링
    const chipsToAdd = {}
    Object.keys(addChips.value).forEach(chipType => {
      if (addChips.value[chipType] > 0) {
        chipsToAdd[chipType] = addChips.value[chipType]
      }
    })

    if (Object.keys(chipsToAdd).length === 0) {
      alert('추가할 칩을 입력하세요')
      return
    }

    await axios.post(`/api/v1/user/${props.userId}/chips`, {
      chips: chipsToAdd
    })

    alert('칩이 추가되었습니다')
    emit('saved')
    emit('close')
  } catch (err) {
    console.error('칩 저장 실패:', err)
    alert('칩 저장에 실패했습니다')
  }
}

// 칩 타입 포맷팅
const formatChipType = (type) => {
  const value = type.replace('CHIP_', '')
  return `${Number(value).toLocaleString()}원`
}

// 칩 증가
const incrementChip = (chipType) => {
  addChips.value[chipType] = (addChips.value[chipType] || 0) + 1
}

// 칩 감소
const decrementChip = (chipType) => {
  if (addChips.value[chipType] > 0) {
    addChips.value[chipType]--
  }
}

// 입력 유효성 검사
const validateInput = (chipType) => {
  if (addChips.value[chipType] < 0 || isNaN(addChips.value[chipType])) {
    addChips.value[chipType] = 0
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  max-width: 400px;  /* 앱 컨테이너 너비 */
  height: 100%;
  background: rgba(0, 0, 0, 0.85);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  animation: fadeIn 0.2s ease-out;
  padding: 1rem;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal-content {
  background: rgba(13, 17, 23, 0.98);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 204, 0, 0.2);
  border-radius: 16px;
  width: 100%;
  max-width: 360px;  /* 더 컴팩트하게 */
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 0 40px rgba(0, 0, 0, 0.5), 0 0 20px rgba(255, 204, 0, 0.15);
  animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
  from { transform: translateY(20px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.875rem 1rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  position: sticky;
  top: 0;
  background: rgba(13, 17, 23, 0.98);
  backdrop-filter: blur(20px);
  z-index: 1;
  border-radius: 16px 16px 0 0;
}

.modal-header h2 {
  margin: 0;
  font-size: 0.95rem;
  font-weight: 700;
  background: linear-gradient(135deg, #ffcc00 0%, #ff9500 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.close-button {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.2s;
  color: rgba(255, 255, 255, 0.5);
}

.close-button:hover {
  background: rgba(255, 51, 102, 0.2);
  border-color: #ff3366;
  color: #ff3366;
}

.modal-body {
  padding: 0.75rem 1rem;
}

.current-chips-section,
.add-chips-section,
.preview-section {
  margin-bottom: 0.75rem;
}

.current-chips-section h3,
.add-chips-section h3,
.preview-section h3 {
  font-size: 0.7rem;
  font-weight: 600;
  margin-bottom: 6px;
  color: rgba(255, 255, 255, 0.5);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.loading {
  text-align: center;
  padding: 12px;
  color: rgba(255, 255, 255, 0.4);
  font-size: 0.75rem;
}

.chips-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(90px, 1fr));
  gap: 6px;
}

.chip-display {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.08);
  padding: 8px;
  border-radius: 8px;
  text-align: center;
  transition: all 0.2s;
}

.chip-display:hover {
  transform: translateY(-2px);
  border-color: rgba(255, 204, 0, 0.3);
  box-shadow: 0 4px 15px rgba(255, 204, 0, 0.1);
}

.chip-display.preview {
  background: rgba(0, 255, 136, 0.08);
  border: 1px solid rgba(0, 255, 136, 0.3);
  box-shadow: 0 0 15px rgba(0, 255, 136, 0.1);
}

.chip-type {
  display: block;
  font-size: 0.6rem;
  color: rgba(255, 255, 255, 0.4);
  margin-bottom: 2px;
  font-weight: 500;
}

.chip-count {
  display: block;
  font-size: 0.9rem;
  font-weight: 700;
  color: #ffcc00;
  text-shadow: 0 0 8px rgba(255, 204, 0, 0.3);
}

.chip-display.preview .chip-count {
  color: #00ff88;
  text-shadow: 0 0 8px rgba(0, 255, 136, 0.3);
}

.chips-input-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 8px;
}

.chip-input label {
  display: block;
  font-size: 0.65rem;
  margin-bottom: 4px;
  color: rgba(255, 255, 255, 0.5);
  font-weight: 500;
}

/* 커스텀 Stepper Input */
.input-stepper {
  display: flex;
  align-items: center;
  gap: 0;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  overflow: hidden;
}

.stepper-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  background: transparent;
  border: none;
  color: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: all 0.2s;
}

.stepper-btn:hover:not(:disabled) {
  color: #ffcc00;
  background: rgba(255, 204, 0, 0.15);
}

.stepper-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.stepper-btn.minus:hover:not(:disabled) {
  color: #ff3366;
  background: rgba(255, 51, 102, 0.15);
}

.stepper-btn.plus:hover:not(:disabled) {
  color: #00ff88;
  background: rgba(0, 255, 136, 0.15);
}

.chip-input input {
  flex: 1;
  width: 100%;
  padding: 4px 6px;
  background: transparent;
  border: none;
  border-left: 1px solid rgba(255, 255, 255, 0.1);
  border-right: 1px solid rgba(255, 255, 255, 0.1);
  font-size: 0.85rem;
  color: #fff;
  text-align: center;
  transition: all 0.2s;
  /* 기본 스피너 숨기기 */
  -moz-appearance: textfield;
}

.chip-input input::-webkit-outer-spin-button,
.chip-input input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

.chip-input input::placeholder {
  color: rgba(255, 255, 255, 0.2);
}

.chip-input input:focus {
  outline: none;
  background: rgba(255, 204, 0, 0.05);
}

.input-stepper:focus-within {
  border-color: #ffcc00;
  box-shadow: 0 0 12px rgba(255, 204, 0, 0.2);
}

.modal-footer {
  display: flex;
  gap: 8px;
  padding: 0.75rem 1rem;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  position: sticky;
  bottom: 0;
  background: rgba(13, 17, 23, 0.98);
  backdrop-filter: blur(20px);
  border-radius: 0 0 16px 16px;
}

.button {
  flex: 1;
  padding: 10px;
  border: none;
  border-radius: 8px;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.button.primary {
  background: linear-gradient(135deg, #ffcc00 0%, #ff9500 100%);
  color: #0a0a0f;
  box-shadow: 0 0 20px rgba(255, 204, 0, 0.3);
}

.button.primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 0 30px rgba(255, 204, 0, 0.5);
}

.button.secondary {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.6);
}

.button.secondary:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
}

/* Responsive */
@media (max-width: 640px) {
  .modal-content {
    max-height: 90vh;
  }

  .chips-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .chips-input-grid {
    grid-template-columns: 1fr 1fr;
  }

  .modal-body {
    padding: 1rem 1.25rem;
  }
}
</style>
