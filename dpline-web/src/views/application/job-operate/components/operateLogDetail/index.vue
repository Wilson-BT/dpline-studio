<template>
  <div>
    <a-modal
        v-model="controller.visible"
        width="70%"
        :body-style="controller.modalStyle"
        :destroy-on-close="controller.modalDestroyOnClose"
        :footer="null"
        v-drag
        @cancel="handleClose">
      <template slot="title">
        {{ controller.consoleName }}
      </template>
      <div class="run-log"
           v-loading="isLoading"
           v-defaultPage="!logContent">
        <div ref="logContent"
             class="log-content"
             :class="{'black':logContent}">
        </div>
      </div>
    </a-modal>
  </div>
</template>
<script>
// import { Terminal } from 'xterm'
// import 'xterm/css/xterm.css'
// import { FitAddon } from 'xterm-addon-fit'
import {config} from '@/utils/config'
import {sessionType} from '@/utils/enumType'


export default {
  name: "operateLogDetail",
  components: {},
  data() {
    return {
      // terminal: null,
      read_eof: "READ EOF",
      socket: null,
      // showMask: false,
      controller: {
        ellipsis: 100,
        modalStyle: {
          height: '630px',
          padding: '5px'
        },
        visible: false,
        modalDestroyOnClose: true,
        consoleName: null
      },
      isLoading: false,
      logContent: '', // 日志内容
      timer: null,
      height: null,
      flag: true,
    }
  },
  created() {
    this.filterContent(null)
  },
  methods: {
    handleSeeLog(item) {
      console.log(item)
      this.controller.consoleName = item.id + ' 部署日志 '
      this.controller.visible = true

      this.$nextTick(function () {
        this.handleOpenWS(item.id)
        this.openSocket(item.id)
      })
    },
    // 内容过滤超链接
    filterContent (text) {
      if (text === null) return
      this.$nextTick(() => { // 保证ref挂载
        // // console.log('refs: ', this.$refs)
        if(this.$refs['logContent'].innerHTML !== null || this.$refs['logContent'].innerHTML !== undefined){
          this.$refs['logContent'].innerHTML = text
          if (this.flag) this.$refs['logContent'].scrollTop = this.$refs['logContent'].scrollHeight - this.height - 10
        }
      })
    },
    openSocket(id) {
      // const terminal = this.terminal
      // const read_eof = this.read_eof
      const _this = this
      // 判断页面有没有存在websocket连接
      // console.log(_this.terminal)
      if (window.WebSocket) {
        // 获取到IP
        var url = 'ws://' + config.socketHost + ":" + config.socketPort + '/dpline/websocket/'+ sessionType[0] +'/'+ id;
        this.socket = new WebSocket(url);
        this.socket.onopen = function() {
          console.log("服务器连接成功: " + url);
          // this.$nextTick(() => {
          if (this.flag) this.$refs['logContent'].scrollTop = this.$refs['logContent'].scrollHeight - this.height - 10
          // })
        };
        this.socket.onclose = function(event) {
          console.log("服务器连接关闭: " + event);
        };
        this.socket.onerror = function() {
          console.log("服务器连接出错: " + url);
        };
        this.socket.onmessage = function(event) {
          //接收服务器返回的数据
          // console.log(event.data)
          // console.log(_this.read_eof)
          if (event.data === _this.read_eof){
              _this.socket.close()
            return
          }
          // 内容过滤超链接
          // function filterContent (text) {
          //   if (text === null) return
            // this.$nextTick(() => { // 保证ref挂载
              // // console.log('refs: ', this.$refs)
            // if(this.$refs['logContent'].innerHTML !== null || this.$refs['logContent'].innerHTML !== undefined){
            //   this.$refs['logContent'].innerHTML = text
            //   if (this.flag) this.$refs['logContent'].scrollTop = this.$refs['logContent'].scrollHeight - this.height - 10
            //   }
            // })
          // }
          if(_this.logContent === ''){
            _this.logContent = _this.logContent + event.data
          } else {
            _this.logContent = _this.logContent + '\n' + event.data
          }

          _this.filterContent(_this.logContent)
        };
      }
    },
    handleOpenWS(jobId) {
      this.jobId = jobId

      // const rows = parseInt(this.controller.modalStyle.height.replace('px', '')) / 16
      // const cols = (document.querySelector('.terminal').offsetWidth + 50) / 9
      // this.terminal = new Terminal({
      //   cursorBlink: true,
      //   rendererType: 'canvas',
      //   termName: 'xterm',
      //   useStyle: true,
      //   screenKeys: true,
      //   convertEol: true,
      //   scrollback: 1000,
      //   tabstopwidth: 4,
      //   disableStdin: true,
      //   rows: parseInt(rows), // 行数
      //   cols: parseInt(cols),
      //   fontSize: 14,
      //   cursorStyle: 'underline', // 光标样式
      //   theme: {
      //     foreground: "yellow", //字体
      //     background: "#060101", //背景色
      //     fontFamily: 'DejaVu',
      //     cursor: "help", //设置光标
      //   }
      // })
      // 窗口自适应
      // const fitAddon = new FitAddon()
      // window.onresize = () => {
      //   fitAddon.fit();
      // };
      // this.terminal.loadAddon(fitAddon)
      // const container = document.getElementById('terminal')
      // this.terminal.open(container, true)
      // this.terminal.focus()
    },
    handleClose() {
      if(this.socket !== null){
        this.socket.close()
      }
      this.controller.visible = false
      this.logContent = ''
    }
  },
  beforeDestroy() {
    this.handleClose()
  }
}
</script>
<style lang="scss" scoped>
 /deep/ .ant-modal-content {
   width: 90%;
 }
 .run-log {
   width: 100%;
   height: 100%;
   .log-content {
     width: 100%;
     height: 100%;
     white-space: pre-wrap;
     padding: 4px;
     line-height: 25px;
     overflow-y: scroll;
     &.black {
       background-color: #282923;
       color: #ffffff;
       font-family: Sans-serif;
       border: 1px solid #333;
     }
     /deep/ a {
       color: #0066ff;
       text-decoration: underline;
     }
   }
 }
</style>