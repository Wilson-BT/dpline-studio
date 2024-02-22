<template>
    <el-drawer v-model="drawer" direction="rtl" :title="curFormData.drawerTitle" :show-close="false">
        <template #default>
            <div>
                <el-form label-position="top" label-width="100%" size="default">
                    <el-form-item :label="curFormData.nameTitle">
                        <el-input placeholder="请输入名称" v-model="projectName" />
                    </el-form-item>
                    <el-form-item label="描述:">
                        <el-input :rows="5" type="textarea" placeholder="请输入描述信息" v-model="projectDesc" />
                    </el-form-item>
                </el-form>
            </div>
        </template>
        <template #footer>
            <div style="flex: auto">
                <el-button @click="cancelClick">取消</el-button>
                <el-button type="primary" @click="confirmClick">确定</el-button>
            </div>
        </template>
    </el-drawer>
</template>

<script lang="ts" setup>
import { ElMessageBox } from 'element-plus';
import { ElMessage } from 'element-plus';
import { reactive, ref, Ref, toRefs } from "vue";
import { useProjectApi } from '/@/api/project';
import mittBus from '/@/utils/mitt';

type FormInfo = {
    drawerTitle: string;
    nameTitle: string;
    description: string;
}
// 当前编辑drawer
let curFormData: FormInfo = reactive({
    drawerTitle: '新建项目',
    nameTitle: '新建项目',
    description: '项目描述'
});

// drawer开关配置
let drawer: Ref<boolean> = ref(false);
mittBus.on('projectRelateDrawer', (value) => {
    if ("project" == value) {
        drawer.value = true;
    }
});

// project属性相关
let projectName: Ref<string> = ref('');
let projectDesc: Ref<string> = ref('');


/**
 * 重置输入框内容
 */
const resetInputText = () => {
    projectName.value = '';
    projectDesc.value = '';
};
/**
 * 取消
 */
const cancelClick = () => {
    drawer.value = false;
    resetInputText();
};

/**
 * drawer确定按钮
 */
const confirmClick = () => {
    ElMessageBox.confirm(`你确定保存?`)
        .then(async () => {
            // 提交保存
            const addProjectParams: object = {
                "projectName": projectName.value,
                "description": projectDesc.value
            }
            const flag: boolean = await addProject(addProjectParams);
            if (flag) {
                ElMessage.success("保存成功");
            } else {
                ElMessage.error("保存失败");
            }
            mittBus.emit('refreshProjectTree');
            drawer.value = false;
            resetInputText();
        })
        .catch(() => {
            // catch error
        })
};

/**
 * 新增项目
 * @param params 待保存项目参数
 */
const addProject = async (params: object) => {
    const resp: any = await useProjectApi().addProject(params);
    if (resp.code == 200) {
        return true;
    } else {
        return false;
    }
}
</script>

<style scoped>

</style>