<template>

  <div class="senior"
       :class="{ 'is-max': isMaxWidth }">
    <a-collapse class="collapse-container"
                :bordered="false">
      <template #expandIcon="props">
        <a-icon type="caret-right"
                :rotate="props.isActive ? 90 : 0" />
      </template>
      <a-collapse-panel key="1"
                        header="JAR包资源"
                        :style="customStyle">
        <div v-for="(paramItem, index) in jarDependList" :key="index">
          <div class="input-row">
            <p style="margin-right: 4px">类型</p>
            <a-select class="fileType"
                      @change="(value) => handleTypeChange(value,index)"
                      v-model="paramItem.jarFunctionType"
                      style="width: 25%; margin-right: 15px;"
            >
              <a-select-option v-for="item in jarFunctionTypeList"
                               :key="item.value"
                               :value="item.value">
                {{item.value}}
              </a-select-option>
            </a-select>
            <p style="margin-right: 2px">名称</p>
            <a-select v-model="paramItem.name"
                      placeholder="name"
                      option-label-prop="label"
                      style="width: 30%; margin-right: 15px;"
                      showSearch
                      autoClearSearchValue
                      @search="(value) => handleJarSearch(value)"
                      @select="(value) => handleJarSelect(value,index)"
            >
              <a-select-option v-for="(item, index) in filterJarDataList"
                               :value="item.id + ',,' + item.name"
                               :key="index"
                               :label="item.name"
              >
                {{item.name}}
              </a-select-option>
            </a-select>
<!--            <p style="margin-right: 2px">版本</p>-->
<!--            <a-input v-model="paramItem.jarVersion"-->
<!--                     placeholder="jarVersion"-->
<!--                     style="width: 15%; margin-right: 15px;"-->
<!--                     :disabled="true"-->
<!--            />-->
            <a-icon v-if="jarDependList.length > 0"
                    class="dynamic-delete-button"
                    type="minus-circle-o"
                    :disabled="jarDependList.length === 0"
                    @click="removeParam(index)"
            />
          </div>
        </div>
        <a-button type="dashed" style="width: 80%;margin-left: 0px" @click="addParam()">
          <a-icon type="plus" />
          添加参数
        </a-button>
      </a-collapse-panel>
    </a-collapse>
  </div>

</template>

<script>
  import {jarFunctionTypeList} from "@/utils/enumType";

  export default {
    name: "ResourceConfig",
    data () {
      return {
        filterJarDataList: [],
        collapseActive: true,
        customStyle: 'border-radius: 4px;margin-bottom: 0;border: 0;overflow: hidden;background:#FFF',
        jarDependList:[],
        jarSourceList: []
      }
    },
    props: {
      config: {
        type: Object,
        default: () => {
          return {}
        }
      },
      isMaxWidth: {
        type: Boolean,
        default: true
      }
    },
    computed: {
      jarFunctionTypeList(){
        return jarFunctionTypeList.filter(item => item.value !== 'MAIN');
      }
    },
    components: {
    },
    watch: {
      config: {
        handler (val) {
          this.sourceConfig = JSON.parse(JSON.stringify(val))
          this.jarDependList = this.sourceConfig.jarDependList
        },
        immediate: true
      }
    },
    created () {
    },
    methods: {
      handleTypeChange(value,index){
        // 类型改变，需要将jar包名称置空，版本置空
        this.jarSourceList=[]
        this.filterJarDataList=[]
        this.truncateList(index)
        // 然后查询所有的 jar 包资源
        this.searchJarList(value)
      },
      async searchJarList(value){
        const params = {
          // name: value || '',
          projectId: Number(this.$route.query.projectId),
          jarFunctionType: value,
        }
        this.isFetchingSource = true
        let res = await this.$http.post('/jar/searchJar', params, {
          headers: {
            projectId: Number(this.$route.query.projectId)
          }
        })
        this.isFetchingSource = false
        if (res.code === 200) {
          console.log(this.jarSourceList)
          this.jarSourceList = res.data
          this.filterJarDataList  = res.data
        } else {
          this.$message.error(res.msg)
        }
      },
      async addParam () {
        // 判断前一个键值对是否为空
        const length = this.jarDependList.length;
        if (this.jarDependList.length > 0 && this.jarDependList[length-1].mainResourceId === '') {
          this.$message.warning({ content: "当前jar包属性不能为空", duration: 2 })
          return
        }
        this.jarDependList.push({
          jarFunctionType :'',
          mainResourceId: '',
          name:''
        })

        // 需要将 jarSourceList 置为空，重新在新的框里面筛选
        this.jarSourceList=[]
      },
      async handleJarSearch(value){
        this.filterJarDataList = this.jarSourceList.filter(item => item.name.includes(value))
      },
      async handleJarSelect(value,index){
        let str_arr = value.split(",,");
        console.log(this.jarDependList)
        const isExist = this.jarDependList.some((item,ind) => ind !== index && Number(item.mainResourceId) === Number(str_arr[0]));
        console.log(isExist)
        if(!isExist){
          this.jarDependList[index].mainResourceId = str_arr[0]
          this.jarDependList[index].name = str_arr[1]
        }else {
          this.$message.error("资源jar包已经存在，请重新选择");
          this.truncateList(index)
        }
        console.log(this.jarDependList)
      },
      truncateList(index){
        // this.jarDependList[index].jarId = ''
        this.jarDependList[index].mainResourceId = ''
        this.jarDependList[index].name = ''
        // this.jarDependList[index].jarVersion = ''
        // this.jarDependList[index].jarName = ''
        // this.jarDependList[index].jarPath = ''
      },
      // 移除参数对
      async removeParam (index) {
        console.log(index);
        this.jarDependList.splice(index, 1);
        console.log(this.jarDependList);
      },
    },
    mounted () {

    }
  }
</script>
<style lang="scss" scoped>
  .senior{
    width: 100%;
    font-size: 12px;
    height: calc(100% - 32px);
    display: flex;
    justify-content: flex-start;
    align-items: stretch;
    flex-direction: column;
    overflow-x: hidden;
    overflow-y: auto;
    padding: 0 16px;
    .collapse-container {
      /deep/ .ant-collapse-header {
        font-size: 14px !important;
        font-weight: 900;
        padding: 5px 16px;
        padding-left: 40px;
        i {
          color: #006fff !important;
        }
      }
      /deep/ .ant-collapse-item {
        font-size: 12px;
      }
      .flink-collapse {
        /deep/ .ant-collapse-content-box {
          padding: 12px 16px 0 16px;
        }
      }
      .input-row {
        display: flex;
        margin-bottom: 10px;
        align-items: center;
        .dynamic-delete-button {
          cursor: pointer;
          position: relative;
          font-size: 18px;
          color: #999;
          transition: all 0.3s;
        }
      }
    }
  }
</style>