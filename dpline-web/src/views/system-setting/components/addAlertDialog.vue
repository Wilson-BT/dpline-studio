<template>
  <a-modal class="add-alert-dialog"
           v-model="isShowDialog"
           title="新增告警"
           width="480px"
           :footer="null">
    <!-- 修改弹框右上角关闭图标 -->
    <template slot="closeIcon"><i class="chitutree-h5 chitutreeguanbi"></i></template>
    <div class="form-info"
         v-loading="isLoading">
      <a-form>
        <a-form-item class="name-url">
          <p>告警名称<span>*</span></p>
          <a-input v-model="form.instanceName"
                   class="name"
                   placeholder="请输入名称"></a-input>
        </a-form-item>
        <a-form-item class="form-item">
          <p>告警类型<span>*</span></p>
          <a-select v-model="form.alertType"
                    placeholder="请选择告警类型">
            <a-select-option v-for="(item, index) in alertTypeList"
                             :value="item.value"
                             :key="index">{{ item.label }}</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item v-if="form.alertType === 'HTTP'">
          <p>请求类型<span>*</span></p>
          <a-select v-model="http.requestType"
                    placeholder="请选择请求类型">
            <a-select-option v-for="(item, index) in requestTypeList"
                             :value="item.value"
                             :key="index">{{ item.label }}</a-select-option>
          </a-select>
          <p>URL<span>*</span></p>
          <a-input v-model="http.url"
                   class="name"
                   placeholder="请输入URL"></a-input>
          <p>内容字段<span>*</span></p>
          <a-input v-model="http.requestColumn"
                   class="name"
                   placeholder="请输入内容字段"></a-input>
          <p>请求头配置</p>
          <div v-for="(paramItem, index) in http.requestHeader" :key="index">
            <a-input v-model="paramItem.key"
                     placeholder="label key"
                     style="width: 45%; margin-right: 8px;"
            />
            <a-input v-model="paramItem.value"
                     placeholder="label value"
                     style="width: 45%; margin-right: 8px;"
            />
            <a-icon v-if="http.requestHeader.length > 0"
                    class="dynamic-delete-button"
                    type="minus-circle-o"
                    :disabled="http.requestHeader.length === 0"
                    @click="removeRequestHeader(index)"
            />
          </div>
            <a-button type="dashed" style="width: 100%;" @click="addRequestHeader()">
              <a-icon type="plus" />
              添加请求头参数
            </a-button>

          <p>请求体配置</p>
          <div v-for="(paramItem, index) in http.requestParams" :key="index">
            <a-input v-model="paramItem.key"
                     placeholder="label key"
                     style="width: 45%; margin-right: 8px;"
            />
            <a-input v-model="paramItem.value"
                     placeholder="label value"
                     style="width: 45%; margin-right: 8px;"
            />
            <a-icon v-if="http.requestParams.length > 0"
                    class="dynamic-delete-button"
                    type="minus-circle-o"
                    :disabled="http.requestParams.length === 0"
                    @click="removeRequestParam(index)"
            />
          </div>
          <a-button type="dashed" style="width: 100%;" @click="addRequestParam()">
            <a-icon type="plus" />
            添加请求头参数
          </a-button>
        </a-form-item>
        <a-form-item v-else-if="form.alertType === 'WECOM'">
          <p>WEBHOOK<span>*</span></p>
          <a-input v-model="wecom.webhook"
                   class="name"
                   placeholder="请输入WEBHOOK"></a-input>
        </a-form-item>

        <a-form-item v-else>

        </a-form-item>

      </a-form>
    </div>
    <div class="footer justify-end">
      <div class="confirm-footer justify-end">
        <a-button @click="cancelEvent"
                  size="small">取消</a-button>
        <a-button style="margin-left:8px"
                  @click="confirm"
                  size="small"
                  type="primary">确定</a-button>
      </div>
    </div>
  </a-modal>
</template>

<script>
import {alertTypeList,requestTypeList} from "@/utils/enumType";

export default {
  data () {
    return {
      isLoading: false,
      isShowDialog: false,
      wecom: {
        webhook: "",
      },
      http: {
        url: "",
        requestType: "POST",
        requestHeader: [],
        requestParams: [],
        requestColumn: ""
      },
      form: {
        alertType: 'HTTP',
        instanceName: null,
        instanceParams: null,
      },
      requestTypeList: requestTypeList,
      alertTypeList: alertTypeList
    }
  },
  watch: {
    isShowDialog: {
      async handler (val) {
        if (val) {
          this.initForm()
          // this.isLoading = true
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
        instanceName: null,
        alertType: 'HTTP',
        instanceParams: null,
      }
      this.wecom = {
        webhook: ""
      }
      this.http = {
        url: "",
        requestType: "POST",
        requestHeader: [],
        requestParams: [],
        requestColumn: ""
      }
      this.alertTypeList = alertTypeList;
    },

    // 创建参数对
    async addRequestHeader () {
      // 判断前一个键值对是否为空
      const length = this.http.requestHeader.length;
      if (this.http.requestHeader.length > 0 && this.http.requestHeader[length-1].key === ''
          && this.http.requestHeader[length-1].value === '') {
        this.$message.warning({ content: "当前请求头不能为空", duration: 2 })
        return
      }
      this.http.requestHeader.push({
        key: "",
        value: ""
      })
    },

    // 移除参数对
    async removeRequestHeader (index) {
      console.log(index);
      this.http.requestHeader.splice(index, 1);
    },

    // 创建参数对
    async addRequestParam () {
      // 判断前一个键值对是否为空
      const length = this.http.requestParams.length;
      if (this.http.requestParams.length > 0 && this.http.requestParams[length-1].key === ''
          && this.http.requestParams[length-1].value === '') {
        this.$message.warning({ content: "当前请求参数不能为空", duration: 2 })
        return
      }
      this.http.requestParams.push({
        key: "",
        value: ""
      })
    },

    // 移除参数对
    async removeRequestParam (index) {
      console.log(index);
      this.http.requestParams.splice(index, 1);
    },

    // 确认点击事件
    async confirm () {
      if (this.form.instanceName === null || this.form.instanceName === '' || this.form.instanceName.length === 0) {
        return this.$message.warning({ content: '告警名称不能为空', duration: 2 })
      }
      if (this.form.alertType === null || this.form.alertType === '' || this.form.alertType.length === 0) {
        return this.$message.warning({ content: '未选择告警类型', duration: 2 })
      }

      let params = {
        instanceName: this.form.instanceName,
        alertType: this.form.alertType,
        instanceParams: null,
      }
      switch (this.form.alertType) {
        case "HTTP":
          params.instanceParams = JSON.stringify(this.http);
          break
        case "WECOM":
          params.instanceParams = JSON.stringify(this.wecom);
          break
      }

      let res = await this.$http.post('/system/alertManagement/add', params)
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
.add-alert-dialog {
  /deep/ .ant-modal-body {
    padding: 0;
  }
  .form-info {
    height: 330px;
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
