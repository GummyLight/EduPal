<template>
  <div class="credits-game" @keydown="handleKeyDown" @keyup="handleKeyUp" tabindex="0" ref="gameContainer">
    <!-- 背景 -->
    <div class="game-background"></div>
    
    <!-- 游戏标题 -->
    <div class="game-title" v-if="!gameStarted">
      <h1>🎮 贡献者致谢弹幕游戏</h1>
      <p>按空格键开始游戏</p>
      <p>使用方向键移动红心，躲避贡献者名字的攻击！</p>
    </div>
    
    <!-- 游戏区域 -->
    <div class="game-area" v-if="gameStarted">
      <!-- 玩家红心 -->
      <div 
        class="player-heart" 
        :style="{ left: player.x + 'px', top: player.y + 'px' }"
        :class="{ 'invulnerable': player.invulnerable }"
      >
        ❤️
      </div>
      
      <!-- 弹幕（贡献者名字） -->
      <div 
        v-for="bullet in bullets" 
        :key="bullet.id"
        class="bullet"
        :style="{ 
          left: bullet.x + 'px', 
          top: bullet.y + 'px',
          color: bullet.color,
          fontSize: bullet.size + 'px',
          transform: `rotate(${bullet.rotation}deg)`
        }"
      >
        {{ bullet.text }}
      </div>
      
      <!-- 游戏UI -->
      <div class="game-ui">
        <div class="health-bar">
          <span v-for="i in player.maxHealth" :key="i" class="health-heart">
            {{ i <= player.health ? '❤️' : '🖤' }}
          </span>
        </div>
        <div class="score">得分: {{ score }}</div>
        <div class="time">倒计时: {{ Math.max(0, Math.floor((gameDuration - gameTimer) / 1000)) }}s</div>
      </div>
    </div>
    
    <!-- 游戏结束画面 -->
    <div class="game-over" v-if="gameOver">
      <h1 v-if="victory">🎉 恭喜通关！</h1>
      <h1 v-else>💔 游戏结束</h1>
      <p>最终得分: {{ score }}</p>
      <p v-if="victory">感谢所有为项目做出贡献的开发者们！</p>
      <div class="final-credits" v-if="victory">
        <h3 @click="openTeamGitHub" class="team-title clickable">🌟 EduPal开发团队 🌟</h3>
        <div class="developer-showcase">
          <div v-for="developer in realDevelopers" :key="developer.name" 
               @click="openGitHub(developer.github)"
               class="developer-card clickable" 
               :class="{ 'center-position': developer.isCenter }">
            <div class="developer-avatar">
              <img :src="developer.avatar" :alt="developer.name" />
            </div>
            <div class="developer-name">{{ developer.name }}</div>
            <div class="developer-role">{{ developer.role }}</div>
          </div>
        </div>
      </div>
      <button @click="restartGame" class="restart-btn">重新开始</button>
      <button @click="goBack" class="back-btn">返回登录</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';

const router = useRouter();
const gameContainer = ref<HTMLElement>();

// 背景音乐播放器
const bgmPlayer = ref<HTMLAudioElement | null>(null);

// 游戏状态
const gameStarted = ref(false);
const gameOver = ref(false);
const victory = ref(false);
const score = ref(0);
const gameDuration = 60000; // 60秒游戏总时长
const gameTimer = ref(0);
const gameProgress = ref(0);

// 玩家状态
const player = ref({
  x: 400,
  y: 300,
  width: 20,
  height: 20,
  health: 3,
  maxHealth: 3,
  invulnerable: false,
  speed: 5
});

// 按键状态
const keys = ref({
  left: false,
  right: false,
  up: false,
  down: false
});

// 弹幕数组
interface Bullet {
  id: number;
  x: number;
  y: number;
  vx: number;
  vy: number;
  text: string;
  color: string;
  size: number;
  rotation: number;
  rotationSpeed: number;
  width?: number;  // 实际文本宽度
  height?: number; // 实际文本高度
  // 新增弹幕样式属性
  patternType: 'straight' | 'spiral' | 'wave' | 'orbit' | 'zigzag' | 'bounce';
  time: number;     // 生存时间，用于计算复杂轨迹
  amplitude?: number; // 波浪/螺旋幅度
  frequency?: number; // 波浪/螺旋频率
  centerX?: number;   // 轨道中心X
  centerY?: number;   // 轨道中心Y
  radius?: number;    // 轨道半径
  angle?: number;     // 当前角度
  bounceDirection?: number; // 弹跳方向
}

