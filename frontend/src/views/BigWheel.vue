<template>
  <div class="bigwheel-page">
    <!-- 상단 고정 상태 바 -->
    <div class="page-header" :class="getHeaderClass()">
      <div v-if="!gameStarted" class="header-status">빅휠 게임</div>
      <div v-else-if="!bettingClosed" class="header-status">
        <span class="status-dot betting"></span>
        베팅 진행 중
      </div>
      <div v-else-if="!gameResult" class="header-status">
        <span class="status-dot result"></span>
        결과를 입력하세요
      </div>
      <div v-else class="header-status">
        <span class="status-dot complete"></span>
        라운드 완료
      </div>

      <!-- 게임 모드 표시 배지 -->
      <div class="mode-badge" :class="gameMode">
        <span class="mode-icon">{{ gameMode === 'online' ? '🌐' : '📝' }}</span>
        <span class="mode-text">{{ gameMode === 'online' ? '온라인' : '오프라인' }}</span>
      </div>
    </div>

    <!-- 게임 히스토리 바 -->
    <div v-if="gameStarted && gameHistory.length > 0" class="history-bar">
      <div class="history-scroll">
        <div
          v-for="(history, index) in gameHistory"
          :key="index"
          class="history-item"
          :class="{ 'history-win': history.isWin, 'history-lose': !history.isWin }"
        >
          <div class="history-zone">{{ history.zone }}</div>
          <div class="history-amount">
            {{ history.isWin ? '+' : '-' }}{{ history.amount.toLocaleString() }}
          </div>
        </div>
      </div>
    </div>

    <main class="main-content">
      <!-- 게임 시작 버튼 (게임 시작 전에만 표시) -->
      <section v-if="!gameStarted" class="game-start-section">
        <button class="btn-start-game" @click="startGame">게임 시작</button>
      </section>

      <!-- 게임 시작 후 빅휠 섹션 -->
      <section v-else class="game-section">
        <div class="wheel-container">
          <!-- 빅휠 SVG (화려한 네온 스타일) -->
          <svg
            class="wheel-svg"
            width="100"
            height="100"
            viewBox="0 0 200 200"
            xmlns="http://www.w3.org/2000/svg"
          >
            <defs>
              <linearGradient id="neonWheelGradient" x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%" style="stop-color:#ff2d7b;stop-opacity:1" />
                <stop offset="50%" style="stop-color:#9945ff;stop-opacity:1" />
                <stop offset="100%" style="stop-color:#00d9ff;stop-opacity:1" />
              </linearGradient>
              <filter id="wheelGlow">
                <feGaussianBlur stdDeviation="4" result="coloredBlur"/>
                <feMerge>
                  <feMergeNode in="coloredBlur"/>
                  <feMergeNode in="SourceGraphic"/>
                </feMerge>
              </filter>
              <filter id="innerGlow">
                <feGaussianBlur stdDeviation="2" result="coloredBlur"/>
                <feMerge>
                  <feMergeNode in="coloredBlur"/>
                  <feMergeNode in="SourceGraphic"/>
                </feMerge>
              </filter>
            </defs>
            <!-- 외곽 글로우 원 -->
            <circle cx="100" cy="100" r="95" fill="none" stroke="url(#neonWheelGradient)" stroke-width="4" filter="url(#wheelGlow)"/>
            <!-- 중간 원 -->
            <circle cx="100" cy="100" r="75" fill="none" stroke="url(#neonWheelGradient)" stroke-width="2.5" opacity="0.7"/>
            <!-- 내부 원 -->
            <circle cx="100" cy="100" r="55" fill="none" stroke="url(#neonWheelGradient)" stroke-width="1.5" opacity="0.5"/>
            <!-- 십자 구분선 -->
            <line x1="100" y1="5" x2="100" y2="195" stroke="#00d9ff" stroke-width="1.5" opacity="0.8"/>
            <line x1="5" y1="100" x2="195" y2="100" stroke="#ff2d7b" stroke-width="1.5" opacity="0.8"/>
            <!-- 대각선 구분선 -->
            <line x1="30" y1="30" x2="170" y2="170" stroke="#9945ff" stroke-width="1" opacity="0.6"/>
            <line x1="170" y1="30" x2="30" y2="170" stroke="#9945ff" stroke-width="1" opacity="0.6"/>
            <!-- 중앙 그라데이션 원 -->
            <circle cx="100" cy="100" r="18" fill="url(#neonWheelGradient)" filter="url(#innerGlow)"/>
            <!-- 중앙 작은 흰색 원 -->
            <circle cx="100" cy="100" r="6" fill="#fff"/>
          </svg>

          <!-- 게임 결과 표시 (빅휠 왼쪽) -->
          <transition name="slide-left">
            <div v-if="showResult && gameResult" class="result-overlay" @click="hideResult">
              <div class="result-content-compact">
                <div class="result-zone-name">{{ getZoneName(gameResult.zone) }}</div>
                <div class="result-amount">
                  <span v-if="gameResult.isWin" class="amount-win">
                    {{ gameResult.profit >= 0 ? '+' : '' }}{{ gameResult.profit.toLocaleString() }}원
                  </span>
                  <span v-else class="amount-lose">
                    {{ gameResult.profit.toLocaleString() }}원
                  </span>
                </div>
                <div class="tap-hint">탭하여 닫기</div>
              </div>
            </div>
          </transition>
        </div>
      </section>

      <!-- 게임 정보 (게임 시작 후에만 표시) -->
      <section v-if="gameStarted" class="game-info-section">
        <div class="info-cards-row">
          <div class="info-card stats-card">
            <h3>승패 기록</h3>
            <div class="stats-info">
              <div class="stat-item win">
                <span>승</span>
                <strong>{{ gameStats.wins }}</strong>
              </div>
              <div class="stat-item loss">
                <span>패</span>
                <strong>{{ gameStats.losses }}</strong>
              </div>
            </div>
            <div class="stats-total" :class="{ 'profit-positive': gameStats.totalProfit >= 0, 'profit-negative': gameStats.totalProfit < 0 }">
              {{ gameStats.totalProfit >= 0 ? '+' : '' }}{{ gameStats.totalProfit.toLocaleString() }}원
            </div>
          </div>
          <div class="info-card chip-card">
            <h3>칩 정보</h3>
            <div class="chip-info">
              <div class="info-item">
                <span>보유</span>
                <strong>{{ totalCurrentChips.toLocaleString() }}</strong>
              </div>
              <div class="info-item">
                <span>베팅</span>
                <strong>{{ totalBettedAmount.toLocaleString() }}</strong>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 베팅 구역 -->
      <section v-if="gameStarted" class="zones-section">
        <h2 class="section-title">베팅 구역</h2>
        <div class="zones-row">
          <div
            v-for="zone in zones"
            :key="zone.id"
            class="zone-card"
            :class="[zone.id, { 'betting-closed': bettingClosed && !gameResult, 'result-mode': bettingClosed && !gameResult, 'active': selectedZone === zone.id && showChipSelector }]"
            @click="handleZoneClick(zone.id)"
          >
            <div class="zone-icon">{{ zone.icon }}</div>
            <h3>{{ zone.name }}</h3>
            <p class="zone-odds">x{{ zone.multiplier }}</p>
            <div v-if="bets[zone.id] > 0" class="bet-amount">
              {{ bets[zone.id].toLocaleString() }}원
            </div>
          </div>
        </div>

        <!-- 베팅 종료 버튼 (베팅 중일 때만 표시) -->
        <div v-if="!bettingClosed && totalBettedAmount > 0" class="betting-control">
          <button class="btn-cancel-betting" @click="cancelAllBets">베팅 취소</button>
          <button class="btn-end-betting" @click="endBetting">베팅 종료</button>
        </div>

        <!-- 다음 라운드 버튼 (결과 입력 후) -->
        <div v-if="gameResult" class="betting-control">
          <button class="btn-next-round" @click="nextRound">다음 라운드</button>
        </div>

        <!-- 베팅 확률 및 손익 분석 -->
        <div v-if="totalBettedAmount > 0" class="probability-box">
          <h3 class="probability-title">베팅 분석</h3>

          <!-- 7개 구역 가로 배치 (베팅 구역과 1:1 매핑) -->
          <div class="analysis-zones-row">
            <div
              v-for="zone in zones"
              :key="zone.id"
              class="analysis-zone-item"
              :class="[
                zone.id,
                {
                  'has-bet': bets[zone.id] > 0,
                  'is-profit': getZoneAnalysis(zone.id)?.isProfit,
                  'is-loss': getZoneAnalysis(zone.id) && !getZoneAnalysis(zone.id)?.isProfit,
                  'cancel-mode': cancelTargetZone === zone.id
                }
              ]"
              @click="toggleCancelMode(zone.id)"
            >
              <div class="analysis-icon">{{ zone.icon }}</div>
              <div v-if="bets[zone.id] > 0" class="analysis-details">
                <div class="analysis-bet">{{ formatAmount(bets[zone.id]) }}</div>
                <div class="analysis-profit" :class="{ 'positive': getZoneAnalysis(zone.id)?.isProfit, 'negative': !getZoneAnalysis(zone.id)?.isProfit }">
                  {{ getZoneAnalysis(zone.id)?.profit >= 0 ? '+' : '' }}{{ formatAmount(getZoneAnalysis(zone.id)?.profit || 0) }}
                </div>
              </div>
              <div v-else class="analysis-empty">-</div>
              <!-- 취소 버튼 (클릭 시 표시) -->
              <div v-if="cancelTargetZone === zone.id && bets[zone.id] > 0" class="cancel-overlay" @click.stop="cancelZoneBet(zone.id)">
                <span class="cancel-icon">✕</span>
                <span class="cancel-text">취소</span>
              </div>
            </div>
          </div>

          <!-- 수익/손실 확률 & 기대 수익 -->
          <div class="probability-footer">
            <div class="prob-stat profit-stat">
              <span class="stat-label">수익</span>
              <span class="stat-value">{{ profitProbability.toFixed(1) }}%</span>
            </div>
            <div class="prob-stat expected-stat">
              <span class="stat-label">기대값</span>
              <span class="stat-value" :class="{ 'positive': expectedProfit >= 0, 'negative': expectedProfit < 0 }">
                {{ expectedProfit >= 0 ? '+' : '' }}{{ formatAmount(expectedProfit) }}
              </span>
            </div>
            <div class="prob-stat loss-stat">
              <span class="stat-label">손실</span>
              <span class="stat-value">{{ lossProbability.toFixed(1) }}%</span>
            </div>
          </div>
        </div>
      </section>

      <!-- 칩 선택 팝업 (네비게이션 바 위) -->
      <div v-if="showChipSelector" class="chip-selector-overlay" @click="cancelBetting">
        <div class="chip-selector-popup" @click.stop>
          <div class="popup-header">{{ getZoneName(selectedZone) }}</div>
          <div class="chip-icons">
            <div
              v-for="chip in chipTypes"
              :key="chip.value"
              class="chip-icon-btn"
              @click="selectChip(chip.value, chip.key)"
              :class="{
                disabled: !canAddChip(chip.key)
              }"
            >
              <svg width="50" height="50" viewBox="0 0 120 120" xmlns="http://www.w3.org/2000/svg">
                <circle cx="60" cy="60" r="58" :fill="chip.color" :stroke="chip.strokeColor" stroke-width="2"/>
                <circle cx="60" cy="60" r="54" fill="none" :stroke="chip.strokeColor" stroke-width="8" stroke-dasharray="8 4"/>
                <circle cx="60" cy="60" r="38" fill="#FFFFFF"/>
                <text x="60" y="63" font-family="Arial, sans-serif" font-size="16" font-weight="bold" :fill="chip.strokeColor" text-anchor="middle">{{ chip.displayValue }}</text>
                <text v-if="chip.unit" x="60" y="78" font-family="Arial, sans-serif" font-size="12" :fill="chip.strokeColor" text-anchor="middle">{{ chip.unit }}</text>
              </svg>
              <div class="chip-count-badge">{{ currentChips[chip.key] }}</div>
              <div v-if="tempBets[chip.key] > 0" class="selected-count-badge">+{{ tempBets[chip.key] }}</div>
            </div>
          </div>
          <button
            class="btn-confirm-bet"
            @click="confirmBet"
            :disabled="getTotalTempBets() === 0"
          >
            베팅 완료 ({{ getTotalTempBetsAmount().toLocaleString() }}원)
          </button>
        </div>
      </div>

    </main>

    <!-- 온라인 모드: 스핀 휠 모달 -->
    <SpinWheelModal
      :show="showSpinModal"
      :zones="zones"
      @spinComplete="handleSpinComplete"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import SpinWheelModal from '../components/SpinWheelModal.vue'

