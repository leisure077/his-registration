<template>
	<div class="app-shell">
		<div class="bg-decor" aria-hidden="true">
			<span class="blob blob-a"></span>
			<span class="blob blob-b"></span>
			<span class="blob blob-c"></span>
			<span class="grid-layer"></span>
		</div>

		<aside class="app-aside">
			<div class="brand">
				<div class="brand-mark">+</div>
				<div class="brand-text">
					<strong>智慧院区 HIS</strong>
					<span>SMART HOSPITAL</span>
				</div>
			</div>
			<wy-menu />
			<div class="aside-foot">
				<span class="foot-dot"></span>
				<div>
					<strong>系统运行中</strong>
					<p>数字院区 · 实时同步</p>
				</div>
			</div>
		</aside>

		<div class="app-body">
			<header class="app-header">
				<div class="header-left">
					<span class="crumb">数字院区</span>
					<span class="crumb-sep">/</span>
					<span class="crumb-current">{{ currentTitle }}</span>
				</div>
				<div class="header-right">
					<div class="status-pill"><i></i>服务正常</div>
					<span class="header-date">{{ today }}</span>
					<el-dropdown trigger="click" @command="onCommand">
						<button type="button" class="user-chip">
							<span class="avatar">{{ initial }}</span>
							<span class="user-meta">
								<strong>{{ user.realName || '医护人员' }}</strong>
								<em>{{ user.employeeNo || 'HIS 用户' }}</em>
							</span>
							<el-icon class="chip-arrow"><ArrowDown /></el-icon>
						</button>
						<template #dropdown>
							<el-dropdown-menu>
								<el-dropdown-item disabled>{{ user.phone || '未绑定手机号' }}</el-dropdown-item>
								<el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
							</el-dropdown-menu>
						</template>
					</el-dropdown>
				</div>
			</header>

			<main class="app-main">
				<router-view />
			</main>
		</div>

		<wy-ai-chat />
	</div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowDown } from '@element-plus/icons-vue'
import WyMenu from '@@/menu'
import WyAiChat from '@@/ai'
import { useUserStore } from '@/stores'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const user = computed(() => userStore.user || {})
const initial = computed(() => (user.value.realName || 'H').slice(0, 1))
const today = new Date().toLocaleDateString('zh-CN', {
	month: 'long',
	day: 'numeric',
	weekday: 'long'
})

const titles = {
	'/dashboard': '工作台',
	'/dept': '科室管理',
	'/user': '医生管理',
	'/registration': '挂号管理',
	'/registration/add': '挂号登记'
}
const currentTitle = computed(() => titles[route.path] || '工作台')

function onCommand(command) {
	if (command === 'logout') {
		userStore.logout()
		router.replace('/login')
	}
}
</script>

<style scoped lang="scss">
.app-shell {
	position: relative;
	display: flex;
	gap: 18px;
	min-height: 100vh;
	padding: 18px;
	background: #f2f7fd;
}

