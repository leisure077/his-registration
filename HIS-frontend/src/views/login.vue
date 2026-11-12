<template>
	<div class="auth-page">
		<div class="decor" aria-hidden="true">
			<span class="blob blob-a"></span>
			<span class="blob blob-b"></span>
			<span class="blob blob-c"></span>
			<span class="grid-layer"></span>
		</div>

		<div class="auth-shell">
			<section class="auth-intro">
				<div class="intro-brand">
					<div class="brand-mark">+</div>
					<div>
						<strong>智慧院区 HIS</strong>
						<span>SMART HOSPITAL</span>
					</div>
				</div>

				<div class="intro-badge"><i></i>数字院区 · 一体化工作台</div>
				<h1>让院区管理<br /><em>像数据一样清透</em></h1>
				<p>科室、医护、挂号与智能助手汇聚同一处，河南师范大学智慧医院平台为每一次就诊提供清晰路径。</p>

				<ul class="intro-points">
					<li v-for="item in points" :key="item.no">
						<span class="point-icon">{{ item.no }}</span>
						<div>
							<strong>{{ item.title }}</strong>
							<p>{{ item.desc }}</p>
						</div>
					</li>
				</ul>

				<div class="intro-meta">
					<span>HIS 3.0</span>
					<span>超聚变 1 &amp; 2 班</span>
					<span>河南师范大学</span>
				</div>
			</section>

			<section class="auth-card">
				<div class="auth-tabs">
					<button type="button" :class="{ active: mode === 'login' }" @click="go('login')">登录</button>
					<button type="button" :class="{ active: mode === 'register' }" @click="go('register')">注册</button>
					<span class="tab-slider" :class="{ register: mode === 'register' }"></span>
				</div>

				<h2>{{ mode === 'login' ? '欢迎回来' : '创建账号' }}</h2>
				<p class="auth-sub">
					{{ mode === 'login' ? '使用工号或手机号进入智慧医疗工作台' : '登记院内医护人员账号，即刻开启数字院区' }}
				</p>

				<el-form
					ref="formRef"
					:model="form"
					:rules="rules"
					label-position="top"
					class="auth-form"
					@submit.prevent>
					<template v-if="mode === 'register'">
						<el-form-item label="真实姓名" prop="realName">
							<el-input v-model="form.realName" placeholder="请输入真实姓名" :prefix-icon="User" />
						</el-form-item>
						<div class="form-row">
							<el-form-item label="工号" prop="employeeNo">
								<el-input v-model="form.employeeNo" placeholder="如 D000001" :prefix-icon="Postcard" />
							</el-form-item>
							<el-form-item label="手机号" prop="phone">
								<el-input v-model="form.phone" placeholder="11 位手机号" :prefix-icon="Iphone" maxlength="11" />
							</el-form-item>
						</div>
					</template>
					<template v-else>
						<el-form-item label="账号" prop="account">
							<el-input v-model="form.account" placeholder="请输入工号或手机号" :prefix-icon="User" />
						</el-form-item>
					</template>

					<el-form-item label="密码" prop="password">
						<el-input
							v-model="form.password"
							type="password"
							show-password
							placeholder="请输入密码"
							:prefix-icon="Lock"
							@keydown.enter.exact="submit" />
					</el-form-item>

					<el-form-item v-if="mode === 'register'" label="确认密码" prop="confirm">
						<el-input
							v-model="form.confirm"
							type="password"
							show-password
							placeholder="请再次输入密码"
							:prefix-icon="Lock"
							@keydown.enter.exact="submit" />
					</el-form-item>

					<div v-if="mode === 'login'" class="auth-row">
						<el-checkbox v-model="remember">记住账号</el-checkbox>
						<span class="auth-link">忘记密码请联系管理员</span>
					</div>

					<el-button class="auth-submit" type="primary" @click="submit">
						{{ mode === 'login' ? '登 录' : '注 册' }}
					</el-button>
				</el-form>

				<p class="auth-foot">
					{{ mode === 'login' ? '还没有账号？' : '已有账号？' }}
					<a @click="go(mode === 'login' ? 'register' : 'login')">
						{{ mode === 'login' ? '立即注册' : '直接登录' }}
					</a>
				</p>
			</section>
		</div>
	</div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Iphone, Lock, Postcard, User } from '@element-plus/icons-vue'
