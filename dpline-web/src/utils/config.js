export const config = {
  isOpenLog: false,
  isCheckToken: true,//是否要登录获取token,主要用于dev和uat调试时不需要登录调用接口
  isOpenMock: true,//是否要使用mockjs
  domain: '',//正式
  socketHost: 'localhost',//正式
  socketPort: '9099',
  apiRoot: '/dpline',
}