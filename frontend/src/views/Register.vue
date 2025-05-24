<template>
  <div class="login-container">
    <div class="background-layer"></div>
    <div class="content-wrapper">
      <LoginForm />
    </div>
  </div>
</template>

<script setup lang="ts">
import LoginForm from '../components/RegisterForm.vue';
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  width: 100%;
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
}

.background-layer {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url('../assets/LoginBackground.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  filter: blur(1px);
  transform: scale(1);
  z-index: 1;
}

.content-wrapper {
  position: relative;
  z-index: 2;
  padding: 20px;
}
</style>