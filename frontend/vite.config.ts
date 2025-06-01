// vite.config.ts
import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
      alias: {
        '@': path.resolve(__dirname, 'src'),
      },
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      },
      '/file': {
        target: 'http://localhost:8080', // 确保这是你后端服务的实际端口
        changeOrigin: true, // 保持为 true

      }
    }
  }
});