const bullets = ref<Bullet[]>([]);
let bulletIdCounter = 0;

// 波次相关
const currentWave = ref(1);
const totalWaves = ref(5);

// 弹幕列表
const allContributors = [
  'curl调试也不行?', '再pull一下', '我push了呀', '代码截图发我', '代码逃跑了', '503', '400', '工作量更大了',
  '什么时候给我后端', '大人谢罪了', '我忘了做删除', '别急', '然后呢', '整个项目垮了', '这是什么奇怪报错', '全是红',
  '我修一下', '什么b动静', 'Stack Overflow', 'access denied', '前端都他妈写完了', '自己不会重启？', 'git上没有啊', '什么时候有的需求',
  '自己写文档', '你药剂吧干啥', 'ai岂是如此不便之物', '文件名莫名其妙', '你端口号不对', '谁动我代码了', '为什么这么多参数', '我路由呢？'
];

const realDevelopers = [
  {
    name: '孙梓康',
    avatar: '/src/assets/contributorIcon1.jpg',
    role: '前端工程师',
    isCenter: false,
    github: 'https://github.com/Ripple-Sunzikang'
  },
  {
    name: '谢宇阳',
    avatar: '/src/assets/contributorIcon2.jpg',
    role: '前端工程师',
    isCenter: false,
    github: 'https://github.com/XunMengDeFengZheng'
  },
  {
    name: '李昶瑾',
    avatar: '/src/assets/contributorIcon3.jpg',
    role: '项目架构师',
    isCenter: true,
    github: 'https://github.com/GummyLight'
  },
  {
    name: '梁耀欣',
    avatar: '/src/assets/contributorIcon4.jpg',
    role: '项目负责人',
    isCenter: false,
    github: 'https://github.com/liangxinlizi'
  },
  {
    name: '田昊',
    avatar: '/src/assets/contributorIcon5.jpg',
    role: '后端工程师',
    isCenter: false,
    github: 'https://github.com/Rubiaaaaa'
  }
];

// 游戏循环
let gameLoopInterval: NodeJS.Timeout;
let spawnInterval: NodeJS.Timeout;

// 键盘事件处理
const handleKeyDown = (event: KeyboardEvent) => {
  event.preventDefault();
  
  if (!gameStarted.value && event.code === 'Space') {
    startGame();
    return;
  }
  
  if (!gameStarted.value || gameOver.value) return;
  
  switch (event.code) {
    case 'ArrowLeft':
      keys.value.left = true;
      break;
    case 'ArrowRight':
      keys.value.right = true;
      break;
    case 'ArrowUp':
      keys.value.up = true;
      break;
    case 'ArrowDown':
      keys.value.down = true;
      break;
  }
};

const handleKeyUp = (event: KeyboardEvent) => {
  switch (event.code) {
    case 'ArrowLeft':
      keys.value.left = false;
      break;
    case 'ArrowRight':
      keys.value.right = false;
      break;
    case 'ArrowUp':
      keys.value.up = false;
      break;
    case 'ArrowDown':
      keys.value.down = false;
      break;
  }
};

// 开始游戏
const startGame = async () => {
  gameStarted.value = true;
  gameOver.value = false;
  victory.value = false;
  score.value = 0;
  currentWave.value = 1;
  player.value = {
    x: 400,
    y: 300,
    width: 20,
    height: 20,
    health: 3,
    maxHealth: 3,
    invulnerable: false,
    speed: 5
  };
  bullets.value = [];
  gameTimer.value = 0; // 重置计时器
  
  // 播放背景音乐
  if (bgmPlayer.value) {
    try {
      bgmPlayer.value.currentTime = 0; // 重置到开始
      await bgmPlayer.value.play();
      console.log('BGM started playing');
    } catch (error) {
      console.warn("BGM autoplay blocked by browser. User interaction required:", error);
      // 如果自动播放被阻止，添加一个提示
      ElMessage({
        type: 'info',
        message: '点击页面任意位置开启背景音乐',
        duration: 3000
      });
      
      // 添加点击事件监听器来播放音乐
      const playBGMOnClick = async () => {
        try {
          if (bgmPlayer.value) {
            await bgmPlayer.value.play();
            console.log('BGM started after user interaction');
          }
          document.removeEventListener('click', playBGMOnClick);
        } catch (err) {
          console.error('Failed to play BGM after user interaction:', err);
        }
      };
      document.addEventListener('click', playBGMOnClick);
    }
  }
  
  // 开始游戏循环
  gameLoopInterval = setInterval(gameLoop, 16); // 60 FPS
  spawnInterval = setInterval(spawnBullet, 800); // 每800ms生成一个弹幕
};

