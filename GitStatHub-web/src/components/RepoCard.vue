<template>
  <div class="card">
    <h2>{{ repo.name }}</h2>

    <div
        class="description-wrapper"
        @mouseenter="showTooltip = true"
        @mouseleave="showTooltip = false"
    >
      <p class="description">
        {{ repo.description || 'No description available' }}
      </p>
      <div v-if="showTooltip && repo.description" class="tooltip">
        {{ repo.description }}
      </div>
    </div>

    <p>🌐 {{ repo.language || 'N/A' }} ｜ ⭐ {{ repo.stargazersCount }}</p>
    <p><strong>更新日:</strong> {{ formatDate(repo.updatedAt) }}</p>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { RepoInfo } from '@/types'

const props = defineProps<{
  repo: RepoInfo
}>()

const showTooltip = ref(false)

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
  position: relative;
}

/* 描述文本截断 */
.description-wrapper {
  position: relative;
  max-width: 100%;
}

.description {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100%;
  cursor: default;
}

/* Tooltip */
.tooltip {
  position: absolute;
  top: 100%;
  left: 0;
  background: rgba(0, 0, 0, 0.85);
  color: #fff;
  padding: 0.5rem;
  border-radius: 4px;
  white-space: normal;
  z-index: 10;
  margin-top: 4px;
  max-width: 300px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.2);
}
</style>