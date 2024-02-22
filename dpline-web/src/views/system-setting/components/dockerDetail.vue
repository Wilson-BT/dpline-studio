<template>
  <a-modal class="add-engine-dialog"
           v-model="isShowFlinkDetailDialog"
           title="flink版本详情"
           width="600px"
           :footer="null">
    <!-- 修改弹框右上角关闭图标 -->
    <template slot="closeIcon"><i class="chitutree-h5 chitutreeguanbi"></i></template>
    <div class="form-info"
         v-loading="isLoading">
      <a-form>
        <a-form-item class="name-url">
          <p>Docker名称<span>*</span></p>
          <a-input v-model="dockerImageInfo.shortName"
                   class="name"
                   placeholder="请输入Image版本名称"></a-input>
        </a-form-item>
        <a-form-item class="name-url">
          <p>Docker镜像地址<span>*</span></p>
          <a-input v-model="dockerImageInfo.imageName"
                   class="name"
                   placeholder="请输入全路径"></a-input>
        </a-form-item>
        <a-form-item class="name-url">
          <p>注册地址<span>*</span></p>
          <a-input v-model="dockerImageInfo.registerAddress"
                   class="name"
                   placeholder="请输入地址"></a-input>
        </a-form-item>
        <a-form-item class="name-url">
          <p>用户名<span>*</span></p>
          <a-input v-model="dockerImageInfo.registerUser"
                   class="name"
                   placeholder="请输入用户名"></a-input>
        </a-form-item>
        <a-form-item class="name-url">
          <p>密码<span>*</span></p>
          <a-input v-model="dockerImageInfo.registerPassword"
                   class="name"
                   type="password"
                   placeholder="请输入密码"></a-input>
        </a-form-item>
        <a-form-item>
          <p>引擎类型<span>*</span></p>
          <a-select v-model="dockerImageInfo.motorType"
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
          <a-select class="fileType"
                    @search="handleTypeChange"
                    @select="(value) => handleVersionChange(value)"
                    v-model="dockerImageInfo.motorRealVersion">
            <a-select-option v-for="item in motorVersionList"
                             :key="item.value"
                             :value="item.id">
              {{item.realVersion}}
            </a-select-option>
          </a-select>
        </a-form-item>
<!--        <a-form-item class="name-url">-->
<!--          <p>描述信息</p>-->
<!--          <a-textarea v-model="dockerImageInfo.description"-->
<!--                      class="name"-->
<!--                      placeholder="请输入描述信息"-->
<!--                      :auto-size="{ minRows: 3, maxRows: 5 }"></a-textarea>-->
<!--        </a-form-item>-->
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
import {motorTypeList} from '@/utils/enumType'
export default {
  data() {
    return {
      motorVersionList: [],
      motorTypeList: motorTypeList,
      isLoading: false,
      isShowDialog: false,
      dockerImageInfo: {
        id: null,
        imageName: null,
        shortName: null,
        registerPassword: null,
        registerAddress: null,
        registerUser: null,
        motorType: null,
        motorRealVersion: null,
        motorVersionId: null
      },
      isShowFlinkDetailDialog: false
    }
  },
  watch: {
    isShowFlinkDetailDialog: {
      async handler(val) {
        if (val) {
          this.isLoading = true
          this.isLoading = false
          this.curEnv = sessionStorage.getItem('env');
        }
      }
    }
  },
  methods: {
    created() {
    },
    open(dockerImageInfo) {
      this.isShowFlinkDetailDialog = true;
      this.dockerImageInfo = dockerImageInfo;
    },
    handleVersionChange (value){
       this.dockerImageInfo.motorVersionId = value
    },
    async handleTypeChange(){
      const params = {
        motorType: this.dockerImageInfo.motorType
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
    // 确认点击事件
    async confirmEvent() {
      // let params = this.dockerImageInfo;
      const params = {
        id: this.dockerImageInfo.id,
        shortName: this.dockerImageInfo.shortName,
        imageName: this.dockerImageInfo.imageName,
        registerAddress: this.dockerImageInfo.registerAddress,
        registerUser: this.dockerImageInfo.registerUser,
        registerPassword: this.dockerImageInfo.registerPassword,
        motorType: this.dockerImageInfo.motorType,
        motorVersionId: this.dockerImageInfo.motorVersionId
      }
      let res = await this.$http.post('/system/docker/update', params)
      if (res.code === 200) {
        this.$message.success({ content: '更新成功', duration: 2 });
        this.isShowFlinkDetailDialog = false;
        this.$emit('confirmEvent', true);
      } else {
        this.$message.error({ content: res.msg, duration: 2 });
      }
    },
    // 取消点击事件
    cancelEvent() {
      this.isShowFlinkDetailDialog = false
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
