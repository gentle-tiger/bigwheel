<template>
  <div class="game-history-page">
    <main class="main-content">
      <div class="header">
        <button @click="goBack" class="back-button">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M19 12H5M5 12L12 19M5 12L12 5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
        <h1 class="page-title">빅휠 게임 기록</h1>
      </div>

      <!-- Filter Buttons -->
      <div class="filter-section">
        <button
          @click="setFilter('ALL')"
          :class="{ active: filter === 'ALL' }"
          class="filter-button"
        >
          전체
        </button>
        <button
          @click="setFilter('ONLINE')"
          :class="{ active: filter === 'ONLINE' }"
          class="filter-button"
        >
          온라인 게임
        </button>
        <button
          @click="setFilter('OFFLINE')"
          :class="{ active: filter === 'OFFLINE' }"
          class="filter-button"
        >
          오프라인 게임
        </button>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="loading-container">
        <div class="spinner"></div>
        <p>게임 기록을 불러오는 중...</p>
      </div>

      <!-- Empty State -->
      <div v-else-if="games.length === 0" class="empty-state">
        <svg width="80" height="80" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M9 5H7C6.46957 5 5.96086 5.21071 5.58579 5.58579C5.21071 5.96086 5 6.46957 5 7V19C5 19.5304 5.21071 20.0391 5.58579 20.4142C5.96086 20.7893 6.46957 21 7 21H17C17.5304 21 18.0391 20.7893 18.4142 20.4142C18.7893 20.0391 19 19.5304 19 19V7C19 6.46957 18.7893 5.96086 18.4142 5.58579C18.0391 5.21071 17.5304 5 17 5H15" stroke="var(--gray)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <h3>게임 기록이 없습니다</h3>
        <p>빅휠 게임을 플레이하고 기록을 확인하세요!</p>
        <button @click="goToGame" class="play-button">게임하러 가기</button>
      </div>

      <!-- Game List -->
      <div v-else class="games-list">
        <div
          v-for="game in games"
          :key="game.gameId"
          class="game-card"
          :class="{ 'win': game.netProfit >= 0, 'loss': game.netProfit < 0 }"
          @click="openDetailModal(game)"
        >
          <!-- Header Row -->
          <div class="game-card-header">
            <div class="game-info">
              <span class="result-indicator" :class="game.netProfit >= 0 ? 'win' : 'loss'">
                {{ game.netProfit >= 0 ? '승' : '패' }}
              </span>
              <span class="game-title">게임 #{{ game.gameId }}</span>
              <span class="game-mode">{{ game.gameMode === 'ONLINE' ? '온라인' : '오프라인' }}</span>
            </div>
            <span class="game-date">{{ formatDate(game.playedAt) }}</span>
          </div>

          <!-- Stats Row -->
          <div class="game-card-stats">
            <div class="stat">
              <span class="stat-label">베팅</span>
              <span class="stat-value">{{ formatCurrency(game.totalBetAmount) }}</span>
            </div>
            <span class="arrow">→</span>
            <div class="stat">
              <span class="stat-label">당첨</span>
              <span class="stat-value">{{ formatCurrency(game.winningAmount) }}</span>
            </div>
            <span class="arrow">→</span>
            <div class="stat profit-stat">
              <span
                class="profit-value"
                :class="game.netProfit >= 0 ? 'profit' : 'loss'"
              >
                {{ game.netProfit >= 0 ? '+' : '' }}{{ formatCurrency(game.netProfit) }}
              </span>
            </div>
          </div>
        </div>

        <!-- Intersection Observer Target -->
        <div ref="observerTarget" class="observer-target"></div>

        <!-- Loading More Indicator -->
        <div v-if="loadingMore" class="loading-more">
          <div class="spinner-small"></div>
          <p>더 불러오는 중...</p>
        </div>

        <!-- No More Data -->
        <div v-if="!hasMore && games.length > 0" class="no-more-data">
          <p>모든 게임 기록을 불러왔습니다</p>
        </div>
      </div>
    </main>

    <!-- Detail Modal -->
    <div v-if="showDetailModal" class="modal-overlay" @click="closeDetailModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>게임 #{{ selectedGame.gameId }} 상세 정보</h2>
          <button @click="closeDetailModal" class="close-button">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
        </div>

        <div class="modal-body">
          <!-- 플레이 시간 -->
          <div class="detail-section">
            <h3>플레이 시간</h3>
            <p class="detail-value">{{ formatFullDate(selectedGame.playedAt) }}</p>
          </div>

          <!-- 결과 -->
          <div class="detail-section">
            <h3>결과</h3>
            <div
              class="result-badge large"
              :class="getResultBadgeClass(selectedGame.resultSector)"
            >
              {{ selectedGame.resultSector }}
            </div>
          </div>

          <!-- 베팅 분석 -->
          <div class="detail-section">
            <h3>베팅 분석</h3>
            <div class="analysis-grid">
              <!-- 승/패 여부 -->
              <div class="analysis-item">
                <span class="analysis-label">결과</span>
                <span
                  class="analysis-value"
                  :class="selectedGame.netProfit >= 0 ? 'win' : 'lose'"
                >
                  {{ selectedGame.netProfit >= 0 ? '승리' : '패배' }}
                </span>
              </div>

              <!-- 당첨 구역 베팅 여부 -->
              <div class="analysis-item">
                <span class="analysis-label">당첨 구역 베팅</span>
                <span class="analysis-value">
                  {{ isBetOnWinningZone(selectedGame) ? '예' : '아니오' }}
                </span>
              </div>

              <!-- 총 베팅 구역 수 -->
              <div class="analysis-item">
                <span class="analysis-label">베팅 구역 수</span>
                <span class="analysis-value">{{ Object.keys(selectedGame.betDetails).length }}개</span>
              </div>

              <!-- 수익률 -->
              <div class="analysis-item full-width">
                <span class="analysis-label">수익률</span>
                <span
                  class="analysis-value"
                  :class="selectedGame.netProfit >= 0 ? 'profit' : 'loss'"
                >
                  {{ calculateReturnRate(selectedGame) }}%
                </span>
              </div>
            </div>
          </div>

          <!-- 베팅 내역 -->
          <div class="detail-section">
            <h3>베팅 내역</h3>
            <div class="bet-details">
              <div
                v-for="(chips, zone) in selectedGame.betDetails"
                :key="zone"
                class="bet-zone"
                :class="{ 'winning-zone': zone === selectedGame.resultSector }"
              >
                <div class="zone-header">
                  <div class="zone-name" :class="getZoneClass(zone)">{{ zone }}</div>
                  <span v-if="zone === selectedGame.resultSector" class="win-badge">당첨</span>
                  <span v-else class="lose-badge">낙첨</span>
                </div>
                <div class="chip-list">
                  <div v-for="(count, chipType) in chips" :key="chipType" class="chip-item">
                    <span class="chip-type">{{ formatChipType(chipType) }}</span>
                    <span class="chip-count">{{ count }}개 ({{ formatCurrency(getChipValue(chipType) * count) }}원)</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 결과 요약 -->
          <div class="detail-section">
            <h3>결과 요약</h3>
            <div class="summary-grid">
              <div class="summary-item">
                <span class="summary-label">총 베팅</span>
                <span class="summary-value">{{ formatCurrency(selectedGame.totalBetAmount) }}원</span>
              </div>
              <div class="summary-item">
                <span class="summary-label">당첨 금액</span>
                <span class="summary-value win">{{ formatCurrency(selectedGame.winningAmount) }}원</span>
              </div>
              <div class="summary-item full-width">
                <span class="summary-label">순손익</span>
                <span
                  class="summary-value"
                  :class="selectedGame.netProfit >= 0 ? 'profit' : 'loss'"
                >
                  {{ selectedGame.netProfit >= 0 ? '+' : '' }}{{ formatCurrency(selectedGame.netProfit) }}원
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { gameApi } from '../api/api'