// 게임 모드 (localStorage에서 로드)
const gameMode = ref('offline') // 'online' | 'offline'

// 스핀 모달 표시 여부
const showSpinModal = ref(false)

// 게임 상태
const gameStarted = ref(false)
const bettingClosed = ref(false)
const gameResult = ref(null) // 게임 결과
const showResult = ref(false) // 결과 표시 여부

// 승패 기록
const gameStats = ref({
  wins: 0,
  losses: 0,
  totalProfit: 0 // 총 손실/수익 금액
})

// 게임 히스토리
const gameHistory = ref([])

// 칩 데이터
const currentChips = ref({
  chip1000: 0,
  chip5000: 0,
  chip10000: 0,
  chip100000: 0,
  chip1000000: 0
})

// 베팅 구역 정의 (slots = 빅휠에서 차지하는 칸 수)
const zones = [
  { id: 'silver', name: 'SILVER', icon: '🥈', multiplier: 2, slots: 25 },
  { id: 'gold', name: 'GOLD', icon: '🥇', multiplier: 3, slots: 15 },
  { id: 'emerald', name: 'EMERALD', icon: '💚', multiplier: 6, slots: 7 },
  { id: 'diamond', name: 'DIAMOND', icon: '💎', multiplier: 11, slots: 4 },
  { id: 'crystal', name: 'CRYSTAL', icon: '🔮', multiplier: 21, slots: 2 },
  { id: 'joker', name: 'JOKER', icon: '🃏', multiplier: 41, slots: 1 },
  { id: 'mega', name: 'MEGA', icon: '⭐', multiplier: 41, slots: 1 }
]

