<template>
	<el-form size="large" class="login-content-form">
		<el-form-item class="login-animation1">
			<el-input text :placeholder="$t('message.account.accountPlaceholder1')" v-model="ruleForm.userName"
				clearable autocomplete="off">
				<template #prefix>
					<el-icon class="el-input__icon">
						<ele-User />
					</el-icon>
				</template>
			</el-input>
		</el-form-item>
		<el-form-item class="login-animation2">
			<el-input :type="isShowPassword ? 'text' : 'password'"
				:placeholder="$t('message.account.accountPlaceholder2')" v-model="ruleForm.password" autocomplete="off">
				<template #prefix>
					<el-icon class="el-input__icon">
						<ele-Unlock />
					</el-icon>
				</template>
				<template #suffix>
					<i class="iconfont el-input__icon login-content-password"
						:class="isShowPassword ? 'icon-yincangmima' : 'icon-xianshimima'"
						@click="isShowPassword = !isShowPassword">
					</i>
				</template>
			</el-input>
		</el-form-item>
		<el-form-item class="login-animation4">
			<el-button type="primary" class="login-content-submit" round v-waves @click="onSignIn"
				:loading="loading.signIn">
				<span>{{ $t('message.account.accountBtnText') }}</span>
			</el-button>
		</el-form-item>
	</el-form>
</template>

<script lang="ts">
import { toRefs, reactive, defineComponent, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useI18n } from 'vue-i18n';
import Cookies from 'js-cookie';
import { CurUserBaseInfo } from '/@/interfaces/userInfo';
import { initFrontEndControlRoutes } from '/@/router/frontEnd';
import { Session } from '/@/utils/storage';
import { formatAxis } from '/@/utils/formatTime';
import { NextLoading } from '/@/utils/loading';
import { useLoginApi } from '/@/api/login';

export default defineComponent({
	name: 'loginAccount',
	setup() {
		const { t } = useI18n();
		const route = useRoute();
		const router = useRouter();
		const state = reactive({
			isShowPassword: false,
			ruleForm: {
				userName: 'admin',
				password: 'admin123456'
			},
			loading: {
				signIn: false,
			},
		});
		// 时间获取
		const currentTime = computed(() => {
			return formatAxis(new Date());
		});
		// 登录
		const onSignIn = async () => {
			state.loading.signIn = true;
			// 请求远程登录数据
			const userInfo = { "userName": state.ruleForm.userName, "userPassword": state.ruleForm.password }
			const resp: any = await useLoginApi().signIn(userInfo);
			// 存储 token 到浏览器缓存
			Session.set('sessionId', resp.data.sessionId);
			const curUserInfo: CurUserBaseInfo = {
				"id": resp.data.sessionUserId,
				"name": resp.data.sessionUserName,
				"roleType": resp.data.sessionUserType,
				"headUrl": "/photo.jpeg"
			};
			Session.set('curUserInfo', curUserInfo);
			Cookies.set('userName', state.ruleForm.userName);
			// 前端控制路由，2、请注意执行顺序
			await initFrontEndControlRoutes();
			signInSuccess();
		};
		// 登录成功后的跳转
		const signInSuccess = () => {
			// 初始化登录成功时间问候语
			let currentTimeInfo = currentTime.value;
			// 登录成功，跳到转首页
			// 如果是复制粘贴的路径，非首页/登录页，那么登录成功后重定向到对应的路径中
			if (route.query?.redirect) {
				router.push({
					path: <string>route.query?.redirect,
					query: Object.keys(<string>route.query?.params).length > 0 ? JSON.parse(<string>route.query?.params) : '',
				});
			} else {
				router.push('/');
			}
			// 登录成功提示
			// 关闭 loading
			state.loading.signIn = true;
			const signInText = t('message.signInText');
			ElMessage.success(`${currentTimeInfo}，${signInText}`);
			// 添加 loading，防止第一次进入界面时出现短暂空白
			NextLoading.start();
		};
		return {
			onSignIn,
			...toRefs(state)
		};
	},
});
</script>

<style scoped lang="scss">
.login-content-form {
	margin-top: 20px;

	@for $i from 1 through 4 {
		.login-animation#{$i} {
			opacity: 0;
			animation-name: error-num;
			animation-duration: 0.5s;
			animation-fill-mode: forwards;
			animation-delay: calc($i/10) + s;
		}
	}

	.login-content-password {
		display: inline-block;
		width: 20px;
		cursor: pointer;

		&:hover {
			color: #909399;
		}
	}

	.login-content-submit {
		width: 100%;
		letter-spacing: 2px;
		font-weight: 300;
		margin-top: 15px;
	}
}
</style>