const router = useRouter()
const games = ref([])
const loading = ref(true)
const showDetailModal = ref(false)
const selectedGame = ref(null)
const filter = ref('ALL')  // 'ALL', 'ONLINE', 'OFFLINE'

// 무한 스크롤 상태
const page = ref(1)
const pageSize = 10
const hasMore = ref(true)
const loadingMore = ref(false)
const observerTarget = ref(null)
let observer = null

// 칩 타입별 가치 매핑
const chipValues = {
  'CHIP_1000': 1000,
  'CHIP_5000': 5000,
  'CHIP_10000': 10000,
  'CHIP_100000': 100000,
  'CHIP_1000000': 1000000
}

// 목업 데이터 생성 (총 30개)
const createMockGames = () => {
  const sectors = ['SILVER', 'GOLD', 'PURPLE', 'DIAMOND', 'BIGWHEEL']
  const gameModes = ['ONLINE', 'OFFLINE']
  const games = []

  const baseDate = new Date('2025-11-23T15:00:00')

  for (let i = 0; i < 30; i++) {
    const gameMode = i % 3 === 0 ? 'OFFLINE' : 'ONLINE'
    const resultSector = sectors[Math.floor(Math.random() * sectors.length)]

    // 베팅 구역 생성 (1~3개)
    const numBetZones = Math.floor(Math.random() * 3) + 1
    const betZones = []
    for (let j = 0; j < numBetZones; j++) {
      let zone = sectors[Math.floor(Math.random() * sectors.length)]
      while (betZones.includes(zone)) {
        zone = sectors[Math.floor(Math.random() * sectors.length)]
      }
      betZones.push(zone)
    }

    // 당첨 여부 결정 (50% 확률)
    const isWin = Math.random() > 0.5

    // 당첨이면 당첨 구역을 베팅 구역에 포함
    if (isWin && !betZones.includes(resultSector)) {
      betZones[0] = resultSector
    } else if (!isWin && betZones.includes(resultSector)) {
      // 패배면 당첨 구역을 제외
      const idx = betZones.indexOf(resultSector)
      betZones[idx] = sectors.find(s => s !== resultSector && !betZones.includes(s))
    }

    // 베팅 상세 생성
    const betDetails = {}
    let totalBetAmount = 0

    betZones.forEach(zone => {
      const chipTypes = ['CHIP_1000', 'CHIP_5000', 'CHIP_10000', 'CHIP_100000']
      const numChipTypes = Math.floor(Math.random() * 2) + 1
      const chips = {}

      for (let k = 0; k < numChipTypes; k++) {
        const chipType = chipTypes[Math.floor(Math.random() * chipTypes.length)]
        const count = Math.floor(Math.random() * 10) + 1
        chips[chipType] = count
        totalBetAmount += chipValues[chipType] * count
      }

      betDetails[zone] = chips
    })

    // 당첨 금액 계산
    let winningAmount = 0
    if (isWin) {
      // 당첨 구역의 베팅액 계산
      const winningBet = betDetails[resultSector]
      let winningBetAmount = 0
      Object.entries(winningBet).forEach(([chipType, count]) => {
        winningBetAmount += chipValues[chipType] * count
      })

      // 구역별 배당률 (간단히)
      const multipliers = {
        'SILVER': 2,
        'GOLD': 3,
        'PURPLE': 5,
        'DIAMOND': 9,
        'BIGWHEEL': 45
      }
      winningAmount = winningBetAmount * multipliers[resultSector]
    }

    const netProfit = winningAmount - totalBetAmount

    // 시간 계산 (최신 순)
    const gameDate = new Date(baseDate)
    gameDate.setHours(baseDate.getHours() - i * 2)

    games.push({
      gameId: i + 1,
      gameMode,
      playedAt: gameDate.toISOString(),
      resultSector,
      betDetails,
      totalBetAmount,
      winningAmount,
      netProfit
    })
  }

  return games
}