.bg-decor {
	position: fixed;
	inset: 0;
	z-index: 0;
	overflow: hidden;
	pointer-events: none;
}
.blob {
	position: absolute;
	border-radius: 50%;
	filter: blur(90px);
}
.blob-a {
	top: -160px;
	right: -120px;
	width: 520px;
	height: 520px;
	background: radial-gradient(circle, rgba(31, 200, 232, 0.22), transparent 68%);
}
.blob-b {
	bottom: -200px;
	left: -140px;
	width: 560px;
	height: 560px;
	background: radial-gradient(circle, rgba(47, 107, 255, 0.16), transparent 68%);
}
.blob-c {
	top: 38%;
	left: 46%;
	width: 360px;
	height: 360px;
	background: radial-gradient(circle, rgba(123, 134, 247, 0.12), transparent 70%);
}
.grid-layer {
	position: absolute;
	inset: 0;
	background-image:
		linear-gradient(rgba(110, 152, 220, 0.07) 1px, transparent 1px),
		linear-gradient(90deg, rgba(110, 152, 220, 0.07) 1px, transparent 1px);
	background-size: 52px 52px;
	-webkit-mask-image: radial-gradient(circle at 62% 8%, #000 12%, transparent 72%);
	mask-image: radial-gradient(circle at 62% 8%, #000 12%, transparent 72%);
}

.app-aside {
	position: sticky;
	top: 18px;
	z-index: 2;
	display: flex;
	flex: 0 0 232px;
	flex-direction: column;
	width: 232px;
	height: calc(100vh - 36px);
	overflow: hidden;
	background: rgba(255, 255, 255, 0.78);
	border: 1px solid rgba(255, 255, 255, 0.95);
	border-radius: 22px;
	box-shadow:
		0 20px 46px rgba(38, 74, 140, 0.08),
		inset 0 1px 0 #fff;
	backdrop-filter: blur(22px);
}

.brand {
	display: flex;
	align-items: center;
	gap: 12px;
	padding: 24px 20px 16px;
}

.brand-mark {
	display: grid;
	flex: 0 0 42px;
	width: 42px;
	height: 42px;
	place-items: center;
	border-radius: 13px;
	background: linear-gradient(135deg, #2f6bff, #1fc8e8);
	box-shadow: 0 10px 22px rgba(47, 107, 255, 0.28);
	color: #fff;
	font-size: 24px;
	font-weight: 500;
	line-height: 1;
}

.brand-text strong {
	display: block;
	color: #16233f;
	font-size: 14.5px;
	font-weight: 600;
	letter-spacing: 0.02em;
}
.brand-text span {
	color: #93a4c2;
	font-size: 9.5px;
	letter-spacing: 0.16em;
}

.aside-foot {
	display: flex;
	align-items: center;
	gap: 10px;
	margin: 0 14px 16px;
	padding: 12px 14px;
	border: 1px solid rgba(47, 107, 255, 0.12);
	border-radius: 14px;
	background: linear-gradient(135deg, rgba(47, 107, 255, 0.07), rgba(31, 200, 232, 0.06));
}
.foot-dot {
	width: 7px;
	height: 7px;
	border-radius: 50%;
	background: #2fd0a6;
	box-shadow: 0 0 0 4px rgba(47, 208, 166, 0.16);
	animation: breathe 2.4s ease-in-out infinite;
}
.aside-foot strong {
	display: block;
	color: #2c3c5c;
	font-size: 12px;
	font-weight: 600;
}
.aside-foot p {
	margin-top: 2px;
	color: #93a4c2;
	font-size: 10.5px;
}

.app-body {
	position: relative;
	z-index: 1;
	display: flex;
	flex: 1;
	flex-direction: column;
	min-width: 0;
}

.app-header {
	position: sticky;
	top: 18px;
	z-index: 10;
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 20px;
	margin-bottom: 18px;
	padding: 12px 18px;
	border: 1px solid rgba(255, 255, 255, 0.95);
	border-radius: 16px;
	background: rgba(255, 255, 255, 0.72);
	box-shadow: 0 12px 30px rgba(38, 74, 140, 0.06);
	backdrop-filter: blur(20px);
}

.header-left {
	display: flex;
	align-items: center;
	gap: 9px;
	color: #93a4c2;
	font-size: 12.5px;
}
.crumb-sep {
	color: #c3d0e4;
}
.crumb-current {
	padding: 5px 11px;
	border-radius: 8px;
	background: rgba(47, 107, 255, 0.09);
	color: #1f4fd8;
	font-size: 12.5px;
	font-weight: 600;
}

.header-right {
	display: flex;
	align-items: center;
	gap: 14px;
}

.status-pill {
	display: inline-flex;
	align-items: center;
	gap: 7px;
	padding: 6px 12px;
	border: 1px solid rgba(47, 208, 166, 0.28);
	border-radius: 999px;
	background: rgba(47, 208, 166, 0.08);
	color: #12946f;
	font-size: 11.5px;
}
.status-pill i {
	width: 6px;
	height: 6px;
	border-radius: 50%;
	background: #2fd0a6;
	box-shadow: 0 0 8px rgba(47, 208, 166, 0.7);
	animation: breathe 2.4s ease-in-out infinite;
}

.header-date {
	color: #7c8cab;
	font-size: 12.5px;
	letter-spacing: 0.02em;
}

.user-chip {
	display: flex;
	align-items: center;
	gap: 10px;
	padding: 5px 10px 5px 5px;
	border: 1px solid rgba(140, 168, 210, 0.22);
	border-radius: 999px;
	background: rgba(255, 255, 255, 0.9);
	color: #16233f;
	font-family: inherit;
	cursor: pointer;
	transition: all 0.2s;
}
.user-chip:hover {
	border-color: rgba(47, 107, 255, 0.32);
	box-shadow: 0 8px 18px rgba(47, 107, 255, 0.1);
}
.avatar {
	display: grid;
	width: 34px;
	height: 34px;
	place-items: center;
	border-radius: 50%;
	background: linear-gradient(135deg, #2f6bff, #1fc8e8);
	color: #fff;
	font-size: 14px;
	font-weight: 600;
}
.user-meta {
	display: flex;
	flex-direction: column;
	align-items: flex-start;
	line-height: 1.2;
}
.user-meta strong {
	font-size: 12.5px;
	font-weight: 600;
}
.user-meta em {
	color: #93a4c2;
	font-size: 10.5px;
	font-style: normal;
}
.chip-arrow {
	color: #9aabc6;
	font-size: 12px;
}

.app-main {
	flex: 1;
	padding: 0 2px 30px;
}

@keyframes breathe {
	0%,
	100% {
		opacity: 1;
		transform: scale(1);
	}
	50% {
		opacity: 0.45;
		transform: scale(0.85);
	}
}
</style>
