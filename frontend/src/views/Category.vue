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
  min-height: calc(100vh - 200px);
}

.page-title {
  text-align: center;
  font-size: 2rem;
  font-weight: 700;
  margin: 2rem 0;
  color: var(--dark);
}

.game-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
  max-width: 1000px;
  margin: 0 auto;
  padding: 2rem;
}

.game-card {
  background: var(--white);
  border-radius: 16px;
  padding: 2rem;
  text-align: center;
  box-shadow: 0 4px 12px var(--shadow);
  transition: transform 0.3s, box-shadow 0.3s;
  cursor: pointer;
}

.game-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 8px 24px rgba(99, 102, 241, 0.3);
}

.game-icon {
  margin-bottom: 1rem;
}

.game-card h2 {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--primary);
  margin-bottom: 0.5rem;
}

.game-card p {
  color: var(--gray);
  font-size: 0.875rem;
}
</style>
