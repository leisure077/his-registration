<template>
	<el-menu router class="side-menu" :default-active="active">
		<p class="menu-group">总览</p>
		<el-menu-item index="/dashboard">
			<el-icon><DataBoard /></el-icon>
			<span>工作台</span>
		</el-menu-item>

		<p class="menu-group">医疗业务</p>
		<el-menu-item index="/dept">
			<el-icon><OfficeBuilding /></el-icon>
			<span>科室管理</span>
		</el-menu-item>
		<el-menu-item index="/user">
			<el-icon><User /></el-icon>
			<span>医生管理</span>
		</el-menu-item>
		<el-menu-item index="/registration">
			<el-icon><Tickets /></el-icon>
			<span>挂号管理</span>
		</el-menu-item>

		<p class="menu-group">扩展能力</p>
		<el-menu-item index="schedule" disabled>
			<el-icon><Calendar /></el-icon>
			<span>排班管理</span>
			<em class="menu-soon">即将上线</em>
		</el-menu-item>
		<el-menu-item index="queue" disabled>
			<el-icon><Bell /></el-icon>
			<span>叫号队列</span>
			<em class="menu-soon">即将上线</em>
		</el-menu-item>
		<el-menu-item index="triage" disabled>
			<el-icon><FirstAidKit /></el-icon>
			<span>分诊管理</span>
			<em class="menu-soon">即将上线</em>
		</el-menu-item>
	</el-menu>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import {
	Bell,
	Calendar,
	DataBoard,
	FirstAidKit,
	OfficeBuilding,
	Tickets,
	User
} from '@element-plus/icons-vue'

const route = useRoute()
const active = computed(() => route.path)
</script>

<style scoped lang="scss">
.side-menu {
	--el-menu-bg-color: transparent;
	--el-menu-text-color: #5a6b88;
	--el-menu-hover-bg-color: rgba(47, 107, 255, 0.06);
	--el-menu-active-color: #1f4fd8;
	flex: 1;
	padding: 2px 12px 14px;
	border-right: none;
	overflow-y: auto;
}

.menu-group {
	margin: 14px 10px 6px;
	color: #a3b1c9;
	font-size: 10.5px;
	letter-spacing: 0.16em;
}

.side-menu :deep(.el-menu-item) {
	position: relative;
	height: 44px;
	margin: 3px 0;
	padding-left: 14px !important;
	border-radius: 12px;
	font-size: 13.5px;
	line-height: 44px;
	transition: all 0.2s ease;
}
.side-menu :deep(.el-menu-item .el-icon) {
	margin-right: 10px;
	font-size: 16px;
}
.side-menu :deep(.el-menu-item:hover) {
	color: #1f4fd8;
	background: rgba(47, 107, 255, 0.07);
}
.side-menu :deep(.el-menu-item.is-active) {
	background: linear-gradient(92deg, rgba(47, 107, 255, 0.13), rgba(31, 200, 232, 0.09));
	color: #1f4fd8;
	font-weight: 600;
	box-shadow: inset 0 0 0 1px rgba(47, 107, 255, 0.1);
}
.side-menu :deep(.el-menu-item.is-active)::before {
	position: absolute;
	top: 12px;
	left: 4px;
	width: 3px;
	height: 20px;
	border-radius: 3px;
	background: linear-gradient(180deg, #2f6bff, #1fc8e8);
	content: '';
}
.side-menu :deep(.el-menu-item.is-disabled) {
	opacity: 0.42;
	cursor: not-allowed;
}

.menu-soon {
	margin-left: auto;
	padding: 1px 6px;
	border: 1px solid rgba(140, 168, 210, 0.32);
	border-radius: 999px;
	color: #9aabc6;
	font-size: 9.5px;
	font-style: normal;
	line-height: 16px;
}
</style>
