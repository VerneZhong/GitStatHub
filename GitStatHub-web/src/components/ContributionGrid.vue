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

    <!-- Contribution Grid -->
    <div class="contribution-grid">
      <div v-for="(row, rowIndex) in displayGrid" :key="rowIndex" class="grid-row">
        <div
            v-for="(cell, cellIndex) in row"
            :key="cellIndex"
            :class="['grid-cell', 'tooltip-enabled', { 'has-contributions': cell.count > 0 }]"
            :style="{ backgroundColor: getColor(cell.count) }"
            :data-tooltip="getTooltip(cell.count, cell.date)"
        ></div>
      </div>
    </div>

    <!-- Contribution Statistics -->
    <div v-if="contributionStats.total > 0" class="contribution-stats">
      <p>今月の貢献数: {{ contributionStats.currentMonth }} / 総貢献数: {{ contributionStats.total }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch, defineProps, withDefaults } from 'vue';
import { getContributions } from '@/services/api'

// コンポーネントプロパティの定義
const props = withDefaults(defineProps<{
  username?: string
}>(), {
  username: 'VerneZhong'
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
const contributionGrid = ref([]);
const displayGrid = ref([]);
const contributionStats = ref({
  total: 0,
  currentMonth: 0
});

// Initialize grid
function initializeGrid() {
  const today = new Date();
  const todayDateString = today.toISOString().slice(0, 10);

  const grid = [];
  for (let i = 0; i < 7; i++) {
    const row = [];
    for (let j = 0; j < 52; j++) {
      row.push({
        count: 0,
        date: todayDateString,
      });
    }
    grid.push(row);
  }

  contributionGrid.value = grid;
  displayGrid.value = grid;
}

// Later acquisition data
async function fetchContributionData() {
  try {
    initializeGrid(); // 先初始化一个空网格，确保显示

    const data = await getContributions(props.username);

    // Updated statistics
    contributionStats.value = {
      total: data.totalContributions || 0,
      currentMonth: data.currentMonthContributions || 0
    };

    if (data.weeks && Array.isArray(data.weeks)) {
      // Convert data to network format
      const grid = processContributionData(data.weeks);
      displayGrid.value = grid;
    }
  } catch (error) {
    console.error('获取贡献数据失败:', error);
  }
}

// Process contribution data
function processContributionData(weeks) {
  const today = new Date();
  const todayDateString = today.toISOString().slice(0, 10);

  // 初始化7×53的网格，默认所有格子都可见
  const grid = Array(7).fill().map(() =>
      Array(53).fill().map(() => ({
        count: 0,
        date: todayDateString,
      }))
  );

  if (!weeks || !Array.isArray(weeks)) {
    return grid;
  }

  // 用实际贡献数据填充网格
  weeks.forEach((week, weekIndex) => {
    if (week.contributionDays && Array.isArray(week.contributionDays)) {
      week.contributionDays.forEach(day => {
        if (!day || !day.date) return;

        const date = new Date(day.date);
        const dateString = day.date;
        const dayOfWeek = date.getDay(); // 0(周日)到6(周六)

        if (weekIndex < 53 && dayOfWeek >= 0 && dayOfWeek < 7) {
          // 超过今天的日期设为不可见
          const isVisible = dateString <= todayDateString;

          grid[dayOfWeek][weekIndex] = {
            count: day.contributionCount || 0,
            date: day.date,
            isVisible: isVisible
          };
        }
      });
    }
  });

  return grid;
}

function getTooltip(count: number, dateStr: string) {
  if (!dateStr) return '';

  const formatted = formatDate(dateStr);
  return count > 0
      ? `${count} contributions on ${formatted}`
      : `No contributions on ${formatted}`;
}

// 组件挂载时获取数据
onMounted(() => {
  initializeGrid(); // 确保先有个默认网格
  fetchContributionData();
});

// 当用户名变化时重新获取数据
watch(() => props.username, () => {
  fetchContributionData();
});
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

.contribution-grid {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.grid-row {
  display: flex;
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

/* 统一工具提示样式，移除类选择器的差异 */
.grid-cell.tooltip-enabled:hover::after,
.grid-cell.has-contributions:hover::after {
  content: attr(data-tooltip);
  position: absolute;
  bottom: 100%;
  left: 50%;
  transform: translateX(-50%);
  background-color: #24292f;
  color: #ffffff;
  padding: 6px 8px;
  border-radius: 6px;
  font-size: 12px;
  white-space: nowrap;
  z-index: 10;
  margin-bottom: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.12), 0 1px 2px rgba(0,0,0,0.24);
}

/* 统一小三角形样式 */
.grid-cell.tooltip-enabled:hover::before,
.grid-cell.has-contributions:hover::before {
  content: "";
  position: absolute;
  bottom: 100%;
  left: 50%;
  transform: translateX(-50%);
  border-width: 5px;
  border-style: solid;
  border-color: #24292f transparent transparent transparent;
  margin-bottom: 3px;
  z-index: 10;
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

  .month-label {
    color: #8b949e;
  }

  .contribution-stats {
    color: #8b949e;
  }
}
</style>