<template>
  <div class="font-sans max-w-[900px] mx-auto py-5">
    <!-- 月のラベル -->
    <div class="flex pl-2 mb-2">
      <span
          v-for="(month, index) in months"
          :key="index"
          class="flex-1 text-center text-xs text-gray-500 dark:text-gray-400"
      >
        {{ month }}
      </span>
    </div>

    <!-- 貢献グリッド -->
    <div class="overflow-visible">
      <div class="flex flex-row gap-[2px] overflow-x-visible relative">
        <div
            v-for="(column, columnIndex) in displayGrid"
            :key="columnIndex"
            class="flex flex-col gap-[2px]"
        >
          <div
              v-for="(cell, cellIndex) in column"
              :key="cellIndex"
              :class="[
              'grid-cell',
              'w-[15px] h-[15px] rounded-sm relative cursor-pointer tooltip-enabled',
              cell.count > 0 ? 'has-contributions' : '',
            ]"
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
.grid-cell.tooltip-enabled:hover::after {
  content: attr(data-tooltip);
  position: absolute;
  z-index: 50;
  background-color: #24292f;
  color: #fff;
  padding: 6px 8px;
  border-radius: 6px;
  font-size: 12px;
  white-space: nowrap;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.12), 0 1px 2px rgba(0, 0, 0, 0.24);
  bottom: calc(100% + 10px);
  left: 50%;
  transform: translateX(-50%);
  pointer-events: none;
}

.grid-cell.tooltip-enabled:hover::before {
  content: "";
  position: absolute;
  z-index: 50;
  border-width: 5px;
  border-style: solid;
  border-color: #24292f transparent transparent transparent;
  bottom: calc(100% + 0px);
  left: 50%;
  transform: translateX(-50%);
  pointer-events: none;
}
</style>