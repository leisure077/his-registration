<template>
	<div class="page dash">
		<section class="dash-hero glass-card">
			<div class="hero-main">
				<span class="hero-eyebrow"><i></i>数字院区 · 智慧医疗</span>
				<h1>{{ greeting }}，{{ name }}</h1>
				<p class="hero-sub">{{ today }}，今日有 {{ stats.waiting }} 位患者等待就诊，院区运行平稳。</p>
				<div class="hero-actions">
					<el-button type="primary" :icon="Plus" @click="router.push('/registration/add')">挂号登记</el-button>
					<el-button plain @click="router.push('/registration')">查看挂号</el-button>
				</div>
			</div>
			<div class="hero-art" aria-hidden="true">
				<span class="ring ring-a"></span>
				<span class="ring ring-b"></span>
				<span class="ring ring-c"></span>
				<div class="art-card">
					<span class="art-cross">+</span>
					<span class="art-line"></span>
					<span class="art-line short"></span>
					<div class="art-bars"><i></i><i></i><i></i><i></i><i></i><i></i></div>
				</div>
			</div>
		</section>

		<section class="stat-grid">
			<article v-for="card in cards" :key="card.label" class="stat-card glass-card">
				<span class="stat-icon" :class="card.tone">
					<el-icon><component :is="card.icon" /></el-icon>
				</span>
				<div class="stat-meta">
					<p>{{ card.label }}</p>
					<strong>{{ card.value }}<em>{{ card.unit }}</em></strong>
				</div>
			</article>
		</section>

		<section class="dash-body">
			<div class="quick-card glass-card">
				<div class="card-head">
					<h3>快捷入口</h3>
					<span>常用操作</span>
				</div>
				<div class="quick-list">
					<button v-for="item in quickActions" :key="item.path" type="button" class="quick-item" @click="router.push(item.path)">
						<span class="quick-icon" :class="item.tone">
							<el-icon><component :is="item.icon" /></el-icon>
						</span>
						<span class="quick-text">
							<strong>{{ item.title }}</strong>
							<em>{{ item.desc }}</em>
						</span>
						<el-icon class="quick-arrow"><ArrowRight /></el-icon>
					</button>
				</div>
			</div>

			<div class="recent-card glass-card">
				<div class="card-head">
					<h3>最近挂号</h3>
					<span>最新 5 条记录</span>
				</div>
				<el-table :data="recent.records" empty-text="暂无挂号记录">
					<el-table-column label="挂号单号" prop="regNo" min-width="165"></el-table-column>
					<el-table-column label="患者" prop="patientName" width="90"></el-table-column>
					<el-table-column label="科室" prop="deptName" width="100"></el-table-column>
					<el-table-column label="医生" prop="doctorName" width="90"></el-table-column>
					<el-table-column label="就诊日期" prop="regDate" width="110"></el-table-column>
					<el-table-column label="状态" width="90">
						<template #default="{ row }">
							<el-tag v-if="row.status === '1'" type="warning">待就诊</el-tag>
							<el-tag v-else-if="row.status === '2'" type="success">已就诊</el-tag>
							<el-tag v-else type="info">已取消</el-tag>
						</template>
					</el-table-column>
				</el-table>
			</div>
		</section>
	</div>
</template>

