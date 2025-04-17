<template>
  <div v-if="calendar">
    <h2 class="text-xl font-bold mb-4">月間/年間アクティビティ</h2>
    <div class="flex gap-8">
      <!-- 1. 左边：当月统计 -->
      <div>
        <p>今月の貢献数：<strong>{{ currentMonthTotal }}</strong></p>
        <p>総貢献数：{{ calendar.totalContributions }}</p>
      </div>

      <!-- 2. 右边：贡献日历格子 -->
      <div class="grid grid-cols-53 gap-[2px]">
        <div
            v-for="(week, wIdx) in calendar.weeks"
            :key="wIdx"
            class="flex flex-col gap-[2px]"
        >
          <div
              v-for="(day, dIdx) in week.contributionDays"
              :key="dIdx"
              :title="`${day.date}: ${day.contributionCount} contributions`"
              class="w-3 h-3 rounded-sm"
              :style="{ backgroundColor: day.color }"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'

const props = defineProps<{
  username: string
}>()

const calendar = ref<any>(null)

const currentMonthTotal = computed(() => {
  if (!calendar.value) return 0

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
    const res = await axios.get('/api/github/contributions', {
      params: { username: props.username }
    })
    calendar.value = res.data
  } catch (err) {
    console.error('貢献データの取得に失敗しました:', err)
  }
})
</script>

<style scoped>
/* 简单美化格子颜色 */
</style>