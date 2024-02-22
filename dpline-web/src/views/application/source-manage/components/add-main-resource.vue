<template>
  <!-- 一定要用v-if="isShow"摧毁dom重新渲染，不然会有莫名其妙的问题 -->
  <a-modal v-if="isShow"
           wrapClassName="add-jar-dialog"
           v-model="isShow"
           :mask-closable="false"
           :footer="null"
           :title="title">
    <div class="add-jar">
      <a-form-model>
        <div class="form-body">
          <div class="justify-start item">
            <p class="label">资源名称<span class="red">*</span></p>
            <a-input v-model="form.name"
                     placeholder="请输入资源名称" />
          </div >
          <div class="justify-between item" >
            <div class="justify-start left-content" >
              <p class="label">文件类型<span class="red">*</span></p>
              <a-select class="fileType" v-model="form.jarFunctionType"
                        @change="handleFileTypeChange"
                        :disabled="this.type !== 'add'"
              >
                <a-select-option v-for="item in jarFunctionTypeList"
                                 :key="item.value"
                                 :value="item.value">
                  {{item.value}}
                </a-select-option>
              </a-select>
            </div>
            <div class="justify-end" >
              <p class="label">引擎类型</p>
              <a-select class="versionType"
                        :disabled = "this.type !== 'add'"
                        v-model="form.runMotorType">
                <a-select-option v-for="item in motorTypeList"
                                 :key="item.label"
                                 :value="item.value">
                  {{item.value}}
                </a-select-option>
              </a-select>
            </div>
          </div>
<!--          <div class="justify-between item" >-->
<!--            -->
<!--          </div>-->
          <div  class="item justify-start">
            <p class="label">资源描述</p>
            <div class="description-content">
              <a-input class="description"
                       type="textarea"
                          :max-length="250"
                          v-model="form.description"
                          placeholder="请输入资源描述" />
              <span class="max-length">{{form.description.length}}/250</span>
            </div>
          </div>
        </div>
        <div class="footer justify-end">
          <a-button @click="cancelEvent"
                    size="small">取消</a-button>
          <a-button style="margin-left:8px"
                    @click="clickSubmit"
                    :loading="loading"
                    size="small"
                    type="primary">

            <template >保存</template>
          </a-button>
        </div>
      </a-form-model>

    </div>
  </a-modal>
</template>

<script>
import _ from 'lodash'
import {jarFunctionTypeList, motorTypeList} from '@/utils/enumType'

export default {
    components: {},
    data () {
      return {
        // motorRealVersion: '',
        // motorVersionList: [],
        // motorType: '',
        motorTypeList: motorTypeList,
        // fileLimit: 500,//文件最大500M
        // fileList: [],
        jarFunctionTypeList: jarFunctionTypeList,
        isShow: false,
        // isUploading: false,
        // uploadStatus: '',
        headers: {},
        form: {
          mainResourceId: '',
          runMotorType: '',
          jarFunctionType: '',
          jarAuthType: '',
          projectId: '',
          name: '',
          description: '',
        },
        type: '',
        id: '',
      }
    },
    props: {
      jarAuthType: String,
    },
    computed: {
      loading () {
        return this.isLoading === true
      }
    },
    watch: {
      isShow: {
        handler () {

        }
      }
    },
    methods: {
      open (data) {
        if (data) {
          this.progress = 0
          this.title = data.title
          this.type = data.type || ''
          // 权限范围
          this.form.jarAuthType=this.jarAuthType
          // add / update / reload
          // add /reload 的时候必须 primaryJar 为1
          // update： 分为 primary 时候的update,直接为 或者是 primary
          //          仅在 history 的时候 update，才能更改primary=0 的部分
          // versionType: primary / history
          if (this.type === 'add') {
            //点击新增jar
            this.form.name = 'xxxxxx'
            this.form.description = ''
            this.form.mainResourceId = ''
            // UDF or connector
            this.form.jarFunctionType = this.jarFunctionTypeList[0].value
            // 新增jar 自动作为primaryJar
            // this.form.primaryJar=1
            this.form.runMotorType = ''
          } else {
            // Md5 在上传新版本的时候需要指定
            if (data.data) {
              this.form.mainResourceId = Number(data.data.id)
              this.form.name = data.data.name
              this.form.description = data.data.description
              this.form.runMotorType = data.data.runMotorType
              this.form.jarFunctionType=data.data.jarFunctionType
            }
            // update 的时候
          }
        }
        this.isShow = true
      },
      handleFileTypeChange (value) {
        console.log('value', value)
      },
      close () {
        this.isShow = false
      },
      clickSubmit:
        _.debounce(function () {
          console.log('debounce')
          if(this.type === 'update'){
            this.confirmUpdateEvent()
          } else {
            this.confirmSubmitEvent()
          }
        }, 500),
      async confirmUpdateEvent(){
        console.log("update")
        const updateUrl = '/jar/updateMainResource'
        this.form.name = encodeURIComponent(this.form.name.trim())
        this.form.description = this.form.description.trim()
        // if (this.form.description === '') {
        //   this.$message.warning('版本描述不能为空')
        //   return
        // }
        if(this.form.name === ''){
          this.$message.warning('Jar包名称不能为空')
          return
        }
        const params = JSON.parse(JSON.stringify(this.form))
        // 项目私有资源 or 公有资源
        if(this.jarAuthType === "project"){
          params.projectId = this.$route.query.projectId
        }else {
          params.projectId = ''
        }
        // params.type = 'UPDATE'
        params.id = this.id
        params.description = encodeURIComponent(params.description)

        let res = await this.$http.post(updateUrl, params)
        if (res.code === 200) {
          this.isShow = false
          this.$message.success('更新成功')
          this.$emit("addSuccess");
        } else {
          this.$message.error(res.msg);
        }
      },
      async confirmSubmitEvent () {
        console.log("submit")
        this.form.name = this.form.name.trim()
        this.form.description = this.form.description.trim()
        if (this.form.name === '') {
          this.$message.warning('jar包名称不能为空')
          return
        }
        // if (this.form.description === '') {
        //   this.$message.warning('版本描述不能为空')
        //   return
        // }
        const url = '/jar/createMainSource'
        const params = JSON.parse(JSON.stringify(this.form))
        params.name = encodeURIComponent(params.name)
        params.description = encodeURIComponent(params.description)
        // 项目私有资源 or 公有资源
        params.projectId = this.$route.query.projectId
        console.log(params)
        let res = await this.$http.post(url, params, {
          headers: {
              projectId: Number(this.$route.query.projectId)
          }
        })
        this.isLoading = false
        if (res.code === 200) {
          this.$message.success('添加成功')
          this.isShow = false
          this.$emit('addSuccess')
        } else {
          this.$message.error(res.msg)
        }
      },
      cancelEvent () {
        this.isShow = false
      },
    },
    mounted () {

    }
  }
