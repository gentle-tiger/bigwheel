<template>
  <div class="statistics-page">
    <!-- 상단 헤더 -->
    <div class="page-header">
      <h1>📊 게임 통계</h1>
    </div>

    <!-- GameHistory 컴포넌트를 여기에 통합 -->
    <div class="content">
      <GameHistory />
    </div>
  </div>
</template>

<script setup>
import GameHistory from './GameHistory.vue'
</script>

<style scoped>
.statistics-page {
  min-height: calc(100vh - 130px);
  padding-bottom: 80px;
  background: #f3f4f6;
}

.page-header {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  padding: 1.5rem 1rem;
  color: white;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.page-header h1 {
  font-size: 1.5rem;
  font-weight: 700;
  margin: 0;
}

.content {
  padding: 0;
}
</style>
