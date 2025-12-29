<template>
  <div class="category-page">
    <main class="main-content">
      <h1 class="page-title">게임 선택</h1>

      <div class="game-grid">
        <router-link to="/bigwheel" class="game-card">
          <div class="game-icon">
            <svg width="80" height="80" viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
              <circle cx="100" cy="100" r="95" fill="url(#wheelGradient)" stroke="#333" stroke-width="3"/>
              <defs>
                <linearGradient id="wheelGradient" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" style="stop-color:#6366f1;stop-opacity:1" />
                  <stop offset="100%" style="stop-color:#8b5cf6;stop-opacity:1" />
                </linearGradient>
              </defs>
              <line x1="100" y1="5" x2="100" y2="195" stroke="#fff" stroke-width="2"/>
              <line x1="5" y1="100" x2="195" y2="100" stroke="#fff" stroke-width="2"/>
              <circle cx="100" cy="100" r="15" fill="#fff"/>
            </svg>
          </div>
          <h2>빅휠</h2>
          <p>행운의 빅휠을 돌려보세요!</p>
        </router-link>
      </div>
    </main>
  </div>
</template>

<script setup>
</script>

<style scoped>
.category-page {
  min-height: calc(100vh - 130px);
  background: linear-gradient(180deg, #1a0a2e 0%, #0d1117 50%, #0a1628 100%);
  padding-bottom: 80px;
}

.page-title {
  text-align: center;
  font-size: 1.5rem;
  font-weight: 700;
  margin: 1.5rem 0;
  background: linear-gradient(135deg, #ff2d7b 0%, #9945ff 50%, #00d9ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.game-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 1rem;
  max-width: 400px;
  margin: 0 auto;
  padding: 1rem;
}

.game-card {
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 16px;
  padding: 2rem;
  text-align: center;
  transition: all 0.3s;
  cursor: pointer;
  text-decoration: none;
}

.game-card:hover {
  transform: translateY(-8px);
  border-color: rgba(153, 69, 255, 0.5);
  box-shadow: 0 0 30px rgba(153, 69, 255, 0.3), 0 0 60px rgba(255, 45, 123, 0.15);
}

.game-icon {
  margin-bottom: 1rem;
  filter: drop-shadow(0 0 15px rgba(153, 69, 255, 0.5));
}

.game-card h2 {
  font-size: 1.5rem;
  font-weight: 700;
  background: linear-gradient(135deg, #ff2d7b 0%, #9945ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 0.5rem;
}

.game-card p {
  color: rgba(255, 255, 255, 0.6);
  font-size: 0.875rem;
}
</style>
