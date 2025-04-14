<!-- src/App.vue -->
<template>
  <div class="app-container">
    <h1>📊 GitStatHub</h1>
    <p>VerneZhong の GitHub プロジェクト一覧</p>

    <div class="repo-list">
      <RepoCard
          v-for="repo in repos"
          :key="repo.id"
          :repo="repo"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getRepos } from '@/services/api'
import RepoCard from '@/components/RepoCard.vue'
import type { RepoInfo } from '@/types'

const repos = ref<RepoInfo[]>([])

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

.repo-list {
  margin-top: 2rem;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1.5rem;
}
</style>