<script setup>
import { computed, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { get } from '@/util/axios'
import { useUserStore } from '@/stores'
import {
	ArrowRight,
	FirstAidKit,
	OfficeBuilding,
	Plus,
	Tickets,
	User
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const hour = new Date().getHours()
const greeting = hour < 6 ? '夜深了' : hour < 12 ? '上午好' : hour < 18 ? '下午好' : '晚上好'
const name = computed(() => (userStore.user && userStore.user.realName) || '医护人员')
const today = new Date().toLocaleDateString('zh-CN', {
	year: 'numeric',
	month: 'long',
	day: 'numeric',
	weekday: 'long'
})

const stats = reactive({ dept: 0, user: 0, registration: 0, waiting: 0 })
const recent = reactive({ records: [] })

const cards = computed(() => [
	{ label: '科室总数', value: stats.dept, unit: '个', tone: 'blue', icon: OfficeBuilding },
	{ label: '医护人员', value: stats.user, unit: '位', tone: 'cyan', icon: User },
	{ label: '挂号总量', value: stats.registration, unit: '条', tone: 'violet', icon: Tickets },
	{ label: '待就诊', value: stats.waiting, unit: '人', tone: 'mint', icon: FirstAidKit }
])

const quickActions = [
	{ title: '挂号登记', desc: '为患者建档并选择医生', path: '/registration/add', tone: 'blue', icon: Plus },
	{ title: '科室管理', desc: '维护院区科室档案', path: '/dept', tone: 'cyan', icon: OfficeBuilding },
	{ title: '医生管理', desc: '维护医护与职称信息', path: '/user', tone: 'violet', icon: User }
]

get('/sysDept/list', { pageNo: 1, pageSize: 1 }, content => {
	stats.dept = content.totalRow
})
get('/sysUser/list', { pageNo: 1, pageSize: 1 }, content => {
	stats.user = content.totalRow
})
get('/sysRegistration/list', { pageNo: 1, pageSize: 5 }, content => {
	stats.registration = content.totalRow
	recent.records = content.records
})
get('/sysRegistration/list', { pageNo: 1, pageSize: 1, status: '1' }, content => {
	stats.waiting = content.totalRow
})
</script>

<style scoped lang="scss">
.dash-hero {
	position: relative;
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 24px;
	padding: 30px 34px;
	overflow: hidden;
	background:
		radial-gradient(120% 160% at 100% 0%, rgba(31, 200, 232, 0.13), transparent 58%),
		radial-gradient(90% 140% at 0% 100%, rgba(47, 107, 255, 0.09), transparent 60%),
		rgba(255, 255, 255, 0.86);
}

.hero-eyebrow {
	display: inline-flex;
	align-items: center;
	gap: 8px;
	padding: 6px 12px;
	border: 1px solid rgba(47, 107, 255, 0.16);
	border-radius: 999px;
	background: rgba(47, 107, 255, 0.06);
	color: #3b5fa8;
	font-size: 11.5px;
	letter-spacing: 0.06em;
}
.hero-eyebrow i {
	width: 6px;
	height: 6px;
	border-radius: 50%;
	background: #1fc8e8;
	box-shadow: 0 0 8px rgba(31, 200, 232, 0.8);
}

.hero-main h1 {
	margin: 18px 0 8px;
	color: #12203c;
	font-size: 27px;
	font-weight: 600;
	letter-spacing: 0.01em;
}
.hero-sub {
	color: var(--his-muted);
	font-size: 13px;
	line-height: 1.8;
}
.hero-actions {
	display: flex;
	gap: 10px;
	margin-top: 20px;
}

.hero-art {
	position: relative;
	flex: 0 0 250px;
	height: 150px;
}
.ring {
	position: absolute;
	border-radius: 50%;
	border: 1px solid rgba(47, 107, 255, 0.14);
}
.ring-a {
	top: -40px;
	right: -30px;
	width: 200px;
	height: 200px;
}
.ring-b {
	top: -10px;
	right: 0;
	width: 140px;
	height: 140px;
	border-color: rgba(31, 200, 232, 0.22);
}
.ring-c {
	bottom: -50px;
	right: 60px;
	width: 120px;
	height: 120px;
	border-color: rgba(123, 134, 247, 0.16);
}
.art-card {
	position: absolute;
	top: 24px;
	right: 26px;
	display: flex;
	flex-direction: column;
	gap: 8px;
	width: 168px;
	padding: 16px 16px 14px;
	border: 1px solid rgba(255, 255, 255, 0.9);
	border-radius: 16px;
	background: rgba(255, 255, 255, 0.78);
	box-shadow: 0 18px 36px rgba(38, 74, 140, 0.12);
	backdrop-filter: blur(12px);
}
.art-cross {
	display: grid;
	width: 30px;
	height: 30px;
	place-items: center;
	border-radius: 9px;
	background: linear-gradient(135deg, #2f6bff, #1fc8e8);
	color: #fff;
	font-size: 18px;
	line-height: 1;
}
.art-line {
	width: 82%;
	height: 7px;
	border-radius: 6px;
	background: rgba(47, 107, 255, 0.14);
}
.art-line.short {
	width: 55%;
	background: rgba(31, 200, 232, 0.18);
}
.art-bars {
	display: flex;
	align-items: flex-end;
	gap: 5px;
	height: 30px;
	margin-top: 2px;
}
.art-bars i {
	flex: 1;
	border-radius: 4px 4px 2px 2px;
	background: linear-gradient(180deg, rgba(47, 107, 255, 0.55), rgba(31, 200, 232, 0.25));
}
.art-bars i:nth-child(1) { height: 38%; }
.art-bars i:nth-child(2) { height: 62%; }
.art-bars i:nth-child(3) { height: 48%; }
.art-bars i:nth-child(4) { height: 82%; }
.art-bars i:nth-child(5) { height: 58%; }
.art-bars i:nth-child(6) { height: 100%; }

.stat-grid {
	display: grid;
	grid-template-columns: repeat(4, minmax(0, 1fr));
	gap: 16px;
}
.stat-card {
	display: flex;
	align-items: center;
	gap: 14px;
	padding: 18px 20px;
}
.stat-icon {
	display: grid;
	flex: 0 0 44px;
	width: 44px;
	height: 44px;
	place-items: center;
	border-radius: 13px;
	font-size: 19px;
}
.stat-icon.blue {
	background: linear-gradient(135deg, rgba(47, 107, 255, 0.16), rgba(47, 107, 255, 0.06));
	color: #2f6bff;
}
.stat-icon.cyan {
	background: linear-gradient(135deg, rgba(31, 200, 232, 0.18), rgba(31, 200, 232, 0.06));
	color: #0fa8c4;
}
.stat-icon.violet {
	background: linear-gradient(135deg, rgba(123, 134, 247, 0.18), rgba(123, 134, 247, 0.06));
	color: #6c74e8;
}
.stat-icon.mint {
	background: linear-gradient(135deg, rgba(47, 208, 166, 0.2), rgba(47, 208, 166, 0.06));
	color: #14a67f;
}
.stat-meta p {
	color: var(--his-muted);
	font-size: 12px;
}
.stat-meta strong {
	display: block;
	margin-top: 3px;
	color: #16233f;
	font-size: 23px;
	font-weight: 600;
	letter-spacing: 0.01em;
}
.stat-meta em {
	margin-left: 3px;
	color: #93a4c2;
	font-size: 11px;
	font-style: normal;
	font-weight: 400;
}

.dash-body {
	display: grid;
	grid-template-columns: 320px minmax(0, 1fr);
	gap: 16px;
}

.quick-card,
.recent-card {
	padding: 18px 20px 16px;
}
.card-head {
	display: flex;
	align-items: baseline;
	justify-content: space-between;
	margin-bottom: 12px;
}
.card-head h3 {
	color: #16233f;
	font-size: 14.5px;
	font-weight: 600;
}
.card-head span {
	color: #93a4c2;
	font-size: 11.5px;
}

.quick-list {
	display: flex;
	flex-direction: column;
	gap: 8px;
}
.quick-item {
	display: flex;
	align-items: center;
	gap: 12px;
	padding: 11px 12px;
	border: 1px solid rgba(140, 168, 210, 0.16);
	border-radius: 13px;
	background: rgba(255, 255, 255, 0.7);
	color: inherit;
	font-family: inherit;
	text-align: left;
	cursor: pointer;
	transition: all 0.2s;
}
.quick-item:hover {
	border-color: rgba(47, 107, 255, 0.26);
	background: #fff;
	box-shadow: 0 10px 22px rgba(47, 107, 255, 0.08);
	transform: translateY(-1px);
}
.quick-icon {
	display: grid;
	flex: 0 0 34px;
	width: 34px;
	height: 34px;
	place-items: center;
	border-radius: 10px;
	font-size: 16px;
}
.quick-icon.blue {
	background: rgba(47, 107, 255, 0.1);
	color: #2f6bff;
}
.quick-icon.cyan {
	background: rgba(31, 200, 232, 0.12);
	color: #0fa8c4;
}
.quick-icon.violet {
	background: rgba(123, 134, 247, 0.14);
	color: #6c74e8;
}
.quick-text {
	display: flex;
	flex: 1;
	flex-direction: column;
	line-height: 1.3;
}
.quick-text strong {
	color: #24344f;
	font-size: 13px;
	font-weight: 600;
}
.quick-text em {
	margin-top: 2px;
	color: #93a4c2;
	font-size: 11px;
	font-style: normal;
}
.quick-arrow {
	color: #b6c3d8;
	font-size: 12px;
}
</style>
