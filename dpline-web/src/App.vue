<template>
  <a-config-provider :locale="locale">
    <div id="app"
         v-loading="isLoading">
      <router-view />
    </div>
  </a-config-provider>
</template>

<script>
  import zhCN from 'ant-design-vue/lib/locale-provider/zh_CN'
  import { mapActions, mapState } from 'vuex'
  export default {
    name: 'App',
    data () {
      return {
        locale: zhCN,
        showModal: true,
        script: null,
        // env: '',
        isLoading: false
      }
    },
    computed: {
      // envList: function () {
      //   return this.$store.getters.lookUpOptions['env_list']
      // },
      ...mapState('permission', {
        'buttonPermission': 'buttonPermission'
      }),
      ...mapState('user', {
        'dict': 'dict'
      })
    },
    watch: {
      '$store.getters.userId': {
        handler (newVal) {
          if (newVal) {
            this.getProjectHistory()
            this.getInfo()

          }
        }
      }


    },
    methods: {
      ...mapActions('user', {
        'setUserInfo': 'setUserInfo',
        'setDict': 'setDict'
      }),
      ...mapActions('permission', {
        'setUserRoutes': 'setUserRoutes'
      }),
      // initEnv () {
      //   let localEnv = sessionStorage.getItem('env') || 'test'
      //   this.$store.dispatch('global/setEnv', localEnv)
      // },
      async getInfo () {
        let data = JSON.parse(localStorage.getItem('userInfo'))

        this.setUserInfo(data)
      },
      async getProjectHistory () {//获取最近打开的项目
        this.isLoading = true
        let res = await this.$http.post('/project/history')
        this.isLoading = false
        if (res.code === 200 && res.data && res.data.projectId && res.data.projectName) {
          const currentProject = {
            id: res.data.projectId,
            name: encodeURIComponent(res.data.projectName),
            // code: res.data.projectCode
          }
          this.$store.dispatch('global/saveCurrentProject', currentProject)

        }
      },
    },
    created () {
      // this.initEnv()

    },
    mounted () {
      // this.getInfo()
    }
  }

</script>
<style >
</style>
