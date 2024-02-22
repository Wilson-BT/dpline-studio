<template>
	<div class="layout-breadcrumb-seting">
		<el-drawer :title="$t('message.layout.configTitle')" v-model="getThemeConfig.isDrawer" direction="rtl"
			destroy-on-close size="260px" @close="onDrawerClose">
			<el-scrollbar class="layout-breadcrumb-seting-bar">
				<!-- 其它设置 -->
				<div class="layout-breadcrumb-seting-bar-flex mt15">
					<div class="layout-breadcrumb-seting-bar-flex-label">{{ $t('message.layout.fiveTagsStyle') }}</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<el-select v-model="getThemeConfig.tagsStyle" placeholder="请选择" size="default"
							style="width: 90px" @change="setLocalThemeConfig">
							<el-option label="风格1" value="tags-style-one"></el-option>
							<el-option label="风格2" value="tags-style-two"></el-option>
							<el-option label="风格3" value="tags-style-three"></el-option>
						</el-select>
					</div>
				</div>
				<div class="layout-breadcrumb-seting-bar-flex mt15">
					<div class="layout-breadcrumb-seting-bar-flex-label">{{ $t('message.layout.fiveAnimation') }}</div>
					<div class="layout-breadcrumb-seting-bar-flex-value">
						<el-select v-model="getThemeConfig.animation" placeholder="请选择" size="default"
							style="width: 90px" @change="setLocalThemeConfig">
							<el-option label="slide-right" value="slide-right"></el-option>
							<el-option label="slide-left" value="slide-left"></el-option>
							<el-option label="opacitys" value="opacitys"></el-option>
						</el-select>
					</div>
				</div>

				<!-- 布局切换 -->
				<div class="layout-drawer-content-flex">
					<!-- defaults 布局 -->
					<div class="layout-drawer-content-item" @click="onSetLayout('defaults')">
						<section class="el-container el-circular"
							:class="{ 'drawer-layout-active': getThemeConfig.layout === 'defaults' }">
							<aside class="el-aside" style="width: 20px"></aside>
							<section class="el-container is-vertical">
								<header class="el-header" style="height: 10px"></header>
								<main class="el-main"></main>
							</section>
						</section>
						<div class="layout-tips-warp"
							:class="{ 'layout-tips-warp-active': getThemeConfig.layout === 'defaults' }">
							<div class="layout-tips-box">
								<p class="layout-tips-txt">{{ $t('message.layout.sixDefaults') }}</p>
							</div>
						</div>
					</div>
					<!-- transverse 布局 -->
					<div class="layout-drawer-content-item" @click="onSetLayout('transverse')">
						<section class="el-container is-vertical el-circular"
							:class="{ 'drawer-layout-active': getThemeConfig.layout === 'transverse' }">
							<header class="el-header" style="height: 10px"></header>
							<section class="el-container">
								<section class="el-container is-vertical">
									<main class="el-main"></main>
								</section>
							</section>
						</section>
						<div class="layout-tips-warp"
							:class="{ 'layout-tips-warp-active': getThemeConfig.layout === 'transverse' }">
							<div class="layout-tips-box">
								<p class="layout-tips-txt">{{ $t('message.layout.sixTransverse') }}</p>
							</div>
						</div>
					</div>
				</div>

			</el-scrollbar>
		</el-drawer>
	</div>
</template>

<script lang="ts">
import { nextTick, onUnmounted, onMounted, defineComponent, computed, reactive, toRefs } from 'vue';
import { ElMessage } from 'element-plus';
import { useI18n } from 'vue-i18n';
import { storeToRefs } from 'pinia';
import { useThemeConfig } from '/@/stores/themeConfig';
import { getLightColor, getDarkColor } from '/@/utils/theme';
import { Local } from '/@/utils/storage';
import other from '/@/utils/other';
import mittBus from '/@/utils/mitt';

