<template>
  <div class="contribution-graph">
    <!-- 年份标题 -->
    <h2 class="year-header">2025年</h2>

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
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';

// 定义组件props
const props = defineProps({
  contributionData: {
    type: Object,
    default: () => ({})
  }
});

// 固定的月份显示
const months = ['Aug', 'Sep', 'Oct', 'Nov', 'Dec', 'Jan', 'Feb', 'Mar', 'Apr'];

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

// 创建示例数据网格(7行×天数)
const contributionGrid = ref([]);

onMounted(() => {
  // 这是一个模拟实现，创建与你图片相似的布局
  // 在实际实现中，应该用真实数据替换这部分
  const rows = 7;
  const cols = 53; // 大约一年的数据(53周)

  const grid = [];
  for (let i = 0; i < rows; i++) {
    const row = [];
    for (let j = 0; j < cols; j++) {
      // 年初大部分时间创建空白单元格
      if (j < 35) {
        row.push({ count: 0, date: null });
      } else {
        // 为3月和4月生成一些活动(最后~15列)
        // 4月份的活动概率更高
        const isActive = j > 40 || (j > 35 && Math.random() > 0.5);
        const count = isActive ? Math.floor(Math.random() * 12) : 0;

        // 为此单元格生成日期字符串(用于工具提示)
        // 在实际实现中，你应该使用真实日期
        const month = j < 40 ? 'Mar' : 'Apr';
        const day = (j % 30) + 1;
        const date = `${month} ${day}, 2025`;

        row.push({ count, date });
      }
    }
    grid.push(row);
  }

  contributionGrid.value = grid;
});

// 在实际实现中，你应该这样转换API数据:
/*
function transformContributionData(apiData) {
  // 初始化7×53的网格，填充零(每周7天，每年约53周)
  const grid = Array(7).fill().map(() => Array(53).fill().map(() => ({ count: 0, date: null })));

  // 用实际贡献数据填充网格
  if (apiData && apiData.weeks) {
    apiData.weeks.forEach((week, weekIndex) => {
      if (week.contributionDays) {
        week.contributionDays.forEach(day => {
          const date = new Date(day.date);
          const dayOfWeek = date.getDay(); // 0(周日)到6(周六)
          // 调整以匹配GitHub的显示(周一在索引0)
          const rowIndex = dayOfWeek === 0 ? 6 : dayOfWeek - 1;

          if (weekIndex < 53) {
            grid[rowIndex][weekIndex] = {
              count: day.contributionCount,
              date: day.date,
            };
          }
        });
      }
    });
  }

  return grid;
}
*/
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