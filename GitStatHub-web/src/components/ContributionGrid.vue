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

    <!-- 贡献网格 -->
    <div class="contribution-grid">
      <div v-for="(row, rowIndex) in contributionGrid" :key="rowIndex" class="grid-row">
        <div
            v-for="(cell, cellIndex) in row"
            :key="cellIndex"
            class="grid-cell"
            :class="{ 'has-contributions': cell.count > 0 }"
            :style="{ backgroundColor: getColor(cell.count) }"
            :data-tooltip="cell.date ? `${cell.count} contributions on ${formatDate(cell.date)}` : ''"
        ></div>
      </div>
    </div>

    <!-- 贡献统计 -->
    <div v-if="contributionStats.total > 0" class="contribution-stats">
      <p>今月の貢献数: {{ contributionStats.currentMonth }} / 総貢献数: {{ contributionStats.total }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch, defineProps, withDefaults } from 'vue';
import { getContributions } from '@/services/api'

// 定义组件 props
const props = withDefaults(defineProps<{
  username: string
}>(), {
  username: 'VerneZhong'
})

// 当前年份
const currentYear = new Date().getFullYear();

// 定义月份显示
const months = computed(() => {
  // 获取过去一年的月份
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

// GitHub贡献颜色
const colors = ['#ebedf0', '#9be9a8', '#40c463', '#30a14e', '#216e39'];

// 根据贡献数量确定颜色
function getColor(count: number) {
  if (count === 0) return colors[0];
  if (count <= 3) return colors[1];
  if (count <= 6) return colors[2];
  if (count <= 9) return colors[3];
  return colors[4];
}

// 格式化日期为"April 13th"这样的格式
function formatDate(dateStr: string) {
  const date = new Date(dateStr);
  const month = date.toLocaleString('en', { month: 'long' });
  const day = date.getDate();

  // 添加英文序数后缀(1st, 2nd, 3rd, 4th等)
  let suffix = 'th';
  if (day % 10 === 1 && day !== 11) {
    suffix = 'st';
  } else if (day % 10 === 2 && day !== 12) {
    suffix = 'nd';
  } else if (day % 10 === 3 && day !== 13) {
    suffix = 'rd';
  }

  return `${month} ${day}${suffix}`;
}

// 贡献网格和统计数据
const contributionGrid = ref([]);
const contributionStats = ref({
  total: 0,
  currentMonth: 0
});

// 从后端获取数据
async function fetchContributionData() {
  try {
    const data = await getContributions(props.username);
    console.log(data)

    // 更新统计信息
    contributionStats.value = {
      total: data.totalContributions || 0,
      currentMonth: data.currentMonthContributions || 0
    };

    // 转换数据为网格格式
    contributionGrid.value = transformContributionData(data.weeks);
  } catch (error) {
    console.error('获取贡献数据失败:', error);
    // 加载失败时初始化空网格
    initializeEmptyGrid();
  }
}

// 将API返回的周数据转换为网格格式
function transformContributionData(weeks) {
  // 初始化7×53的网格，填充零(每周7天，每年约53周)
  const grid = Array(7).fill().map(() => Array(53).fill().map(() => ({ count: 0, date: null })));

  if (!weeks || !Array.isArray(weeks)) {
    return grid;
  }

  // 用实际贡献数据填充网格
  weeks.forEach((week, weekIndex) => {
    if (week.contributionDays && Array.isArray(week.contributionDays)) {
      week.contributionDays.forEach(day => {
        const date = new Date(day.date);
        const dayOfWeek = date.getDay(); // 0(周日)到6(周六)
        // 调整以匹配GitHub的显示(周一在索引0)
        const rowIndex = dayOfWeek === 0 ? 6 : dayOfWeek - 1;

        if (weekIndex < 53) {
          grid[rowIndex][weekIndex] = {
            count: day.contributionCount,
            date: day.date
          };
        }
      });
    }
  });

  return grid;
}

// 初始化空网格（用于加载失败或等待数据时）
function initializeEmptyGrid() {
  const grid = [];
  for (let i = 0; i < 7; i++) {
    const row = [];
    for (let j = 0; j < 53; j++) {
      row.push({ count: 0, date: null });
    }
    grid.push(row);
  }
  contributionGrid.value = grid;
}

function getTooltip(count: number, dateStr: string) {
  const formatted = formatDate(dateStr);
  return count > 0
      ? `${count} contributions on ${formatted}`
      : `No contributions on ${formatted}`;
}

// 组件挂载时获取数据
onMounted(() => {
  initializeEmptyGrid(); // 先初始化空网格
  fetchContributionData(); // 然后获取真实数据
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
}

.grid-cell.has-contributions {
  cursor: pointer;
}

/* 工具提示样式 */
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

/* 工具提示箭头 */
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

/* 浅色模式默认颜色 */
.grid-cell {
  background-color: #ebedf0;
}

/* 可选：深色模式 */
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

  .grid-cell {
    background-color: #161b22;
  }

  .grid-cell.has-contributions:hover::after {
    background-color: #2d333b;
    color: #ffffff;
  }

  .grid-cell.has-contributions:hover::before {
    border-color: #2d333b transparent transparent transparent;
  }
}
</style>