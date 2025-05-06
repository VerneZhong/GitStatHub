<template>
  <div class="contribution-graph">
    <!-- 月のラベル -->
    <div class="month-labels">
      <span v-for="(month, index) in months" :key="index" class="month-label">
        {{ month }}
      </span>
    </div>

    <div class="contribution-container">
      <!-- 貢献グリッド -->
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
  </div>
</template>

<script setup lang="ts">
import {ref, computed, defineProps, PropType} from 'vue';

// コンポーネントのプロパティ定義
const props = defineProps({
  weeks: {
    type: Array as PropType<any[]>,
    required: true
  }
});

// 月の表示を定義
const months = computed(() => {
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

// GitHubスタイルの貢献度に応じた色
const colors = ['#ebedf0', '#9be9a8', '#40c463', '#30a14e', '#216e39'];

// 貢献数に基づく色を取得
function getColor(count: number) {
  if (count === 0) return colors[0];
  if (count <= 3) return colors[1];
  if (count <= 6) return colors[2];
  if (count <= 9) return colors[3];
  return colors[4];
}

// 日付を「April 13th」の形式に変換
function formatDate(dateStr: string) {
  if (!dateStr) return '';

  try {
    const date = new Date(dateStr);
    const month = date.toLocaleString('en', { month: 'long' });
    const day = date.getDate();

    // 英語の序数 (1st, 2nd, 3rd, 4thなど) を追加
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
    console.error('日付フォーマットの失敗:', error);
    return dateStr;
  }
}

// 表示用グリッドデータを取得
const displayGrid = computed(() => {
  const grid = [];

  if (props.weeks) {
    props.weeks.forEach(week => {
      if (week.contributionDays) {
        const weekData = [];
        // 曜日順に並べる
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

// ツールチップ内容を生成
function getTooltip(count: number, dateStr: string) {
  if (!dateStr) return '';

  const formatted = formatDate(dateStr);
  return count > 0
      ? `${count} contributions on ${formatted}`
      : `No contributions on ${formatted}`;
}
</script>

<style scoped>
/* 全体レイアウト */
.contribution-graph {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Helvetica, Arial, sans-serif;
  max-width: 900px;
  margin: 0 auto;
  padding: 20px 0;
}

/* 月のラベルエリア */
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

/* グリッドコンテナ */
.contribution-container {
  overflow: visible;
}

/* グリッド本体 */
.contribution-grid {
  display: flex;
  flex-direction: row;
  gap: 2px;
  overflow-x: visible;
  position: relative;
}

/* 列（1週間分） */
.grid-row {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

/* セル */
.grid-cell {
  width: 15px;
  height: 15px;
  border-radius: 2px;
  position: relative;
  background-color: #ebedf0;
}

.grid-cell {
  cursor: pointer;
}

/* GitHub風ツールチップ */
.grid-cell.tooltip-enabled:hover::after {
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

  bottom: calc(100% + 10px);
  left: 50%;
  transform: translateX(-50%);
  pointer-events: none;
}

/* ツールチップの三角形 */
.grid-cell.tooltip-enabled:hover::before {
  content: "";
  position: absolute;
  z-index: 1000;
  border-width: 5px;
  border-style: solid;
  border-color: #24292f transparent transparent transparent;

  bottom: calc(100% + 0px);
  left: 50%;
  transform: translateX(-50%);
  pointer-events: none;
}

/* 上部セルのツールチップ位置調整 */
.grid-cell:hover::after {
  transform: translateX(-50%) translateY(0);
  top: -45px;
  bottom: auto;
}

.grid-cell:hover::before {
  top: -15px;
  bottom: auto;
}

/* モバイル対応のツールチップ調整 */
@media (max-width: 768px) {
  .grid-cell:hover::after {
    transform: translateX(-50%) translateY(0);
    max-width: 200px;
    text-overflow: ellipsis;
    overflow: hidden;
  }
}

/* ダークモード調整 */
@media (prefers-color-scheme: dark) {
  .month-label {
    color: #8b949e;
  }
}
</style>