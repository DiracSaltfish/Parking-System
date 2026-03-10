import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  build: {
    rollupOptions: {
      output: {
        manualChunks(id) {
          if (id.includes('element-plus')) {
            return 'element-plus'
          }
          if (id.includes('node_modules')) {
            return 'vendor'
          }
        }
      }
    }
  },
  server: {
    port: 5173
  }
})
