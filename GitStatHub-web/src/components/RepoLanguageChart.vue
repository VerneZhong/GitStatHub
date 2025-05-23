<!-- src/components/RepoLanguageChart.vue -->
<template>
  <v-chart class="chart" :option="chartOption" />
</template>

<script setup lang="ts">
import { ref, watchEffect } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { PieChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

import type { RepoInfo } from '@/types'

const props = defineProps<{ repos: RepoInfo[] }>()

use([TitleComponent, TooltipComponent, LegendComponent, PieChart, CanvasRenderer])

const chartOption = ref({})

watchEffect(() => {
  const langMap: Record<string, number> = {}

  props.repos.forEach((repo) => {
    const lang = repo.language || 'Other'
    langMap[lang] = (langMap[lang] || 0) + 1
  })

  const sortedData = Object.entries(langMap)
      .sort((a, b) => b[1] - a[1]) // 按数量从大到小排序
      .map(([name, value]) => ({ name, value }))

  chartOption.value = {
    title: {
      text: '使用言語の割合',
      left: 'center',
    },
    tooltip: {
      trigger: 'item',
    },
    legend: {
      bottom: 0,
      left: 'center',
    },
    series: [
      {
        name: '言語',
        type: 'pie',
        radius: '50%',
        data: sortedData,
      },
    ],
  }
})
</script>

<style scoped>
.chart {
  width: 100%;
  height: 400px;
}
</style>