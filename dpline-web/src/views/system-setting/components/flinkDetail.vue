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
          <p>flink版本名称<span>*</span></p>
          <a-input v-model="flinkVersion.flinkName"
                   class="name"
                   placeholder="请输入flink版本名称"></a-input>
        </a-form-item>
        <a-form-item class="name-url">
          <p>flink客户端路径<span>*</span></p>
          <a-input v-model="flinkVersion.flinkPath"
                   class="name"
                   placeholder="请输入路径"></a-input>
        </a-form-item>
        <a-form-item class="name-url">
          <p>版本<span>*</span></p>
          <a-input v-model="flinkVersion.realVersion"
                   class="name"
                   placeholder="请输入版本"></a-input>
        </a-form-item>
        <a-form-item class="name-url">
          <p>描述信息</p>
          <a-textarea v-model="flinkVersion.description"
                      class="name"
                      placeholder="请输入描述信息"
                      :auto-size="{ minRows: 3, maxRows: 5 }"></a-textarea>
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

export default {
  data() {
    return {
      isLoading: false,
      isShowDialog: false,
      flinkVersion: {
        flinkName: null,
        flinkPath: null,
        realVersion: null,
        description: null
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
    open(flinkVersion) {
      this.isShowFlinkDetailDialog = true;
      this.flinkVersion = flinkVersion;
    },

    // 确认点击事件
    async confirmEvent() {
      let params = this.flinkVersion;
      let res = await this.$http.post('/system/motorVersion/updateInfo', params)
      if (res.code == 200) {
        this.$message.success({ content: '添加成功', duration: 2 });
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
