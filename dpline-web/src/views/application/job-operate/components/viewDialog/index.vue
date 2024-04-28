<template>
  <div>
    <a-modal
        v-model="controller.visible"
        width="65%"
        :body-style="controller.modalStyle"
        :destroy-on-close="controller.modalDestroyOnClose"
        :footer="null"
        @cancel="handleClose">
      <template slot="title">
<!--        <svg-icon name="code" />-->
        {{ controller.consoleName }}
      </template>
      <div
          id="terminal"
          ref="terminal"
          class="terminal" />
    </a-modal>
  </div>
</template>
<script>
import { Terminal } from 'xterm'
import 'xterm/css/xterm.css'
import { FitAddon } from 'xterm-addon-fit'
import {config} from '@/utils/config'
import {sessionType} from '@/utils/enumType'


export default {
  name: "viewDialog",
  components: {},
  data() {
    return {
      terminal: null,
      read_eof: "READ EOF",
      socket: null,
      showMask: false,
      controller: {
        ellipsis: 100,
        modalStyle: {
          height: '600px',
          padding: '5px'
        },
        visible: false,
        modalDestroyOnClose: true,
        consoleName: null
      },

    }
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

    openSocket(id) {
      // const terminal = this.terminal
      // const read_eof = this.read_eof
      const _this = this
      // 判断页面有没有存在websocket连接
      // console.log(_this.terminal)
      if (window.WebSocket) {
        // 获取到IP
        var url = 'ws://' + config.socketHost + '/dpline/websocket/'+ sessionType[0] +'/'+ id;
        this.socket = new WebSocket(url);
        this.socket.onopen = function() {
          console.log("服务器连接成功: " + url);
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
          _this.terminal.writeln(event.data)
        };
      }
    },
    handleOpenWS(jobId) {
      this.jobId = jobId
      const rows = parseInt(this.controller.modalStyle.height.replace('px', '')) / 16
      const cols = (document.querySelector('.terminal').offsetWidth + 50) / 9
      this.terminal = new Terminal({
        cursorBlink: true,
        rendererType: 'canvas',
        termName: 'xterm',
        useStyle: true,
        screenKeys: true,
        convertEol: true,
        scrollback: 1000,
        tabstopwidth: 4,
        disableStdin: true,
        rows: parseInt(rows), // 行数
        cols: parseInt(cols),
        fontSize: 14,
        cursorStyle: 'underline', // 光标样式
        theme: {
          foreground: "yellow", //字体
          background: "#060101", //背景色
          fontFamily: 'DejaVu',
          cursor: "help", //设置光标
        }
      })
      // 窗口自适应
      const fitAddon = new FitAddon()
      window.onresize = () => {
        fitAddon.fit();
      };
      this.terminal.loadAddon(fitAddon)
      const container = document.getElementById('terminal')
      this.terminal.open(container, true)
      this.terminal.focus()
    },
    handleClose() {
      if(this.socket !== null){
        this.socket.close()
      }
      this.controller.visible = false
      if(this.terminal !== null){
        this.terminal.clear()
        this.terminal.clearSelection()
      }
      this.terminal = null
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
//.ant-modal-body{
//
//}
//.xterm .xterm-screen {
//  width: auto;
//}
//.xterm-dom-renderer-owner-4 .xterm-rows {
//  width: 100%;
//}
 html,
 body {
   width: 100%;
   height: 100%;
   margin: 0;
   overflow: hidden;
 }
</style>