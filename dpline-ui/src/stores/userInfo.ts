import { defineStore } from 'pinia';
import Cookies from 'js-cookie';
import { UserInfosStates } from './interface';
import { Session } from '/@/utils/storage';
import { CurUserBaseInfo } from '../interfaces/userInfo';

/**
 * 用户信息
 * @methods setUserInfos 设置用户信息
 */
export const useUserInfo = defineStore('userInfo', {
	state: (): UserInfosStates => ({
		userInfos: {
			userName: '',
			time: 0,
			roles: []
		},
	}),
	actions: {
		async setUserInfos() {
			// 存储用户信息到浏览器缓存
			if (Session.get('curUserInfo')) {
				const curUserInfo: CurUserBaseInfo = Session.get("curUserInfo");
				const roleType: string = (curUserInfo.roleType == 0) ? "admin" : "common";
				const roles = [roleType];
				this.userInfos = {
					"time": new Date().getTime(),
					"userName": curUserInfo.name,
					"roles": roles
				}
			} else {
				const userInfos: any = await this.getApiUserInfo();
				this.userInfos = userInfos;
			}
		},
		// 模拟接口数据
		async getApiUserInfo() {
			return new Promise((resolve) => {
				setTimeout(() => {
					// 模拟数据，请求接口时，记得删除多余代码及对应依赖的引入
					const userName = Cookies.get('userName');
					// 模拟数据
					let defaultRoles: Array<string> = [];
					// admin 页面权限标识，对应路由 meta.roles，用于控制路由的显示/隐藏
					let adminRoles: Array<string> = ['admin'];
					// test 页面权限标识，对应路由 meta.roles，用于控制路由的显示/隐藏
					let testRoles: Array<string> = ['common'];
					// 不同用户模拟不同的用户权限
					if (userName === 'admin') {
						defaultRoles = adminRoles;
					} else {
						defaultRoles = testRoles;
					}
					// 用户信息模拟数据
					const userInfos = {
						userName: userName,
						time: new Date().getTime(),
						roles: defaultRoles
					};
					resolve(userInfos);
				}, 0);
			});
		},
	},
});
