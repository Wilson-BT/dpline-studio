<template>
  <a-modal v-if="isShow"
           wrapClassName="add-tag-dialog"
           :mask-closable="false"
           v-model="isShow"
           :footer="null"
           :title="title"
           v-drag
           width="400px">
    <!-- <span slot="closeIcon">x</span> -->
    <div class="add-tag">
      <a-form-model :model="form">
        <div class="form-body">
          <a-form-model-item>
            <p class="label"><span>*</span>TAG名称</p>
            <a-input v-model="form.tagName"
                     v-decorator="[
          'tagName',
          { rules: [{ required: true, message: '请输入TAG名称' }] },
        ]"
                     placeholder="请输入TAG名称" />
          </a-form-model-item>
          <a-form-model-item>
            <p class="label"><span></span>TAG描述</p>
            <a-input type="textarea"
                     v-model="form.remark"
                     v-decorator="[
          'remark',
          { rules: [{ required: false, message: '请输入TAG描述' }] },
        ]"
                     placeholder="请输入TAG描述" />
          </a-form-model-item>

        </div>
        <div class="footer justify-end">
          <a-button @click="cancelEvent"
                    size="small">取消</a-button>
          <a-button style="margin-left:8px"
                    @click="confirmEvent"
                    size="small"
                    type="primary">保存</a-button>
        </div>
      </a-form-model>
    </div>
<!--    <drawer ref="drawer"-->
<!--            :drawerVisible="drawerVisible"-->
<!--            @closeDrawer="drawerVisible = false" />-->
  </a-modal>
</template>

<script>
// import drawer from '@/components/priority-drawer.vue'
import _ from 'lodash'

export default {
  name: "addTag",
  data () {
    return {
      // drawerVisible: false,
      fileId: 0,
      isShow: false,
      title: '新建TAG',
      form: { tagName: '', remark:'' },
      // fileTypeList: [{ value: 'SQL' }, { value: 'DS' }],
      // sourceData: [],
      remark: '',
    }
  },
  props: {

  },
  computed: {

  },
  components: {
     // drawer
  },
  watch: {

  },
  created () {

  },
  methods: {
    open (data) {
      // Object.assign(this.$data, this.$options.data())
      // this.fileType = this.fileTypeList[0].value
      this.fileId = data.fileId
      this.form.tagName= ''
      this.form.remark= ''
      // console.log(data.fileId)
      this.isShow = true
      this.$nextTick(() => {
        // this.$refs.saveFolder.getTree()
      })
      // this.getSource()
      // this.initPriority()
    },
    close () {
      this.isShow = false
    },
    confirmEvent: _.debounce(function () {
      // console.log('debounce')
      this.addSubmit()
    }, 500),
    cancelEvent () {
      this.isShow = false
    },
    async addSubmit () {
      if (!this.form.tagName.trim()) {
        this.$message.warning('TAG名称不能为空')
        return
      }
      const params = {
        fileTagName: this.form.tagName,
        remark: this.form.remark,
        projectId: Number(this.$route.query.projectId),
        fileId: this.fileId
      }
      const res = await this.$http.post('/file/tag/addTag', params, {
        headers: {
          projectId: Number(this.$route.query.projectId)
        }
      })
      // 与父组件通信，调用父组件的addFileSuccess方法
      if (res.code === 200) {
        // 发送事件，在main-board.vue监听，监听到事件之后需要调用版本的接口，刷新版本发布的页面
        this.$bus.$emit('reInitTag')
        this.isShow = false
      } else {
        this.$message.error(res.msg)
      }
    },
    // openDrawer () {
    //   this.drawerVisible = true
    // }
  },
  mounted () {

  }
}
</script>
<style lang="scss">
.add-tag-dialog {
  .ant-modal {
    width: 470px !important;
    .ant-modal-close-x {
      width: 44px;
      height: 44px;
      line-height: 44px;
    }
    .ant-modal-header {
      //弹窗头部
      height: 44px;
      padding: 0;
      .ant-modal-title {
        line-height: 44px;
        padding-left: 16px;
        font-size: 16px;
        font-weight: 600;
      }
    }
  }
}
.select-source-option {
  // .ant-select-dropdown-content .ant-select-dropdown-menu {
  //   max-height: 220px !important;
  // }
  .name {
    color: #0066ff;
  }
  .description {
    color: #999;
  }
}
</style>
<style lang="scss" scoped>
/deep/ .ant-modal-body {
  padding: 10px;
  .add-tag {
    .form-body {
      padding: 0 25px 20px 10px;
      .ant-form-item {
        margin-bottom: 0;
        margin-top: 12px;
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
}
</style>