// 游戏主循环
const gameLoop = () => {
  updatePlayer();
  updateBullets();
  checkCollisions();
  updateScore();
  checkWaveProgress();
  updateTimer(); // 添加计时器更新
};

// 更新玩家位置
const updatePlayer = () => {
  const speed = player.value.speed;
  
  if (keys.value.left && player.value.x > 0) {
    player.value.x -= speed;
  }
  if (keys.value.right && player.value.x < 780) {
    player.value.x += speed;
  }
  if (keys.value.up && player.value.y > 0) {
    player.value.y -= speed;
  }
  if (keys.value.down && player.value.y < 580) {
    player.value.y += speed;
  }
  
  // 更新无敌状态
  if (player.value.invulnerable) {
    setTimeout(() => {
      player.value.invulnerable = false;
    }, 1000);
  }
};

// 生成弹幕
const spawnBullet = () => {
  if (!gameStarted.value || gameOver.value) return;
  
  const contributor = allContributors[Math.floor(Math.random() * allContributors.length)];
  const colors = ['#ff6b6b', '#4ecdc4', '#45b7d1', '#f9ca24', '#f0932b', '#eb4d4b', '#6c5ce7'];
  
  // 随机从四个方向生成
  const side = Math.floor(Math.random() * 4);
  let x, y, vx, vy;
  
  switch (side) {
    case 0: // 从上方
      x = Math.random() * 800;
      y = -50;
      vx = (Math.random() - 0.5) * 2;
      vy = 1 + Math.random() * 2;
      break;
    case 1: // 从右方
      x = 850;
      y = Math.random() * 600;
      vx = -(1 + Math.random() * 2);
      vy = (Math.random() - 0.5) * 2;
      break;
    case 2: // 从下方
      x = Math.random() * 800;
      y = 650;
      vx = (Math.random() - 0.5) * 2;
      vy = -(1 + Math.random() * 2);
      break;
    case 3: // 从左方
      x = -50;
      y = Math.random() * 600;
      vx = 1 + Math.random() * 2;
      vy = (Math.random() - 0.5) * 2;
      break;
  }
  
  const bullet: Bullet = {
    id: bulletIdCounter++,
    x: x!,
    y: y!,
    vx: vx! * (1 + currentWave.value * 0.2), // 波次越高速度越快
    vy: vy! * (1 + currentWave.value * 0.2),
    text: contributor,
    color: colors[Math.floor(Math.random() * colors.length)],
    size: 16 + Math.random() * 8,
    rotation: Math.random() * 360,
    rotationSpeed: (Math.random() - 0.5) * 5,
    patternType: 'straight', // 添加必需的属性
    time: 0 // 添加必需的属性
  };
  
  // 立即计算文本尺寸
  const dimensions = measureTextSize(bullet.text, bullet.size);
  bullet.width = dimensions.width;
  bullet.height = dimensions.height;
  
  bullets.value.push(bullet);
};

// 更新弹幕
const updateBullets = () => {
  bullets.value = bullets.value.filter(bullet => {
    bullet.x += bullet.vx;
    bullet.y += bullet.vy;
    bullet.rotation += bullet.rotationSpeed;
    bullet.time += 16; // 更新生存时间
    
    // 移除超出边界的弹幕
    return bullet.x > -100 && bullet.x < 900 && bullet.y > -100 && bullet.y < 700;
  });
};

// 测量文本实际尺寸的辅助函数
const measureTextSize = (text: string, fontSize: number): { width: number; height: number } => {
  // 创建临时canvas来测量文本
  const canvas = document.createElement('canvas');
  const context = canvas.getContext('2d');
  if (!context) {
    // 如果无法获取context，使用估算值
    return {
      width: text.length * fontSize * 0.6,
      height: fontSize
    };
  }
  
  context.font = `bold ${fontSize}px 'Courier New', monospace`;
  const metrics = context.measureText(text);
  
  return {
    width: metrics.width,
    height: fontSize * 1.2 // 字体高度通常比字体大小略大
  };
};

