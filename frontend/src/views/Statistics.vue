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
  background: linear-gradient(180deg, #1a0a2e 0%, #0d1117 50%, #0a1628 100%);
}

.page-header {
  background: linear-gradient(135deg, rgba(153, 69, 255, 0.15) 0%, rgba(0, 217, 255, 0.1) 100%);
  border-bottom: 1px solid rgba(153, 69, 255, 0.2);
  padding: 1rem;
  color: white;
  text-align: center;
}

.page-header h1 {
  font-size: 1.25rem;
  font-weight: 700;
  margin: 0;
  background: linear-gradient(135deg, #9945ff 0%, #00d9ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.content {
  padding: 0;
}
</style>