export default defineComponent({
	name: 'layoutBreadcrumbSeting',
	setup() {
		const { locale } = useI18n();
		const storesThemeConfig = useThemeConfig();
		const { themeConfig } = storeToRefs(storesThemeConfig);
		const state = reactive({
			isMobile: false,
		});
		// 获取布局配置信息
		const getThemeConfig = computed(() => {
			return themeConfig.value;
		});
		// 1、全局主题
		const onColorPickerChange = () => {
			if (!getThemeConfig.value.primary) return ElMessage.warning('主题颜色值不能为空');
			// 颜色加深
			document.documentElement.style.setProperty('--el-color-primary-dark-2', `${getDarkColor(getThemeConfig.value.primary, 0.1)}`);
			document.documentElement.style.setProperty('--el-color-primary', getThemeConfig.value.primary);
			// 颜色变浅
			for (let i = 1; i <= 9; i++) {
				document.documentElement.style.setProperty(`--el-color-primary-light-${i}`, `${getLightColor(getThemeConfig.value.primary, i / 10)}`);
			}
			setDispatchThemeConfig();
		};

		// 5、布局切换
		const onSetLayout = (layout: string) => {
			Local.set('oldLayout', layout);
			if (getThemeConfig.value.layout === layout) return false;
			if (layout === 'transverse') getThemeConfig.value.isCollapse = false;
			getThemeConfig.value.layout = layout;
			getThemeConfig.value.isDrawer = false;
		};
		// 关闭弹窗时，初始化变量。变量用于处理 layoutScrollbarRef.value.update() 更新滚动条高度
		const onDrawerClose = () => {
			getThemeConfig.value.isFixedHeaderChange = false;
			getThemeConfig.value.isShowLogoChange = false;
			getThemeConfig.value.isDrawer = false;
			setLocalThemeConfig();
		};
		// 布局配置弹窗打开
		const openDrawer = () => {
			getThemeConfig.value.isDrawer = true;
		};
		// 触发 store 布局配置更新
		const setDispatchThemeConfig = () => {
			setLocalThemeConfig();
			setLocalThemeConfigStyle();
		};
		// 存储布局配置
		const setLocalThemeConfig = () => {
			Local.remove('themeConfig');
			Local.set('themeConfig', getThemeConfig.value);
		};
		// 存储布局配置全局主题样式（html根标签）
		const setLocalThemeConfigStyle = () => {
			Local.set('themeConfigStyle', document.documentElement.style.cssText);
		};
		// 初始化菜单样式等
		const initSetStyle = () => {
		};
		onMounted(() => {
			nextTick(() => {
				Local.set('frequency', 1);
				// 监听窗口大小改变，非默认布局，设置成默认布局（适配移动端）
				mittBus.on('layoutMobileResize', (res: any) => {
					getThemeConfig.value.layout = res.layout;
					getThemeConfig.value.isDrawer = false;
					state.isMobile = other.isMobile();
				});
				setTimeout(() => {
					// 默认样式
					onColorPickerChange();
					// 语言国际化
					if (Local.get('themeConfig')) locale.value = Local.get('themeConfig').globalI18n;
					// 初始化菜单样式等
					initSetStyle();
				}, 100);
			});
		});
		onUnmounted(() => {
			mittBus.off('layoutMobileResize', () => { });
		});
		return {
			openDrawer,
			onColorPickerChange,
			getThemeConfig,
			onDrawerClose,
			onSetLayout,
			setLocalThemeConfig,
			...toRefs(state)
		};
	},
});
</script>

<style scoped lang="scss">
.layout-breadcrumb-seting-bar {
	height: calc(100vh - 50px);
	padding: 0 15px;

	:deep(.el-scrollbar__view) {
		overflow-x: hidden !important;
	}

	.layout-breadcrumb-seting-bar-flex {
		display: flex;
		align-items: center;
		margin-bottom: 5px;

		&-label {
			flex: 1;
			color: var(--el-text-color-primary);
		}
	}

	.layout-drawer-content-flex {
		overflow: hidden;
		display: flex;
		flex-wrap: wrap;
		align-content: flex-start;
		margin: 0 -5px;

		.layout-drawer-content-item {
			width: 50%;
			height: 70px;
			cursor: pointer;
			border: 1px solid transparent;
			position: relative;
			padding: 5px;

			.el-container {
				height: 100%;

				.el-aside-dark {
					background-color: var(--next-color-seting-header);
				}

				.el-aside {
					background-color: var(--next-color-seting-aside);
				}

				.el-header {
					background-color: var(--next-color-seting-header);
				}

				.el-main {
					background-color: var(--next-color-seting-main);
				}
			}

			.el-circular {
				border-radius: 2px;
				overflow: hidden;
				border: 1px solid transparent;
				transition: all 0.3s ease-in-out;
			}

			.drawer-layout-active {
				border: 1px solid;
				border-color: var(--el-color-primary);
			}

			.layout-tips-warp,
			.layout-tips-warp-active {
				transition: all 0.3s ease-in-out;
				position: absolute;
				left: 50%;
				top: 50%;
				transform: translate(-50%, -50%);
				border: 1px solid;
				border-color: var(--el-color-primary-light-5);
				border-radius: 100%;
				padding: 4px;

				.layout-tips-box {
					transition: inherit;
					width: 30px;
					height: 30px;
					z-index: 9;
					border: 1px solid;
					border-color: var(--el-color-primary-light-5);
					border-radius: 100%;

					.layout-tips-txt {
						transition: inherit;
						position: relative;
						top: 5px;
						font-size: 12px;
						line-height: 1;
						letter-spacing: 2px;
						white-space: nowrap;
						color: var(--el-color-primary-light-5);
						text-align: center;
						transform: rotate(30deg);
						left: -1px;
						background-color: var(--next-color-seting-main);
						width: 32px;
						height: 17px;
						line-height: 17px;
					}
				}
			}

			.layout-tips-warp-active {
				border: 1px solid;
				border-color: var(--el-color-primary);

				.layout-tips-box {
					border: 1px solid;
					border-color: var(--el-color-primary);

					.layout-tips-txt {
						color: var(--el-color-primary) !important;
						background-color: var(--next-color-seting-main) !important;
					}
				}
			}

			&:hover {
				.el-circular {
					transition: all 0.3s ease-in-out;
					border: 1px solid;
					border-color: var(--el-color-primary);
				}

				.layout-tips-warp {
					transition: all 0.3s ease-in-out;
					border-color: var(--el-color-primary);

					.layout-tips-box {
						transition: inherit;
						border-color: var(--el-color-primary);

						.layout-tips-txt {
							transition: inherit;
							color: var(--el-color-primary) !important;
							background-color: var(--next-color-seting-main) !important;
						}
					}
				}
			}
		}
	}

	.copy-config {
		margin: 10px 0;

		.copy-config-btn {
			width: 100%;
			margin-top: 15px;
		}

		.copy-config-btn-reset {
			width: 100%;
			margin: 10px 0 0;
		}
	}
}
</style>
