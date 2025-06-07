<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-100">
    <div class="bg-white p-8 rounded-xl shadow-md w-full max-w-md">
      <h2 class="text-2xl font-bold text-center mb-6">📝 アカウント登録</h2>
      <form @submit.prevent="handleRegister">
        <input
            v-model="username"
            type="text"
            placeholder="ユーザー名"
            class="w-full mb-4 px-4 py-2 border rounded-lg"
        />
        <input
            v-model="email"
            type="email"
            placeholder="メールアドレス"
            class="w-full mb-4 px-4 py-2 border rounded-lg"
        />
        <input
            v-model="password"
            type="password"
            placeholder="パスワード"
            class="w-full mb-6 px-4 py-2 border rounded-lg"
        />
        <button
            :disabled="isSubmitting"
            type="submit"
            class="w-full py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
        >
          登録する
        </button>
      </form>
      <p class="mt-4 text-sm text-center">
        すでにアカウントをお持ちですか？
        <router-link to="/login" class="text-blue-600 hover:underline">ログイン</router-link>
      </p>
      <p v-if="errorMessage" class="text-red-500 text-sm mt-4 text-center">
        {{ errorMessage }}
      </p>
    </div>
  </div>
</template>

<script setup>
import {ref} from 'vue'
import {register} from "@/services/api.js";
import {useRouter} from 'vue-router'

const router = useRouter()

const username = ref('')
const email = ref('')
const password = ref('')
const errorMessage = ref('')
const isSubmitting = ref(false)

const handleRegister = async () => {
  if (isSubmitting.value) return
  isSubmitting.value = true
  try {
    await register(username.value, password.value, email.value)
    router.push('/login')
  } catch (err) {
    console.error(err)
    errorMessage.value = err?.response?.data?.message || '登録に失敗しました'
  } finally {
    isSubmitting.value = false
  }
}
</script>