// 更新弹幕尺寸信息
const updateBulletDimensions = () => {
  bullets.value.forEach(bullet => {
    if (!bullet.width || !bullet.height) {
      const dimensions = measureTextSize(bullet.text, bullet.size);
      bullet.width = dimensions.width;
      bullet.height = dimensions.height;
    }
  });
};

// 碰撞检测
const checkCollisions = () => {
  if (player.value.invulnerable) return;
  
  // 首先更新所有弹幕的尺寸信息
  updateBulletDimensions();
  
  for (const bullet of bullets.value) {
    // 使用实际测量的文本尺寸
    const textWidth = bullet.width || bullet.text.length * bullet.size * 0.6;
    const textHeight = bullet.height || bullet.size;
    
    // 玩家红心的实际尺寸 (❤️ emoji)
    const playerCenterX = player.value.x + player.value.width / 2;
    const playerCenterY = player.value.y + player.value.height / 2;
    const playerRadius = Math.min(player.value.width, player.value.height) / 2 * 0.8; // 稍微缩小碰撞半径
    
    // 考虑文本旋转的影响
    const rotationRad = (bullet.rotation * Math.PI) / 180;
    
    // 计算旋转后的文本边界框
    const cos = Math.abs(Math.cos(rotationRad));
    const sin = Math.abs(Math.sin(rotationRad));
    const rotatedWidth = textWidth * cos + textHeight * sin;
    const rotatedHeight = textWidth * sin + textHeight * cos;
    
    // 文本中心点
    const bulletCenterX = bullet.x + rotatedWidth / 2;
    const bulletCenterY = bullet.y + rotatedHeight / 2;
    
    // 更精确的圆形与旋转矩形碰撞检测
    // 将问题转换为局部坐标系
    const localX = playerCenterX - bulletCenterX;
    const localY = playerCenterY - bulletCenterY;
    
    // 反向旋转坐标点
    const cosInv = Math.cos(-rotationRad);
    const sinInv = Math.sin(-rotationRad);
    const rotatedLocalX = localX * cosInv - localY * sinInv;
    const rotatedLocalY = localX * sinInv + localY * cosInv;
    
    // 在局部坐标系中进行矩形与圆形碰撞检测
    const halfWidth = textWidth / 2;
    const halfHeight = textHeight / 2;
    
    const closestX = Math.max(-halfWidth, Math.min(rotatedLocalX, halfWidth));
    const closestY = Math.max(-halfHeight, Math.min(rotatedLocalY, halfHeight));
    
    const dx = rotatedLocalX - closestX;
    const dy = rotatedLocalY - closestY;
    const distance = Math.sqrt(dx * dx + dy * dy);
    
    if (distance < playerRadius) {
      // 碰撞发生
      player.value.health--;
      player.value.invulnerable = true;
      
      // 移除碰撞的弹幕
      bullets.value = bullets.value.filter(b => b.id !== bullet.id);
      
      // 增加得分（击中也算得分）
      score.value += 10;
      
      if (player.value.health <= 0) {
        endGame(false);
      }
      break;
    }
  }
};

// 更新分数
const updateScore = () => {
  score.value += 1;
};

// 更新计时器
const updateTimer = () => {
  gameTimer.value += 16; // 每帧增加16ms (60 FPS)
  
  // 检查时间是否到达
  if (gameTimer.value >= gameDuration) {
    endGame(true); // 时间到达，游戏胜利
  }
};

// 检查波次进度
const checkWaveProgress = () => {
  if (score.value > 0 && score.value % 1000 === 0) {
    if (currentWave.value < totalWaves.value) {
      currentWave.value++;
      // 清空当前弹幕，增加难度
      bullets.value = [];
    } else {
      // 胜利
      endGame(true);
    }
  }
};

// 结束游戏
const endGame = (isVictory: boolean) => {
  gameStarted.value = false;
  gameOver.value = true;
  victory.value = isVictory;
  
  clearInterval(gameLoopInterval);
  clearInterval(spawnInterval);
  
  // 不停止背景音乐，让它继续播放
  // stopBGM(); // 注释掉这一行
};

// 重新开始游戏
const restartGame = () => {
  gameOver.value = false;
  startGame();
};

// 返回登录页面
const goBack = () => {
  // 停止背景音乐
  stopBGM();
  router.push('/login');
};

