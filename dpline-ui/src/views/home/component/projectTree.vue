<template>
  <div>
    <el-input v-model="query" placeholder="请输入名称查询" @input="onQueryChanged" />
    <el-tree-v2 ref="treeRef" :data="dataList" :props="props" :height="800" @node-click="handleNodeClick">
      <template #default="{ node }">
        <el-icon color="#409EFC" :size="20">
          <Folder />
        </el-icon>
        <span class="nodeText">&nbsp;{{ node.label }}</span>
      </template>
    </el-tree-v2>
  </div>
</template>

<script lang="ts" setup>
import { ProjectTree } from "/@/interfaces/projectTree"
import { useProjectApi } from "/@/api/project";
import { onMounted, ref } from "vue";
import { ElTreeV2 } from 'element-plus';
import type { TreeNode, TreeNodeData } from 'element-plus/es/components/tree-v2/src/types'
import { Folder } from "@element-plus/icons-vue";
import mittBus from '/@/utils/mitt';


const query = ref('')
const treeRef = ref<InstanceType<typeof ElTreeV2>>()
const props = {
  value: 'id',
  label: 'name',
  children: 'children',
}

const onQueryChanged = (query: string) => {
  // treeRef.value!.filter(query);
  console.log("搜索框");
};

// 项目数据列表数据
let dataList = ref();

onMounted(() => {
  setListData();
});

// 获取远程数据
const setListData = async () => {
  let reqParam = { "pageNo": 1, "pageSize": 20 };
  let resData: any = await useProjectApi().listProject(reqParam);
  // 根据返回组装数据
  if (resData.code != 200 || resData.data.totalList.size < 1) {
    return
  }
  let respDataList: Array<Object> = resData.data.totalList;
  let treeDataList: Array<ProjectTree> = [];
  for (let i in respDataList) {
    let obj: any = respDataList[i];
    let p: ProjectTree = {
      "id": obj.id,
      "name": obj.name,
      "code": obj.code
    }
    treeDataList.push(p);
  }
  dataList.value = treeDataList;
};

/**
 * 刷新工程树相关
 */
mittBus.on('refreshProjectTree', () => {
  setListData();
});


// 处理点击节点方法
const handleNodeClick = (data: TreeNodeData, node: TreeNode, e: MouseEvent) => {
  console.log("当前节点:" + JSON.stringify(data));
}
</script>

<style scoped lang="scss">
.nodeText {
  font-size: large;
}
</style>