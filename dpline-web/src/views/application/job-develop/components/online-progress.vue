<template>
  <a-modal wrapClassName="online-progress-dialog"
           :mask-closable="false"
           v-model="isShow"
           :footer="null"
           width="470px"
           v-drag
           :title="title">
    <div class="form-body">
        <a-form-model :model="form">
          <a-form-model-item>
            <p class="label"><span>*</span>Job 任务名称</p>
            <a-input v-model="form.jobName"
                     @blur="validateName"
                     v-decorator="[
            'tagName',
            { rules: [{ required: true, message: '^[a-z]([-a-z0-9]{10,42})?[a-z0-9]$' }] },
          ]"
                     placeholder="^[a-z]([-a-z0-9]{10,42})?[a-z0-9]$" />
          </a-form-model-item>
          <a-form-model-item>
            <p class="label">请输入备注</p>
            <a-input type="textarea"
                        v-model="form.remark"
                        :maxLength="100"
                        placeholder="请输入发布备注（可选）"
                        @input="handleTextareaInput"
            >
              </a-input>
          </a-form-model-item>
          <p class="input-length justify-end">{{form.remark.length}}/100</p>
        </a-form-model>
      </div>

    <div class="footer justify-end">
      <template>
        <a-button class="button-restyle button-cancel"
                  @click="cancelEvent">取消</a-button>
      </template>
      <a-button
          :disabled="validate === false"
          class="button-restyle button-confirm"
                @click="confirmEvent()">
        <template>确定</template>

      </a-button>
    </div>

  </a-modal>
</template>

<script>
  export default {
    name: "OnlineProgress",
    data () {
      return {
        // tagId:'',
        validate: false,
        isShow: false,
        baseTitle: '发布新版本',
        title: '',
        progress: 0,
        strokeColor: '#32DB7E',
        // remark: '',
        form: {
          tagId:"",
          onlineType: "",
          remark:"",
          jobName:""
        }
        // isShowApply: false,
        // isApplying: false,
        // isInWhite: ''
      }
    },
    props: {
      progressData: {
        type: Object
      }
    },
    computed: {

    },
    components: {
    },
    watch: {
      progressData: {
        handler (val) {
          if (val) {
            this.title = this.baseTitle + val.version
            this.progress = val.progress
            if (val.isCheckSuccess === false) {
              this.strokeColor = '#FF5555'
            } else {
              this.strokeColor = '#32DB7E'
            }

          }
        },
        deep: true,
        immediate: true
      }
    },
    created () {
      this.title = this.baseTitle
    },
    methods: {
      open (onlineType, tagId) {
        this.isShow = true
        this.form.onlineType = onlineType
        if(onlineType === 'tag'){
          this.form.tagId = tagId
        }
        this.form.jobName = ''
        this.form.remark = ''
        // this.tagId = value
      },
      validateName(){
        const regex = /^[a-z]([-a-z0-9]{10,42})?[a-z0-9]$/;
        if (!regex.test(this.form.jobName)) {
          this.fileNameError = '文件名必须由字母、数字、中划线组成且长度在10到45个字符,且只能以字母开头，数字或者字母结束';
          this.$message.error(this.fileNameError)
          this.validate = false;
        } else {
          this.fileNameError = '';
          this.validate = true;
        }
      },
      close () {
        this.isShow = false
      },
      handleTextareaInput(event) {
        if (event.target.value.length > this.maxLength) {
          this.form.remark = event.target.value.slice(0, this.maxLength);
        }
      },
      confirmEvent () {
        this.$emit('confirm', this.form) // 触发检验和发布
      },
      cancelEvent () {
        this.isShow = false
      },

      select (selectedKeys) {
        this.selectedKeys = selectedKeys
      },
    },
    mounted () {

    }
  }
</script>
<style lang="scss">
  .online-progress-dialog {
    .ant-modal-body {
      padding: 0;
    }
    .form-body {
      padding: 0 25px 10px 20px;
      .ant-form-item {
        margin-bottom: 0;
        margin-top: 20px;
        font-size: 12px;

        .ant-form-item-control {
          line-height: normal;
          .ant-form-item-children {
            display: flex;
            align-items: center;
          }

          .select-source {
            // flex-shrink: 0;
            .description {
              display: none;
            }
          }
        }
        input {
          margin-top: 0px;
          font-size: 12px;
        }
        .label {
          line-height: normal;
          margin-bottom: 4px;
          width: 97px;
          font-size: 12px;
          text-align: right;
          margin-right: 8px;
          span {
            color: red;
          }
          .question {
            margin-left: 3px;
            cursor: pointer;
          }
        }
        .red {
          color: red;
        }
      }
    }
    .save-folder_item {
      .ant-form-item-children {
        display: flex;
        align-items: baseline !important;
      }
      .save-folder-tree {
        width: 100%;
      }
    }
    .footer {
      height: 44px;
      padding-right: 16px;
      border-top: solid 1px #d9d9d9;
    }
  }
</style>