// 전체 칸 수
const totalSlots = 55

// 베팅 현황
const bets = ref({
  silver: 0,
  gold: 0,
  emerald: 0,
  diamond: 0,
  crystal: 0,
  joker: 0,
  mega: 0
})

// 칩 선택 팝업 상태
const showChipSelector = ref(false)
const selectedZone = ref(null)
const tempBets = ref({}) // 임시 베팅 (완료 전)

// 개별 베팅 취소 상태
const cancelTargetZone = ref(null)

// 칩 종류 정의
const chipTypes = [
  { value: 1000, label: '1천원', key: 'chip1000', color: '#E8749B', strokeColor: '#C94A7A', displayValue: '1천', unit: '원' },
  { value: 5000, label: '5천원', key: 'chip5000', color: '#E89F5A', strokeColor: '#C87B3A', displayValue: '5천', unit: '원' },
  { value: 10000, label: '1만원', key: 'chip10000', color: '#5FB57C', strokeColor: '#3D8B5C', displayValue: '1만', unit: '원' },
  { value: 100000, label: '10만원', key: 'chip100000', color: '#2B4B7C', strokeColor: '#1A3A5C', displayValue: '10만', unit: '원' },
  { value: 1000000, label: '100만원', key: 'chip1000000', color: '#4A6B9C', strokeColor: '#3A5A8C', displayValue: '100만', unit: '원' }
]

// 현재 보유한 총 칩 금액
const totalCurrentChips = computed(() => {
  return (
    currentChips.value.chip1000 * 1000 +
    currentChips.value.chip5000 * 5000 +
    currentChips.value.chip10000 * 10000 +
    currentChips.value.chip100000 * 100000 +
    currentChips.value.chip1000000 * 1000000
  )
})

// 총 베팅 금액
const totalBettedAmount = computed(() => {
  return Object.values(bets.value).reduce((sum, bet) => sum + bet, 0)
})

// 베팅한 구역 정보 및 손익 계산
const bettedZonesAnalysis = computed(() => {
  const result = []
  const total = totalBettedAmount.value

  for (const [zoneId, betAmount] of Object.entries(bets.value)) {
    if (betAmount > 0) {
      const zone = zones.find(z => z.id === zoneId)
      if (zone) {
        // 해당 구역 당첨 시 받는 금액
        const winAmount = betAmount + (betAmount * zone.multiplier)
        // 순수익 계산
        const profit = winAmount - total
        // 확률 계산
        const probability = (zone.slots / totalSlots) * 100

        result.push({
          id: zoneId,
          name: zone.name,
          icon: zone.icon,
          probability: probability,
          betAmount: betAmount,
          profit: profit,
          isProfit: profit > 0
        })
      }
    }
  }
  return result
})

// 수익 확률 계산 (순수익이 플러스인 구역들의 확률 합)
const profitProbability = computed(() => {
  return bettedZonesAnalysis.value
    .filter(z => z.isProfit)
    .reduce((sum, z) => sum + z.probability, 0)
})

// 손실 확률 계산 (순수익이 마이너스인 구역들의 확률 합)
const lossProbability = computed(() => {
  return bettedZonesAnalysis.value
    .filter(z => !z.isProfit)
    .reduce((sum, z) => sum + z.probability, 0)
})

// 기대 수익 계산 (각 구역의 확률 × 손익의 합)
const expectedProfit = computed(() => {
  return bettedZonesAnalysis.value
    .reduce((sum, z) => sum + (z.probability / 100) * z.profit, 0)
})

// 구역별 분석 데이터 가져오기
const getZoneAnalysis = (zoneId) => {
  return bettedZonesAnalysis.value.find(z => z.id === zoneId)
}

// 금액 포맷팅 (만원 단위)
const formatAmount = (amount) => {
  const absAmount = Math.abs(amount)
  if (absAmount >= 10000) {
    return Math.floor(amount / 10000) + '만'
  }
  return amount.toLocaleString()
}

// 취소 모드 토글
const toggleCancelMode = (zoneId) => {
  // 베팅이 없는 구역이면 무시
  if (bets.value[zoneId] <= 0) return
  // 베팅 종료 후에는 취소 불가
  if (bettingClosed.value) return

  if (cancelTargetZone.value === zoneId) {
    cancelTargetZone.value = null
  } else {
    cancelTargetZone.value = zoneId
  }
}

