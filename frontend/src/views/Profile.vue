<template>
  <div class="profile-page">
    <main class="main-content">
      <h1 class="page-title">내 정보</h1>

      <!-- Profile Card -->
      <div class="profile-card">
        <div class="profile-info-wrapper">
          <!-- Left: Avatar & Nickname -->
          <div class="profile-left">
            <div class="profile-avatar">
              <svg width="80" height="80" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="12" cy="12" r="11" fill="url(#avatarGradient)" stroke="var(--primary)" stroke-width="2"/>
                <defs>
                  <linearGradient id="avatarGradient" x1="0%" y1="0%" x2="100%" y2="100%">
                    <stop offset="0%" style="stop-color:#6366f1;stop-opacity:0.8" />
                    <stop offset="100%" style="stop-color:#8b5cf6;stop-opacity:0.8" />
                  </linearGradient>
                </defs>
                <path d="M12 12C14.2091 12 16 10.2091 16 8C16 5.79086 14.2091 4 12 4C9.79086 4 8 5.79086 8 8C8 10.2091 9.79086 12 12 12Z" fill="white"/>
                <path d="M20 20C20 16.6863 16.4183 14 12 14C7.58172 14 4 16.6863 4 20" stroke="white" stroke-width="2" stroke-linecap="round"/>
              </svg>
            </div>
            <h2 class="profile-nickname">{{ nickname }}</h2>
          </div>

          <!-- Right: Chips Display -->
          <div class="chips-container">
            <!-- Left Column -->
            <div class="chip-column">
              <!-- 1,000원 칩 -->
              <div class="chip-display">
                <svg width="20" height="20" viewBox="0 0 120 120" xmlns="http://www.w3.org/2000/svg">
                  <circle cx="60" cy="60" r="58" fill="#E8749B" stroke="#C94A7A" stroke-width="2"/>
                  <circle cx="60" cy="60" r="54" fill="none" stroke="#C94A7A" stroke-width="8" stroke-dasharray="8 4"/>
                  <circle cx="60" cy="60" r="38" fill="#FFFFFF"/>
                  <text x="60" y="63" font-family="Arial, sans-serif" font-size="16" font-weight="bold" fill="#C94A7A" text-anchor="middle">1K</text>
                </svg>
                <span class="chip-count">{{ userChips.chip1000 }}개</span>
              </div>

              <!-- 5,000원 칩 -->
              <div class="chip-display">
                <svg width="20" height="20" viewBox="0 0 120 120" xmlns="http://www.w3.org/2000/svg">
                  <circle cx="60" cy="60" r="58" fill="#E89F5A" stroke="#C87B3A" stroke-width="2"/>
                  <circle cx="60" cy="60" r="54" fill="none" stroke="#8B5A2B" stroke-width="8" stroke-dasharray="8 4"/>
                  <circle cx="60" cy="60" r="38" fill="#FFFFFF"/>
                  <text x="60" y="63" font-family="Arial, sans-serif" font-size="16" font-weight="bold" fill="#C87B3A" text-anchor="middle">5K</text>
                </svg>
                <span class="chip-count">{{ userChips.chip5000 }}개</span>
              </div>

              <!-- 10,000원 칩 -->
              <div class="chip-display">
                <svg width="20" height="20" viewBox="0 0 120 120" xmlns="http://www.w3.org/2000/svg">
                  <circle cx="60" cy="60" r="58" fill="#5FB57C" stroke="#3D8B5C" stroke-width="2"/>
                  <circle cx="60" cy="60" r="54" fill="none" stroke="#2A6B45" stroke-width="8" stroke-dasharray="8 4"/>
                  <circle cx="60" cy="60" r="38" fill="#FFFFFF"/>
                  <text x="60" y="63" font-family="Arial, sans-serif" font-size="16" font-weight="bold" fill="#3D8B5C" text-anchor="middle">10K</text>
                </svg>
                <span class="chip-count">{{ userChips.chip10000 }}개</span>
              </div>
            </div>

            <!-- Right Column -->
            <div class="chip-column">
              <!-- 100,000원 칩 -->
              <div class="chip-display">
                <svg width="20" height="20" viewBox="0 0 120 120" xmlns="http://www.w3.org/2000/svg">
                  <circle cx="60" cy="60" r="58" fill="#2B4B7C" stroke="#1A3A5C" stroke-width="2"/>
                  <circle cx="60" cy="60" r="54" fill="none" stroke="#0F2544" stroke-width="8" stroke-dasharray="8 4"/>
                  <circle cx="60" cy="60" r="38" fill="#FFFFFF"/>
                  <text x="60" y="63" font-family="Arial, sans-serif" font-size="14" font-weight="bold" fill="#1A3A5C" text-anchor="middle">100K</text>
                </svg>
                <span class="chip-count">{{ userChips.chip100000 }}개</span>
              </div>

              <!-- 1,000,000원 칩 -->
              <div class="chip-display">
                <svg width="20" height="20" viewBox="0 0 120 120" xmlns="http://www.w3.org/2000/svg">
                  <circle cx="60" cy="60" r="58" fill="#4A6B9C" stroke="#3A5A8C" stroke-width="2"/>
                  <circle cx="60" cy="60" r="54" fill="none" stroke="#2A4A7C" stroke-width="8" stroke-dasharray="8 4"/>
                  <circle cx="60" cy="60" r="38" fill="#FFFFFF"/>
                  <text x="60" y="63" font-family="Arial, sans-serif" font-size="14" font-weight="bold" fill="#3A5A8C" text-anchor="middle">1M</text>
                </svg>
                <span class="chip-count">{{ userChips.chip1000000 }}개</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Action Buttons -->
      <div class="action-section">
        <button @click="goToGameHistory" class="game-history-button">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M9 5H7C6.46957 5 5.96086 5.21071 5.58579 5.58579C5.21071 5.96086 5 6.46957 5 7V19C5 19.5304 5.21071 20.0391 5.58579 20.4142C5.96086 20.7893 6.46957 21 7 21H17C17.5304 21 18.0391 20.7893 18.4142 20.4142C18.7893 20.0391 19 19.5304 19 19V7C19 6.46957 18.7893 5.96086 18.4142 5.58579C18.0391 5.21071 17.5304 5 17 5H15M9 5C9 5.53043 9.21071 6.03914 9.58579 6.41421C9.96086 6.78929 10.4696 7 11 7H13C13.5304 7 14.0391 6.78929 14.4142 6.41421C14.7893 6.03914 15 5.53043 15 5M9 5C9 4.46957 9.21071 3.96086 9.58579 3.58579C9.96086 3.21071 10.4696 3 11 3H13C13.5304 3 14.0391 3.21071 14.4142 3.58579C14.7893 3.96086 15 4.46957 15 5M12 12H15M12 16H15M9 12H9.01M9 16H9.01" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <span>게임 기록</span>
        </button>

        <button @click="handleLogout" class="logout-button">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M9 21H5C4.46957 21 3.96086 20.7893 3.58579 20.4142C3.21071 20.0391 3 19.5304 3 19V5C3 4.46957 3.21071 3.96086 3.58579 3.58579C3.96086 3.21071 4.46957 3 5 3H9M16 17L21 12M21 12L16 7M21 12H9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <span>로그아웃</span>
        </button>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const nickname = ref('게스트')

