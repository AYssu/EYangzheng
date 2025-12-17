<template>
  <div class="dashboard">
    <div class="title">欢迎来到控制台</div>
    <div class="desc">这里可以放置数据概览、快捷入口等内容。</div>
    <a-empty description="敬请期待" />
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.title {
  font-size: 20px;
  font-weight: 600;
}

.desc {
  color: #666;
}
</style>


