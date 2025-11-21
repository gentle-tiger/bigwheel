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
          <!-- 빅휠 SVG (항상 표시) -->
          <svg
            width="300"
            height="300"
            viewBox="0 0 200 200"
            xmlns="http://www.w3.org/2000/svg"
          >
            <circle cx="100" cy="100" r="95" fill="url(#wheelGradient)" stroke="#333" stroke-width="3"/>
            <defs>
              <linearGradient id="wheelGradient" x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%" style="stop-color:#6366f1;stop-opacity:1" />
                <stop offset="100%" style="stop-color:#8b5cf6;stop-opacity:1" />
              </linearGradient>
            </defs>
            <line x1="100" y1="5" x2="100" y2="195" stroke="#fff" stroke-width="2"/>
            <line x1="5" y1="100" x2="195" y2="100" stroke="#fff" stroke-width="2"/>
            <line x1="30" y1="30" x2="170" y2="170" stroke="#fff" stroke-width="1.5"/>
            <line x1="170" y1="30" x2="30" y2="170" stroke="#fff" stroke-width="1.5"/>
            <circle cx="100" cy="100" r="15" fill="#fff"/>
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

          <!-- 각 구역별 손익 -->
          <div class="probability-items">
            <div
              v-for="zone in bettedZonesAnalysis"
              :key="zone.name"
              class="probability-item"
              :class="{ 'profit': zone.isProfit, 'loss': !zone.isProfit }"
            >
              <span class="prob-icon">{{ zone.icon }}</span>
              <span class="prob-name">{{ zone.name }}</span>
              <span class="prob-percent">{{ zone.probability.toFixed(2) }}%</span>
              <span class="prob-profit" :class="{ 'positive': zone.isProfit, 'negative': !zone.isProfit }">
                {{ zone.profit >= 0 ? '+' : '' }}{{ Math.floor(zone.profit / 10000) }}만
              </span>
            </div>
          </div>

          <!-- 수익/손실 확률 -->
          <div class="probability-summary">
            <div class="summary-item profit-item">
              <span>수익 확률</span>
              <strong>{{ profitProbability.toFixed(2) }}%</strong>
            </div>
            <div class="summary-item loss-item">
              <span>손실 확률</span>
              <strong>{{ lossProbability.toFixed(2) }}%</strong>
            </div>
          </div>

          <!-- 기대 수익 -->
          <div class="expected-profit">
            <span>기대 수익</span>
            <strong :class="{ 'positive': expectedProfit >= 0, 'negative': expectedProfit < 0 }">
              {{ expectedProfit >= 0 ? '+' : '' }}{{ Math.floor(expectedProfit / 10000).toLocaleString() }}만원
            </strong>
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
  min-height: calc(100vh - 200px);
  padding-bottom: 1rem;
}

.main-content {
  padding: 0 0.5rem;
}

.page-header {
  position: sticky;
  top: 60px; /* header 높이만큼 */
  z-index: 100;
  padding: 0.75rem 1rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
}

.page-header.header-default {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
}

.page-header.header-betting {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
}

.page-header.header-result {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  animation: pulseHeader 2s infinite;
}

.page-header.header-complete {
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
}

@keyframes pulseHeader {
  0%, 100% {
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }
  50% {
    box-shadow: 0 4px 12px rgba(16, 185, 129, 0.4);
  }
}

