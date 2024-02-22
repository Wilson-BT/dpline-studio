<template>
  <a-modal class="add-engine-dialog"
           v-model="isShowEngineDetailDialog"
           title="集群详情"
           width="600px"
           :footer="null">
    <!-- 修改弹框右上角关闭图标 -->
    <template slot="closeIcon"><i class="chitutree-h5 chitutreeguanbi"></i></template>
    <div class="form-info"
         v-loading="isLoading">
      <a-form>
        <a-form-item class="name-url">
          <p>集群名称<span>*</span></p>
          <a-input v-model="clusterInfo.clusterName"
                   class="name"
                   placeholder="请输入名称"></a-input>
        </a-form-item>
        <!-- 集群部署类型 -->
        <a-form-item class="form-item">
          <p>集群部署类型<span>*</span></p>
          <a-select v-model="clusterInfo.clusterType"
                    placeholder="请选择集群部署类型">
            <a-select-option v-for="(item, index) in clusterTypeList"
                             :value="item.value"
                             :key="index">{{ item.label }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item class="form-item">
          <p>集群环境类型<span>*</span></p>
          <a-select v-model="clusterInfo.envType"
                    placeholder="请选择集群环境类型">
            <a-select-option v-for="(item, index) in engineEnvList"
                             :value="item.value"
                             :key="index">{{ item.label }}
            </a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item v-if="clusterInfo.clusterType === 'kubernetes'" class="name-url">
          <p>NAME_SPACE<span>*</span></p>
          <a-input v-model="clusterInfo.nameSpace"
                   class="name"
                   placeholder="请输入NAMESPACE">
          </a-input>
        </a-form-item>
        <a-form-item v-if="clusterInfo.clusterType === 'kubernetes'" class="name-url">
          <p>KUBE_PATH<span>*</span></p>
          <a-input v-model="clusterInfo.kubePath"
                   class="name"
                   placeholder="请输入kube config path 的绝对路径">
          </a-input>
        </a-form-item>

        <a-form-item v-if="clusterInfo.clusterType === 'kubernetes'" class="name-url">
          <p>ServiceAccount<span>*</span></p>
          <a-input v-model="clusterInfo.serviceAccount"
                   class="name"
                   placeholder="请输入ServiceAccount">
          </a-input>
        </a-form-item>
        <a-form-item v-if="clusterInfo.clusterType === 'kubernetes'" class="name-url">
          <p>IngressName<span>*</span></p>
          <a-input v-model="clusterInfo.ingressName"
                   class="name"
                   placeholder="请输入IngressName">
          </a-input>
        </a-form-item>
        <a-form-item v-if="clusterInfo.clusterType === 'kubernetes'" class="name-url">
          <p>IngressHost<span>*</span></p>
          <a-input v-model="clusterInfo.ingressHost"
                   class="name"
                   placeholder="请输入IngressHost">
          </a-input>
        </a-form-item>
        <a-form-item v-if="clusterInfo.clusterType === 'kubernetes'">
          <p>label属性配置</p>
          <div v-for="(paramItem, index) in this.clusterInfo.paramsPairList" :key="index">
            <a-input v-model="paramItem.key"
                     placeholder="label key"
                     style="width: 45%; margin-right: 8px;"
            />
            <a-input v-model="paramItem.value"
                     placeholder="label value"
                     style="width: 45%; margin-right: 8px;"
            />
            <a-icon v-if="clusterInfo.paramsPairList.length > 0"
                    class="dynamic-delete-button"
                    type="minus-circle-o"
                    :disabled="clusterInfo.paramsPairList.length === 0"
                    @click="removeParam(index)"
            />
          </div>
          <a-form-item v-if="clusterInfo.clusterType === 'kubernetes'" class="name-url">
            <a-button type="dashed" style="width: 100%;" @click="addParam()">
              <a-icon type="plus" />
              添加参数
            </a-button>
          </a-form-item>
        </a-form-item>
      </a-form>
    </div>
    <div class="footer justify-end">
      <div class="confirm-footer justify-end">
        <a-button @click="cancelEvent"
                  size="small">取消
        </a-button>
        <a-button style="margin-left:8px"
                  @click="confirmEvent"
                  size="small"
                  type="primary">更新
        </a-button>
      </div>
    </div>
  </a-modal>
</template>

<script>
import {envTypeList, clusterTypeList} from '@/utils/enumType'

export default {
  data() {
    return {
      isLoading: false,
      isShowDialog: false,
      clusterInfo: {
        id: 0,
        clusterType: '',
        clusterName: '',
        envType: '',
        kubePath: '',
        ingressHost: '',
        ingressName: '',
        nameSpace: '',
        serviceAccount: '',
        paramsPairList: []
      },
      clusterTypeList: clusterTypeList,
      engineEnvList: envTypeList,
      isShowEngineDetailDialog: false
    }
  },
  watch: {
    isShowEngineDetailDialog: {
      async handler(val) {
        if (val) {
          this.isLoading = true
          this.isLoading = false
        }
      }
    }
  },
  methods: {
    created() {
    },
    initData() {

    },
    open(clusterInfoItem) {
      this.isShowEngineDetailDialog = true;
      this.clusterInfo.id=clusterInfoItem.id;
      this.clusterInfo.clusterName = clusterInfoItem.clusterName;
      this.clusterInfo.envType = clusterInfoItem.envType;
      this.clusterInfo.clusterType = clusterInfoItem.clusterType;
      const clusterParamstr = clusterInfoItem.clusterParams;
      if (clusterParamstr != null) {
        const clusterParams = JSON.parse(clusterParamstr);
        this.clusterInfo.nameSpace = clusterParams.nameSpace;
        this.clusterInfo.kubePath = clusterParams.kubePath;
        this.clusterInfo.serviceAccount = clusterParams.serviceAccount;
        this.clusterInfo.ingressHost = clusterParams.ingressHost;
        this.clusterInfo.ingressName = clusterParams.ingressName;
        const length = clusterParams.extraParam.length;
        if (length < 1) {
          return
        }
        // 初始化参数列表
        this.clusterInfo.paramsPairList = [];
        for (let i = 0; i <= length - 1; i++) {
          let tempObj = clusterParams.extraParam[i];
          this.clusterInfo.paramsPairList.push(tempObj);
        }
      }
    },

    // 创建参数对
    async addParam() {
      // 判断前一个键值对是否为空
      const length = this.clusterInfo.paramsPairList.length;
      if (this.clusterInfo.paramsPairList.length > 0 && this.clusterInfo.paramsPairList[length - 1].key == ''
          && this.clusterInfo.paramsPairList[length - 1].value === '') {
        this.$message.warning({content: "当前label属性不能为空", duration: 2})
        return
      }
      this.clusterInfo.paramsPairList.push({
        key: "",
        value: ""
      });
      console.log("添加成功，添加之后的数据:", this.clusterInfo);
    },

    // 移除参数对
    async removeParam(index) {
      this.clusterInfo.paramsPairList.splice(index, 1);
    },

    // 更新点击事件
    async confirmEvent() {
      if (this.clusterInfo.clusterName === null || this.clusterInfo.clusterName === '' || this.clusterInfo.clusterName.length === 0) {
        return this.$message.warning({content: '集群名称不能为空', duration: 2})
      }
      if (this.clusterInfo.clusterType === null || this.clusterInfo.clusterType === '' || this.clusterInfo.clusterType.length === 0) {
        return this.$message.warning({content: '未选择集群部署类型', duration: 2})
      }
      let params = {
        id: this.clusterInfo.id,
        clusterName: this.clusterInfo.clusterName,
        clusterType: this.clusterInfo.clusterType,
        clusterParams: '',
        envType: this.clusterInfo.envType
      }
      switch (this.clusterInfo.clusterType) {
        case "kubernetes":
          var enginParams = {
            "nameSpace": this.clusterInfo.nameSpace,
            "kubePath": this.clusterInfo.kubePath,
            "serviceAccount": this.clusterInfo.serviceAccount,
            "ingressHost": this.clusterInfo.ingressHost,
            "ingressName": this.clusterInfo.ingressName,
            "extraParam": []
          }
          if (this.clusterInfo.nameSpace.trim() === ''){
            this.$message.warning("nameSpace is empty")
            return
          }
          if (this.clusterInfo.kubePath.trim() === ''){
            this.$message.warning("kubePath is empty")
            return
          }
          if (this.clusterInfo.ingressHost.trim() === ''){
            this.$message.warning("ingressHost is empty")
            return
          }
          if (this.clusterInfo.ingressName.trim() === ''){
            this.$message.warning("ingressName is empty")
            return
          }
          if (this.clusterInfo.serviceAccount.trim() === ''){
            this.$message.warning("serviceAccount is empty")
            return
          }
          // 如果补充参数不为空
          if (this.clusterInfo.paramsPairList.length > 0) {
            for (let i = 0; i <= this.clusterInfo.paramsPairList.length - 1; i++) {
              let keyValuePair = this.clusterInfo.paramsPairList[i];
              if (keyValuePair.key != '' && keyValuePair.value != '') {
                enginParams.extraParam.push(keyValuePair);
              }
            }
          }
          params.clusterParams = JSON.stringify(enginParams);
          break
        case "yarn":
          // TODO
          break
      }
      let res = await this.$http.post('/system/clusterManagement/updateInfo', params)
      if (res.code === 200) {
        this.$message.success({ content: '更新成功', duration: 2 });
        this.isShowEngineDetailDialog = false;
        this.$emit('confirmEvent', true);
      } else {
        this.$message.error({ content: res.msg, duration: 2 });
      }
    },
    // 取消点击事件
    cancelEvent() {
      this.isShowEngineDetailDialog = false
      this.$emit('cancelEvent', false)
    }
  }
}
</script>

<style lang="scss" scoped>
.add-engine-dialog {
  /deep/ .ant-modal-body {
    padding: 0;
  }

  .form-info {
    height: 412px;
    padding: 12px 16px 0;
    overflow-y: auto;

    /deep/ .ant-form-item {
      margin-bottom: 12px;
      height: 48px;
    }

    p {
      height: 16px;
      font-size: 12px;
      line-height: 16px;
      color: #333;
      font-weight: 600;

      span {
        color: red;
      }
    }

    .form-item {
      /deep/ .ant-form-item-children .ant-input {
        height: 28px;
      }

      .user-select {
        /deep/ .ant-select-selection--multiple {
          height: auto;
          min-height: 28px;
        }

        /deep/ .ant-select-selection__rendered {
          height: 28px;

          ul > li {
            margin-top: 1px;
          }
        }
      }
    }

    .name-url {
      /deep/ .ant-form-item-children .ant-input {
        height: 28px;
      }

      .name {
        width: 100%;
      }

      .url {
        width: calc(100% - 184px);
        border-left: 0;
      }
    }
  }

  .footer {
    height: 44px;
    border-top: 1px solid #ddd;
    padding-right: 16px;
  }

  .dynamic-delete-button {
    cursor: pointer;
    position: relative;
    top: 4px;
    font-size: 24px;
    color: #999;
    transition: all 0.3s;
  }

  .dynamic-delete-button:hover {
    color: #777;
  }

  .dynamic-delete-button[disabled] {
    cursor: not-allowed;
    opacity: 0.5;
  }
}
</style>
