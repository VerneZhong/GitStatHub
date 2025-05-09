<template>
  <div>
    <h2>年間アクティビティ</h2>
    <!-- 年份选择器 -->
    <div class="year-selector">
      <button
          v-for="year in availableYears"
          :key="year"
          :class="{ active: selectedYear === year }"
          @click="selectYear(year)"
      >
        {{ year }}
      </button>
    </div>
    <p>今月の貢献数: {{ currentMonthTotal }} / 総貢献数: {{ totalContributions }}</p>
    <p>{{ totalContributions }} contributions in the last year</p>
    <ContributionGrid v-if="calendar && selectedYear" :weeks="calendar.weeks" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import {getContributions, getContributionsByYear} from '@/services/api'
import ContributionGrid from './ContributionGrid.vue'

const props = defineProps<{
  username: string
}>()

// 年份选择
const selectedYear = ref(new Date().getFullYear());
const availableYears = Array.from({ length: 7 }, (_, i) => 2025 - i); // [2025, 2024, ..., 2019]

const calendar = ref<any>({ weeks: [] });

onMounted(async () => {
  calendar.value = await getContributions(props.username)
})

async function fetchContributions(year: number) {
  calendar.value = null;
  calendar.value = await getContributionsByYear(props.username, year);
}

// 切换年份
function selectYear(year: number) {
  selectedYear.value = year;
  fetchContributions(year);
}

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

<style scoped>
.year-selector {
  margin-bottom: 1rem;
  display: flex;
  gap: 0.5rem;
  margin-left: 250px;
}
.year-selector button {
  padding: 0.5rem 1rem;
  border: 1px solid #ccc;
  background: white;
  cursor: pointer;
}
.year-selector button.active {
  background: #3b82f6;
  color: white;
  border-color: #3b82f6;
}
</style>