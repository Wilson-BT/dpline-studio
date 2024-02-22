<template>
    <el-tabs v-model="activeName" class="demo-tabs" @tab-click="handleClick">
        <el-tab-pane label="日志" name="log">日志</el-tab-pane>
        <el-tab-pane label="输出" name="console">输出</el-tab-pane>
    </el-tabs>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import type { TabsPaneContext } from 'element-plus';

const activeName = ref('log');

const handleClick = (tab: TabsPaneContext, event: Event) => {
    console.log(tab.paneName);
}
</script>

<style scoped>
.demo-tabs>.el-tabs__content {
    padding: 32px;
    color: #6b778c;
    font-size: 32px;
    font-weight: 600;
}
</style>