.header-status {
  grid-column: 2;
  text-align: center;
  font-size: 1rem;
  font-weight: 700;
  margin: 0;
  color: var(--white);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

/* 모드 배지 */
.mode-badge {
  grid-column: 3;
  justify-self: end;
  display: flex;
  align-items: center;
  gap: 0.375rem;
  padding: 0.375rem 0.75rem;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--white);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.mode-icon {
  font-size: 1rem;
}

.mode-text {
  white-space: nowrap;
}

.status-dot {
  width: 8px;
  height: 8px;
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
  top: calc(60px + 48px); /* header + status bar */
  background: var(--white);
  border-bottom: 1px solid var(--border);
  padding: 0.5rem;
  z-index: 99;
  overflow: hidden;
}

.history-scroll {
  display: flex;
  gap: 0.4rem;
  overflow-x: auto;
  overflow-y: hidden;
  padding-bottom: 0.25rem;
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
  min-width: 80px;
  padding: 0.4rem 0.5rem;
  border-radius: 8px;
  font-size: 0.7rem;
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
  background: rgba(16, 185, 129, 0.1);
  border: 1.5px solid #10b981;
}

.history-item.history-lose {
  background: rgba(239, 68, 68, 0.1);
  border: 1.5px solid #ef4444;
}

.history-zone {
  font-size: 0.65rem;
  font-weight: 700;
  margin-bottom: 0.1rem;
  white-space: nowrap;
}

.history-item.history-win .history-zone {
  color: #059669;
}

.history-item.history-lose .history-zone {
  color: #dc2626;
}

.history-amount {
  font-size: 0.7rem;
  font-weight: 700;
}

.history-item.history-win .history-amount {
  color: #10b981;
}

.history-item.history-lose .history-amount {
  color: #ef4444;
}

.section-title {
  font-size: 1rem;
  font-weight: 700;
  margin-bottom: 0.75rem;
  text-align: center;
  color: var(--dark);
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
  background: linear-gradient(135deg, var(--primary) 0%, var(--secondary) 100%);
  color: var(--white);
  border: none;
  border-radius: 14px;
  font-size: 1.25rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 8px 20px rgba(99, 102, 241, 0.4);
}

.btn-start-game:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 28px rgba(99, 102, 241, 0.5);
}

/* 게임 섹션 */
.game-section {
  background: var(--white);
  border-radius: 12px;
  padding: 0.75rem;
  margin-bottom: 0.75rem;
  text-align: center;
  box-shadow: 0 2px 8px var(--shadow);
  position: relative;
  overflow: visible;
}

.wheel-container {
  margin: 0.5rem auto;
  position: relative;
  width: 100px;
  height: 100px;
  display: inline-block;
}

.wheel-container svg {
  width: 100px;
  height: 100px;
}

/* 게임 정보 섹션 */
.game-info-section {
  margin-bottom: 0.75rem;
}

.info-cards-row {
  display: flex;
  gap: 0.5rem;
}

.info-card {
  flex: 1;
  background: var(--white);
  border-radius: 12px;
  padding: 0.75rem;
  box-shadow: 0 2px 8px var(--shadow);
}

.info-card h3 {
  font-size: 0.875rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
  text-align: center;
  color: var(--dark);
}

.chip-info, .stats-info {
  display: flex;
  gap: 0.5rem;
  justify-content: center;
}

.info-item, .stat-item {
  flex: 1;
  padding: 0.5rem;
  background: var(--light-gray);
  border-radius: 8px;
  text-align: center;
}

.stat-item.win {
  background: rgba(16, 185, 129, 0.1);
}

.stat-item.loss {
  background: rgba(239, 68, 68, 0.1);
}

.info-item span, .stat-item span {
  display: block;
  font-size: 0.7rem;
  color: var(--gray);
  margin-bottom: 0.25rem;
}

.info-item strong {
  display: block;
  font-size: 0.95rem;
  color: var(--primary);
}

.stat-item.win strong {
  display: block;
  font-size: 0.85rem;
  color: #10b981;
  font-weight: 700;
}

.stat-item.loss strong {
  display: block;
  font-size: 0.85rem;
  color: #ef4444;
  font-weight: 700;
}

.stats-total {
  margin-top: 0.5rem;
  padding-top: 0.5rem;
  border-top: 1px solid var(--border);
  text-align: center;
  font-size: 0.8rem;
  font-weight: 700;
}

.stats-total.profit-positive {
  color: #10b981;
}

.stats-total.profit-negative {
  color: #ef4444;
}

/* 베팅 컨트롤 */
.betting-control {
  display: flex;
  gap: 0.5rem;
  align-items: center;
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 2px dashed var(--border);
}

.btn-cancel-betting {
  padding: 0.75rem 1rem;
  background: transparent;
  color: #6b7280;
  border: 2px solid #e5e7eb;
  border-radius: 10px;
  font-size: 0.875rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
}

