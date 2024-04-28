<template>
  <confirm-dialog :visible="isPop"
                  title="告警配置"
                  :width="300"
                  @close="destory()"
                  @confirm="confirm()">
    <div class="start-main"
         v-loading="isLoading">
      <p>告警模式<span class="red">*</span></p>
      <a-select v-model="alertMode"
                @change="(value) => updateAlertMode(value)"
                placeholder="请选择告警模式">
        <a-select-option v-for="(item, index) in alertModeList"
                         :value="item.label"
                         :key="index">
          {{ item.value }}
        </a-select-option>
      </a-select>
      <p class="label">告警类型<span class="red">*</span></p>
      <a-select v-model="alertInfo.alertType"
                @change="reFilterAlertInstance"
                placeholder="请选择告警类型">
        <a-select-option v-for="(item, index) in alertTypeList"
                         :value="item.label"
                         :key="index">
          {{ item.value }}
        </a-select-option>
      </a-select>
      <p class="label">告警实例<span class="red">*</span></p>
      <a-select v-model="alertName"
                @change="handleAlertChange"
                placeholder="请选择告警类型">
        <a-select-option v-for="(item, index) in filerAlertInstanceList"
                         :value="item.id"
                         :key="index">
          {{ item.instanceName }}
        </a-select-option>
      </a-select>
    </div>
    <div slot="footer"></div>
  </confirm-dialog>
</template>
<script>
import ConfirmDialog from '@/components/confirm-dialog/index'
import resourceColor from '../../mixins/resource-color.js'
import {alertModeList,alertTypeList} from "@/utils/enumType";
export default {
  name: 'alertConfigEdit',
  mixins: [resourceColor],
  components: {
    ConfirmDialog
  },
  data () {
    return {
      fileType: '',
      confirmDisabled: false,
      jobId: '',
      isPop: false,
      isLoading: false,
      projectId: '',
      isFetchingSource: false,
      alertMode: null,
      alertName: null,
      alertModeList: alertModeList,
      alertTypeList: alertTypeList,
      alertInfo: {
        alertMode: alertModeList[0].label,
        alertType: null,
        alertId: null
      },
      alertInstanceList: [],
      filerAlertInstanceList: [],
    }
  },
  created () {

  },
  methods: {
    convert(label){
      let alertMode = this.alertModeList.find(item => item.label === label)
      return alertMode.value
    },
    confirm(){
      let params = {
        id: Number(this.jobId),
        alertMode: this.alertInfo.alertMode,
        alertInstanceId: Number(this.alertInfo.alertId)
      }
      if(params.alertMode === null){
        this.$message.error("告警模式不能为空")
        return
      }
      if(params.alertInstanceId === null){
        this.$message.error("告警实例不能为空")
        return
      }
      this.updateJobAlert(params)
    },

    handleAlertChange(id){
      this.alertInfo.alertId = Number(id)
    },
    updateAlertMode(value){
      this.alertInfo.alertMode = value
    },
    async updateJobAlert(params){
      let res = await this.$http.post('/job/updateAlertConfig', this.$qs.stringify(params))
      if (res.code === 200) {
        this.isPop = false
        this.$message.success("更新成功")
      } else {
        this.$message.error("更新失败")
      }
      this.$emit("runCallback",res)
    },
    // 重新过滤告警实例
    reFilterAlertInstance(){
      this.alertName = null
      this.alertInfo.alertId = null
      this.filerAlertInstanceList = this.alertInstanceList.filter(item => item.alertType === this.alertInfo.alertType)
    },
    async open (record) {
      this.isPop = true
      this.isLoading = true
      this.jobId = record.id
      // 告警模式，默认为 NONE
      this.alertInfo.alertMode = record.alertMode || alertModeList[0].label
      this.alertInfo.alertType = record.alertType || null
      this.alertInfo.alertId = record.alertInstanceId
      this.alertMode = this.convert(this.alertInfo.alertMode)
      await this.getAlertInstanceList()
      this.convertIdToName(record.alertInstanceId)
    },
    convertIdToName(alertInstanceId){
      let alertInstance = this.alertInstanceList.find(item => Number(item.id) === Number(alertInstanceId))
      if(alertInstance !== undefined && alertInstance !== null){
        this.alertName = alertInstance.instanceName
      }
    },
    async getAlertInstanceList(){
      // 找不到
      this.isLoading = true
      let params = {
        alertType: null,
      }
      let res = await this.$http.post('/system/alertManagement/search', params)
      this.isLoading = false
      if (res.code === 200) {
        this.alertInstanceList = res.data
        this.filerAlertInstanceList = this.alertInstanceList.filter(item => item.alertType === this.alertInfo.alertType)
      }
    },
    destory(){
      this.isPop = false;
      this.alertName = null
    }
  }
}
</script>
<style lang="scss" scoped>
/deep/ .confirm-dialog {
  .ant-modal {
    .ant-modal-content {
      width: 400px;
      .confirm-main {
        margin-top: 10px;
      }
    }
    .ant-modal-body{
      width: 100%;
    }
  }
}
.start-main {
  width: 100%;
  height: 250px;
  font-size: 14px;
  display: flex;
  justify-content: flex-start;
  align-items: stretch;
  flex-direction: column;
  overflow-x: hidden;
  overflow-y: auto;
  .label {
    line-height: normal;
    margin-top: 20px;
    margin-bottom: 4px;
    width: 97px;
    .red {
      color: #ff1414;
    }
  }

}
</style>
