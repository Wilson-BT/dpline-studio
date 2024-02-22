import Vue from "vue";
import Router from "vue-router";
import Layout from "@/layout";

// import { config } from "@/utils/config";
// import Login from '@/layout/pages/login'
import cas from "@/api/cas.js";
// import Store from '@/store/'
// 解决 vue-router.esm.js?fe87:1958 Uncaught (in promise) NavigationDuplicated: Avoided redundant navigation to current location
const originalPush = Router.prototype.push;
Router.prototype.push = function push (location) {
  return originalPush.call(this, location).catch((err) => err);
};

Vue.use(Router);
// const otherRoutes = getRoutes(require.context('./', false, /\.js$/), './index.js')
// console.log('otherRoutes', otherRoutes)
// detailBack - 用于标记切换环境时，详情自动返回到列表


/**
 * 路由参数配置
 * 
 * hidden: true                   如果设置为true，项目将不会显示在侧栏中（默认值为false）
 * alwaysShow: true               如果设置为true，将始终显示根菜单，如果未设置alwaysShow，则当项目有多个子路由时，它将成为嵌套模式，否则不显示根菜单
 * 
 * meta : {
    roles: ['admin']             控制页面角色（可以设置多个角色）
    title: 'title'               名称显示在导航菜单
    isDataHub: true              为true，该菜单为dataHub菜单
    icon: ''                     菜单图标
    activeIcon:''                高亮菜单图标
    tag:''                       tab页签
    activeMenu: '/example/list'  指定菜单高亮路由
    detailBack                   用于标记切换环境时，详情自动返回到列表
  }
 */

export const baseRoutes = [
  {
    path: "/",
    name: "App",
    redirect: "/index",
    hidden: true
  },
  {
    path: "/index",
    component: () =>
      import(/* webpackChunkName: "noAccess" */ "@/views/index/"),
    redirect: "/project",
    hidden: true
  },
  {
    name: 'login',
    path: "/login",
    component: () =>
      import(/* webpackChunkName: "noAccess" */ "@/views/login/index"),
    hidden: true
  },
  {
    path: "/guide",
    name: "Guide",
    component: Layout,
    redirect: "/guide/index",
    children: [
      {
        path: "/guide/index",
        name: "GuideIndex",
        meta: {
          title: "DPLINE首页",
          tag: "/guide",
          icon: require('@/assets/icons/top-logo.png'),
          activeIcon: require('@/assets/icons/top-logo-guid.png'),
        },
        component: () =>
          import(
              /* webpackChunkName: "flowProject" */ "@/views/project/components/guide-page"
          ),
      }
    ]
  },
  {
    path: "/project",
    name: "Project",
    component: Layout,
    redirect: "/project/list",
    children: [
      {
        path: "/project/list",
        name: "ProjectList",
        meta: {
          title: "项目管理",
          keepAlive: true,
          tag: "/project",
          icon: require('@/assets/icons/nav-xmgl.png'),
          activeIcon: require('@/assets/icons/nav-xmgl_active.png'),
        },
        component: () =>
          import(/* webpackChunkName: "flowProject" */ "@/views/project/index"),
      },
    ]
  },
  {
    path: "/test",
    name: "test",
    hidden: true,
    component: () =>
      import(
    /* webpackChunkName: "flowProject" */ "@/views/test/index"
      ),
    redirect: "/test/info",
    children: [
      {
        path: "/test/info",//测试接口的页面
        name: "TestInfo",
        meta: { title: "test" },
        component: () =>
          import(
          /* webpackChunkName: "flowProject" */ "@/views/test/info"
          ),
      },
    ]
  },
  {
    path: "/404",
    hidden: true,
    component: () =>
      import(/* webpackChunkName: "notFound" */ "@/views/notFound/"),
  },
  {
    path: "/noAccess",
    hidden: true,
    component: () =>
      import(/* webpackChunkName: "noAccess" */ "@/views/noAccess/"),
  },
];