onMounted(async () => {
  await fetchGames()
  setupIntersectionObserver()
})

const fetchGames = async () => {
  loading.value = true
  page.value = 1
  games.value = []

  // 목업 데이터 사용
  await new Promise(resolve => setTimeout(resolve, 500)) // 로딩 시뮬레이션

  const allGames = createMockGames()

  // 필터링
  let filteredGames = filter.value === 'ALL'
    ? allGames
    : allGames.filter(game => game.gameMode === filter.value)

  // 페이지네이션: 첫 페이지만 로드
  const start = 0
  const end = pageSize
  games.value = filteredGames.slice(start, end)

  // 더 불러올 데이터가 있는지 확인
  hasMore.value = filteredGames.length > pageSize

  loading.value = false

  /* 실제 API 사용 시 아래 코드 활성화
  const userId = localStorage.getItem('userId')

  if (!userId) {
    const localGames = localStorage.getItem('gameHistory')
    if (localGames) {
      try {
        games.value = JSON.parse(localGames)
      } catch (error) {
        console.error('로컬 게임 기록 파싱 실패:', error)
      }
    }
    loading.value = false
    return
  }

  try {
    let url = `/api/v1/games/${userId}`
    if (filter.value !== 'ALL') {
      url += `?gameMode=${filter.value}`
    }

    const response = await gameApi.getUserGames(userId, filter.value !== 'ALL' ? filter.value : undefined)
    games.value = response.data
  } catch (error) {
    console.error('게임 기록 조회 실패:', error)
    const localGames = localStorage.getItem('gameHistory')
    if (localGames) {
      try {
        games.value = JSON.parse(localGames)
      } catch (parseError) {
        console.error('로컬 게임 기록 파싱 실패:', parseError)
      }
    }
  } finally {
    loading.value = false
  }
  */
}

