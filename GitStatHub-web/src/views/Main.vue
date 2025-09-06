<template>
  <div class="absolute top-4 right-4">
    <button
        @click="logout"
        class="flex items-center gap-2 bg-white hover:bg-gray-100 text-gray-800 border border-gray-300 px-4 py-2 rounded-full shadow-sm transition"
    >
      <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5 text-gray-600" viewBox="0 0 24 24" fill="currentColor">
        <path d="M16 17v1a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h8a2 2 0 0 1 2 2v1h-2V6H6v12h8v-1h2ZM21 12l-4-4v3h-5v2h5v3l4-4Z"/>
      </svg>
      ログアウト
    </button>
  </div>
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
      <button :class="['px-4 py-2 rounded font-medium border', viewTab === 'search' ? 'bg-blue-500 text-white' : 'bg-white text-gray-800 border-gray-300']" @click="viewTab = 'search'">🔎 他のユーザー</button>
    </div>

    <!-- リポジトリ一覧 -->
    <div v-if="viewTab === 'list'" class="space-y-6">
      <!-- ソートオプション -->
      <div class="flex items-center gap-4 mt-4">
        <span class="text-gray-700 font-medium">並び替え:</span>
        <div class="flex gap-2">
          <button
              @click="setSort('updated')"
              :class="[
        'px-3 py-1 border rounded-full text-sm transition',
        sortKey === 'updated' ? 'bg-blue-500 text-white border-blue-500' : 'bg-white text-gray-800 border-gray-300 hover:bg-gray-100'
      ]"
          >更新日</button>

          <button
              @click="setSort('stars')"
              :class="[
        'px-3 py-1 border rounded-full text-sm transition',
        sortKey === 'stars' ? 'bg-blue-500 text-white border-blue-500' : 'bg-white text-gray-800 border-gray-300 hover:bg-gray-100'
      ]"
          >⭐ スター数</button>

          <button
              @click="setSort('name')"
              :class="[
        'px-3 py-1 border rounded-full text-sm transition',
        sortKey === 'name' ? 'bg-blue-500 text-white border-blue-500' : 'bg-white text-gray-800 border-gray-300 hover:bg-gray-100'
      ]"
          >🔤 名前</button>
        </div>

        <button
            @click="toggleSortOrder"
            class="ml-2 px-2 py-1 border rounded-full text-sm transition bg-white text-gray-800 border-gray-300 hover:bg-gray-100"
            :title="sortOrder === 'desc' ? '降順' : '昇順'"
        >
          <span v-if="sortOrder === 'desc'">⬇</span>
          <span v-else>⬆</span>
        </button>
      </div>
      <div v-if="paginatedRepos.length === 0" class="text-gray-500 text-center">
        リポジトリが見つかりませんでした。
      </div>
      <div v-else class="grid gap-4">
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
              @blur="jumpToPage"
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

    <div v-else-if="viewTab === 'search'" class="mt-6">
      <GithubRepoList />
    </div>

    <div v-else class="mt-6">
      <ContributionCalendar :username="user.login"/>
    </div>

  </div>
</template>

<script setup lang="ts">
import {onMounted, ref, computed, watch} from 'vue'
import {getRepos, getUserInfo} from '@/services/api'
import RepoCard from '@/components/RepoCard.vue'
import RepoLanguageChart from '@/components/RepoLanguageChart.vue'
import ContributionCalendar from '@/components/ContributionCalendar.vue'
import GithubRepoList from '@/components/GithubRepoList.vue'
import type {RepoInfo} from '@/types'

const repos = ref<RepoInfo[]>([])
const viewTab = ref<'list' | 'chart' | 'calendar' | 'search'>('list')
const user = ref({
  login: '',
  avatarUrl: 'https://avatars.githubusercontent.com/u/28047190?s=400&u=aa42d63223ab9dacd73967056a49f1e69149071d&v=4'
})
const sortKey = ref<'updated' | 'stars' | 'name'>(localStorage.getItem('sortKey') as any || 'updated')
const sortOrder = ref<'asc' | 'desc'>(localStorage.getItem('sortOrder') as any || 'desc')
function setSort(key: typeof sortKey.value) {
  sortKey.value = key
}

function toggleSortOrder() {
  sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc'
}

// 自动保存到 localStorage
watch([sortKey, sortOrder], () => {
  localStorage.setItem('sortKey', sortKey.value)
  localStorage.setItem('sortOrder', sortOrder.value)
  currentPage.value = 1
})

onMounted(async () => {
  try {
    const res = await getRepos()
    repos.value = res.data
  } catch (err) {
    console.error('GitHub リポジトリの取得に失敗しました:', err)
  }
})

onMounted(async () => {
  const username = localStorage.getItem('username');
  if (username) {
    const userInfo = await getUserInfo(username);
    user.value.login = userInfo.gitAccount;
  }
})

onMounted(() => {
  const savedTab = localStorage.getItem('viewTab')
  if (savedTab === 'chart' || savedTab === 'calendar') {
    viewTab.value = savedTab
  }
})

watch(viewTab, (val) => {
  localStorage.setItem('viewTab', val)
})

const reposPerPage = 5
const currentPage = ref(1)
const totalPages = computed(() => Math.ceil(repos.value.length / reposPerPage))
const paginatedRepos = computed(() =>
    sortedRepos.value.slice((currentPage.value - 1) * reposPerPage, currentPage.value * reposPerPage)
)

const sortedRepos = computed(() => {
  const copied = [...repos.value]
  let sorted = copied

  if (sortKey.value === 'updated') {
    sorted = copied.sort((a, b) =>
        new Date(a.updated_at).getTime() - new Date(b.updated_at).getTime()
    )
  } else if (sortKey.value === 'stars') {
    sorted = copied.sort((a, b) =>
        a.stargazers_count - b.stargazers_count
    )
  } else if (sortKey.value === 'name') {
    sorted = copied.sort((a, b) =>
        a.name.localeCompare(b.name)
    )
  }

  return sortOrder.value === 'asc' ? sorted : sorted.reverse()
})

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

function logout() {
  localStorage.removeItem('authToken')
  window.location.href = '/'
}
</script>