<template>
  <div class="DS"
       :class="{'is-max':isMaxWidth}">
    <div class="row">
      <p class="label">请选择MAIN资源</p>
      <div class="justify-between">
        <a-select placeholder="请选择MAIN资源"
                  class="select-source"
                  dropdownClassName="select-source-option"
                  show-search
                  :allow-clear="true"
                  v-model="dataStreamContent.name"
                  :filter-option="false"
                  :not-found-content="isFetchingSource ? undefined : null"
                  @search="handleSourceSearch"
                  @change="handleSourceChange"
                  :disabled="!isAllowEdit">
          <a-spin v-if="isFetchingSource"
                  slot="notFoundContent"
                  size="small" />
          <a-select-option v-for="(item) in filterJarData"
                           :value="item.id"
                           :key="item.id">
            <a-tooltip placement="topLeft">
              <template slot="title">
                <div>
                  <p class="name"> {{ item.name }}</p>
                </div>
              </template>
              <div>
                <div>
                  <p class="name"> {{ item.name }}</p>
                </div>
              </div>
            </a-tooltip>
          </a-select-option>
        </a-select>
      </div>

    </div>
<!--    <div class="row">-->
<!--      <p class="label">选择MAIN JAR版本</p>-->
<!--      <div class="justify-between">-->
<!--        <a-select placeholder="选择MAIN JAR版本"-->
<!--                  class="select-source"-->
<!--                  dropdownClassName="select-source-option"-->
<!--                  label-in-value-->
<!--                  :allow-clear="true"-->
<!--                  v-model="jarVersion"-->
<!--                  :filter-option="false"-->
<!--                  :not-found-content="isFetchingJarVersion ? undefined : null"-->
<!--                  @change="handleChangeJarVersion"-->
<!--                  :disabled="!isAllowEdit">-->
<!--          <a-spin v-if="isFetchingJarVersion"-->
<!--                  slot="notFoundContent"-->
<!--                  size="small" />-->
<!--          <a-select-option v-for="(item,index) in filterJarVersionData"-->
<!--                           :value="JSON.stringify(item)"-->
<!--                           :key="String(item.id) + index">-->
<!--            <a-tooltip placement="topLeft">-->
<!--              <template slot="title">-->
<!--                <div>-->
<!--                  <p class="name"> {{ item.jarVersion }}</p>-->
<!--                  <p class="description">{{item.jarVersion}}</p>-->
<!--                </div>-->
<!--              </template>-->
<!--              <div>-->
<!--                <div>-->
<!--                  <p class="name"> {{ item.jarVersion }}</p>-->
<!--                  <p class="description">{{item.jarVersion}}</p>-->
<!--                </div>-->
<!--              </div>-->
<!--            </a-tooltip>-->
<!--          </a-select-option>-->
<!--        </a-select>-->
<!--      </div>-->

<!--    </div>-->
    <div class="row">
      <p class="label">Main-Class</p>
      <a-input v-model="dataStreamContent.mainClass"
               class="main-class"
               placeholder="请输入jar包的Main-Class，示例：com.chitu.Application"
               :disabled="!isAllowEdit" />
    </div>
    <div class="row">
      <p class="label">App Args</p>
      <a-input v-model="dataStreamContent.appArgs"
               type="textarea"
               class="main-class"
               placeholder="请输入jar包的额外参数，如-Dxxx=xxx等，具体由main-jar包自行控制"
               />
    </div>
  </div>
</template>