import { post } from '@/util/axios'
import { useUserStore } from '@/stores'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const mode = computed(() => (route.path === '/register' ? 'register' : 'login'))
const remember = ref(true)
const formRef = ref()

const points = [
	{ no: '01', title: '科室协同', desc: '科室档案、类别与启停状态统一编排。' },
	{ no: '02', title: '智能挂号', desc: '患者建档、医生选择与挂号计费串联。' },
	{ no: '03', title: 'AI 就医助手', desc: '自然语言推荐科室与医生。' }
]

const form = reactive({
	account: '',
	realName: '',
	employeeNo: '',
	phone: '',
	password: '',
	confirm: ''
})

const validateConfirm = (rule, value, callback) => {
	if (!value) {
		callback(new Error('请再次输入密码'))
	} else if (value !== form.password) {
		callback(new Error('两次输入的密码不一致'))
	} else {
		callback()
	}
}

const rules = computed(() => {
	if (mode.value === 'login') {
		return {
			account: [{ required: true, message: '请输入工号或手机号', trigger: 'blur' }],
			password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
		}
	}
	return {
		realName: [
			{ required: true, message: '请输入真实姓名', trigger: 'blur' },
			{ max: 50, message: '姓名最大 50 个字符', trigger: 'blur' }
		],
		employeeNo: [
			{ required: true, message: '请输入工号', trigger: 'blur' },
			{ min: 3, max: 20, message: '工号长度 3-20 个字符', trigger: 'blur' }
		],
		phone: [
			{ required: true, message: '请输入手机号', trigger: 'blur' },
			{ pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
		],
		password: [
			{ required: true, message: '请输入密码', trigger: 'blur' },
			{ min: 6, max: 20, message: '密码长度 6-20 个字符', trigger: 'blur' }
		],
		confirm: [{ validator: validateConfirm, trigger: 'blur' }]
	}
})

watch(mode, () => {
	form.password = ''
	form.confirm = ''
	formRef.value?.clearValidate()
})

onMounted(() => {
	const saved = localStorage.getItem('his_account')
	if (saved) {
		form.account = saved
	}
})

function go(value) {
	router.replace(value === 'register' ? '/register' : '/login')
}

function submit() {
	formRef.value.validate(valid => {
		if (!valid) {
			return
		}
		if (mode.value === 'login') {
			login()
		} else {
			register()
		}
	})
}

function login() {
	const account = form.account.trim()
	post('/sysUser/login', { account, password: form.password }, content => {
		userStore.setUser(content)
		if (remember.value) {
			localStorage.setItem('his_account', account)
		} else {
			localStorage.removeItem('his_account')
		}
		const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/dashboard'
		router.replace(redirect)
	})
}

function register() {
	post(
		'/sysUser/register',
		{
			realName: form.realName.trim(),
			employeeNo: form.employeeNo.trim(),
			phone: form.phone.trim(),
			password: form.password
		},
		() => {
			form.account = form.employeeNo.trim()
			go('login')
		}
	)
}
</script>

<style scoped lang="scss">
.auth-page {
	position: relative;
	display: flex;
	align-items: center;
	justify-content: center;
	min-height: 100vh;
	padding: 44px 54px;
	overflow: hidden;
	background: #eef5fd;
	color: var(--his-ink);
}

.decor {
	position: absolute;
	inset: 0;
	overflow: hidden;
}
.blob {
	position: absolute;
	border-radius: 50%;
	filter: blur(90px);
}
.blob-a {
	top: -220px;
	left: -160px;
	width: 620px;
	height: 620px;
	background: radial-gradient(circle, rgba(47, 107, 255, 0.24), transparent 66%);
}
.blob-b {
	right: -180px;
	bottom: -220px;
	width: 600px;
	height: 600px;
	background: radial-gradient(circle, rgba(31, 200, 232, 0.24), transparent 64%);
}
.blob-c {
	top: 20%;
	right: 30%;
	width: 340px;
	height: 340px;
	background: radial-gradient(circle, rgba(123, 134, 247, 0.16), transparent 68%);
}
.grid-layer {
	position: absolute;
	inset: 0;
	background-image:
		linear-gradient(rgba(110, 152, 220, 0.09) 1px, transparent 1px),
		linear-gradient(90deg, rgba(110, 152, 220, 0.09) 1px, transparent 1px);
	background-size: 54px 54px;
	-webkit-mask-image: radial-gradient(circle at 42% 38%, #000 16%, transparent 74%);
	mask-image: radial-gradient(circle at 42% 38%, #000 16%, transparent 74%);
}

.auth-shell {
	position: relative;
	z-index: 1;
	display: grid;
	grid-template-columns: minmax(0, 1fr) 440px;
	gap: 72px;
	align-items: center;
	width: min(1140px, 100%);
}

.intro-brand {
	display: flex;
	align-items: center;
	gap: 12px;
	margin-bottom: 30px;
}
.brand-mark {
	display: grid;
	width: 44px;
	height: 44px;
	place-items: center;
	border-radius: 13px;
	background: linear-gradient(135deg, #2f6bff, #1fc8e8);
	box-shadow: 0 12px 24px rgba(47, 107, 255, 0.26);
	color: #fff;
	font-size: 24px;
	line-height: 1;
}
.intro-brand strong {
	display: block;
	color: #16233f;
	font-size: 15px;
	letter-spacing: 0.02em;
}
.intro-brand span {
	color: #93a4c2;
	font-size: 9.5px;
	letter-spacing: 0.18em;
}

.intro-badge {
	display: inline-flex;
	align-items: center;
	gap: 8px;
	padding: 6px 13px;
	border: 1px solid rgba(47, 107, 255, 0.16);
	border-radius: 999px;
	background: rgba(255, 255, 255, 0.6);
	color: #3b5fa8;
	font-size: 11.5px;
	letter-spacing: 0.06em;
	backdrop-filter: blur(10px);
}
.intro-badge i {
	width: 6px;
	height: 6px;
	border-radius: 50%;
	background: #1fc8e8;
	box-shadow: 0 0 8px rgba(31, 200, 232, 0.85);
}

.auth-intro h1 {
	margin: 22px 0 16px;
	color: #101d38;
	font-size: 42px;
	font-weight: 600;
	line-height: 1.26;
	letter-spacing: 0.01em;
}
.auth-intro h1 em {
	font-style: normal;
	background: linear-gradient(100deg, #2f6bff, #1fc8e8);
	-webkit-background-clip: text;
	background-clip: text;
	color: transparent;
}
.auth-intro > p {
	max-width: 460px;
	color: #62748f;
	font-size: 13.5px;
	line-height: 1.9;
}

.intro-points {
	display: flex;
	flex-direction: column;
	gap: 10px;
	margin-top: 28px;
}
.intro-points li {
	display: flex;
	align-items: center;
	gap: 13px;
	padding: 11px 16px;
	border: 1px solid rgba(255, 255, 255, 0.9);
	border-radius: 14px;
	background: rgba(255, 255, 255, 0.6);
	box-shadow: 0 10px 24px rgba(38, 74, 140, 0.05);
	backdrop-filter: blur(12px);
}
.point-icon {
	display: grid;
	flex: 0 0 34px;
	width: 34px;
	height: 34px;
	place-items: center;
	border-radius: 10px;
	background: linear-gradient(135deg, rgba(47, 107, 255, 0.14), rgba(31, 200, 232, 0.12));
	color: #2f6bff;
	font-size: 11.5px;
	letter-spacing: 0.04em;
}
.intro-points strong {
	color: #24344f;
	font-size: 13px;
}
.intro-points p {
	margin-top: 3px;
	color: #8a9bb8;
	font-size: 12px;
}

.intro-meta {
	display: flex;
	gap: 24px;
	margin-top: 30px;
	padding-top: 18px;
	border-top: 1px solid rgba(140, 168, 210, 0.24);
	color: #9aabc6;
	font-size: 11.5px;
	letter-spacing: 0.06em;
}

.auth-card {
	position: relative;
	padding: 30px 32px 26px;
	border: 1px solid rgba(255, 255, 255, 0.95);
	border-radius: 26px;
	background: rgba(255, 255, 255, 0.9);
	box-shadow:
		0 30px 70px rgba(38, 74, 140, 0.14),
		inset 0 1px 0 #fff;
	backdrop-filter: blur(26px);
}

.auth-tabs {
	position: relative;
	display: flex;
	margin-bottom: 24px;
	padding: 4px;
	border-radius: 12px;
	background: #f1f6fd;
}
.auth-tabs button {
	position: relative;
	z-index: 1;
	flex: 1;
	height: 38px;
	border: none;
	border-radius: 9px;
	background: transparent;
	color: #6b7c9a;
	font-family: inherit;
	font-size: 13.5px;
	cursor: pointer;
	transition: color 0.25s;
}
.auth-tabs button.active {
	color: #1f4fd8;
	font-weight: 600;
}
.tab-slider {
	position: absolute;
	top: 4px;
	left: 4px;
	width: calc(50% - 4px);
	height: 38px;
	border-radius: 9px;
	background: #fff;
	box-shadow: 0 6px 14px rgba(40, 80, 160, 0.1);
	transition: transform 0.28s cubic-bezier(0.4, 0, 0.2, 1);
}
.tab-slider.register {
	transform: translateX(100%);
}

.auth-card h2 {
	color: #12203c;
	font-size: 22px;
	font-weight: 600;
}
.auth-sub {
	margin: 8px 0 20px;
	color: #7c8cab;
	font-size: 12.5px;
}

.auth-form :deep(.el-form-item) {
	margin-bottom: 15px;
}
.auth-form :deep(.el-form-item__label) {
	padding-bottom: 4px;
	color: #41527a;
	font-size: 12.5px;
	line-height: 1.4;
}
.auth-form :deep(.el-input__wrapper) {
	height: 44px;
	border-radius: 11px;
	background: #f8fbff;
	box-shadow: 0 0 0 1px #e3ebf8 inset;
}
.auth-form :deep(.el-input__wrapper.is-focus) {
	background: #fff;
	box-shadow:
		0 0 0 1px #2f6bff inset,
		0 0 0 4px rgba(47, 107, 255, 0.1);
}
.form-row {
	display: grid;
	grid-template-columns: 1fr 1fr;
	gap: 12px;
}

.auth-row {
	display: flex;
	align-items: center;
	justify-content: space-between;
	margin: 2px 0 18px;
	font-size: 12.5px;
}
.auth-link {
	color: #93a4c2;
	cursor: default;
}

.auth-submit {
	width: 100%;
	height: 46px;
	border: none;
	border-radius: 12px;
	background: linear-gradient(115deg, #2f6bff, #2f8bfa 55%, #1fc8e8);
	box-shadow: 0 14px 28px rgba(47, 107, 255, 0.28);
	font-size: 14.5px;
	letter-spacing: 0.24em;
}
.auth-submit:hover {
	filter: brightness(1.05);
	box-shadow: 0 16px 32px rgba(47, 107, 255, 0.34);
}

.auth-foot {
	margin-top: 18px;
	color: #8a9bb8;
	font-size: 12px;
	text-align: center;
}
.auth-foot a {
	margin-left: 4px;
	color: #2f6bff;
	cursor: pointer;
}
</style>
