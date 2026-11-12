<template>
	<div class="page">
		<div class="page-hero">
			<div>
				<h2>科室管理</h2>
				<p class="sub">维护院内科室档案，掌控科室类别与启停状态</p>
			</div>
			<el-button type="primary" :icon="Plus" @click="add">新增科室</el-button>
		</div>

		<div class="glass-card data-card">
			<div class="toolbar">
				<div class="toolbar-left">
					<el-input
						class="search-input"
						v-model="name"
						placeholder="请输入要查询的科室名称"
						clearable
						@keydown.enter.exact="search" />
					<el-button type="primary" plain :icon="Search" @click="search">查询</el-button>
				</div>
				<span class="record-count">共 {{ data.totalRow }} 个科室</span>
			</div>

			<el-table :data="data.records" empty-text="暂无科室数据">
				<el-table-column label="编号" prop="id" width="80"></el-table-column>
				<el-table-column label="科室名称" prop="deptName" min-width="140"></el-table-column>
				<el-table-column label="科室编号" prop="deptCode" show-overflow-tooltip min-width="120"></el-table-column>
				<el-table-column label="科室类别" min-width="110">
					<template #default="{row}"> {{dept_type_dict[row.deptType]}}</template>
				</el-table-column>
				<el-table-column label="状态" width="90">
					<template #default="{row}">
						<el-tag v-if="row.enabled" type="success">启动</el-tag>
						<el-tag v-else type="danger">禁用</el-tag>
					</template>
				</el-table-column>
				<el-table-column label="创建日期" prop="createDate" width="115"></el-table-column>
				<el-table-column label="创建时间" prop="createTime" width="100"></el-table-column>
				<el-table-column label="修改日期" prop="updateDate" width="115"></el-table-column>
				<el-table-column label="修改时间" prop="updateTime" width="100"></el-table-column>
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
			width="500px">
			<dept-editor
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
import DeptEditor from './editor'
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
	name: ''
})
const dept_type_dict = {
	'1': '临床科室',
	'2': '行政科室',
	'3': '医技科室'
}
getTableData()
function getTableData () {
	get('/sysDept/list', params, content => {
		data.records = content.records
		data.totalPage = content.totalPage
		data.totalRow = content.totalRow
	})
	
}
function search () {
	params.name = name.value
	params.pageNo = 1
	getTableData()
}
function add () {
	dialog.title = '添加科室'
	dialog.id = null
	dialog.visible = true
	
}
function del (id, enabled) {
	ElMessageBox.confirm(enabled ? '确定要启用该科室吗?' : '确定要删除该科室吗?', '警告', {
		type: 'warning',
		cancelButtonText: '取消',
		confirmButtonText: '确定'
	}).then(() => {
		post('/sysDept/del', { id: id, enabled: enabled }, content => {
			getTableData()
		})
	}).catch(() => {})
	
}
function update(id) {
	dialog.title = '修改科室'
	dialog.id = id
	dialog.visible = true
}

</script>

<style scoped lang="scss">
</style>