// 打开开发者GitHub页面
const openGitHub = (url: string) => {
  window.open(url, '_blank');
};

// 打开团队GitHub页面
const openTeamGitHub = () => {
  window.open('https://github.com/GummyLight/EduPal/', '_blank');
};

// 初始化背景音乐
const initBGM = () => {
  try {
    bgmPlayer.value = new Audio('/ContributorsBgm.mp3');
    bgmPlayer.value.loop = true; // 循环播放
    bgmPlayer.value.volume = 0.3; // 降低音量为30%，避免太吵
    bgmPlayer.value.preload = 'auto'; // 预加载音频
    
    // 监听音频加载状态
    bgmPlayer.value.addEventListener('canplaythrough', () => {
      console.log('BGM loaded successfully');
    });
    
    bgmPlayer.value.addEventListener('error', (e) => {
      console.error('BGM loading error:', e);
    });
    
    bgmPlayer.value.addEventListener('loadstart', () => {
      console.log('BGM loading started');
    });
    
    bgmPlayer.value.addEventListener('loadeddata', () => {
      console.log('BGM data loaded');
    });
  } catch (error) {
    console.error('Failed to load BGM:', error);
  }
};

// 停止背景音乐
const stopBGM = () => {
  if (bgmPlayer.value) {
    bgmPlayer.value.pause();
    bgmPlayer.value.currentTime = 0;
  }
};

// 组件挂载和卸载
onMounted(() => {
  if (gameContainer.value) {
    gameContainer.value.focus();
  }
  // 初始化背景音乐
  initBGM();
});

onUnmounted(() => {
  clearInterval(gameLoopInterval);
  clearInterval(spawnInterval);
  // 停止背景音乐
  stopBGM();
});
</script>

<style scoped>
.credits-game {
  width: 100vw;
  height: 100vh;
  position: relative;
  background: linear-gradient(135deg, #0c0c0c 0%, #2d1b69 50%, #0c0c0c 100%);
  overflow: hidden;
  outline: none;
  font-family: 'Courier New', monospace;
}

.game-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: 
    radial-gradient(circle at 20% 80%, rgba(120, 119, 198, 0.3) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(255, 119, 198, 0.3) 0%, transparent 50%),
    radial-gradient(circle at 40% 40%, rgba(120, 219, 255, 0.2) 0%, transparent 50%);
  animation: backgroundPulse 4s ease-in-out infinite;
}

@keyframes backgroundPulse {
  0%, 100% { opacity: 0.8; }
  50% { opacity: 1; }
}

.game-title {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  color: white;
  z-index: 10;
}

.game-title h1 {
  font-size: 3rem;
  margin-bottom: 1rem;
  text-shadow: 0 0 20px rgba(255, 255, 255, 0.8);
  animation: titleGlow 2s ease-in-out infinite alternate;
}

@keyframes titleGlow {
  from { text-shadow: 0 0 20px rgba(255, 255, 255, 0.8); }
  to { text-shadow: 0 0 30px rgba(255, 255, 255, 1), 0 0 40px rgba(120, 119, 198, 0.8); }
}

.game-title p {
  font-size: 1.2rem;
  margin: 0.5rem 0;
  opacity: 0.9;
}

.game-area {
  position: relative;
  width: 800px;
  height: 600px;
  margin: 50px auto;
  border: 2px solid #7877c6;
  background: rgba(0, 0, 0, 0.3);
  box-shadow: 
    0 0 30px rgba(120, 119, 198, 0.5),
    inset 0 0 30px rgba(0, 0, 0, 0.5);
}

.player-heart {
  position: absolute;
  font-size: 20px;
  z-index: 5;
  transition: opacity 0.1s;
  filter: drop-shadow(0 0 10px rgba(255, 0, 0, 0.8));
}

.player-heart.invulnerable {
  animation: blink 0.1s infinite;
}

@keyframes blink {
  0%, 50% { opacity: 1; }
  51%, 100% { opacity: 0.3; }
}

.bullet {
  position: absolute;
  font-weight: bold;
  text-shadow: 
    0 0 10px currentColor,
    0 0 20px currentColor,
    0 0 30px currentColor;
  filter: drop-shadow(2px 2px 4px rgba(0, 0, 0, 0.5));
  z-index: 3;
  pointer-events: none;
  user-select: none;
  white-space: nowrap;
  overflow: visible;
  text-overflow: clip;
}

