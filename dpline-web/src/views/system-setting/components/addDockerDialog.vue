<template>
  <a-modal class="add-engine-dialog"
           v-model="isShowDialog"
           title="新增镜像"
           width="500px"
           v-drag
           :footer="null">
    <!-- 修改弹框右上角关闭图标 -->
    <template slot="closeIcon"><i class="chitutree-h5 chitutreeguanbi"></i></template>
    <div class="form-info"
         v-loading="isLoading">
      <a-form>
        <a-form-item class="name-url">
          <p>镜像名称<span>*</span></p>
          <a-input v-model="form.shortName"
                   class="name"
                   placeholder="请输入flink镜像名称"></a-input>
        </a-form-item>
        <a-form-item class="name-url">
          <p>镜像全路径<span>*</span></p>
          <a-input v-model="form.imageName"
                   class="name"
                   placeholder="请输入路径"></a-input>
        </a-form-item>
        <a-form-item class="name-url">
          <p>HTTP 地址<span>*</span></p>
          <a-input v-model="form.registerAddress"
                   class="name"
                   placeholder="请输入http地址"></a-input>
        </a-form-item>
        <a-form-item class="name-url">
          <p>用户名<span>*</span></p>
          <a-input v-model="form.registerUser"
                   class="name"
                   placeholder="请输入版本"></a-input>
        </a-form-item>
        <a-form-item class="name-url">
          <p>密码<span>*</span></p>
          <a-input v-model="form.registerPassword"
                   class="name"
                   type="password"
                   placeholder="请输入密码"></a-input>
        </a-form-item>
        <a-form-item>
          <p>引擎类型<span>*</span></p>
          <a-select v-model="form.motorType"
                    @change="handleTypeChange">
            <a-select-option v-for="item in motorTypeList"
                             :key="item.label"
                             :value="item.value">
              {{item.value}}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item class="name-url">
          <p>引擎版本<span>*</span></p>
          <a-select class="fileType" v-model="form.motorVersionId">
            <a-select-option v-for="item in motorVersionList"
                             :key="item.value"
                             :value="item.id">
              {{item.realVersion}}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item class="name-url">
          <p>描述信息</p>
          <a-textarea v-model="form.description"
                   class="name"
                   placeholder="请输入描述信息"
                   :auto-size="{ minRows: 2, maxRows: 3 }"></a-textarea>
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
import {motorTypeList} from '@/utils/enumType'
export default {

    data () {
      return {
        motorTypeList: motorTypeList,
        motorVersionList: [],
        isLoading: false,
        isShowDialog: false,
        form: {
          imageName: null,
          shortName: null,
          registerAddress: null,
          registerPassword: null,
          registerUser: null,
          description: null,
          motorVersionId: null,
          motorType: null
        }
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
          imageName: null,
          shortName: null,
          registerAddress: null,
          registerPassword: null,
          registerUser: null,
          description: null,
          motorVersionId: null,
          motorType: null,
        }
      },

      // 确认点击事件
      async confirmEvent () {
        if (this.form.imageName === null || this.form.imageName === '' || this.form.imageName.length === 0) {
          return this.$message.warning({ content: '镜像全路径不能为空', duration: 2 })
        }
        if (this.form.shortName === null || this.form.shortName === '' || this.form.shortName.length === 0) {
          return this.$message.warning({ content: '镜像名称不能为空', duration: 2 })
        }
        if (this.form.registerPassword === null || this.form.registerPassword === '' || this.form.registerPassword.length === 0) {
          return this.$message.warning({ content: '注册密码不能为空', duration: 2 })
        }
        if (this.form.registerUser === null || this.form.registerUser === '' || this.form.registerUser.length === 0) {
          return this.$message.warning({ content: '注册用户不能为空', duration: 2 })
        }
        if (this.form.registerAddress === null || this.form.registerAddress === '' || this.form.registerAddress.length === 0) {
          return this.$message.warning({ content: '注册地址不能为空', duration: 2 })
        }

        let params = this.form;

        let res = await this.$http.post('/system/docker/add', params)
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
      },
      async handleTypeChange(){
        const params = {
          motorType: this.form.motorType
        }
        let res = await this.$http.post('/system/motorVersion/search', this.$qs.stringify(params), {
          headers: {
            projectId: Number(this.$route.query.projectId)
          }
        })
        if (res.code === 200 && res.data){
          this.motorVersionList = res.data
        }
      },
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
