<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-100">
    <div class="bg-white p-8 rounded-xl shadow-md w-full max-w-md">
      <h2 class="text-2xl font-bold text-center mb-6">📝 アカウント登録</h2>
      <Form @submit="handleRegister" :validation-schema="schema">
        <Field
            name="username"
            type="text"
            placeholder="ユーザー名"
            class="w-full mb-2 px-4 py-2 border rounded-lg"
        />
        <ErrorMessage name="username" class="text-red-500 text-sm mb-2 block text-left" />

        <Field
            name="email"
            type="email"
            placeholder="メールアドレス"
            class="w-full mb-2 px-4 py-2 border rounded-lg"
        />
        <ErrorMessage name="email" class="text-red-500 text-sm mb-2 block text-left" />

        <Field
            name="password"
            type="password"
            placeholder="パスワード（6文字以上）"
            class="w-full mb-4 px-4 py-2 border rounded-lg"
        />
        <ErrorMessage name="password" class="text-red-500 text-sm mb-4 block text-left" />

        <button
            :disabled="isSubmitting"
            type="submit"
            class="w-full py-2 text-white rounded-lg transition"
            :class="isSubmitting ? 'bg-gray-400 cursor-not-allowed' : 'bg-blue-600 hover:bg-blue-700'"
        >
          登録する
        </button>
      </Form>
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
import { Form, Field, ErrorMessage } from 'vee-validate'
import * as yup from 'yup'
import {register, checkUsername} from "@/services/api.js";
import {useRouter} from 'vue-router'

const router = useRouter()

const errorMessage = ref('')
const isSubmitting = ref(false)

// ✅ Yup 校验规则
const schema = yup.object({
  username: yup.string()
      .required('ユーザー名を入力してください。')
      .test(
          'unique-check',
          'このユーザー名は既に使われています。',
          async (value) => {
            if (!value) return false
            try {
              return await checkUsername(value)
            } catch (e) {
              console.error('ユーザー名の確認に失敗しました', e)
              return false
            }
          }
      ),
  email: yup.string()
      .email('有効なメールアドレスを入力してください。')
      .required('メールアドレスを入力してください。'),
  password: yup.string()
      .min(6, 'パスワードは6文字以上で入力してください。')
      .required('パスワードを入力してください。')
})

const handleRegister = async (values) => {
  if (isSubmitting.value) return
  isSubmitting.value = true
  errorMessage.value = ''

  try {
    await register(values.username, values.password, values.email)
    router.push('/login')
  } catch (err) {
    console.error(err)
    errorMessage.value = err?.response?.data?.message || '登録に失敗しました'
  } finally {
    isSubmitting.value = false
  }
}
</script>