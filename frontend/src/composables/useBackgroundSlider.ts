import { ref, onMounted, onUnmounted } from 'vue'

export function useBackgroundSlider(interval: number = 5000) {
  const currentImageIndex = ref(0)
  const isLoaded = ref(false)
  
  // 背景图片列表 - 您可以根据实际图片修改这个数组
  const backgroundImages = [
    '/src/assets/LoginBackground1.jpg',
    '/src/assets/LoginBackground2.jpg', // 临时使用相同图片
    '/src/assets/LoginBackground3.jpg', // 添加不同图片时请替换这些路径
  ]
  
  let timer: NodeJS.Timeout | null = null
  
  const nextImage = () => {
    currentImageIndex.value = (currentImageIndex.value + 1) % backgroundImages.length
  }
  
  const startSlider = () => {
    timer = setInterval(nextImage, interval)
  }
  
  const stopSlider = () => {
    if (timer) {
      clearInterval(timer)
      timer = null
    }
  }
  
  const getCurrentImage = () => {
    return backgroundImages[currentImageIndex.value]
  }
  
  const goToImage = (index: number) => {
    currentImageIndex.value = index
  }
  
  onMounted(() => {
    isLoaded.value = true
    startSlider()
  })
  
  onUnmounted(() => {
    stopSlider()
  })
  
  return {
    currentImageIndex,
    isLoaded,
    getCurrentImage,
    backgroundImages,
    startSlider,
    stopSlider,
    nextImage,
    goToImage
  }
}
