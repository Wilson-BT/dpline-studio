<template>
  <!-- 一定要用v-if="isShow"摧毁dom重新渲染，不然会有莫名其妙的问题 -->
  <a-modal v-if="isShow"
           wrapClassName="add-jar-dialog"
           v-model="isShow"
           :mask-closable="false"
           :footer="null"
           :title="title">
    <!-- <span slot="closeIcon">x</span> -->
    <div class="add-jar">
      <a-form-model>
        <div class="form-body">
          <div v-if="this.type !== 'update' " class="justify-start item">
            <p class="label">上传jar包</p>
            <div style="width:400px">
              <a-upload-dragger :file-list="fileList"
                                :remove="handleRemove"
                                :disabled="progress > 0"
                                :before-upload="beforeUpload"
                                accept=".jar">
                <p class="ant-upload-drag-icon">
                  <img src="@/assets/icons/icon_upload.png"
                       alt="">
                </p>
                <p class="ant-upload-text">
                  将文件拖拽到此处，或<a-button type="link">点击上传jar包</a-button>
                </p>
                <p class="ant-upload-hint">
                  文件最大{{fileLimit}}MB，支持 .jar格式
                </p>
              </a-upload-dragger>
            </div>
          </div>
<!--          <div class="justify-start item">-->
<!--            <p class="label">资源名称<span class="red">*</span></p>-->
<!--            <a-input v-model="form.name"-->
<!--                     :disabled="true"-->
<!--                     placeholder="请输入资源名称" />-->
<!--          </div >-->
          <div class="justify-start item">
            <p class="label">jar包名称</p>
            <a-input :disabled="true"
                     v-model="form.jarName"
                     placeholder="请输入jar包名称" />
          </div>
