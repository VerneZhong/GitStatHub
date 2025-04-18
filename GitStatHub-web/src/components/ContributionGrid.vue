<template>
  <div class="calendar-grid">
    <div
        v-for="(day, index) in flattenedDays"
        :key="index"
        class="day-cell"
        :title="`${day.date} の貢献数: ${day.contributionCount}`"
        :style="{ backgroundColor: getColor(day.contributionCount) }"
    ></div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { defineProps } from 'vue'

const props = defineProps({
  weeks: Array as () => any[]
})

// 2次元配列（週）を単一のリストに平坦化する
const flattenedDays = computed(() => {
  return props.weeks.flatMap((week: any) => week.contributionDays)
})

// 貢献数に応じて色を設定するだけです
function getColor(count: number) {
  if (count === 0) return '#ebedf0'
  if (count < 5) return '#9be9a8'
  if (count < 10) return '#40c463'
  if (count < 20) return '#30a14e'
  return '#216e39'
}
</script>

<style scoped>
.calendar-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, 12px);
  grid-auto-rows: 12px;
  gap: 3px;
  max-width: 100%;
}
.day-cell {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}
</style>