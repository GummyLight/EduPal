<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script setup lang="ts">
// App.vue 是根组件，不需要导入和挂载router
// router 已经在 main.ts 中配置好了
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

body {
  margin: 0;
  padding: 0;
}
</style>