.game-ui {
  position: absolute;
  top: 10px;
  left: 10px;
  color: white;
  font-size: 18px;
  z-index: 10;
  text-shadow: 0 0 10px rgba(0, 0, 0, 0.8);
}

.health-bar {
  margin-bottom: 10px;
}

.health-heart {
  margin-right: 5px;
  filter: drop-shadow(0 0 5px rgba(255, 0, 0, 0.8));
}

.score, .wave {
  margin-bottom: 5px;
}

.game-over {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  color: white;
  z-index: 10;
  background: rgba(0, 0, 0, 0.85);
  padding: 2rem 2.5rem;
  border-radius: 25px;
  border: 2px solid #7877c6;
  box-shadow: 
    0 0 40px rgba(120, 119, 198, 0.6),
    inset 0 0 20px rgba(120, 119, 198, 0.1);
  backdrop-filter: blur(20px);
  max-width: 95vw;
  min-width: 900px;
}

.game-over h1 {
  font-size: 2.2rem;
  margin-bottom: 0.8rem;
  text-shadow: 0 0 20px currentColor;
}

.game-over p {
  font-size: 1.1rem;
  margin: 0.8rem 0;
}

.final-credits {
  margin: 1.5rem 0;
}

.final-credits h3 {
  margin-bottom: 1.5rem;
  color: #7877c6;
  font-size: 1.6rem;
  text-shadow: 0 0 15px rgba(120, 119, 198, 0.8);
  animation: titleGlow 2s ease-in-out infinite alternate;
}

.developer-showcase {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  flex-wrap: nowrap;
  max-width: 850px;
  margin: 0 auto;
  padding: 15px;
}

.developer-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  background: rgba(120, 119, 198, 0.2);
  border: 2px solid rgba(120, 119, 198, 0.5);
  border-radius: 18px;
  padding: 18px 12px;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
  min-width: 120px;
  max-width: 140px;
  flex: 1;
}

.developer-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(120, 119, 198, 0.3);
  border-color: rgba(120, 119, 198, 0.8);
  background: rgba(120, 119, 198, 0.3);
}

.developer-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  margin-bottom: 12px;
  border: 3px solid rgba(255, 255, 255, 0.4);
  transition: all 0.3s ease;
}

.developer-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.developer-card:hover .developer-avatar img {
  transform: scale(1.1);
}

.developer-name {
  font-size: 1rem;
  font-weight: bold;
  margin-bottom: 6px;
  text-shadow: 0 0 10px rgba(0, 0, 0, 0.8);
  letter-spacing: 0.5px;
}

.developer-role {
  font-size: 0.8rem;
  opacity: 0.9;
  text-align: center;
  line-height: 1.2;
  padding: 4px 8px;
  background: rgba(0, 0, 0, 0.2);
  border-radius: 10px;
  backdrop-filter: blur(5px);
}

.restart-btn, .back-btn {
  margin: 0.5rem;
  padding: 12px 24px;
  font-size: 1rem;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: bold;
}

.restart-btn {
  background: linear-gradient(135deg, #7877c6, #a855f7);
  color: white;
}

.restart-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(120, 119, 198, 0.4);
}

.back-btn {
  background: linear-gradient(135deg, #ef4444, #dc2626);
  color: white;
}

.back-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(239, 68, 68, 0.4);
}

/* 滚动条样式 */
.final-credits::-webkit-scrollbar {
  width: 6px;
}

.final-credits::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.3);
  border-radius: 3px;
}

.final-credits::-webkit-scrollbar-thumb {
  background: rgba(120, 119, 198, 0.6);
  border-radius: 3px;
}

.final-credits::-webkit-scrollbar-thumb:hover {
  background: rgba(120, 119, 198, 0.8);
}

/* 可点击元素样式 */
.clickable {
  cursor: pointer;
  transition: all 0.3s ease;
}

.team-title.clickable:hover {
  color: #7877c6;
  text-shadow: 0 0 15px rgba(120, 119, 198, 0.6);
  transform: scale(1.05);
}

.developer-card.clickable {
  cursor: pointer;
  user-select: none;
}

.developer-card.clickable:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 15px 35px rgba(120, 119, 198, 0.4);
  border-color: rgba(120, 119, 198, 1);
  background: rgba(120, 119, 198, 0.4);
}

.developer-card.clickable:active {
  transform: translateY(-5px) scale(0.98);
}
</style>
