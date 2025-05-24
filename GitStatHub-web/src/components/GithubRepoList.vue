<template>
  <div class="p-6 max-w-4xl mx-auto">
    <h1 class="text-2xl font-bold mb-4">🔍 GitHub 用户仓库查询</h1>

    <div class="flex gap-2 mb-6">
      <input
          v-model="username"
          @keyup.enter="fetchRepos"
          type="text"
          placeholder="输入 GitHub 用户名..."
          class="flex-1 px-4 py-2 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-400"
      />
      <button
          @click="fetchRepos"
          class="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition"
      >
        查询
      </button>
    </div>

    <div v-if="loading" class="text-gray-500">加载中...</div>
    <div v-if="error" class="text-red-500">获取失败：{{ error }}</div>

    <div v-if="repos.length > 0" class="grid gap-4">
      <div
          v-for="repo in repos"
          :key="repo.id"
          class="p-4 border border-gray-200 rounded-xl shadow-sm hover:shadow-md transition"
      >
        <a
            :href="repo.html_url"
            target="_blank"
            class="text-lg font-semibold text-blue-600 hover:underline"
        >
          {{ repo.name }}
        </a>
        <p class="text-sm text-gray-600">{{ repo.description || '无描述' }}</p>
        <div class="text-sm mt-2 flex flex-wrap gap-4 text-gray-500">
          <span>⭐ {{ repo.stargazers_count }}</span>
          <span>🧑‍💻 {{ repo.language || '未知语言' }}</span>
          <span>🔀 Forks: {{ repo.forks_count }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { queryRepos } from '@/services/api'

const username = ref('')
const repos = ref([])
const loading = ref(false)
const error = ref('')

const fetchRepos = async () => {
  if (!username.value) return
  loading.value = true
  error.value = ''
  repos.value = []

  try {
    const data = await queryRepos(username.value)
    repos.value = data
  } catch (err: any) {
    error.value = err?.message || '请求失败'
  } finally {
    loading.value = false
  }
}
</script>