<!--          <div class="justify-between item" >-->
<!--            <div class="justify-start left-content" >-->
<!--              <p class="label">文件类型<span class="red">*</span></p>-->
<!--              <a-select class="fileType" v-model="form.jarFunctionType"-->
<!--                        @change="handleFileTypeChange"-->
<!--                        :disabled="true"-->
<!--              >-->
<!--                <a-select-option v-for="item in jarFunctionTypeList"-->
<!--                                 :key="item.value"-->
<!--                                 :value="item.value">-->
<!--                  {{item.value}}-->
<!--                </a-select-option>-->
<!--              </a-select>-->
<!--            </div>-->
<!--          </div>-->

          <div class="justify-between item" >
            <div class="justify-start" >
              <p class="label">引擎类型</p>
              <a-select class="fileType"
                        :disabled = "true"
                        v-model="form.runMotorType">
                <a-select-option v-for="item in motorTypeList"
                                 :key="item.label"
                                 :value="item.value">
                  {{item.value}}
                </a-select-option>
              </a-select>
            </div>
            <div class="justify-end">
              <p class="label">引擎版本</p>
              <a-select class="versionType"
                        @select="(value) => handleMotorVersionChange(value)"
                        v-model="motorRealVersion">
                <a-select-option v-for="item in motorVersionList"
                                 :key="item.value"
                                 :value="item.id">
                  {{item.realVersion}}
                </a-select-option>
              </a-select>

            </div>
          </div>

          <div class="item justify-start">
            <p class="label">资源描述 </p>
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
        <div class="pocess-load"
             v-show="isLoading">
          <div class="title">进度</div>
          <div v-if="isShowProcess"
               class="progress">
            <a-progress :percent="progress"
                        :show-info="true"
                        status="active" />
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

            <template v-if="isLoading || isLoading === ''">保存</template>
            <template v-else-if="isLoading === false">重试</template>
          </a-button>
          <!-- <a-button class="button-restyle button-confirm"
                    :disabled="isLoading === true?true:false"
                    @click="clickSubmit">
            <template v-if="isLoading || isLoading === ''">确认</template>
            <template v-else-if="isLoading === false">重试</template>
          </a-button>
          <a-button class="button-restyle button-cancel"
                    @click="cancelEvent">取消</a-button>  -->
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
        motorRealVersion: '',
        mainResourceId: '',
        motorVersionList: [],
        // motorType: '',
        motorTypeList: motorTypeList,
        fileLimit: 500,//文件最大500M
        fileList: [],
        jarFunctionTypeList: jarFunctionTypeList,
        isShow: false,
        isUploading: false,
        uploadStatus: '',
        headers: {},
        isLoading: '',
        form: {
          mainResourceId: '',
          runMotorType: '',
          motorVersionId: '',
          jarName: '',
          // jarFunctionType: '',
          jarAuthType: '',
          projectId: '',
          description: '',
          fileMd5: '',
          id:'',
        },
        type: '',
        // versionType: '',
        id: '',
        isShowProcess: true,
        progress: 0
      }
    },
    props: {
      jarAuthType: String,
    },
    computed: {
      loading () {
        return this.isLoading == true
      }
    },
    watch: {
      isShow: {
        handler () {

        }
      }
    },
    methods: {
      handleMotorVersionChange(value){
        this.form.motorVersionId = value
      },
      handleRemove (file) {
        const index = this.fileList.indexOf(file);
        const newFileList = this.fileList.slice();
        newFileList.splice(index, 1);
        this.fileList = newFileList;
        this.form.jarName = ''
      },
      beforeUpload (file) {
        if (this.type === 'add') {
          this.form.jarName = file.name
        }
        this.fileList = [file]
        // 这里是为了计算文件的md5值并且做回传， 如果没有则不需要计算
        this.getFileMd5(file)
            .then(result => {
              // 处理解析情况
              this.form.fileMd5 = result;
            });
        return false
      },
      getFileMd5(file){
        return new Promise((resolve) => {
          var tmp_md5;
          var blobSlice = File.prototype.slice || File.prototype.mozSlice || File.prototype.webkitSlice,
              // file = this.files[0],
              chunkSize = 8097152, // Read in chunks of 2MB
              chunks = Math.ceil(file.size / chunkSize),
              currentChunk = 0,
              spark = new this.$SparkMD5.ArrayBuffer(),
              fileReader = new FileReader();

          fileReader.onload = function (e) {
            spark.append(e.target.result); // Append array buffer
            currentChunk++;
            var md5_progress = Math.floor((currentChunk / chunks) * 100);
            console.log(file.name + "  正在处理，请稍等," + "已完成" + md5_progress + "%");
            if (currentChunk < chunks) {
              loadNext();
            } else {
              tmp_md5 = spark.end();
              console.log(file.name + "的MD5值是：" + tmp_md5)
              resolve(tmp_md5);
            }
          };
          fileReader.onerror = function () {
            console.warn('oops, something went wrong.');
          };
          function loadNext() {
            var start = currentChunk * chunkSize,
                end = ((start + chunkSize) >= file.size) ? file.size : start + chunkSize;
            fileReader.readAsArrayBuffer(blobSlice.call(file, start, end));
          }
          loadNext();
        })
      },

      open (data) {
        if (data) {
          // this.runMotorType = data.runMotorType
          this.form.runMotorType = data.runMotorType
          // this.mainResourceId = data.mainResourceId
          this.isShowProcess = true
          this.isLoading = ''
          this.progress = 0
          this.fileList = []
          // this.headers = {}
          this.isUploading = false
          this.uploadStatus = ''
          this.title = data.title
          this.type = data.type || ''
          this.form.mainResourceId = data.mainResourceId
          // this.form.jarFunctionType = data.jarFunctionType
          // this.versionType = data.versionType
          // 权限范围
          this.form.jarAuthType=this.jarAuthType
          // add / update / reload
          // add /reload 的时候必须 primaryJar 为1
          // update： 分为 primary 时候的update,直接为 或者是 primary
          //          仅在 history 的时候 update，才能更改primary=0 的部分
          // versionType: primary / history
          if (this.type === 'add') {
            //点击新增jar
            this.form.jarName = ''
            // this.form.name = 'xxxxxx'
            this.form.description = ''
            // this.form.jarFunctionType = this.jarFunctionTypeList[0].value
            // 新增jar 自动作为primaryJar
            // this.form.primaryJar=1
            this.motorRealVersion = ''
            this.form.motorVersionId = ''
            // this.form.runMotorType = ''
          } else {
            // Md5 在上传新版本的时候需要指定
            if (data.data) {
              this.form.id = Number(data.data.id)
              // console.log(this.form)
              this.form.jarName = data.data.jarName
              this.form.motorVersionId = data.data.motorVersionId

              this.motorRealVersion = data.data.motorRealVersion
              // this.form.jarFunctionType=data.data.jarFunctionType
              // console.log(this.form.jarFunctionType)
              // this.id = Number(data.data.id) || ''
              // this.form.primaryJar = data.data.primaryJar
            }
            // update 的时候
          }
        }
        this.isShow = true
        this.handleMotorTypeChange()
      },
      handleFileTypeChange (value) {
        console.log('value', value)
      },
      async handleMotorTypeChange(){
        // 先置为空，避免历史的值误操作。
        this.motorRealVersion = ''
        this.form.motorVersionId = ''
        const params = {
          motorType: this.form.runMotorType
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
        const updateUrl = '/jar/update'
        this.form.jarName = this.form.jarName.trim()
        // this.form.name = this.form.name.trim()
        this.form.description = this.form.description.trim()
        // this.form.motorVersionId = this.form.motorVersionId.trim()
        // if (this.form.description === '') {
        //   this.$message.warning('版本描述不能为空')
        //   return
        // }
        // if(this.form.motorVersionId === ''){
        //   this.$message.warning('引擎版本不能为空')
        //   return
        // }
        if(this.form.jarName === ''){
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
        // params.id = this.id
        console.log(params)

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
        this.form.jarName = this.form.jarName.trim()
        // this.form.name = this.form.name.trim()
        this.form.description = this.form.description.trim()
        if (this.fileList.length === 0) {
          this.$message.warning('请上传jar包')
          return
        }
        if (this.form.jarName === '') {
          this.$message.warning('jar包名称不能为空')
          return
        }
        // if (this.form.motorVersionId === '' && (this.form.jarFunctionType === 'CONNECTOR' || this.form.jarFunctionType === 'UDF')) {
        //   this.$message.warning('引擎版本不能为空')
        //   return
        // }
        //限制文件大小
        const isLimit = this.fileList[0].size / 1024 / 1024 <= this.fileLimit
        if (!isLimit) {
          this.$message.warning(`jar包最大不能超过${this.fileLimit}MB`)
          return
        }
        // if (this.form.description === '') {
        //   this.$message.warning('版本描述不能为空')
        //   return
        // }
        const url = '/jar/addJar'
        const params = JSON.parse(JSON.stringify(this.form))
        params.jarName = encodeURIComponent(params.jarName)
        params.description = encodeURIComponent(params.description)
        // 项目私有资源 or 公有资源
        params.projectId = Number(this.$route.query.projectId)
        params.mainResourceId = Number(params.mainResourceId)
        const formData = new FormData()
        formData.append('file', this.fileList[0])
        this.isShowProcess = true
        this.isLoading = true
        this.progress = 0
        console.log(params)
        let res = await this.$http.post(url, formData, {
          headers: params,
          onUploadProgress: progressEvent => {
            let progress = (progressEvent.loaded / progressEvent.total * 100 | 0)
            progress = parseInt(progress)
            if (progress === 100) {
              progress = 99.9
            }
            this.progress = progress
          }
        })
        this.isLoading = false
        if (res.code === 200) {
          this.progress = 100
          this.$message.success('添加成功')
          this.isShow = false
          this.$emit('addSuccess')
        } else {
          this.progress = 0
          this.$message.error(res.msg)
        }
      },
      cancelEvent () {
        this.isShow = false
      },
      handleChange (info) {
        this.isUploading = true
        const file = info.file
        if (file && file.response) {
          if (file.response.code === 200) {
            this.isUploading = false
            this.uploadStatus = '上传成功'
            this.$message.success('上传成功')
            const url = file.response.data
            const lastIndex = file.jarName.lastIndexOf('/')
            const jarName = file.jarName.substr(lastIndex + 1)
            if (this.type === 'add') {
              this.form.jarName = jarName
            }
            this.form.url = url
          } else {
            this.isUploading = false
            this.uploadStatus = '上传失败'
            this.$message.error(file.response.msg)
          }
        }
        // if (info.file.status !== 'uploading') {
        //   // console.log(info.file, info.fileList);
        // }
        // if (info.file.status === 'done') {
        //   this.$message.success(`${info.file.name} file uploaded successfully`);
        // } else if (info.file.status === 'error') {
        //   this.$message.error(`${info.file.name} file upload failed.`);
        // }
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