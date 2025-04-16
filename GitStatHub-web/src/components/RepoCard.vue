<template>
  <div class="card">
    <h2>{{ repo.name }}</h2>
    <p>{{ repo.description || 'No description' }}</p>
    <p>🌐 {{ repo.language || 'N/A' }} ｜ ⭐ {{ repo.stargazersCount }}</p>
    <p><strong>更新日:</strong> {{ formatDate(repo.updatedAt) }}</p>
  </div>
</template>

<script setup lang="ts">
import type { RepoInfo } from '@/types'

const props = defineProps<{
  repo: RepoInfo
}>()

function formatDate(dateStr: string): string {
  return new Date(dateStr).toLocaleDateString()
}
</script>

<style scoped>
.card {
  padding: 1rem;
  border-radius: 8px;
  background-color: #f9f9f9;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  text-align: left;
}
</style>