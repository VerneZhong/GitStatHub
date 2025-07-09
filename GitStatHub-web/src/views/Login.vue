<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-100">
    <div class="bg-white p-10 rounded-2xl shadow-xl w-full max-w-md">
      <h2 class="text-2xl font-bold text-center mb-6">
        🔐 GitStatHub ログイン
      </h2>
      <form @submit.prevent="handleLogin" class="space-y-4">
        <input
            v-model="username"
            type="text"
            placeholder="ユーザー名"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
        <input
            v-model="password"
            type="password"
            placeholder="パスワード"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
        <button
            type="submit"
            class="w-full py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition duration-200"
        >
          ログイン
        </button>
        <button
            type="button"
            @click="goToRegister"
            class="w-full py-2 border border-blue-600 text-blue-600 rounded-lg hover:bg-blue-50 transition duration-200"
        >
          登録
        </button>
      </form>
      <p v-if="errorMessage" class="text-red-500 text-sm mt-4 text-center">
        {{ errorMessage }}
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '@/services/api'

const username = ref('')
const password = ref('')
const errorMessage = ref('')
const router = useRouter()

const handleLogin = async () => {
  try {
    const res = await login(username.value, password.value)
    if (res.code === '200') {
      localStorage.setItem('authToken', res.token)
      localStorage.setItem('username', username.value);
      router.push('/app') // 登录成功后跳转
    } else {
      errorMessage.value = res.message
    }
  } catch (err) {
    console.error(err)
    errorMessage.value = 'ログインに失敗しました'
  }
}

function goToRegister() {
  router.push('/register')
}
</script>