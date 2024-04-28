<!--
 * @Author: hjg
 * @Date: 2021-11-08 21:30:17
 * @LastEditTime: 2022-10-20 10:53:47
 * @LastEditors: Please set LastEditors
 * @Description: In User Settings Edit
 * @FilePath: \src\views\application\job-operate\components\runLog\index.vue
-->
<template>
  <div class="run-log"
       v-loading="isLoading"
       v-defaultPage="!logContent">
    <div ref="logContent"
         class="log-content"
         :class="{'black':logContent}">
      {{ filterContent(logContent) }}
    </div>
  </div>
</template>
<script>
  export default {
    data () {
      return {
        isLoading: false,
        logContent: '', // 日志内容
        timer: null,
        height: null,
        flag: true,
        time: 5000,
        startTime: null,
        latestTime: null // 最后更新时间,用于给后台判断是否有新的日志需要返回给前端显示于页面
      }
    },
    props: {
      activeName: {
        type: String,
        value: ''
      }
    },
    mounted () {
    },
    watch: {
      activeName: {
        async handler (val) {
          if (val === 'runLog') {
            this.clear()
            this.init()
          } else {
            this.clear()
          }
        },
        immediate: true
      }
    },
    methods: {
      init () {
        this.tailLog()
      },

      clear () {
        clearInterval(this.timer)
        this.timer = null
      },
      // 内容过滤超链接
      filterContent (text) {
        if (text === null) return
        this.$nextTick(() => { // 保证ref挂载
          // // console.log('refs: ', this.$refs)
          this.$refs['logContent'].innerHTML = text
          if (this.flag) this.$refs['logContent'].scrollTop = this.$refs['logContent'].scrollHeight - this.height - 10
        })
      },
      timerFunc () {
        this.timer = setInterval(() => {
          this.getRunLog()
        }, this.time)
      },
      // 获取运行日志
      async tailLog () {
        let params = {
          id: Number(this.$store.getters.jobInfo.id)
        }
        this.isLoading = true
        console.log(params)
        let res = await this.$http.post('/log/logTail', this.$qs.stringify(params))
        // // console.log('--------res: ', res)
        this.isLoading = false
        if (res.code === 200) {
          this.logContent = res.data.logs
          this.startTime = res.data.startTime
          this.latestTime = res.data.latestTime
          this.$nextTick(() => {
            if (this.flag) this.$refs['logContent'].scrollTop = this.$refs['logContent'].scrollHeight - this.height - 10
          })
          this.timerFunc()
        } // 运行日志无更新
      },

      // 获取运行日志
      async getRunLog () {
        let params = {
          id: this.$store.getters.jobInfo.id,
          latestTime: this.latestTime
        }
        let res = await this.$http.post('/log/logViewFromTime', this.$qs.stringify(params))
        this.isLoading = false
        if (res.code === 200) {
          this.logContent = this.logContent + res.data.logs
          // this.startTime = res.data.startTime
          this.latestTime = res.data.latestTime
          // this.$nextTick(() => {
          //   if (this.flag) this.$refs['logContent'].scrollTop = this.$refs['logContent'].scrollHeight - this.height - 10
          //
          // })

        } // 运行日志无更新
      },
    },
    beforeDestroy () {
      // window.stop()
      this.clear()
    },

  }
</script>
<style lang="scss" scoped>
  .run-log {
    width: 100%;
    height: 100%;
    .log-content {
      width: 100%;
      height: 100%;
      white-space: pre-wrap;
      padding: 4px;
      line-height: 28px;
      overflow-y: scroll;
      &.black {
        background-color: #282923;
        color: #fff;
        border: 1px solid #333;
      }
      .time {
        width: 50px;
      }
      /deep/ a {
        color: #0066ff;
        text-decoration: underline;
      }
    }
  }
</style>
