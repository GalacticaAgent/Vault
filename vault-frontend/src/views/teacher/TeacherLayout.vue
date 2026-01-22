<template>
  <div class="teacher-layout">
    <Header />
    <div class="main-content">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import Header from '@/components/common/Header.vue'
</script>

<style scoped>
.teacher-layout {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  overflow-y: auto;
  background: #f5f7fa;
}
</style>

