<!-- src/App.vue -->
<template>
  <div class="app-container">
    <h1>📊 GitStatHub</h1>
    <p>桜影 の GitHub プロジェクト一覧</p>
    <div class="profile-info">
      <img :src="user.avatarUrl" alt="avatar" class="avatar"/>
      <p class="username">
        {{ user.login }}
        <a :href="`https://github.com/${user.login}`" class="repo-link" target="_blank" rel="noopener noreferrer">GitHub で見る</a>
      </p>
    </div>

    <!-- 🔽 タブ切り替え -->
    <div class="tab-buttons">
      <button :class="{ active: viewTab === 'list' }" @click="viewTab = 'list'">📦 一覧</button>
      <button :class="{ active: viewTab === 'chart' }" @click="viewTab = 'chart'">📊 チャート</button>
      <button :class="{ active: viewTab === 'calendar' }" @click="viewTab = 'calendar'">📅 カレンダー</button>
    </div>

    <!-- 🔽 コンテンツエリア -->
    <div v-if="viewTab === 'list'" class="repo-list">
      <RepoCard v-for="repo in paginatedRepos" :key="repo.id" :repo="repo" />

      <!-- ページングボタン -->
      <div class="pagination">
        <button @click="goToPage(currentPage - 1)" :disabled="currentPage === 1" class="page-btn">
          ← 前へ
        </button>
        <span class="page-info">ページ {{ currentPage }} / {{ totalPages }}</span>
        <button @click="goToPage(currentPage + 1)" :disabled="currentPage === totalPages" class="page-btn">
          次へ →
        </button>
      </div>
    </div>
    <div v-else-if="viewTab === 'chart'">
      <RepoLanguageChart :repos="repos" />
    </div>
    <div v-else>
      <ContributionCalendar :username="user.login" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, computed } from 'vue'
import { getRepos } from '@/services/api'
import RepoCard from '@/components/RepoCard.vue'
import RepoLanguageChart from '@/components/RepoLanguageChart.vue'
import ContributionCalendar from '@/components/ContributionCalendar.vue'
import type { RepoInfo } from '@/types'

const repos = ref<RepoInfo[]>([])
const viewTab = ref<'list' | 'chart' | 'calendar'>('list')
const user = ref({ login: 'VerneZhong', avatarUrl: 'https://avatars.githubusercontent.com/u/28047190?s=400&u=aa42d63223ab9dacd73967056a49f1e69149071d&v=4' })

onMounted(async () => {
  try {
    const res = await getRepos()
    repos.value = res.data
  } catch (err) {
    console.error('GitHub リポジトリの取得に失敗しました:', err)
  }
})

const reposPerPage = 10
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
</script>

<style scoped>
.app-container {
  max-width: 960px;
  margin: 0 auto;
  padding: 2rem;
  font-family: 'Segoe UI', sans-serif;
  text-align: center;
}

.repo-list {
  margin-top: 2rem;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1.5rem;
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
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-top: 2rem;
  font-size: 0.95rem;
  font-weight: 500;
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
</style>