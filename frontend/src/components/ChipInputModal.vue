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
              <input
                type="number"
                v-model.number="addChips[chipType]"
                min="0"
                placeholder="0"
              />
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
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
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
  border-radius: 20px;
  width: 100%;
  max-width: 420px;
  max-height: 85vh;
  overflow-y: auto;
  box-shadow: 0 0 60px rgba(0, 0, 0, 0.5), 0 0 30px rgba(255, 204, 0, 0.15);
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
  padding: 1.25rem 1.5rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  position: sticky;
  top: 0;
  background: rgba(13, 17, 23, 0.98);
  backdrop-filter: blur(20px);
  z-index: 1;
  border-radius: 20px 20px 0 0;
}

.modal-header h2 {
  margin: 0;
  font-size: 1.1rem;
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
  padding: 1.25rem 1.5rem;
}

.current-chips-section,
.add-chips-section,
.preview-section {
  margin-bottom: 1.5rem;
}

.current-chips-section h3,
.add-chips-section h3,
.preview-section h3 {
  font-size: 0.85rem;
  font-weight: 600;
  margin-bottom: 12px;
  color: rgba(255, 255, 255, 0.5);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.loading {
  text-align: center;
  padding: 20px;
  color: rgba(255, 255, 255, 0.4);
  font-size: 0.85rem;
}

.chips-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(100px, 1fr));
  gap: 10px;
}

.chip-display {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.08);
  padding: 12px;
  border-radius: 12px;
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
  font-size: 0.7rem;
  color: rgba(255, 255, 255, 0.4);
  margin-bottom: 6px;
  font-weight: 500;
}

.chip-count {
  display: block;
  font-size: 1.1rem;
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
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 12px;
}

.chip-input label {
  display: block;
  font-size: 0.75rem;
  margin-bottom: 6px;
  color: rgba(255, 255, 255, 0.5);
  font-weight: 500;
}

.chip-input input {
  width: 100%;
  padding: 10px 12px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  font-size: 0.95rem;
  color: #fff;
  transition: all 0.2s;
}

.chip-input input::placeholder {
  color: rgba(255, 255, 255, 0.2);
}

.chip-input input:focus {
  outline: none;
  border-color: #ffcc00;
  box-shadow: 0 0 15px rgba(255, 204, 0, 0.2);
  background: rgba(255, 204, 0, 0.05);
}

.modal-footer {
  display: flex;
  gap: 10px;
  padding: 1.25rem 1.5rem;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  position: sticky;
  bottom: 0;
  background: rgba(13, 17, 23, 0.98);
  backdrop-filter: blur(20px);
  border-radius: 0 0 20px 20px;
}

.button {
  flex: 1;
  padding: 12px;
  border: none;
  border-radius: 10px;
  font-size: 0.9rem;
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
