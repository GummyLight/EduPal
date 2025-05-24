<template>
  <div class="login-container">
    <!-- 背景图片层 -->
    <div 
      v-for="(image, index) in backgroundImages" 
      :key="index"
      class="background-layer"
      :class="{ 'active': index === currentImageIndex }"
      :style="{ backgroundImage: `url(${image})` }"
    ></div>
    
    <!-- 内容层 -->
    <div class="content-wrapper">
      <LoginForm />
    </div>
  </div>
</template>

<script setup lang="ts">
import LoginForm from '../components/LoginForm.vue'
import { useBackgroundSlider } from '../composables/useBackgroundSlider'

// 使用背景切换功能，每5秒切换一次
const { currentImageIndex, backgroundImages } = useBackgroundSlider(5000)
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
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  filter: blur(8px);
  transform: scale(1.1);
  z-index: 1;
  opacity: 0;
  transition: opacity 1.5s ease-in-out;
}

.background-layer.active {
  opacity: 1;
}

.content-wrapper {
  position: relative;
  z-index: 2;
  padding: 20px;
}
</style>