</script>
<style lang="scss">
  .add-jar-dialog {
    .ant-modal {
      width: 600px !important;
    }
  }
</style>
<style lang="scss" scoped>
  /deep/ .ant-modal-body {
    padding: 0;
    .add-jar {
      position: relative;
      .form-body {
        padding: 20px 40px 10px 15px;
        .ant-upload {
          font-size: 12px;
          .ant-upload-text {
            font-weight: 600;
          }
          .ant-upload-text,
          .ant-upload-hint {
            font-size: 12px;
          }
          button {
            font-size: 12px;
          }
        }
        .ant-upload-list {
          .ant-upload-list-item-card-actions {
            right: -20px;
          }
          .ant-upload-list-item-info {
            display: flex;
            align-items: center;
            .anticon-paper-clip {
              width: 20px;
              height: 20px;
              margin-top: -5px;
              background: url(~@/assets/icons/icon_jar.png) no-repeat;
              background-size: 100%;
              svg {
                display: none !important;
              }
            }
            .ant-upload-list-item-name {
              font-size: 12px;
              color: #2c2f37;
            }
          }
        }
        .upload-status {
          margin-left: 10px;
        }
        .item {
          margin-bottom: 0;
          margin-top: 12px;
          font-size: 12px;
          // .max-length {
          //   text-align: right;
          // }
          &:first-of-type {
            margin-top: 0;
          }
          input {
            font-size: 12px;
          }
          .label {
            line-height: normal;
            width: 90px;
            padding-right: 10px;
            text-align: right;
            flex-shrink: 0;
          }
          .description-content {
            position: relative;
            border: 1px solid #d9d9d9;
            width: 100%;
            border-radius: 6px;
            .description {
              height: 60px;
              resize: none;
              overflow-y: auto;
              border: none;
              outline: none;
              box-shadow: none;
              margin-bottom: 10px;
            }
            .max-length {
              position: absolute;
              bottom: 0;
              right: 12px;
              color: #999;
            }
          }

          .red {
            color: red;
          }
        }
        .fileType {
          width: 200px;
        }
        .versionType {
          width: 130px;
        }
      }
      .pocess-load {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 0 16px;
        height: 32px;
        border-bottom: 1px solid #d9d9d9;
        .progress {
          width: calc(100% - 32px);
        }
      }
      .footer {
        height: 44px;
        padding-right: 16px;
        margin-top: 5px;
      }

      // .progress {
      //   position: absolute;
      //   bottom: 35px;
      //   width: calc(100% - 32px);
      //   left: 16px;
      //   z-index: 10;
      //   display: flex;
      //   justify-content: space-between;
      //   .title {
      //     width: auto;
      //   }
      //   .ant-progress-circle .ant-progress-text {
      //     color: #0066FF;
      //   }
      // }
    }
  }
</style>