.btn-cancel-betting:hover {
  border-color: #ef4444;
  color: #ef4444;
  background: #fef2f2;
}

.btn-end-betting {
  flex: 1;
  padding: 0.75rem 2rem;
  background: var(--secondary);
  color: var(--white);
  border: none;
  border-radius: 10px;
  font-size: 1rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.3);
}

.btn-end-betting:hover {
  background: #7c3aed;
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(139, 92, 246, 0.4);
}

/* 베팅 구역 */
.zones-section {
  background: var(--white);
  border-radius: 12px;
  padding: 0.75rem;
  box-shadow: 0 2px 8px var(--shadow);
}

.zones-row {
  display: flex;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.5rem 0;
  flex-wrap: nowrap;
}

.zone-card {
  background: var(--light-gray);
  border-radius: 8px;
  padding: 0.35rem 0.4rem;
  text-align: center;
  transition: all 0.3s;
  cursor: pointer;
  flex: 1;
  min-width: 50px;
  max-width: 80px;
  position: relative;
  transform: translateY(0);
}

.zone-card:hover:not(.betting-closed):not(.active) {
  transform: translateY(-8px) scale(1.08);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.25);
  z-index: 10;
}

.zone-card.active {
  transform: translateY(-8px) scale(1.08);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.25);
  z-index: 10;
}

.zone-card.betting-closed {
  opacity: 0.7;
  filter: grayscale(30%);
}

.zone-card.result-mode {
  cursor: pointer;
  transition: all 0.2s;
}

.zone-card.result-mode:hover {
  transform: translateY(-4px) scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  opacity: 1;
  filter: grayscale(0%);
}

.zone-icon {
  font-size: 1rem;
  margin-bottom: 0.1rem;
}

.zone-card h3 {
  font-size: 0.5rem;
  font-weight: 700;
  margin-bottom: 0.1rem;
  line-height: 1.2;
}

.zone-odds {
  font-size: 0.55rem;
  font-weight: 600;
  color: var(--dark);
}

.bet-amount {
  margin-top: 0.3rem;
  padding: 0.2rem;
  background: rgba(99, 102, 241, 0.1);
  border-radius: 4px;
  font-size: 0.5rem;
  font-weight: 700;
  color: var(--primary);
}