<script>
  export default {
    name: "DSConfig",
    data () {
      return {
        isAllowEdit: true,
        isLoading: false,
        isFetchingSource: false,
        isFetchingJarVersion: false,
        sourceData: [],
        filterJarData: [],
        source: '',
        dataStreamContent: {
          mainClass: '', //主函数名称
          appArgs: '',
          mainResourceId: '',
          name: ''
        },
      }
    },
    props: {
      config: {
        type: Object,
        default: () => {
          return {}
        }
      },
      isShow: {
        type: Boolean,
        default: false
      },
      isMaxWidth: {
        type: Boolean,
        default: true
      },
      fileId: {
        type: [String, Number],
        default: ''
      }
    },
    computed: {
      // isProdEnv () {
      //   return this.$store.getters.env === 'prod'
      // }
    },
    components: {
    },
    watch: {
      config: {
        handler (val) {
          if (val) {
            this.dataStreamContent = JSON.parse(JSON.stringify(val))
          }
        },
        immediate: true,
        deep: true
      },
      isShow: {
        async handler (val) {
          if (val) {
            this.isLoading = true
            await this.getSource()
            // 如果没有相应资源
            // await this.getJarVersion(this.dataStreamContent.aliasName)
            this.isLoading = false
            // if (this.jarVersionData && this.jarVersionData.length) {
            //   const findItem = this.jarVersionData.filter(item => item.jarVersion === this.dataStreamContent.jarVersion)
            //   if (findItem && findItem.length) {
            //     this.jarVersion = { key: JSON.stringify(findItem[0]), label: findItem[0].jarVersion }
            //   } else {
            //     this.$message.error("Jar包版本已经不存在，请重新选择")
            //   }
            // }
          }
        }
      }
    },
    created () {
    },
    methods: {
      async isAllowEditFile () {
        let res = await this.$http.post('/file/allowEditFile', {
          id: this.fileDetail.id
        }, {
          headers: {
            projectId: Number(this.$route.query.projectId)
          }
        })
        if (res.code === 200) {
          this.isAllowEdit = res.data
        }
      },
      handleSourceSearch (value) {
        this.filterJarData = this.sourceData.filter(item => item.name.includes(value));
      },
      async handleSourceChange (value) {
        if (value) {
          // 默认选择主版本
          this.dataStreamContent.jarId = ''
          this.dataStreamContent.mainResourceId = Number(value)
          // const jar = JSON.parse(value.key)
          // this.dataStreamContent.jarName = jar.jarName
          // this.dataStreamContent.aliasName = jar.name
          // this.dataStreamContent.jarVersion = jar.jarVersion
          // this.dataStreamContent.jarId = jar.id
          // this.dataStreamContent.jarPath = jar.jarPath
          // this.getJarVersion(this.dataStreamContent.aliasName)
          // this.dataStreamContent.jarPath = ''
        }
      },
      async getSource (value) {
        const params = {
          name: value || '', //引擎名称
          projectId: Number(this.$route.query.projectId),
          jarFunctionType: 'MAIN'
        }
        this.isFetchingSource = true
        let res = await this.$http.post('/jar/searchJar', params, {
          headers: {
            projectId: Number(this.$route.query.projectId)
          }
        })
        this.isFetchingSource = false
        // 资源
        if (res.code === 200) {
          this.sourceData = res.data
          this.filterJarData = this.sourceData
        }
      },
      // async getJarVersion (name) {
      //   const params = {
      //     orderByClauses: [{
      //       field: "update_time",
      //       orderByMode: 1
      //     }],
      //     page: 1,
      //     pageSize: 2000,
      //     vo: {
      //       name: name,
      //       projectId: Number(this.$route.query.projectId)
      //     }
      //   }
      //   this.jarVersionData = []
      //   this.isFetchingJarVersion = true
      //   let res = await this.$http.post('/jar/queryJar', params)
      //   this.isFetchingJarVersion = false
      //   if (res.code === 200) {
      //     if (res.data) {
      //       if (res.data.rows) {
      //         this.jarVersionData = res.data.rows
      //         this.filterJarVersionData = res.data.rows
      //       } else {
      //         this.jarVersionData = []
      //       }
      //       return this.jarVersionData
      //     }
      //   } else {
      //     this.$message.error(res.msg);
      //   }
      // },
    },
    mounted () {

    }
  }
</script>
<style lang="scss">
  .select-source-option {
    .ant-select-dropdown-content .ant-select-dropdown-menu {
      max-height: 220px !important;
    }
    .name {
      color: #0066ff;
    }
    .description {
      color: #999;
    }
  }
</style>
<style lang="scss" scoped>
  .DS {
    width: 100%;
    font-size: 12px;
    padding: 0 16px;
    .row {
      margin-top: 12px;
      .label {
        margin-bottom: 10px;
      }
      .main-class {
        width: 100%;
      }
      .appArgs {

      }
      .select-source {
        width: 100%;
        font-size: 12px;
        /deep/ .name {
          width: 100%;
          display: block;
        }
        /deep/ .description {
          display: none;
        }
      }
      .description {
        width: 100px;
        text-align: right;
        margin-right: 16px;
        height: 32px;
        overflow: hidden;
      }
      .col {
        width: 45%;
      }
      /deep/ input {
        font-size: 12px;
      }
      .tip {
        color: #999;
      }
    }
    &.is-max {
      display: flex;
      flex-wrap: wrap;
      .row {
        width: calc(50% - 6px);
        margin-right: 6px;
      }
    }
  }
</style>