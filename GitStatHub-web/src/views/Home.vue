<template>
  <div class="home">
    <RepoCard v-for="repo in repos" :key="repo.id" :repo="repo" />
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getRepos } from '@/services/api'
import RepoCard from '@/components/RepoCard.vue'

const repos = ref([])

onMounted(async () => {
  repos.value = await getRepos()
})
</script>