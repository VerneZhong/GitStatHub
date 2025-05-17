<!-- src/App.vue -->
<template>
  <div class="app-container">
    <h1>📊 GitStatHub</h1>
    <p>桜影 の GitHub プロジェクト一覧</p>
    <div class="profile-info">
      <img :src="user.avatarUrl" alt="avatar" class="avatar"/>
      <p class="username">
        {{ user.login }}
        <a :href="`https://github.com/${user.login}`" class="repo-link" target="_blank" rel="noopener noreferrer">GitHub
          で見る</a>
      </p>
    </div>

    <!-- 🔽 タブ切り替え -->
    <div class="tab-buttons">
      <button :class="{ active: viewTab === 'list' }" @click="viewTab = 'list'">📦 一覧</button>
      <button :class="{ active: viewTab === 'chart' }" @click="viewTab = 'chart'">📊 チャート</button>
      <button :class="{ active: viewTab === 'calendar' }" @click="viewTab = 'calendar'">📅 カレンダー</button>
    </div>

    <!-- 🔽 コンテンツエリア -->
    <div v-if="viewTab === 'list'" class="repo-section">
      <div class="repo-list">
        <RepoCard v-for="repo in paginatedRepos" :key="repo.id" :repo="repo" />
      </div>

      <!-- ページングボタン -->
      <div class="pagination">
        <button @click="goToPage(1)" :disabled="currentPage === 1" class="page-btn">≪</button>
        <button @click="goToPage(currentPage - 1)" :disabled="currentPage === 1" class="page-btn">←</button>
        <span class="page-info">ページ
          <input
              v-model.number="inputPage"
              @keyup.enter="jumpToPage"
              type="number"
              min="1"
              :max="totalPages"
              class="page-input" /> / {{ totalPages }}
        </span>
        <button @click="goToPage(currentPage + 1)" :disabled="currentPage === totalPages" class="page-btn">→</button>
        <button @click="goToPage(totalPages)" :disabled="currentPage === totalPages" class="page-btn">≫</button>
      </div>
    </div>

    <div v-else-if="viewTab === 'chart'">
      <RepoLanguageChart :repos="repos"/>
    </div>
    <div v-else>
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
body {
  margin: 0;
  padding: 0;
  font-family: 'Segoe UI', sans-serif;
  background-color: #f9f9f9;
}
.app-container {
  max-width: 960px;
  margin: 0 auto;
  padding: 2rem;
  font-family: 'Segoe UI', sans-serif;
  text-align: center;
}

.repo-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 2rem;
}

.repo-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1.5rem;
  width: 100%;
}

.repo-link {
  display: inline-block;
  margin-top: 0.5rem;
  color: #0366d6;
  font-size: 0.875rem;
  text-decoration: none;
}

.repo-link:hover {
  text-decoration: underline;
}

.tab-buttons {
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin: 1.5rem 0;
}

.tab-buttons button {
  padding: 0.5rem 1rem;
  background-color: #eee;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.2s ease;
}

.tab-buttons button.active {
  background-color: #007acc;
  color: white;
}

.pagination {
  margin-top: 3rem;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
}

.page-btn {
  padding: 0.5rem 1rem;
  background-color: #f3f4f6;
  border: 1px solid #d1d5db;
  border-radius: 0.5rem;
  cursor: pointer;
  transition: all 0.2s ease-in-out;
}

.page-btn:hover:not(:disabled) {
  background-color: #e5e7eb;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  min-width: 110px;
  text-align: center;
}

.page-input {
  width: 3rem;
  text-align: center;
  border: 1px solid #d1d5db;
  border-radius: 0.375rem;
  padding: 0.25rem;
  margin: 0 0.25rem;
  font-size: 0.9rem;
}
</style>