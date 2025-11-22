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
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 16px;
  width: 90%;
  max-width: 650px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e0e0e0;
  position: sticky;
  top: 0;
  background: white;
  z-index: 1;
  border-radius: 16px 16px 0 0;
}

.modal-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #1a1a1a;
}

.close-button {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  background: #f5f5f5;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.2s;
  color: #666;
}

.close-button:hover {
  background: #ef4444;
  color: white;
}

.modal-body {
  padding: 24px;
}

.current-chips-section,
.add-chips-section,
.preview-section {
  margin-bottom: 24px;
}

.current-chips-section h3,
.add-chips-section h3,
.preview-section h3 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 12px;
  color: #666;
}

.loading {
  text-align: center;
  padding: 20px;
  color: #999;
}

.chips-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(130px, 1fr));
  gap: 12px;
}

.chip-display {
  background: #f5f5f5;
  padding: 16px;
  border-radius: 12px;
  text-align: center;
  transition: all 0.2s;
}

.chip-display:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.chip-display.preview {
  background: rgba(16, 185, 129, 0.1);
  border: 2px solid #10b981;
}

.chip-type {
  display: block;
  font-size: 13px;
  color: #666;
  margin-bottom: 8px;
  font-weight: 500;
}

.chip-count {
  display: block;
  font-size: 20px;
  font-weight: 700;
  color: #1a1a1a;
}

.chips-input-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 16px;
}

.chip-input label {
  display: block;
  font-size: 14px;
  margin-bottom: 8px;
  color: #666;
  font-weight: 500;
}

.chip-input input {
  width: 100%;
  padding: 12px;
  border: 2px solid #e0e0e0;
  border-radius: 10px;
  font-size: 16px;
  transition: all 0.2s;
}

.chip-input input:focus {
  outline: none;
  border-color: #6366f1;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
}

.modal-footer {
  display: flex;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #e0e0e0;
  position: sticky;
  bottom: 0;
  background: white;
  border-radius: 0 0 16px 16px;
}

.button {
  flex: 1;
  padding: 14px;
  border: none;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.button.primary {
  background: #6366f1;
  color: white;
}

.button.primary:hover {
  background: #4f46e5;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
}

.button.secondary {
  background: #f5f5f5;
  color: #666;
}

.button.secondary:hover {
  background: #e0e0e0;
}

/* Responsive */
@media (max-width: 640px) {
  .chips-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .chips-input-grid {
    grid-template-columns: 1fr;
  }
}
</style>