const loadMoreGames = async () => {
  if (loadingMore.value || !hasMore.value) return

  loadingMore.value = true

  // 목업 데이터 사용
  await new Promise(resolve => setTimeout(resolve, 500)) // 로딩 시뮬레이션

  const allGames = createMockGames()

  // 필터링
  let filteredGames = filter.value === 'ALL'
    ? allGames
    : allGames.filter(game => game.gameMode === filter.value)

  // 다음 페이지 데이터 로드
  page.value++
  const start = (page.value - 1) * pageSize
  const end = page.value * pageSize
  const newGames = filteredGames.slice(start, end)

  // 기존 게임 목록에 추가
  games.value = [...games.value, ...newGames]

  // 더 불러올 데이터가 있는지 확인
  hasMore.value = filteredGames.length > page.value * pageSize

  loadingMore.value = false

  /* 실제 API 사용 시
  try {
    const response = await gameApi.getUserGames(userId, filter.value !== 'ALL' ? filter.value : undefined, page.value)
    games.value = [...games.value, ...response.data.games]
    hasMore.value = response.data.hasMore
  } catch (error) {
    console.error('더 많은 게임 로드 실패:', error)
  } finally {
    loadingMore.value = false
  }
  */
}

const setupIntersectionObserver = () => {
  const options = {
    root: null, // viewport
    rootMargin: '100px', // 하단 100px 전에 트리거
    threshold: 0.1
  }

  observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting && hasMore.value && !loadingMore.value) {
        loadMoreGames()
      }
    })
  }, options)

  // observer target이 있으면 관찰 시작
  if (observerTarget.value) {
    observer.observe(observerTarget.value)
  }
}

onBeforeUnmount(() => {
  // Intersection Observer 정리
  if (observer) {
    observer.disconnect()
  }
})

const setFilter = (mode) => {
  filter.value = mode
  fetchGames()  // 재호출
}

const goBack = () => {
  router.back()
}

const goToGame = () => {
  router.push('/category')
}

const openDetailModal = (game) => {
  selectedGame.value = game
  showDetailModal.value = true
}

const closeDetailModal = () => {
  showDetailModal.value = false
  selectedGame.value = null
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hours = date.getHours()
  const minutes = date.getMinutes().toString().padStart(2, '0')
  return `${month}월 ${day}일 ${hours}:${minutes}`
}

const formatFullDate = (dateString) => {
  const date = new Date(dateString)
  const year = date.getFullYear()
  const month = (date.getMonth() + 1).toString().padStart(2, '0')
  const day = date.getDate().toString().padStart(2, '0')
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  const seconds = date.getSeconds().toString().padStart(2, '0')
  return `${year}년 ${month}월 ${day}일 ${hours}:${minutes}:${seconds}`
}

const formatCurrency = (amount) => {
  return amount.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

const formatChipType = (chipType) => {
  const value = chipValues[chipType]
  if (value >= 1000000) return `${value / 1000000}M`
  if (value >= 1000) return `${value / 1000}K`
  return value.toString()
}

const getChipValue = (chipType) => {
  return chipValues[chipType] || 0
}

const getResultBadgeClass = (sector) => {
  const colorMap = {
    'SILVER': 'silver',
    'GOLD': 'gold',
    'PURPLE': 'purple',
    'DIAMOND': 'diamond',
    'BIGWHEEL': 'bigwheel'
  }
  return colorMap[sector] || 'default'
}

const getZoneClass = (zone) => {
  return getResultBadgeClass(zone)
}

// 당첨 구역에 베팅했는지 확인
const isBetOnWinningZone = (game) => {
  return game.betDetails.hasOwnProperty(game.resultSector)
}

// 수익률 계산 (순손익 / 총 베팅액 * 100)
const calculateReturnRate = (game) => {
  if (game.totalBetAmount === 0) return '0.00'
  const rate = (game.netProfit / game.totalBetAmount) * 100
  return rate.toFixed(2)
}
</script>

<style scoped>
.game-history-page {
  min-height: calc(100vh - 100px);
  background: var(--light-gray);
  padding-bottom: 2rem;
}

.main-content {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem 1rem;
}

/* Header */
.header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1rem;
}

