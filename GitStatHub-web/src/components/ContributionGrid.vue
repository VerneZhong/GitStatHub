<template>
  <div class="contribution-graph">
    <!-- 年份标题 -->
    <h2 class="year-header">{{ currentYear }}年</h2>

    <!-- 月份标签 -->
    <div class="month-labels">
      <span v-for="(month, index) in months" :key="index" class="month-label">
        {{ month }}
      </span>
    </div>

    <div class="contribution-container">

    <!-- Contribution Grid -->
      <div class="contribution-grid">
        <div v-for="(column, columnIndex) in displayGrid" :key="columnIndex" class="grid-row">
          <div
              v-for="(cell, cellIndex) in column"
              :key="cellIndex"
              :class="['grid-cell', 'tooltip-enabled', { 'has-contributions': cell.count > 0 }]"
              :style="{ backgroundColor: getColor(cell.count) }"
              :data-tooltip="getTooltip(cell.count, cell.date)"
          ></div>
        </div>
      </div>
    </div>
    <!-- Contribution Statistics -->
    <div v-if="contributionStats.total > 0" class="contribution-stats">
      <p>今月の貢献数: {{ contributionStats.currentMonth }} / 総貢献数: {{ contributionStats.total }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import {ref, computed, defineProps, PropType} from 'vue';

// コンポーネントプロパティの定義
const props = defineProps({
  weeks: {
    type: Array as PropType<any[]>,
    required: true
  }
});

// 今年
const currentYear = new Date().getFullYear();

// 月の表示を定義する
const months = computed(() => {
  // 過去1年間の月を取得する
  const monthNames = [];
  const today = new Date();
  const pastYear = new Date(today);
  pastYear.setMonth(today.getMonth() - 11);

  for (let i = 0; i < 12; i++) {
    const month = new Date(pastYear);
    month.setMonth(pastYear.getMonth() + i);
    monthNames.push(month.toLocaleString('en', { month: 'short' }));
  }

  return monthNames;
});

// GitHub contribution colors
const colors = ['#ebedf0', '#9be9a8', '#40c463', '#30a14e', '#216e39'];

// Color based on contribution amount
function getColor(count: number) {
  if (count === 0) return colors[0];
  if (count <= 3) return colors[1];
  if (count <= 6) return colors[2];
  if (count <= 9) return colors[3];
  return colors[4];
}

// Formalization date: "April 13th"
function formatDate(dateStr: string) {
  if (!dateStr) return '';

  try {
    const date = new Date(dateStr);
    const month = date.toLocaleString('en', { month: 'long' });
    const day = date.getDate();

    // Added English ordinal numbers (1st, 2nd, 3rd, 4th, etc.)
    let suffix = 'th';
    if (day % 10 === 1 && day !== 11) {
      suffix = 'st';
    } else if (day % 10 === 2 && day !== 12) {
      suffix = 'nd';
    } else if (day % 10 === 3 && day !== 13) {
      suffix = 'rd';
    }

    return `${month} ${day}${suffix}`;
  } catch (error) {
    console.error('日付のフォーマットに失敗:', error);
    return dateStr;
  }
}

// Contribution of network data and statistics
const contributionStats = ref({
  total: 0,
  currentMonth: 0
});

// Later acquisition data
const displayGrid = computed(() => {
  // 週ごとに横に並べる（GitHubスタイル）
  const grid = [];

  if (props.weeks) {
    // 各週を横方向に、各日を縦方向（曜日）に配置
    props.weeks.forEach(week => {
      if (week.contributionDays) {
        const weekData = [];
        // 曜日順に並べるために、日付からDay of Weekを取得してソート
        const sortedDays = [...week.contributionDays].sort((a, b) => {
          return new Date(a.date).getDay() - new Date(b.date).getDay();
        });

        sortedDays.forEach((day: any) => {
          weekData.push({
            count: day.contributionCount,
            date: day.date
          });
        });
        grid.push(weekData);
      }
    });
  }

  return grid;
});

function getTooltip(count: number, dateStr: string) {
  if (!dateStr) return '';

  const formatted = formatDate(dateStr);
  return count > 0
      ? `${count} contributions on ${formatted}`
      : `No contributions on ${formatted}`;
}
</script>

<style scoped>
.contribution-graph {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Helvetica, Arial, sans-serif;
  max-width: 900px;
  margin: 0 auto;
  padding: 20px 0;
}

.year-header {
  font-size: 24px;
  color: #57606a;
  margin-bottom: 8px;
  font-weight: normal;
}

.month-labels {
  display: flex;
  padding-left: 10px;
  margin-bottom: 8px;
}

.month-label {
  flex: 1;
  text-align: center;
  font-size: 12px;
  color: #57606a;
}

.contribution-container {
  overflow: visible;
}

.contribution-grid {
  display: flex;
  flex-direction: row;
  gap: 2px;
  overflow-x: visible;
  position: relative;
}

.grid-row {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.grid-cell {
  width: 15px;
  height: 15px;
  border-radius: 2px;
  position: relative;
  background-color: #ebedf0;
}

.grid-cell.has-contributions {
  cursor: pointer;
}

/* GitHub 风格的工具提示 */
.grid-cell.tooltip-enabled:hover::after,
.grid-cell.has-contributions:hover::after {
  content: attr(data-tooltip);
  position: absolute;
  z-index: 1000;
  background-color: #24292f;
  color: #ffffff;
  padding: 6px 8px;
  border-radius: 6px;
  font-size: 12px;
  white-space: nowrap;
  box-shadow: 0 1px 3px rgba(0,0,0,0.12), 0 1px 2px rgba(0,0,0,0.24);

  /* 固定显示在鼠标上方 */
  bottom: calc(100% + 10px);
  left: 50%;
  transform: translateX(-50%);
  pointer-events: none; /* 确保鼠标事件不被提示拦截 */
}

/* GitHub 风格的三角形 */
.grid-cell.tooltip-enabled:hover::before,
.grid-cell.has-contributions:hover::before {
  content: "";
  position: absolute;
  z-index: 1000;
  border-width: 5px;
  border-style: solid;
  border-color: #24292f transparent transparent transparent;

  /* 固定显示在鼠标上方 */
  bottom: calc(100% + 0px);
  left: 50%;
  transform: translateX(-50%);
  pointer-events: none; /* 确保鼠标事件不被提示拦截 */
}

/* 解决顶部被截断的问题 */
.grid-cell:hover::after {
  /* 使用 transform 避免被截断 */
  transform: translateX(-50%) translateY(0);

  /* 使用 top 而不是 bottom 定位，防止被截断 */
  /* 对于顶部单元格，使用固定位置计算 */
  top: -45px; /* 可根据实际提示框大小调整 */
  bottom: auto;
}

.grid-cell:hover::before {
  /* 使用 top 而不是 bottom 定位，防止被截断 */
  top: -15px; /* 可根据实际三角形大小调整 */
  bottom: auto;
}

/* 确保提示框不会超出容器边界 */
@media (max-width: 768px) {
  .grid-cell:hover::after {
    /* 在小屏幕上可能需要调整位置 */
    transform: translateX(-50%) translateY(0);
    max-width: 200px; /* 限制宽度 */
    text-overflow: ellipsis;
    overflow: hidden;
  }
}

/* 贡献统计信息 */
.contribution-stats {
  margin-top: 15px;
  font-size: 14px;
  color: #57606a;
}

/* 深色模式颜色调整 */
@media (prefers-color-scheme: dark) {
  .year-header {
    color: #c9d1d9;
  }

  .month-label,
  .weekday-label {
    color: #8b949e;
  }

  .contribution-stats {
    color: #8b949e;
  }
}

.contribution-stats {
  margin-top: 12px;
  font-size: 14px;
  color: #586069;
}
</style>