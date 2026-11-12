<template>
	<div class="page">
		<div class="page-hero">
			<div>
				<h2>医生管理</h2>
				<p class="sub">维护医护人员档案、科室归属与职称信息</p>
			</div>
			<el-button type="primary" :icon="Plus" @click="add">新增医护人员</el-button>
		</div>

		<div class="glass-card data-card">
			<div class="toolbar">
				<div class="toolbar-left">
					<el-input
						class="search-input"
						v-model="name"
						placeholder="请输入要查询的医生姓名"
						clearable
						@keydown.enter.exact="search" />
					<el-button type="primary" plain :icon="Search" @click="search">查询</el-button>
				</div>
				<span class="record-count">共 {{ data.totalRow }} 位医护人员</span>
			</div>

			<el-table :data="data.records" empty-text="暂无医护人员数据">
				<el-table-column label="编号" prop="id" width="70"></el-table-column>
				<el-table-column label="姓名" prop="realName" min-width="100"></el-table-column>
				<el-table-column label="性别" width="70">
					<template #default="{row}"> {{gender_dict[row.gender]}}</template>
				</el-table-column>
				<el-table-column label="科室" width="110">
					<template #default="{row}"> {{dept_dict[row.deptId]}}</template>
				</el-table-column>
				<el-table-column label="职称" width="100">
					<template #default="{row}"> {{title_dict[row.titleId]}}</template>
				</el-table-column>
				<el-table-column label="工号" prop="employeeNo" width="110"></el-table-column>
				<el-table-column label="手机号" prop="phone" width="125"></el-table-column>
				<el-table-column label="邮箱" prop="email" show-overflow-tooltip min-width="150"></el-table-column>
				<el-table-column label="状态" width="80">
					<template #default="{row}">
						<el-tag v-if="row.enabled" type="success">启用</el-tag>
						<el-tag v-else type="danger">禁用</el-tag>
					</template>
				</el-table-column>
				<el-table-column label="创建日期" prop="createDate" width="110"></el-table-column>
				<el-table-column label="操作" width="135px" fixed="right">
					<template #default="{row}">
						<template v-if="row.enabled">
							<el-button type="primary" plain size="small" @click="update(row.id)">修改</el-button>
							<el-button type="danger" plain size="small" @click="del(row.id, false)">删除</el-button>
						</template>
						<el-button v-else size="small" type="success" plain @click="del(row.id, true)">启用</el-button>
					</template>
				</el-table-column>
			</el-table>

			<div class="pager">
				<el-pagination
					background
					layout="prev, pager, next"
					:total="data.totalRow"
					:page-count="data.totalPage"
					v-model:current-page="params.pageNo"
					@current-change="getTableData"></el-pagination>
			</div>
		</div>

		<el-dialog
			v-model="dialog.visible"
			:title="dialog.title"
			:close-on-click-modal="false"
			width="600px">
			<user-editor
				v-if="dialog.visible"
				v-model:show="dialog.visible"
				:id="dialog.id"
				@getTableData="getTableData" />
		</el-dialog>
	</div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { get, post } from '@/util/axios'
import { Search, Plus } from '@element-plus/icons-vue'
import UserEditor from './editor'
import { ElMessageBox } from 'element-plus'

const dialog = reactive({
	visible: false,
	title: '',
	id: null
})
const name = ref('')
const data = reactive({
	records: [],
	totalPage: 0,
	totalRow: 0
})
const params = reactive({
	pageNo: 1,
	pageSize: 10,
	realName: ''
})
const gender_dict = {
	'0': '未知',
	'1': '男',
	'2': '女'
}
// 科室字典 - 从后端获取
const dept_dict = reactive({})
// 职称字典 - 从后端获取
const title_dict = reactive({})

// 初始化加载科室和职称字典
loadDicts()
function loadDicts() {
	get('/sysDept/list', { pageNo: 1, pageSize: 100 }, content => {
		content.records.forEach(item => {
			dept_dict[item.id] = item.deptName
		})
	})
	get('/sysTitle/list', { pageNo: 1, pageSize: 100 }, content => {
		content.records.forEach(item => {
			title_dict[item.id] = item.titleName
		})
	})
}

getTableData()
function getTableData() {
	get('/sysUser/list', params, content => {
		data.records = content.records
		data.totalPage = content.totalPage
		data.totalRow = content.totalRow
	})
}
function search() {
	params.realName = name.value
	params.pageNo = 1
	getTableData()
}
function add() {
	dialog.title = '添加医护人员'
	dialog.id = null
	dialog.visible = true
}
function del(id, enabled) {
	ElMessageBox.confirm(enabled ? '确定要启用该医护人员吗?' : '确定要删除该医护人员吗?', '警告', {
		type: 'warning',
		cancelButtonText: '取消',
		confirmButtonText: '确定'
	}).then(() => {
		post('/sysUser/del', { id: id, enabled: enabled }, content => {
			getTableData()
		})
	}).catch(() => {})
}
function update(id) {
	dialog.title = '修改医护人员'
	dialog.id = id
	dialog.visible = true
}
</script>

<style scoped lang="scss">
</style>
