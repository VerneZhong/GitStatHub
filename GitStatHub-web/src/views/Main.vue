<!-- src/App.vue -->
<template>
  <div class="max-w-5xl mx-auto px-4 py-6 space-y-6">
    <h1 class="text-3xl font-bold flex items-center gap-2">📊 GitStatHub</h1>
    <p class="text-lg text-gray-600">桜影 の GitHub プロジェクト一覧</p>

    <div class="flex items-center gap-4">
      <img :src="user.avatarUrl" alt="avatar" class="w-32 h-32 rounded-full object-cover shadow" />
      <p class="text-xl font-semibold">
        {{ user.login }}
        <a :href="`https://github.com/${user.login}`" target="_blank" rel="noopener noreferrer" class="ml-2 text-blue-600 underline">GitHub で見る</a>
      </p>
    </div>

    <!-- タブ切り替え -->
    <div class="flex gap-4 mt-4">
      <button :class="['px-4 py-2 rounded font-medium border', viewTab === 'list' ? 'bg-blue-500 text-white' : 'bg-white text-gray-800 border-gray-300']" @click="viewTab = 'list'">📦 一覧</button>
      <button :class="['px-4 py-2 rounded font-medium border', viewTab === 'chart' ? 'bg-blue-500 text-white' : 'bg-white text-gray-800 border-gray-300']" @click="viewTab = 'chart'">📊 チャート</button>
      <button :class="['px-4 py-2 rounded font-medium border', viewTab === 'calendar' ? 'bg-blue-500 text-white' : 'bg-white text-gray-800 border-gray-300']" @click="viewTab = 'calendar'">📅 カレンダー</button>
    </div>

    <!-- リポジトリ一覧 -->
    <div v-if="viewTab === 'list'" class="space-y-6">
      <div class="grid gap-4">
        <RepoCard v-for="repo in paginatedRepos" :key="repo.id" :repo="repo" />
      </div>

      <div class="flex items-center justify-center gap-2 mt-6">
        <button @click="goToPage(1)" :disabled="currentPage === 1" class="px-3 py-1 border rounded disabled:opacity-50">≪</button>
        <button @click="goToPage(currentPage - 1)" :disabled="currentPage === 1" class="px-3 py-1 border rounded disabled:opacity-50">←</button>

        <span class="flex items-center gap-2">
          ページ
          <input
              v-model.number="inputPage"
              @keyup.enter="jumpToPage"
              type="number"
              min="1"
              :max="totalPages"
              class="w-16 px-2 py-1 border rounded text-center"
          />
          / {{ totalPages }}
        </span>

        <button @click="goToPage(currentPage + 1)" :disabled="currentPage === totalPages" class="px-3 py-1 border rounded disabled:opacity-50">→</button>
        <button @click="goToPage(totalPages)" :disabled="currentPage === totalPages" class="px-3 py-1 border rounded disabled:opacity-50">≫</button>
      </div>
    </div>

    <div v-else-if="viewTab === 'chart'" class="mt-6">
      <RepoLanguageChart :repos="repos"/>
    </div>

    <div v-else class="mt-6">
      <ContributionCalendar :username="user.login"/>
    </div>
  </div>
</template>

<script setup lang="ts">
import {onMounted, ref, computed, watch} from 'vue'
import {getRepos, checkLogin} from '@/services/api'
import RepoCard from '@/components/RepoCard.vue'
import RepoLanguageChart from '@/components/RepoLanguageChart.vue'
import ContributionCalendar from '@/components/ContributionCalendar.vue'
import type {RepoInfo} from '@/types'

const repos = ref<RepoInfo[]>([])
const viewTab = ref<'list' | 'chart' | 'calendar'>('list')
const user = ref({
  login: 'VerneZhong',
  avatarUrl: 'https://avatars.githubusercontent.com/u/28047190?s=400&u=aa42d63223ab9dacd73967056a49f1e69149071d&v=4'
})

onMounted(async () => {
  try {
    const res = await getRepos()
    repos.value = res.data
  } catch (err) {
    console.error('GitHub リポジトリの取得に失敗しました:', err)
  }
})

const reposPerPage = 5
const currentPage = ref(1)
const totalPages = computed(() => Math.ceil(repos.value.length / reposPerPage))
const paginatedRepos = computed(() =>
    repos.value.slice((currentPage.value - 1) * reposPerPage, currentPage.value * reposPerPage)
)

function goToPage(page: number) {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
  }
}

const inputPage = ref(1)

watch(currentPage, (newPage) => {
  inputPage.value = newPage
})

function jumpToPage() {
  if (inputPage.value >= 1 && inputPage.value <= totalPages.value) {
    goToPage(inputPage.value)
  } else {
    alert(`1 〜 ${totalPages.value} の間で入力してください`)
    inputPage.value = currentPage.value
  }
}
</script>

<style scoped>

</style>