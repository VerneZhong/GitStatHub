<template>
  <div>
    <h2>年間アクティビティ</h2>
    <p>今月の貢献数: {{ currentMonthTotal }} / 総貢献数: {{ totalContributions }}</p>
    <ContributionGrid v-if="calendar" :weeks="calendar.weeks" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { getContributionCalendar } from '@/services/api'
import ContributionGrid from './ContributionGrid.vue'

const props = defineProps<{
  username: string
}>()

const calendar = ref<any>(null)

const currentMonthTotal = computed(() => {
  if (!calendar.value || !calendar.value.weeks) return 0

  const now = new Date()
  const thisMonth = now.getMonth()
  const thisYear = now.getFullYear()

  let total = 0
  calendar.value.weeks.forEach((week: any) => {
    week.contributionDays.forEach((day: any) => {
      const d = new Date(day.date)
      if (d.getMonth() === thisMonth && d.getFullYear() === thisYear) {
        total += day.contributionCount
      }
    })
  })

  return total
})

onMounted(async () => {
  try {
    const res = await getContributionCalendar(props.username)
    calendar.value = res.data
  } catch (err) {
    console.error('貢献データの取得に失敗しました:', err)
  }
})

const totalContributions = computed(() => {
  if (!calendar.value) return 0
  return calendar.value.weeks.flatMap((w: any) => w.contributionDays)
      .reduce((sum, day) => sum + day.contributionCount, 0)
})
</script>

<style scoped>
/* 简单美化格子颜色 */
</style>