// 특정 구역 베팅 취소
const cancelZoneBet = (zoneId) => {
  const amount = bets.value[zoneId]
  if (amount <= 0) return

  // 베팅 금액을 칩으로 환불
  let remainingAmount = amount
  const chipValues = [
    { key: 'chip1000000', value: 1000000 },
    { key: 'chip100000', value: 100000 },
    { key: 'chip10000', value: 10000 },
    { key: 'chip5000', value: 5000 },
    { key: 'chip1000', value: 1000 }
  ]

  for (const chip of chipValues) {
    if (remainingAmount >= chip.value) {
      const count = Math.floor(remainingAmount / chip.value)
      currentChips.value[chip.key] += count
      remainingAmount -= count * chip.value
    }
  }

  // 베팅 초기화
  bets.value[zoneId] = 0
  cancelTargetZone.value = null

  // localStorage에 칩 정보 저장
  localStorage.setItem('userChips', JSON.stringify(currentChips.value))
}

// 게임 시작
const startGame = () => {
  // localStorage에서 칩 데이터 가져오기
  const savedChips = localStorage.getItem('userChips')
  if (savedChips) {
    currentChips.value = JSON.parse(savedChips)
  }

  if (totalCurrentChips.value === 0) {
    alert('칩을 먼저 입력해주세요!')
    return
  }

  gameStarted.value = true
}

// 돌림판 완료 핸들러 (온라인 모드)
const handleSpinComplete = (zoneId) => {
  showSpinModal.value = false
  inputResult(zoneId)
}

// 베팅 구역 클릭 처리
const handleZoneClick = (zoneId) => {
  // 결과가 이미 입력된 경우 무시
  if (gameResult.value) return

  // 온라인 모드에서는 베팅 종료 후 수동 입력 불가
  if (gameMode.value === 'online' && bettingClosed.value) return

  // 베팅 종료 후 - 결과 입력 모드 (오프라인 모드만)
  if (bettingClosed.value) {
    inputResult(zoneId)
    return
  }

  // 베팅 중 - 칩 선택 팝업
  openChipSelector(zoneId)
}

// 칩 선택 팝업 열기
const openChipSelector = (zoneId) => {
  selectedZone.value = zoneId
  tempBets.value = {}
  showChipSelector.value = true
}

// 칩 선택 팝업 닫기
const closeChipSelector = () => {
  showChipSelector.value = false
  selectedZone.value = null
  tempBets.value = {}
}

// 베팅 취소
const cancelBetting = () => {
  closeChipSelector()
}

// 구역 이름 가져오기
const getZoneName = (zoneId) => {
  const zone = zones.find(z => z.id === zoneId)
  return zone ? zone.name : ''
}

// 헤더 클래스 가져오기
const getHeaderClass = () => {
  if (!gameStarted.value) return 'header-default'
  if (!bettingClosed.value) return 'header-betting'
  if (!gameResult.value) return 'header-result'
  return 'header-complete'
}

// 칩 추가 가능 여부 확인
const canAddChip = (chipKey) => {
  const usedCount = tempBets.value[chipKey] || 0
  return currentChips.value[chipKey] > usedCount
}

// 칩 선택하기 (베팅 완료 전)
const selectChip = (chipValue, chipKey) => {
  // 칩이 더 있는지 확인
  if (!canAddChip(chipKey)) {
    return
  }

  // 임시 베팅에 추가
  if (!tempBets.value[chipKey]) {
    tempBets.value[chipKey] = 0
  }
  tempBets.value[chipKey]++
}

// 총 임시 베팅 개수
const getTotalTempBets = () => {
  return Object.values(tempBets.value).reduce((sum, count) => sum + count, 0)
}

// 총 임시 베팅 금액
const getTotalTempBetsAmount = () => {
  let total = 0
  for (const [chipKey, count] of Object.entries(tempBets.value)) {
    const chipValue = parseInt(chipKey.replace('chip', ''))
    total += chipValue * count
  }
  return total
}

// 베팅 완료
const confirmBet = () => {
  if (getTotalTempBets() === 0) return

  // 모든 임시 베팅을 실제 베팅으로 반영
  for (const [chipKey, count] of Object.entries(tempBets.value)) {
    // 칩 차감
    currentChips.value[chipKey] -= count

    // 베팅 금액 추가
    const chipValue = parseInt(chipKey.replace('chip', ''))
    bets.value[selectedZone.value] += chipValue * count
  }

  // 팝업 닫기
  closeChipSelector()
}

// 베팅 취소
const cancelAllBets = () => {
  // 모든 베팅 금액을 칩으로 환불
  for (const [zoneId, amount] of Object.entries(bets.value)) {
    if (amount > 0) {
      // 각 칩 타입별로 환불 처리
      let remainingAmount = amount

      // 큰 칩부터 환불
      const chipValues = [
        { key: 'chip1000000', value: 1000000 },
        { key: 'chip100000', value: 100000 },
        { key: 'chip10000', value: 10000 },
        { key: 'chip5000', value: 5000 },
        { key: 'chip1000', value: 1000 }
      ]

      for (const chip of chipValues) {
        if (remainingAmount >= chip.value) {
          const count = Math.floor(remainingAmount / chip.value)
          currentChips.value[chip.key] += count
          remainingAmount -= count * chip.value
        }
      }

      // 베팅 초기화
      bets.value[zoneId] = 0
    }
  }

  // localStorage에 칩 정보 저장
  localStorage.setItem('userChips', JSON.stringify(currentChips.value))
}

// 베팅 종료
const endBetting = () => {
  if (totalBettedAmount.value === 0) {
    alert('베팅을 먼저 진행해주세요!')
    return
  }

  bettingClosed.value = true

  // 온라인 모드: 스핀 모달 열기
  if (gameMode.value === 'online') {
    showSpinModal.value = true
  }
}

// 결과 입력
const inputResult = (zoneId) => {
  const zone = zones.find(z => z.id === zoneId)
  if (!zone) return

  // 해당 구역에 베팅한 금액
  const betAmount = bets.value[zoneId]

  // 당첨 금액 계산 (원금 + 배당금)
  const winAmount = betAmount + (betAmount * zone.multiplier)

  // 순수익 계산 (통일된 계산 로직)
  const profit = betAmount > 0 ? (winAmount - totalBettedAmount.value) : (-totalBettedAmount.value)
  const isWin = betAmount > 0

  // 결과 저장
  gameResult.value = {
    zone: zoneId,
    winAmount: winAmount,
    betAmount: betAmount,
    multiplier: zone.multiplier,
    profit: profit,  // 순수익 저장
    isWin: isWin
  }

  // 당첨금 지급 (칩으로 환산)
  if (isWin) {
    addChipsFromWinnings(winAmount)
    gameStats.value.wins++
    gameStats.value.totalProfit += profit
  } else {
    gameStats.value.losses++
    gameStats.value.totalProfit += profit  // profit은 음수
  }

  // 히스토리에 추가 (순수익 사용)
  gameHistory.value.push({
    zone: zone.name,
    isWin: isWin,
    amount: Math.abs(profit)  // 절댓값으로 표시
  })

  // 결과 표시
  showResult.value = true
}

