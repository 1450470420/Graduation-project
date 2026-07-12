<template>
  <!-- 根路由视图，所有页面都在这里渲染 -->
  <router-view />
</template>

<script setup>
// 根组件无需额外逻辑
</script>

<style>
/* 全局样式 */
html, body, #app {
  height: 100%;
  margin: 0;
  padding: 0;
}

/* 滚动条美化 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}
::-webkit-scrollbar-track {
  background: #f1f1f1;
}
::-webkit-scrollbar-thumb {
  background: #c0c0c0;
  border-radius: 3px;
}
::-webkit-scrollbar-thumb:hover {
  background: #a0a0a0;
}
</style>
