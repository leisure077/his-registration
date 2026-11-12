import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
    AutoImport({
      resolvers: [ElementPlusResolver()],
    }),
    Components({
      resolvers: [ElementPlusResolver()],
    })
  ],
  server: {
    host: '0.0.0.0',
    port: 5173, // 本机 80 端口已被 Docker(nginx) 占用，改用 5173
    proxy: {
      // 意思是：当请求以 /api 开头时，转发到后端 18080 端口
      '/api': {
        target: 'http://localhost:18080', 
        changeOrigin: true,
        // ⚠️ 重点：如果你的后端 Controller 里写的是 @RequestMapping("/api/user")，就把下面这行注释掉；
        // 如果你的后端 Controller 里写的是 @RequestMapping("/user")，没有 /api，就保留下面这行去掉 /api 前缀。
        // rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
      '@@': fileURLToPath(new URL('./src/components', import.meta.url)),
    },
    extensions: ['.js', '.json', '.jsx', '.ts', '.tsx', '.vue', '.mjs', '.css'],
  },
})