// 결과 클릭 시 숨기기
const hideResult = () => {
  showResult.value = false
}

// 당첨금을 칩으로 환산해서 지급
const addChipsFromWinnings = (amount) => {
  let remaining = amount

  // 큰 단위부터 차례로 칩 추가
  const chipOrder = [
    { key: 'chip1000000', value: 1000000 },
    { key: 'chip100000', value: 100000 },
    { key: 'chip10000', value: 10000 },
    { key: 'chip5000', value: 5000 },
    { key: 'chip1000', value: 1000 }
  ]

  for (const chip of chipOrder) {
    const count = Math.floor(remaining / chip.value)
    if (count > 0) {
      currentChips.value[chip.key] += count
      remaining -= count * chip.value
    }
  }
}

// 다음 라운드
const nextRound = () => {
  // 베팅 초기화
  bets.value = {
    silver: 0,
    gold: 0,
    emerald: 0,
    diamond: 0,
    crystal: 0,
    joker: 0,
    mega: 0
  }

  // 상태 초기화
  bettingClosed.value = false
  gameResult.value = null
  showResult.value = false
}

onMounted(() => {
  // 페이지 로드 시 게임 모드 로드
  const savedMode = localStorage.getItem('bigwheelGameMode')
  if (savedMode) {
    gameMode.value = savedMode
  }

  // 페이지 로드 시 칩 데이터 확인
  const savedChips = localStorage.getItem('userChips')
  if (savedChips) {
    currentChips.value = JSON.parse(savedChips)
  }
})
</script>

