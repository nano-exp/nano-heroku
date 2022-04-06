import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
    plugins: [react()],
    build: {
        target: ['es2020'],
        rollupOptions: {
            input: [
                './index.html',
                './src/nano/index.html',
                './src/nano/hls/index.html',
                './src/nano/object/index.html',
                './src/nano/pg/index.html',
            ],
        },
    },
})