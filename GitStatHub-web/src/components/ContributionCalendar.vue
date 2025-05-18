<template>
  <div class="max-w-4xl mx-auto px-4 py-6 space-y-4">
    <!-- 年間アクティビティ -->
    <section class="text-center space-y-4">
      <h3 class="text-2xl font-semibold">📅 年間アクティビティ</h3>

      <div class="flex flex-wrap justify-center gap-2">
        <button
            v-for="year in availableYears"
            :key="year"
            :class="[
          'px-3 py-1 rounded border text-sm transition',
          selectedYear === year
            ? 'bg-blue-500 text-white border-blue-500'
            : 'bg-white text-gray-800 border-gray-300 hover:bg-gray-100'
        ]"
            @click="selectYear(year)"
        >
          {{ year }}
        </button>
      </div>

      <div class="text-sm text-gray-700 space-y-1">
        <p>
          今月の貢献数: <span class="font-bold text-blue-600">{{ currentMonthTotal }}</span>
          / 総貢献数: <span class="font-bold">{{ totalContributions }}</span>
        </p>
        <p>
          {{ totalContributions }} contributions in
          {{ selectedYear === new Date().getFullYear() ? 'this year' : selectedYear }}
        </p>
      </div>

      <div class="mt-4">
        <ContributionGrid v-if="calendar && selectedYear" :weeks="calendar.weeks" />
      </div>
    </section>
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