<style scoped>
.bigwheel-page {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: linear-gradient(180deg, #0d1117 0%, #0a1628 100%);
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 0 0.35rem;
  overflow: hidden;
  min-width: 0;
}

.page-header {
  position: sticky;
  top: 0;
  z-index: 100;
  padding: 0.3rem 0.75rem;
  transition: all 0.3s;
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  min-height: 32px;
}

.page-header.header-default {
  background: linear-gradient(135deg, rgba(255, 45, 123, 0.9) 0%, rgba(153, 69, 255, 0.9) 100%);
  box-shadow: 0 0 20px rgba(255, 45, 123, 0.3);
}

.page-header.header-betting {
  background: linear-gradient(135deg, rgba(0, 217, 255, 0.9) 0%, rgba(153, 69, 255, 0.9) 100%);
  box-shadow: 0 0 20px rgba(0, 217, 255, 0.3);
}

.page-header.header-result {
  background: linear-gradient(135deg, rgba(0, 255, 136, 0.9) 0%, rgba(0, 217, 255, 0.9) 100%);
  box-shadow: 0 0 25px rgba(0, 255, 136, 0.4);
  animation: pulseHeader 2s infinite;
}

.page-header.header-complete {
  background: linear-gradient(135deg, rgba(153, 69, 255, 0.9) 0%, rgba(255, 45, 123, 0.9) 100%);
  box-shadow: 0 0 20px rgba(153, 69, 255, 0.3);
}

@keyframes pulseHeader {
  0%, 100% {
    box-shadow: 0 0 20px rgba(0, 255, 136, 0.3);
  }
  50% {
    box-shadow: 0 0 40px rgba(0, 255, 136, 0.6), 0 0 60px rgba(0, 255, 136, 0.3);
  }
}

.header-status {
  grid-column: 2;
  text-align: center;
  font-size: 0.8rem;
  font-weight: 700;
  margin: 0;
  color: var(--white);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.35rem;
}

/* 모드 배지 */
.mode-badge {
  grid-column: 3;
  justify-self: end;
  display: flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.25rem 0.5rem;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  font-size: 0.65rem;
  font-weight: 600;
  color: var(--white);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.mode-icon {
  font-size: 0.75rem;
}

.mode-text {
  white-space: nowrap;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  display: inline-block;
}

.status-dot.betting {
  background: #60a5fa;
  animation: blink 1.5s infinite;
}

.status-dot.result {
  background: #34d399;
  animation: blink 1s infinite;
}

.status-dot.complete {
  background: #a78bfa;
}

@keyframes blink {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.3;
  }
}

/* 게임 히스토리 바 */
.history-bar {
  position: sticky;
  top: 32px;
  background: rgba(13, 17, 23, 0.95);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  padding: 0.2rem 0.35rem;
  z-index: 99;
  overflow: hidden;
  max-height: 44px;
}

.history-scroll {
  display: flex;
  gap: 0.25rem;
  overflow-x: auto;
  overflow-y: hidden;
  padding-bottom: 0.15rem;
  scrollbar-width: thin;
  scrollbar-color: var(--border) transparent;
}

.history-scroll::-webkit-scrollbar {
  height: 4px;
}

.history-scroll::-webkit-scrollbar-thumb {
  background: var(--border);
  border-radius: 2px;
}

.history-scroll::-webkit-scrollbar-track {
  background: transparent;
}

.history-item {
  flex-shrink: 0;
  min-width: 60px;
  padding: 0.2rem 0.35rem;
  border-radius: 6px;
  font-size: 0.55rem;
  font-weight: 600;
  text-align: center;
  transition: all 0.2s;
  animation: slideIn 0.3s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-10px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.history-item.history-win {
  background: rgba(0, 255, 136, 0.1);
  border: 1.5px solid #00ff88;
  box-shadow: 0 0 10px rgba(0, 255, 136, 0.2);
}

.history-item.history-lose {
  background: rgba(255, 51, 102, 0.1);
  border: 1.5px solid #ff3366;
  box-shadow: 0 0 10px rgba(255, 51, 102, 0.2);
}

.history-zone {
  font-size: 0.5rem;
  font-weight: 700;
  margin-bottom: 0;
  white-space: nowrap;
}

.history-item.history-win .history-zone {
  color: #059669;
}

.history-item.history-lose .history-zone {
  color: #dc2626;
}

.history-amount {
  font-size: 0.55rem;
  font-weight: 700;
}

.history-item.history-win .history-amount {
  color: #10b981;
}

.history-item.history-lose .history-amount {
  color: #ef4444;
}

.section-title {
  font-size: 0.65rem;
  font-weight: 700;
  margin-bottom: 0.2rem;
  text-align: center;
  color: #fff;
}

/* 게임 시작 섹션 */
.game-start-section {
  text-align: center;
  padding: 30vh 0 3rem 0;
  min-height: 40vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-start-game {
  padding: 1.2rem 3rem;
  background: linear-gradient(135deg, #ff2d7b 0%, #9945ff 100%);
  color: #fff;
  border: none;
  border-radius: 14px;
  font-size: 1.25rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 0 30px rgba(255, 45, 123, 0.5);
  animation: neon-pulse-btn 2s ease-in-out infinite;
}

@keyframes neon-pulse-btn {
  0%, 100% {
    box-shadow: 0 0 20px rgba(255, 45, 123, 0.4);
  }
  50% {
    box-shadow: 0 0 40px rgba(255, 45, 123, 0.6), 0 0 60px rgba(255, 45, 123, 0.3);
  }
}

.btn-start-game:hover {
  transform: translateY(-4px) scale(1.02);
  box-shadow: 0 0 50px rgba(255, 45, 123, 0.6), 0 0 80px rgba(255, 45, 123, 0.3);
}

/* 게임 섹션 */
.game-section {
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 10px;
  padding: 0.35rem;
  margin-bottom: 0.35rem;
  text-align: center;
  position: relative;
  overflow: visible;
}

.wheel-container {
  margin: 0.15rem auto;
  position: relative;
  width: 100px;
  height: 100px;
  display: inline-block;
}

.wheel-svg {
  width: 100px;
  height: 100px;
  animation: wheelSpin 20s linear infinite;
  filter: drop-shadow(0 0 15px rgba(153, 69, 255, 0.5));
}

@keyframes wheelSpin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 게임 정보 섹션 */
.game-info-section {
  margin-bottom: 0.35rem;
}

.info-cards-row {
  display: flex;
  gap: 0.35rem;
}

.info-card {
  flex: 1;
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 8px;
  padding: 0.35rem;
}

.info-card h3 {
  font-size: 0.65rem;
  font-weight: 700;
  margin-bottom: 0.25rem;
  text-align: center;
  color: rgba(255, 255, 255, 0.8);
}

.chip-info, .stats-info {
  display: flex;
  gap: 0.25rem;
  justify-content: center;
}

.info-item, .stat-item {
  flex: 1;
  padding: 0.25rem;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 6px;
  text-align: center;
}

.stat-item.win {
  background: rgba(0, 255, 136, 0.1);
  border-color: rgba(0, 255, 136, 0.3);
}

.stat-item.loss {
  background: rgba(255, 51, 102, 0.1);
  border-color: rgba(255, 51, 102, 0.3);
}

.info-item span, .stat-item span {
  display: block;
  font-size: 0.5rem;
  color: rgba(255, 255, 255, 0.5);
  margin-bottom: 0.1rem;
}

.info-item strong {
  display: block;
  font-size: 0.7rem;
  color: #ff2d7b;
}

.stat-item.win strong {
  display: block;
  font-size: 0.65rem;
  color: #00ff88;
  font-weight: 700;
}

.stat-item.loss strong {
  display: block;
  font-size: 0.65rem;
  color: #ff3366;
  font-weight: 700;
}

.stats-total {
  margin-top: 0.25rem;
  padding-top: 0.25rem;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  text-align: center;
  font-size: 0.65rem;
  font-weight: 700;
}

.stats-total.profit-positive {
  color: #00ff88;
  text-shadow: 0 0 10px rgba(0, 255, 136, 0.5);
}

.stats-total.profit-negative {
  color: #ff3366;
  text-shadow: 0 0 10px rgba(255, 51, 102, 0.5);
}

/* 베팅 컨트롤 */
.betting-control {
  display: flex;
  gap: 0.35rem;
  align-items: center;
  margin-top: 0.5rem;
  padding-top: 0.5rem;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.btn-cancel-betting {
  padding: 0.5rem 0.75rem;
  background: transparent;
  color: rgba(255, 255, 255, 0.5);
  border: 1.5px solid rgba(255, 255, 255, 0.15);
  border-radius: 8px;
  font-size: 0.75rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
}

.btn-cancel-betting:hover {
  border-color: #ff3366;
  color: #ff3366;
  background: rgba(255, 51, 102, 0.1);
  box-shadow: 0 0 15px rgba(255, 51, 102, 0.3);
}

.btn-end-betting {
  flex: 1;
  padding: 0.5rem 1rem;
  background: linear-gradient(135deg, #9945ff 0%, #00d9ff 100%);
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 0.85rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 0 15px rgba(153, 69, 255, 0.4);
}

.btn-end-betting:hover {
  transform: translateY(-2px);
  box-shadow: 0 0 30px rgba(153, 69, 255, 0.6), 0 0 50px rgba(0, 217, 255, 0.3);
}

/* 베팅 구역 */
.zones-section {
  flex: 1;
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 10px;
  padding: 0.35rem;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-width: 0;
}

.zones-row {
  display: flex;
  justify-content: center;
  gap: 0.2rem;
  padding: 0.2rem 0;
  flex-wrap: nowrap;
}

.zone-card {
  background: rgba(10, 10, 15, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 6px;
  padding: 0.2rem 0.15rem;
  text-align: center;
  transition: all 0.3s;
  cursor: pointer;
  flex: 1;
  min-width: 38px;
  max-width: 48px;
  position: relative;
  transform: translateY(0);
}

.zone-card:hover:not(.betting-closed):not(.active) {
  transform: translateY(-4px) scale(1.05);
  z-index: 10;
}

/* 각 구역별 호버 색상 */
.zone-card.silver:hover:not(.betting-closed):not(.active) {
  box-shadow: 0 0 15px rgba(192, 192, 192, 0.5);
  border-color: #c0c0c0;
}
.zone-card.gold:hover:not(.betting-closed):not(.active) {
  box-shadow: 0 0 15px rgba(255, 215, 0, 0.5);
  border-color: #ffd700;
}
.zone-card.emerald:hover:not(.betting-closed):not(.active) {
  box-shadow: 0 0 15px rgba(0, 255, 136, 0.5);
  border-color: #00ff88;
}
.zone-card.diamond:hover:not(.betting-closed):not(.active) {
  box-shadow: 0 0 15px rgba(0, 217, 255, 0.5);
  border-color: #00d9ff;
}
.zone-card.crystal:hover:not(.betting-closed):not(.active) {
  box-shadow: 0 0 15px rgba(153, 69, 255, 0.5);
  border-color: #9945ff;
}
.zone-card.joker:hover:not(.betting-closed):not(.active) {
  box-shadow: 0 0 15px rgba(255, 51, 102, 0.5);
  border-color: #ff3366;
}
.zone-card.mega:hover:not(.betting-closed):not(.active) {
  box-shadow: 0 0 15px rgba(255, 204, 0, 0.5);
  border-color: #ffcc00;
}

.zone-card.active {
  transform: translateY(-8px) scale(1.08);
  box-shadow: 0 0 25px rgba(255, 45, 123, 0.5);
  border-color: #ff2d7b;
  z-index: 10;
}

.zone-card.betting-closed {
  opacity: 0.5;
  filter: grayscale(40%);
}

.zone-card.result-mode {
  cursor: pointer;
  transition: all 0.2s;
}

.zone-card.result-mode:hover {
  transform: translateY(-4px) scale(1.05);
  box-shadow: 0 0 15px rgba(0, 217, 255, 0.4);
  border-color: #00d9ff;
  opacity: 1;
  filter: grayscale(0%);
}

.zone-icon {
  font-size: 0.85rem;
  margin-bottom: 0;
  line-height: 1;
}

.zone-card h3 {
  font-size: 0.4rem;
  font-weight: 700;
  margin-bottom: 0;
  line-height: 1.1;
  color: rgba(255, 255, 255, 0.9);
}

.zone-odds {
  font-size: 0.45rem;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.6);
}

.bet-amount {
  margin-top: 0.15rem;
  padding: 0.1rem;
  background: rgba(255, 45, 123, 0.15);
  border-radius: 3px;
  font-size: 0.4rem;
  font-weight: 700;
  color: #ff2d7b;
}

.zone-card.silver { border-top: 3px solid #c0c0c0; }
.zone-card.gold { border-top: 3px solid #ffd700; }
.zone-card.emerald { border-top: 3px solid #00ff88; }
.zone-card.diamond { border-top: 3px solid #00d9ff; }
.zone-card.crystal { border-top: 3px solid #9945ff; }
.zone-card.joker { border-top: 3px solid #ff3366; }
.zone-card.mega { border-top: 3px solid #ffcc00; }

/* 칩 선택 팝업 */
.chip-selector-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  z-index: 1000;
  padding-bottom: 80px; /* 네비게이션 바 높이만큼 */
}

.chip-selector-popup {
  background: rgba(13, 17, 23, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 20px 20px 0 0;
  padding: 1rem 1.25rem;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 -10px 40px rgba(0, 0, 0, 0.5), 0 0 30px rgba(153, 69, 255, 0.2);
  animation: slideUp 0.15s ease-out;
}

@keyframes slideUp {
  from {
    transform: translateY(100%);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.popup-header {
  text-align: center;
  font-size: 0.8rem;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.6);
  margin-bottom: 0.75rem;
}

.chip-icons {
  display: flex;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.25rem 0;
  margin-bottom: 0.75rem;
}

.chip-icon-btn {
  position: relative;
  cursor: pointer;
  transition: all 0.15s ease-out;
  opacity: 1;
  border-radius: 50%;
  padding: 3px;
}

.chip-icon-btn:active:not(.disabled) {
  transform: translateY(-8px) scale(1.05);
  transition: all 0.1s ease-in;
}

.chip-icon-btn.disabled {
  opacity: 0.3;
  cursor: not-allowed;
  filter: grayscale(100%);
}

.chip-count-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  background: linear-gradient(135deg, #ff2d7b 0%, #9945ff 100%);
  color: #fff;
  border-radius: 50%;
  width: 18px;
  height: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.65rem;
  font-weight: 700;
  box-shadow: 0 0 10px rgba(255, 45, 123, 0.5);
}

.selected-count-badge {
  position: absolute;
  bottom: -4px;
  left: -4px;
  background: #00ff88;
  color: #0a0a0f;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.7rem;
  font-weight: 700;
  box-shadow: 0 0 10px rgba(0, 255, 136, 0.5);
  animation: popIn 0.2s ease-out;
}

@keyframes popIn {
  0% {
    transform: scale(0);
  }
  50% {
    transform: scale(1.2);
  }
  100% {
    transform: scale(1);
  }
}

.btn-confirm-bet {
  width: 100%;
  padding: 0.625rem;
  background: linear-gradient(135deg, #ff2d7b 0%, #9945ff 100%);
  color: #fff;
  border: none;
  border-radius: 10px;
  font-size: 0.9rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 0 20px rgba(255, 45, 123, 0.4);
}

.btn-confirm-bet:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 0 30px rgba(255, 45, 123, 0.6);
}

.btn-confirm-bet:disabled {
  opacity: 0.4;
  cursor: not-allowed;
  box-shadow: none;
}

/* 결과 표시 오버레이 (빅휠 왼쪽) */
.result-overlay {
  position: absolute;
  right: calc(100% + 6px);
  top: 50%;
  transform: translateY(-50%);
  width: 85px;
  z-index: 50;
  cursor: pointer;
}

.result-content-compact {
  background: linear-gradient(135deg, rgba(153, 69, 255, 0.95) 0%, rgba(0, 217, 255, 0.95) 100%);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 8px;
  padding: 0.4rem 0.35rem;
  box-shadow: 0 0 15px rgba(153, 69, 255, 0.5);
  text-align: center;
}

.result-zone-name {
  font-size: 0.6rem;
  font-weight: 700;
  color: #fff;
  margin-bottom: 0.2rem;
  text-shadow: 0 0 8px rgba(255, 255, 255, 0.5);
}

.result-amount {
  margin: 0;
}

.amount-win {
  font-size: 0.7rem;
  font-weight: 700;
  color: #00ff88;
  text-shadow: 0 0 8px rgba(0, 255, 136, 0.5);
}

.amount-lose {
  font-size: 0.7rem;
  font-weight: 700;
  color: #ff6b6b;
  text-shadow: 0 0 8px rgba(255, 107, 107, 0.5);
}

.tap-hint {
  margin-top: 0.25rem;
  font-size: 0.5rem;
  color: rgba(255, 255, 255, 0.7);
  font-weight: 500;
}

/* 왼쪽으로 슬라이드 애니메이션 */
.slide-left-enter-active {
  transition: all 0.3s ease-out;
}

.slide-left-leave-active {
  transition: all 0.3s ease-in;
}

.slide-left-enter-from {
  transform: translateY(-50%) translateX(20px);
  opacity: 0;
}

.slide-left-leave-to {
  transform: translateY(-50%) translateX(-50px);
  opacity: 0;
}

.btn-next-round {
  width: 100%;
  padding: 0.5rem;
  background: linear-gradient(135deg, #00d9ff 0%, #9945ff 100%);
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 0.85rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 0 15px rgba(0, 217, 255, 0.4);
}

.btn-next-round:hover {
  transform: translateY(-2px);
  box-shadow: 0 0 30px rgba(0, 217, 255, 0.6), 0 0 50px rgba(153, 69, 255, 0.3);
}

/* 베팅 확률 및 손익 분석 */
.probability-box {
  margin-top: 0.35rem;
  padding: 0.35rem;
  background: rgba(153, 69, 255, 0.08);
  border: 1px solid rgba(153, 69, 255, 0.3);
  border-radius: 6px;
}

.probability-title {
  font-size: 0.6rem;
  font-weight: 700;
  text-align: center;
  color: #9945ff;
  margin-bottom: 0.25rem;
}

/* 7개 구역 가로 배치 (베팅 구역과 1:1 매핑) */
.analysis-zones-row {
  display: flex;
  gap: 0.15rem;
  margin-bottom: 0.3rem;
}

.analysis-zone-item {
  flex: 1;
  min-width: 0;
  padding: 0.2rem 0.1rem;
  background: rgba(10, 10, 15, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 4px;
  text-align: center;
  position: relative;
  transition: all 0.2s;
  cursor: pointer;
  opacity: 0.4;
}

.analysis-zone-item.has-bet {
  opacity: 1;
}

.analysis-zone-item.has-bet:hover {
  transform: translateY(-2px);
}

/* 구역별 테두리 색상 (베팅 구역과 동일) */
.analysis-zone-item.silver.has-bet { border-color: #c0c0c0; box-shadow: 0 0 8px rgba(192, 192, 192, 0.3); }
.analysis-zone-item.gold.has-bet { border-color: #ffd700; box-shadow: 0 0 8px rgba(255, 215, 0, 0.3); }
.analysis-zone-item.emerald.has-bet { border-color: #00ff88; box-shadow: 0 0 8px rgba(0, 255, 136, 0.3); }
.analysis-zone-item.diamond.has-bet { border-color: #00d9ff; box-shadow: 0 0 8px rgba(0, 217, 255, 0.3); }
.analysis-zone-item.crystal.has-bet { border-color: #9945ff; box-shadow: 0 0 8px rgba(153, 69, 255, 0.3); }
.analysis-zone-item.joker.has-bet { border-color: #ff3366; box-shadow: 0 0 8px rgba(255, 51, 102, 0.3); }
.analysis-zone-item.mega.has-bet { border-color: #ffcc00; box-shadow: 0 0 8px rgba(255, 204, 0, 0.3); }

/* 손익에 따른 상단 강조 */
.analysis-zone-item.is-profit { border-top: 2px solid #00ff88; }
.analysis-zone-item.is-loss { border-top: 2px solid #ff3366; }

.analysis-icon {
  font-size: 0.7rem;
  line-height: 1;
  margin-bottom: 0.1rem;
}

.analysis-details {
  display: flex;
  flex-direction: column;
  gap: 0.05rem;
}

.analysis-bet {
  font-size: 0.45rem;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.9);
}

.analysis-profit {
  font-size: 0.4rem;
  font-weight: 700;
}

.analysis-profit.positive {
  color: #00ff88;
}

.analysis-profit.negative {
  color: #ff3366;
}

.analysis-empty {
  font-size: 0.5rem;
  color: rgba(255, 255, 255, 0.3);
}

/* 취소 오버레이 */
.cancel-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 51, 102, 0.9);
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.1rem;
  animation: fadeIn 0.15s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.cancel-icon {
  font-size: 0.7rem;
  font-weight: 700;
  color: #fff;
}

.cancel-text {
  font-size: 0.4rem;
  font-weight: 600;
  color: #fff;
}

.analysis-zone-item.cancel-mode {
  transform: scale(1.05);
  z-index: 5;
}

/* 수익/손실 확률 & 기대 수익 푸터 */
.probability-footer {
  display: flex;
  gap: 0.2rem;
  padding-top: 0.25rem;
  border-top: 1px solid rgba(153, 69, 255, 0.2);
}

.prob-stat {
  flex: 1;
  padding: 0.15rem 0.1rem;
  border-radius: 4px;
  text-align: center;
}

.stat-label {
  display: block;
  font-size: 0.4rem;
  font-weight: 600;
  margin-bottom: 0.05rem;
}

.stat-value {
  display: block;
  font-size: 0.55rem;
  font-weight: 700;
}

.profit-stat {
  background: rgba(0, 255, 136, 0.1);
  border: 1px solid rgba(0, 255, 136, 0.2);
}

.profit-stat .stat-label {
  color: rgba(0, 255, 136, 0.7);
}

.profit-stat .stat-value {
  color: #00ff88;
}

.loss-stat {
  background: rgba(255, 51, 102, 0.1);
  border: 1px solid rgba(255, 51, 102, 0.2);
}

.loss-stat .stat-label {
  color: rgba(255, 51, 102, 0.7);
}

.loss-stat .stat-value {
  color: #ff3366;
}

.expected-stat {
  background: rgba(153, 69, 255, 0.1);
  border: 1px solid rgba(153, 69, 255, 0.2);
}

.expected-stat .stat-label {
  color: rgba(153, 69, 255, 0.8);
}

.expected-stat .stat-value {
  color: #9945ff;
}

.expected-stat .stat-value.positive {
  color: #00ff88;
}

.expected-stat .stat-value.negative {
  color: #ff3366;
}
</style>