const userChips = ref({
  chip1000: 0,
  chip5000: 0,
  chip10000: 0,
  chip100000: 0,
  chip1000000: 0
})

onMounted(() => {
  const user = localStorage.getItem('user')
  if (!user) {
    window.location.href = '/login'
  } else {
    // nickname이 있으면 nickname 사용, 없으면 username 사용
    const savedNickname = localStorage.getItem('nickname')
    nickname.value = savedNickname || user
    // TODO: 실제 API에서 칩 개수 가져오기
    userChips.value = {
      chip1000: 10,
      chip5000: 5,
      chip10000: 3,
      chip100000: 2,
      chip1000000: 1
    }
  }
})

const goToGameHistory = () => {
  router.push('/game-history')
}

const handleLogout = () => {
  localStorage.removeItem('user')
  localStorage.removeItem('nickname')
  window.location.href = '/'
}
</script>

<style scoped>
.profile-page {
  min-height: calc(100vh - 200px);
  background: var(--light-gray);
}

.page-title {
  text-align: center;
  font-size: 2rem;
  font-weight: 700;
  margin: 2rem 0;
  color: var(--dark);
}

.main-content {
  max-width: 600px;
  margin: 0 auto;
  padding: 2rem 1rem;
}

/* Profile Card */
.profile-card {
  background: var(--white);
  border-radius: 20px;
  padding: 3rem 2rem;
  box-shadow: 0 4px 12px var(--shadow);
  margin-bottom: 2rem;
}

.profile-info-wrapper {
  display: flex !important;
  flex-direction: row !important;
  justify-content: space-between !important;
  align-items: flex-start !important;
  gap: 2rem;
}

.profile-left {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  flex-shrink: 0;
}

.profile-avatar {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100px;
  height: 100px;
  margin-bottom: 1rem;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary) 0%, var(--secondary) 100%);
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
}

.profile-nickname {
  font-size: 1.75rem;
  font-weight: 700;
  color: var(--dark);
}

/* Chips Display */
.chips-container {
  display: flex;
  flex-direction: row;
  gap: 1rem;
  align-items: flex-start;
}

.chip-column {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.chip-display {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.chip-count {
  font-size: 0.75rem;
  color: var(--gray);
  font-weight: 600;
  min-width: 35px;
}

/* Action Section */
.action-section {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.game-history-button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  width: 100%;
  padding: 1.25rem;
  background: var(--white);
  color: var(--primary);
  border: 2px solid var(--primary);
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px var(--shadow);
}

.game-history-button:hover {
  background: var(--primary);
  color: var(--white);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
}

.game-history-button svg {
  stroke: currentColor;
}

.logout-button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  width: 100%;
  padding: 1.25rem;
  background: var(--white);
  color: var(--danger);
  border: 2px solid var(--danger);
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px var(--shadow);
}

.logout-button:hover {
  background: var(--danger);
  color: var(--white);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.3);
}

.logout-button svg {
  stroke: currentColor;
}

@media (max-width: 768px) {
  .page-title {
    font-size: 1.5rem;
  }

  .profile-nickname {
    font-size: 1.5rem;
  }

  .profile-info-wrapper {
    gap: 1rem;
  }

  .profile-avatar {
    width: 80px;
    height: 80px;
  }

  .chip-display svg {
    width: 16px;
    height: 16px;
  }

  .chip-count {
    font-size: 0.65rem;
  }
}
</style>
