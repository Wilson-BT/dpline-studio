<template>
    <div>
        <el-dropdown @command="clickFileDropDown">
            <span class="el-dropdown-link">
                文件
                <el-icon class="el-icon--right">
                </el-icon>
            </span>
            <template #dropdown>
                <el-dropdown-menu>
                    <el-dropdown-item command="addProject">新建项目</el-dropdown-item>
                    <el-dropdown-item command="addTask">新建任务</el-dropdown-item>
                    <el-dropdown-item command="update" :disabled="updateFlag">修改</el-dropdown-item>
                    <el-dropdown-item command="delete" :disabled="deleteFlag">删除</el-dropdown-item>
                </el-dropdown-menu>
            </template>
        </el-dropdown>
        <el-dropdown @command="clickEditor">
            <span class="el-dropdown-link">
                编辑
                <el-icon class="el-icon--right">
                </el-icon>
            </span>
            <template #dropdown>
                <el-dropdown-menu>
                    <el-dropdown-item command="formatCode">格式化</el-dropdown-item>
                </el-dropdown-menu>
            </template>
        </el-dropdown>
        <el-dropdown>
            <span class="el-dropdown-link">
                运行
                <el-icon class="el-icon--right">
                </el-icon>
            </span>
            <template #dropdown>
                <el-dropdown-menu>
                    <el-dropdown-item>调试</el-dropdown-item>
                    <el-dropdown-item>提交</el-dropdown-item>
                </el-dropdown-menu>
            </template>
        </el-dropdown>
    </div>

    <div v-if="isProject">
        <ProjectDrawer></ProjectDrawer>
    </div>
    <div v-if="!isProject">
        <TaskDrawer></TaskDrawer>
    </div>
</template>

<script lang="ts" setup>
import { ref, Ref } from 'vue';
import mittBus from '/@/utils/mitt';

import ProjectDrawer from "/@/views/home/component/drawers/projectDrawer.vue";
import TaskDrawer from "/@/views/home/component/drawers/taskDrawer.vue";

// drawer标记是project/task
let isProject: Ref<boolean> = ref(true);

// 按钮是否可操作标记
let updateFlag: Ref<boolean> = ref(true);
let deleteFlag: Ref<boolean> = ref(true);


/**
 * 下拉菜单点击事件
 * @param command: 传入的参数
 */
const clickFileDropDown = (command: string) => {
    switch (command) {
        case "addProject":
            mittBus.emit("projectRelateDrawer", "project");
            break;
        case "addTask":
            mittBus.emit("projectRelateDrawer", "task");
            break;
        case "update":
            break;
    }
    drawer.value = true;
}

/**
 * 格式化代码
 * @param command 
 */
const clickEditor = (command: string) => {
    switch (command) {
        case "formatCode":
            mittBus.emit('editorFormat');
            break
        default:
            break
    }
}

const drawer = ref(false);
</script>

<style scoped>
.example-showcase .el-dropdown-link {
    cursor: pointer;
    color: var(--el-color-primary);
    display: flex;
    align-items: center;
}
</style>