.zone-card.silver { border-top: 4px solid #c0c0c0; }
.zone-card.gold { border-top: 4px solid #ffd700; }
.zone-card.emerald { border-top: 4px solid #10b981; }
.zone-card.diamond { border-top: 4px solid #60a5fa; }
.zone-card.crystal { border-top: 4px solid #a78bfa; }
.zone-card.joker { border-top: 4px solid #ef4444; }
.zone-card.mega { border-top: 4px solid #f59e0b; }

/* 칩 선택 팝업 */
.chip-selector-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: transparent;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  z-index: 1000;
  padding-bottom: 60px; /* 네비게이션 바 높이만큼 */
}

.chip-selector-popup {
  background: var(--white);
  border-radius: 16px;
  padding: 0.75rem 1rem;
  width: auto;
  max-width: 350px;
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.2);
  animation: slideUp 0.25s ease-out;
  margin: 0 1rem;
}

@keyframes slideUp {
  from {
    transform: translateY(100%);
  }
  to {
    transform: translateY(0);
  }
}

.popup-header {
  text-align: center;
  font-size: 0.75rem;
  font-weight: 600;
  color: var(--gray);
  margin-bottom: 0.5rem;
}

.chip-icons {
  display: flex;
  justify-content: center;
  gap: 0.4rem;
  padding: 0.25rem 0;
  margin-bottom: 0.5rem;
}

.chip-icon-btn {
  position: relative;
  cursor: pointer;
  transition: all 0.15s ease-out;
  opacity: 1;
  border-radius: 50%;
  padding: 2px;
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
  background: var(--primary);
  color: var(--white);
  border-radius: 50%;
  width: 18px;
  height: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.65rem;
  font-weight: 700;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.selected-count-badge {
  position: absolute;
  bottom: -4px;
  left: -4px;
  background: #10b981;
  color: var(--white);
  border-radius: 50%;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.7rem;
  font-weight: 700;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
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
  padding: 0.5rem;
  background: var(--primary);
  color: var(--white);
  border: none;
  border-radius: 8px;
  font-size: 0.875rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-confirm-bet:hover:not(:disabled) {
  background: var(--primary-dark);
  transform: translateY(-1px);
}

.btn-confirm-bet:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 결과 표시 오버레이 (빅휠 왼쪽) */
.result-overlay {
  position: absolute;
  right: calc(100% + 10px);
  top: 50%;
  transform: translateY(-50%);
  width: 120px;
  z-index: 50;
  cursor: pointer;
}

.result-content-compact {
  background: linear-gradient(135deg, var(--primary) 0%, var(--secondary) 100%);
  border-radius: 12px;
  padding: 0.75rem;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
  text-align: center;
}

.result-zone-name {
  font-size: 0.875rem;
  font-weight: 700;
  color: var(--white);
  margin-bottom: 0.5rem;
}

.result-amount {
  margin: 0;
}

.amount-win {
  font-size: 1rem;
  font-weight: 700;
  color: #34d399;
}

.amount-lose {
  font-size: 1rem;
  font-weight: 700;
  color: #fca5a5;
}

.tap-hint {
  margin-top: 0.5rem;
  font-size: 0.65rem;
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

.btn-next-round:hover {
  background: var(--primary-dark);
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(99, 102, 241, 0.4);
}

/* 베팅 확률 및 손익 분석 */
.probability-box {
  margin-top: 1rem;
  padding: 0.75rem;
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.05) 0%, rgba(139, 92, 246, 0.05) 100%);
  border: 2px solid var(--primary);
  border-radius: 12px;
}

.probability-title {
  font-size: 0.875rem;
  font-weight: 700;
  text-align: center;
  color: var(--primary);
  margin-bottom: 0.5rem;
}

.probability-items {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
  margin-bottom: 0.5rem;
}

.probability-item {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  padding: 0.35rem 0.5rem;
  background: var(--white);
  border-radius: 8px;
  font-size: 0.7rem;
  border-left: 3px solid transparent;
  transition: all 0.2s;
}

.probability-item.profit {
  border-left-color: #10b981;
}

.probability-item.loss {
  border-left-color: #ef4444;
}

.prob-icon {
  font-size: 0.95rem;
  flex-shrink: 0;
}

.prob-name {
  flex: 1;
  font-weight: 600;
  color: var(--dark);
  font-size: 0.65rem;
}

.prob-percent {
  font-weight: 600;
  color: var(--gray);
  font-size: 0.65rem;
  min-width: 45px;
  text-align: right;
}

.prob-profit {
  font-weight: 700;
  font-size: 0.7rem;
  min-width: 50px;
  text-align: right;
}

.prob-profit.positive {
  color: #10b981;
}

.prob-profit.negative {
  color: #ef4444;
}

.probability-summary {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}

.summary-item {
  flex: 1;
  padding: 0.4rem;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.2rem;
}

.summary-item span {
  font-size: 0.65rem;
  font-weight: 600;
}

.summary-item strong {
  font-size: 0.85rem;
  font-weight: 700;
}

.profit-item {
  background: rgba(16, 185, 129, 0.1);
}

.profit-item span {
  color: #059669;
}

.profit-item strong {
  color: #10b981;
}

.loss-item {
  background: rgba(239, 68, 68, 0.1);
}

.loss-item span {
  color: #dc2626;
}

.loss-item strong {
  color: #ef4444;
}

.expected-profit {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem;
  border-top: 2px solid var(--primary);
  font-size: 0.75rem;
  color: var(--dark);
  font-weight: 600;
}

.expected-profit strong {
  font-size: 0.9rem;
  font-weight: 700;
}

.expected-profit strong.positive {
  color: #10b981;
}

.expected-profit strong.negative {
  color: #ef4444;
}
</style>
