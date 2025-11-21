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
          @click="openDetailModal(game)"
        >
          <div class="game-header">
            <div class="game-id">
              <span class="label">게임 #{{ game.gameId }}</span>
              <span class="date">{{ formatDate(game.playedAt) }}</span>
            </div>
            <div
              class="result-badge"
              :class="getResultBadgeClass(game.resultSector)"
            >
              {{ game.resultSector }}
            </div>
          </div>

          <div class="game-stats">
            <div class="stat-item">
              <span class="stat-label">베팅 금액</span>
              <span class="stat-value">{{ formatCurrency(game.totalBetAmount) }}원</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">당첨 금액</span>
              <span class="stat-value">{{ formatCurrency(game.winningAmount) }}원</span>
            </div>
          </div>

          <div class="game-profit">
            <span class="profit-label">순손익</span>
            <span
              class="profit-value"
              :class="game.netProfit >= 0 ? 'profit' : 'loss'"
            >
              {{ game.netProfit >= 0 ? '+' : '' }}{{ formatCurrency(game.netProfit) }}원
            </span>
          </div>
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

          <!-- 베팅 내역 -->
          <div class="detail-section">
            <h3>베팅 내역</h3>
            <div class="bet-details">
              <div
                v-for="(chips, zone) in selectedGame.betDetails"
                :key="zone"
                class="bet-zone"
              >
                <div class="zone-name" :class="getZoneClass(zone)">{{ zone }}</div>
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { gameApi } from '../api/api'

const router = useRouter()
const games = ref([])
const loading = ref(true)
const showDetailModal = ref(false)
const selectedGame = ref(null)

// 칩 타입별 가치 매핑
const chipValues = {
  'CHIP_1000': 1000,
  'CHIP_5000': 5000,
  'CHIP_10000': 10000,
  'CHIP_100000': 100000,
  'CHIP_1000000': 1000000
}

onMounted(async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) {
    // 로그인하지 않은 경우 로컬스토리지에서 게임 기록 로드
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

  // 로그인한 경우 서버에서 게임 기록 로드
  try {
    const response = await gameApi.getUserGames(userId)
    games.value = response.data
  } catch (error) {
    console.error('게임 기록 조회 실패:', error)
    // 서버 조회 실패 시 로컬스토리지에서 로드
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
})

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
  margin-bottom: 2rem;
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
  gap: 1rem;
}

.game-card {
  background: var(--white);
  border-radius: 16px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px var(--shadow);
  cursor: pointer;
  transition: all 0.3s;
}

.game-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.game-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid var(--light-gray);
}

.game-id {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.game-id .label {
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--dark);
}

.game-id .date {
  font-size: 0.85rem;
  color: var(--gray);
}

.result-badge {
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 700;
  text-transform: uppercase;
}

.result-badge.silver {
  background: linear-gradient(135deg, #C0C0C0 0%, #E8E8E8 100%);
  color: #666;
}

.result-badge.gold {
  background: linear-gradient(135deg, #FFD700 0%, #FFA500 100%);
  color: #8B4513;
}

.result-badge.purple {
  background: linear-gradient(135deg, #9B59B6 0%, #8E44AD 100%);
  color: white;
}

.result-badge.diamond {
  background: linear-gradient(135deg, #3498DB 0%, #2980B9 100%);
  color: white;
}

.result-badge.bigwheel {
  background: linear-gradient(135deg, #E74C3C 0%, #C0392B 100%);
  color: white;
}

/* Game Stats */
.game-stats {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
  margin-bottom: 1rem;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.stat-label {
  font-size: 0.85rem;
  color: var(--gray);
  font-weight: 500;
}

.stat-value {
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--dark);
}

/* Game Profit */
.game-profit {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  background: var(--light-gray);
  border-radius: 12px;
}

.profit-label {
  font-size: 0.9rem;
  font-weight: 600;
  color: var(--dark);
}

.profit-value {
  font-size: 1.25rem;
  font-weight: 700;
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
}

.zone-name {
  font-size: 0.9rem;
  font-weight: 700;
  text-transform: uppercase;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  display: inline-block;
  margin-bottom: 0.75rem;
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

/* Responsive */
@media (max-width: 768px) {
  .page-title {
    font-size: 1.5rem;
  }

  .game-stats {
    grid-template-columns: 1fr;
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
}
</style>
