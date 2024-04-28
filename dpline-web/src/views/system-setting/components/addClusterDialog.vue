<template>
  <a-modal class="add-cluster-dialog"
           v-model="isShowDialog"
           title="新增引擎"
           width="600px"
           :footer="null">
    <!-- 修改弹框右上角关闭图标 -->
    <template slot="closeIcon"><i class="chitutree-h5 chitutreeguanbi"></i></template>
    <div class="form-info"
         v-loading="isLoading">
      <a-form>
        <a-form-item class="name-url">
          <p>集群名称<span>*</span></p>
          <a-input v-model="form.clusterName"
                   class="name"
                   placeholder="请输入名称"></a-input>
        </a-form-item>
        <!-- 集群部署类型 -->
        <a-form-item class="form-item">
          <p>集群部署类型<span>*</span></p>
          <a-select v-model="form.clusterType"
                    placeholder="请选择集群部署类型">
            <a-select-option v-for="(item, index) in clusterTypeList"
                             :value="item.value"
                             :key="index">{{ item.label }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item class="form-item">
          <p>集群环境类型<span>*</span></p>
          <a-select v-model="form.clusterEnv"
                    placeholder="请选择集群环境类型">
            <a-select-option v-for="(item, index) in clusterEnvList"
                             :value="item.value"
                             :key="index">{{ item.label }}</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item v-if="form.clusterType === 'kubernetes'" class="name-url">
          <p>NAME_SPACE<span>*</span></p>
          <a-input v-model="form.nameSpace"
                   class="name"
                   placeholder="请输入NAMESPACE">
          </a-input>
        </a-form-item>
        <a-form-item v-if="form.clusterType === 'kubernetes'" class="name-url">
          <p>KUBE_PATH<span>*</span></p>
          <a-input v-model="form.kubePath"
                   class="name"
                   placeholder="请输入kube config path 的绝对路径">
          </a-input>
        </a-form-item>
        <a-form-item v-if="form.clusterType === 'kubernetes'" class="name-url">
          <p>ServiceAccount<span>*</span></p>
          <a-input v-model="form.serviceAccount"
                   class="name"
                   placeholder="请输入ServiceAccount">
          </a-input>
        </a-form-item>
        <a-form-item v-if="form.clusterType === 'kubernetes'" class="name-url">
          <p>IngressName<span>*</span></p>
          <a-input v-model="form.ingressName"
                   class="name"
                   placeholder="请输入IngressName，相同集群 namespace, 不同的Node下，需要指定不同的ingressName">
          </a-input>
        </a-form-item>
        <a-form-item v-if="form.clusterType === 'kubernetes'" class="name-url">
          <p>IngressHost<span>*</span></p>
          <a-input v-model="form.ingressHost"
                   class="name"
                   placeholder="请输入IngressHost">
          </a-input>
        </a-form-item>
        <a-form-item v-if="form.clusterType === 'kubernetes'">
          <p>label属性配置</p>
          <div v-for="(paramItem, index) in form.paramsPairList" :key="index">
            <a-input v-model="paramItem.key"
                placeholder="label key"
                style="width: 45%; margin-right: 8px;"
            />
            <a-input v-model="paramItem.value"
                placeholder="label value"
                style="width: 45%; margin-right: 8px;"
            />
            <a-icon v-if="form.paramsPairList.length > 0"
                class="dynamic-delete-button"
                type="minus-circle-o"
                :disabled="form.paramsPairList.length === 0"
                @click="removeParam(index)"
            />
          </div>
          <a-form-item v-if="form.clusterType === 'kubernetes'" class="name-url">
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
                  size="small">取消</a-button>
        <a-button style="margin-left:8px"
                  @click="confirmEvent"
                  size="small"
                  type="primary">确定</a-button>
      </div>
    </div>
  </a-modal>
</template>

<script>
import {envTypeList, clusterTypeList} from "@/utils/enumType";

export default {
    data () {
      return {
        isLoading: false,
        isShowDialog: false,
        form: {
          clusterType: 'kubernetes',
          clusterName: null,
          clusterEnv: 'test',
          kubePath: '',
          nameSpace: '',
          serviceAccount: '',
          ingressHost: '',
          ingressName: '',
          paramsPairList: []
        },
        clusterTypeList: clusterTypeList,
        clusterEnvList: envTypeList,
      }
    },
    watch: {
      isShowDialog: {
        async handler (val) {
          if (val) {
            this.initForm()
            this.isLoading = true
            this.isLoading = false
          }
        }
      }
    },
    methods: {
      beforeCreate() {
        this.initForm();
      },
      created () {

      },
      // 初始化表单
      initForm () {
        this.isLoading = false;
        this.form = {
          clusterName: null,
          clusterType: 'kubernetes',
          clusterEnv: '',
          paramsPairList: []
        }
        this.clusterTypeList = clusterTypeList;
        this.clusterEnvList = envTypeList;
      },

      // 创建参数对
      async addParam () {
        // 判断前一个键值对是否为空
        const length = this.form.paramsPairList.length;
        if (this.form.paramsPairList.length > 0 && this.form.paramsPairList[length-1].key == ''
            && this.form.paramsPairList[length-1].value == '') {
          this.$message.warning({ content: "当前label属性不能为空", duration: 2 })
          return
        }
        this.form.paramsPairList.push({
          key: "",
          value: ""
        })
      },

      // 移除参数对
      async removeParam (index) {
        console.log(index);
        this.form.paramsPairList.splice(index, 1);
      },

      // 确认点击事件
      async confirmEvent () {
        if (this.form.clusterName === null || this.form.clusterName === '' || this.form.clusterName.length === 0) {
          return this.$message.warning({ content: '集群名称不能为空', duration: 2 })
        }
        if (this.form.clusterType === null || this.form.clusterType === '' || this.form.clusterType.length === 0) {
          return this.$message.warning({ content: '未选择集群部署类型', duration: 2 })
        }
        if (this.form.clusterType === '' || this.form.clusterType.length === 0) {
          return this.$message.warning({ content: '未选择运行环境类型', duration: 2 })
        }
        let params = {
          clusterName: this.form.clusterName,
          clusterType: this.form.clusterType,
          clusterParams: '',
          envType: this.form.clusterEnv
        }
        switch (this.form.clusterType) {
          case "kubernetes":
            var enginParams = {
              "nameSpace": this.form.nameSpace,
              "kubePath": this.form.kubePath,
              "serviceAccount": this.form.serviceAccount,
              "ingressHost": this.form.ingressHost,
              "ingressName": this.form.ingressName,
              "extraParam":[]
            }
            // 如果补充参数不为空
            if (this.form.paramsPairList.length > 0) {
              for (let i = 0; i <= this.form.paramsPairList.length-1; i++) {
                let keyValuePair = this.form.paramsPairList[i];
                if (keyValuePair.key !== '' && keyValuePair.value !== '') {
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

        let res = await this.$http.post('/system/clusterManagement/add', params)
        if (res.code === 200) {
          this.$message.success({ content: '添加成功', duration: 2 });
          this.isShowDialog = false;
          this.$emit('confirmEvent', true);
          this.initForm();
        } else {
          this.$message.error({ content: res.msg, duration: 2 });
        }
      },
      // 取消点击事件
      cancelEvent () {
        this.isShowDialog = false
        this.$emit('cancelEvent', false)
      }
    }
  }
</script>

<style lang="scss" scoped>
  .add-cluster-dialog {
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
