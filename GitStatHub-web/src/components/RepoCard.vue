<template>
  <div class="card">
    <h2>{{ repo.name }}</h2>
    <p>{{ repo.description || 'No description' }}</p>
    <p>🌐 {{ repo.language || 'N/A' }} ｜ ⭐ {{ repo.stargazersCount }}</p>
    <small>更新日: {{ repo.updatedAt }}</small>
  </div>
</template>

<script setup lang="ts">
import type { RepoInfo } from '@/types'

const props = defineProps<{
  repo: RepoInfo
}>()
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