/* Filter Section */
.filter-section {
  display: flex;
  gap: 10px;
  margin-bottom: 2rem;
  padding: 0 0px;
}

.filter-button {
  flex: 1;
  padding: 12px 20px;
  border: 2px solid var(--border-color, #e0e0e0);
  border-radius: 12px;
  background: var(--white);
  color: var(--text-secondary, #666);
  cursor: pointer;
  transition: all 0.3s;
  font-weight: 600;
  font-size: 0.95rem;
}

.filter-button:hover {
  border-color: var(--primary, #6366f1);
  color: var(--primary, #6366f1);
}

.filter-button.active {
  background: var(--primary, #6366f1);
  color: white;
  border-color: var(--primary, #6366f1);
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
}

.back-button {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background: var(--white);
  border: none;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 2px 8px var(--shadow);
}

.back-button:hover {
  background: var(--primary);
  color: var(--white);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
}

.page-title {
  font-size: 2rem;
  font-weight: 700;
  color: var(--dark);
  margin: 0;
}

/* Loading */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 0;
  gap: 1rem;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 4px solid var(--light-gray);
  border-top: 4px solid var(--primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Empty State */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  text-align: center;
  background: var(--white);
  border-radius: 20px;
  box-shadow: 0 4px 12px var(--shadow);
}

.empty-state svg {
  margin-bottom: 1rem;
}

.empty-state h3 {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--dark);
  margin-bottom: 0.5rem;
}

.empty-state p {
  color: var(--gray);
  margin-bottom: 2rem;
}

.play-button {
  padding: 1rem 2rem;
  background: var(--primary);
  color: var(--white);
  border: none;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.play-button:hover {
  background: var(--secondary);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
}

/* Games List */
.games-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.game-card {
  background: var(--white);
  border-radius: 12px;
  padding: 1rem 1.25rem;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.2s;
  border-left: 4px solid transparent;
}

.game-card.win {
  border-left-color: #10b981;
}

.game-card.loss {
  border-left-color: #ef4444;
}

.game-card:hover {
  transform: translateX(4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

/* Card Header */
.game-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
}

.game-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.result-indicator {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 700;
  color: white;
}

.result-indicator.win {
  background: #10b981;
}

.result-indicator.loss {
  background: #ef4444;
}

.game-title {
  font-size: 1rem;
  font-weight: 700;
  color: var(--dark, #1a1a1a);
}

.game-mode {
  font-size: 0.8rem;
  padding: 4px 10px;
  background: var(--light-gray, #f5f5f5);
  border-radius: 6px;
  color: var(--gray, #666);
  font-weight: 600;
}

.game-date {
  font-size: 0.85rem;
  color: var(--gray, #666);
  font-weight: 500;
}

/* Card Stats */
.game-card-stats {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.stat {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
}

.stat-label {
  font-size: 0.75rem;
  color: var(--gray, #999);
  font-weight: 500;
}

.stat-value {
  font-size: 1rem;
  font-weight: 700;
  color: var(--dark, #1a1a1a);
}

.arrow {
  font-size: 1.2rem;
  color: var(--gray, #ccc);
  flex-shrink: 0;
}

.profit-stat {
  flex: 0.8;
}

.profit-value {
  font-size: 1.1rem;
  font-weight: 800;
}

.profit-value.profit {
  color: #10b981;
}

.profit-value.loss {
  color: #ef4444;
}

/* Modal */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 1rem;
}

.modal-content {
  background: var(--white);
  border-radius: 24px;
  max-width: 600px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid var(--light-gray);
  position: sticky;
  top: 0;
  background: var(--white);
  z-index: 1;
  border-radius: 24px 24px 0 0;
}

.modal-header h2 {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--dark);
  margin: 0;
}

.close-button {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  background: var(--light-gray);
  border: none;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.2s;
}

.close-button:hover {
  background: var(--danger);
  color: var(--white);
}

.modal-body {
  padding: 1.5rem;
}

/* Detail Sections */
.detail-section {
  margin-bottom: 2rem;
}

.detail-section h3 {
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--dark);
  margin-bottom: 1rem;
}

.detail-value {
  font-size: 1rem;
  color: var(--gray);
  margin: 0;
}

.result-badge.large {
  display: inline-block;
  padding: 0.75rem 1.5rem;
  font-size: 1.1rem;
}

/* Analysis Grid */
.analysis-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.analysis-item {
  background: var(--light-gray, #f5f5f5);
  padding: 1rem;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.analysis-item.full-width {
  grid-column: 1 / -1;
}

.analysis-label {
  color: var(--text-secondary, #666);
  font-size: 14px;
  font-weight: 500;
}

.analysis-value {
  font-size: 20px;
  font-weight: 700;
  color: var(--dark, #1a1a1a);
}

.analysis-value.win {
  color: #10b981;
}

.analysis-value.lose {
  color: #ef4444;
}

.analysis-value.profit {
  color: #10b981;
}

.analysis-value.loss {
  color: #ef4444;
}

/* Bet Details */
.bet-details {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.bet-zone {
  background: var(--light-gray);
  border-radius: 12px;
  padding: 1rem;
  border: 2px solid transparent;
  transition: all 0.3s;
}

.bet-zone.winning-zone {
  border-color: #10b981;
  background: rgba(16, 185, 129, 0.1);
}

.zone-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
}

.zone-name {
  font-size: 0.9rem;
  font-weight: 700;
  text-transform: uppercase;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  display: inline-block;
}

.win-badge {
  background: #10b981;
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.lose-badge {
  background: var(--gray, #999);
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.zone-name.silver { background: linear-gradient(135deg, #C0C0C0 0%, #E8E8E8 100%); color: #666; }
.zone-name.gold { background: linear-gradient(135deg, #FFD700 0%, #FFA500 100%); color: #8B4513; }
.zone-name.purple { background: linear-gradient(135deg, #9B59B6 0%, #8E44AD 100%); color: white; }
.zone-name.diamond { background: linear-gradient(135deg, #3498DB 0%, #2980B9 100%); color: white; }
.zone-name.bigwheel { background: linear-gradient(135deg, #E74C3C 0%, #C0392B 100%); color: white; }

.chip-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.chip-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem;
  background: var(--white);
  border-radius: 8px;
}

.chip-type {
  font-size: 0.9rem;
  font-weight: 600;
  color: var(--primary);
}

.chip-count {
  font-size: 0.85rem;
  color: var(--gray);
}

/* Summary */
.summary-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.summary-item {
  background: var(--light-gray);
  padding: 1rem;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.summary-item.full-width {
  grid-column: 1 / -1;
}

.summary-label {
  font-size: 0.85rem;
  color: var(--gray);
  font-weight: 500;
}

.summary-value {
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--dark);
}

.summary-value.win {
  color: #f59e0b;
}

.summary-value.profit {
  color: #10b981;
}

.summary-value.loss {
  color: #ef4444;
}

/* Infinite Scroll Elements */
.observer-target {
  height: 20px;
  margin: 1rem 0;
}

.loading-more {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 2rem 0;
  gap: 0.75rem;
}

.loading-more p {
  color: var(--gray);
  font-size: 0.9rem;
}

.spinner-small {
  width: 30px;
  height: 30px;
  border: 3px solid var(--light-gray);
  border-top: 3px solid var(--primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.no-more-data {
  text-align: center;
  padding: 2rem 0;
  color: var(--gray);
  font-size: 0.9rem;
}

.no-more-data p {
  margin: 0;
  padding: 1rem;
  background: var(--light-gray);
  border-radius: 12px;
  color: var(--text-secondary, #666);
}

/* Responsive */
@media (max-width: 768px) {
  .page-title {
    font-size: 1.5rem;
  }

  .filter-section {
    flex-direction: column;
  }

  .filter-button {
    padding: 10px 16px;
  }

  .game-card {
    padding: 0.875rem 1rem;
  }

  .game-card-stats {
    gap: 0.5rem;
  }

  .arrow {
    font-size: 1rem;
  }

  .stat-value {
    font-size: 0.9rem;
  }

  .profit-value {
    font-size: 1rem;
  }

  .game-info {
    gap: 0.5rem;
  }

  .game-title {
    font-size: 0.9rem;
  }

  .game-mode {
    font-size: 0.7rem;
    padding: 3px 8px;
  }

  .summary-grid {
    grid-template-columns: 1fr;
  }

  .summary-item.full-width {
    grid-column: 1;
  }

  .modal-content {
    max-height: 95vh;
  }

  .analysis-grid {
    grid-template-columns: 1fr;
  }
}
</style>
