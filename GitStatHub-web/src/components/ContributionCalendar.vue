<template>
  <div>
    <h2>年間アクティビティ</h2>
    <p>今月の貢献数: {{ currentMonthTotal }} / 総貢献数: {{ totalContributions }}</p>
    <ContributionGrid v-if="calendar" :weeks="calendar.weeks" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { getContributions } from '@/services/api'
import ContributionGrid from './ContributionGrid.vue'

const props = defineProps<{
  username: string
}>()

const calendar = ref<any | null>(null)

onMounted(async () => {
  calendar.value = await getContributions(props.username)
})

const currentMonthTotal = computed(() => {
  if (!calendar.value) return 0
  const thisMonth = new Date().getMonth()
  const thisYear = new Date().getFullYear()
  return calendar.value.weeks
      .flatMap((w: any) => w.contributionDays)
      .filter((d: any) => {
        const date = new Date(d.date)
        return date.getMonth() === thisMonth && date.getFullYear() === thisYear
      })
      .reduce((sum: number, d: any) => sum + d.contributionCount, 0)
})

const totalContributions = computed(() => {
  if (!calendar.value) return 0
  return calendar.value.weeks
      .flatMap((w: any) => w.contributionDays)
      .reduce((sum: number, d: any) => sum + d.contributionCount, 0)
})
</script>