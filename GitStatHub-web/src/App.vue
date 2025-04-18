<!-- src/App.vue -->
<template>
  <div class="app-container">
    <h1>📊 GitStatHub</h1>
    <p>桜影 の GitHub プロジェクト一覧</p>
    <div class="profile-info">
      <img :src="user.avatarUrl" alt="avatar" class="avatar"/>
      <p class="username">
        {{ user.login }}
        <a :href="`https://github.com/${user.login}`" class="repo-link">GitHub で見る</a>
      </p>
    </div>

    <!-- 🔽 选项卡切换 -->
    <div class="tab-buttons">
      <button :class="{ active: viewTab === 'list' }" @click="viewTab = 'list'">📦 一覧</button>
      <button :class="{ active: viewTab === 'chart' }" @click="viewTab = 'chart'">📊 チャート</button>
      <button :class="{ active: viewTab === 'calendar' }" @click="viewTab = 'calendar'">📅 カレンダー</button>
    </div>

    <!-- 🔽 内容区域 -->
    <div v-if="viewTab === 'list'" class="repo-list">
      <RepoCard v-for="repo in repos" :key="repo.id" :repo="repo" />
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
import { onMounted, ref } from 'vue'
import { getRepos } from '@/services/api'
import RepoCard from '@/components/RepoCard.vue'
import RepoLanguageChart from '@/components/RepoLanguageChart.vue'
import ContributionCalendar from '@/components/ContributionCalendar.vue'
import type { RepoInfo } from '@/types'

const repos = ref<RepoInfo[]>([])
const viewMode = ref<'list' | 'chart'>('list')
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
</script>

<style scoped>
.app-container {
  max-width: 960px;
  margin: 0 auto;
  padding: 2rem;
  font-family: 'Segoe UI', sans-serif;
  text-align: center;
}

.toggle-btn {
  margin-top: 1rem;
  margin-bottom: 1.5rem;
  padding: 0.5rem 1.2rem;
  background-color: #007acc;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  transition: background-color 0.3s;
}
.toggle-btn:hover {
  background-color: #005fa3;
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
.card {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
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
</style>