export const constantRoutes = [
  {
    path: "/application",
    component: Layout,
    name: "Application",
    meta: {
      title: "实时开发",
      icon: require('@/assets/icons/nav-sskf.png'),
      activeIcon: require('@/assets/icons/nav-sskf_active.png'),
    },
    redirect: "/application/job-develop",
    // redirect: '/demo/index',
    children: [
      {
        path: "/application/job-develop",
        component: () =>
          import(
            /* webpackChunkName: "flowProject" */ "@/views/application/job-develop/"
          ),
        name: "JobDevelop",
        meta: {
          title: "作业开发",
          keepAlive: true,
          tag: "/application/job-develop"
        },
      },
      {
        path: "/application/job-operate",
        component: () =>
          import(
            /* webpackChunkName: "flowProject" */ "@/views/application/job-operate/"
          ),
        name: "JobOperate",
        meta: {
          title: "作业运维",
          keepAlive: true,
          tag: "/application/job-operate",
        },
      },
      {
        path: "/application/source-manage",
        component: () =>
          import(
            /* webpackChunkName: "flowProject" */ "@/views/application/source-manage/"
          ),
        name: "SourceManage",
        meta: {
          title: "资源管理",
          keepAlive: true,
          tag: "/application/source-manage",
        },
      },
      {
        path: "/application/all-version",
        component: () =>
          import(
            /* webpackChunkName: "flowProject" */ "@/views/application/source-manage/all-version"
          ),
        name: "SourceManage_allVersion",
        meta: {
          title: "全部版本",
          keepAlive: true,
          detailBack: "SourceManage",
          tag: "/application/all-version",
          activeMenu: "/application/source-manage"
        },
        hidden: true,
      },
      {
        path: "/application/data-source-manage",
        component: () =>
          import(
            /* webpackChunkName: "flowProject" */ "@/views/application/data-source-manage/"
          ),
        name: "DataSourceManage",
        meta: {
          title: "数据源管理",
          keepAlive: true,
          tag: "/application/data-source-manage",
        },
      },
    ],
  },
  {
    path: "/approve",
    name: "Approve",
    component: Layout,
    meta: {
      title: "离线开发",
      icon: require('@/assets/icons/nav-wdlc.png'),
      activeIcon: require('@/assets/icons/nav-wdlc_active.png'),
    },
  //   redirect: "/approve/list",
  //   alwaysShow: true,
  //   children: [
  //     {
  //       path: "/approve/list",
  //       name: "ApproveList",
  //       meta: {
  //         title: "实时作业流程",
  //         keepAlive: true,
  //         tag: "/approve/list"
  //       },
  //       component: () =>
  //         import(/* webpackChunkName: "flowProject" */ "@/views/approve/index"),
  //     },
  //     {
  //       path: "/approve/detail",
  //       name: "ApproveDetail",
  //       meta: {
  //         title: "作业审批详情",
  //         tag: "/approve/detail",
  //         activeMenu: "/approve/list",
  //         detailBack: 'ApproveList',
  //       },
  //       hidden: true,
  //       component: () =>
  //         import(
  //           /* webpackChunkName: "flowProject" */ "@/views/approve/detail"
  //         ),
  //     },
  //   ],
  },
  {
    path: "/system-setting",
    name: "SystemSetting",
    meta: {
      title: "系统设置",
      tag: "/system-setting",
      roles: ['admin'],
      icon: require('@/assets/icons/nav-xtsz.png'),
      activeIcon: require('@/assets/icons/nav-xtsz_active.png'),
    },
    component: Layout,
    redirect: "/system-setting/user",
    children: [
      {
        path: "/system-setting/user",
        name: "SystemSettingUser",
        meta: {
          title: "用户管理",
          keepAlive: true,
          tag: "/system-setting/user",
          isDataHub: true
        },
        component: () =>
          import(
          /* webpackChunkName: "system-setting" */ "@/views/system-setting/user/index"
          ),
      },
      {
        path: "/system-setting/cluster",
        name: "SystemSettingCluster",
        meta: {
          title: "集群管理",
          keepAlive: true,
          tag: "/system-setting/cluster"
        },
        component: () =>
          import(
          /* webpackChunkName: "system-setting" */ "@/views/system-setting/cluster/index"
          ),
      },
      {
        path: "/system-setting/flink",
        name: "SystemSettingFlink",
        meta: {
          title: "Flink管理",
          keepAlive: true,
          tag: "/system-setting/flink"
        },
        component: () =>
            import(
                "@/views/system-setting/flink/index"
                ),
      },
      {
        path: "/system-setting/docker",
        name: "SystemSettingDocker",
        meta: {
          title: "镜像管理",
          keepAlive: true,
          tag: "/system-setting/docker"
        },
        component: () =>
            import(
                "@/views/system-setting/docker"
                ),
      },
    ]
  },
  {
    path: "/alert",
    name: "AlertConfig",
    component: Layout,
    redirect: "/alert",
    children: [
      {
        path: "/alert",
        name: "ProjectList",
        meta: {
          title: "告警配置",
          keepAlive: true,
          tag: "/alert",
          icon: require('@/assets/icons/alert.png'),
          activeIcon: require('@/assets/icons/active-alert.png'),
        },
        component: () =>
            import(/* webpackChunkName: "flowProject" */ "@/views/alert/index"),
      },
    ]
  },

  {
    path: '*',
    redirect: '/404',
    hidden: true,
  },
];

const createRouter = () =>
  new Router({
    scrollBehavior: () => ({ y: 0 }),
    routes: baseRoutes.concat(constantRoutes),
  });
const router = createRouter();
router.beforeEach((to, from, next) => {
  if (to.path !== '/test/info') {
    cas.router_verify_cas(to, from, next);
  } else {
    next()
  }

  //暂时去掉路由权限验证
  /*
  let userRoutes = Store.state.permission.userRoutes
  if (userRoutes.length > 0 && to.matched[0] && to.matched[0].meta && to.matched[0].meta.auth) {
    let findItem = userRoutes.find(item => item.url === to.matched[0].path)
    if (!findItem) {
      console.log('userRoutes', userRoutes, to.matched[0].path)
      next('/404')
    }
  }*/
  // next()
});

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export const resetRouter = () => {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router;
