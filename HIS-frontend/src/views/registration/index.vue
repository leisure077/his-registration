<template>
	<div class="page">
		<div class="page-hero">
			<div>
				<h2>挂号管理</h2>
				<p class="sub">查询患者挂号记录，跟踪就诊状态与收费信息</p>
			</div>
			<el-button type="primary" :icon="Plus" @click="add">挂号登记</el-button>
		</div>

		<div class="glass-card data-card">
			<div class="toolbar">
				<div class="toolbar-left">
					<el-input
						class="search-input"
						v-model="patientName"
						placeholder="请输入患者姓名"
						clearable
						@keydown.enter.exact="search" />
					<el-date-picker
						style="width: 160px;"
						v-model="regDate"
						type="date"
						value-format="YYYY-MM-DD"
						placeholder="就诊日期" />
					<el-select style="width: 140px;" v-model="status" placeholder="挂号状态" clearable>
						<el-option label="待就诊" value="1"></el-option>
						<el-option label="已就诊" value="2"></el-option>
						<el-option label="已取消" value="3"></el-option>
					</el-select>
					<el-button type="primary" plain :icon="Search" @click="search">查询</el-button>
				</div>
				<span class="record-count">共 {{ data.totalRow }} 条挂号记录</span>
			</div>

			<el-table :data="data.records" empty-text="暂无挂号记录">
				<el-table-column label="编号" prop="id" width="70"></el-table-column>
				<el-table-column label="挂号单号" prop="regNo" width="175"></el-table-column>
				<el-table-column label="患者姓名" prop="patientName" width="100"></el-table-column>
				<el-table-column label="性别" width="65">
					<template #default="{row}"> {{gender_dict[row.gender]}}</template>
				</el-table-column>
				<el-table-column label="身份证号" prop="idCard" width="175"></el-table-column>
				<el-table-column label="联系电话" prop="phone" width="120"></el-table-column>
				<el-table-column label="科室" prop="deptName" width="105"></el-table-column>
				<el-table-column label="医生" prop="doctorName" width="90"></el-table-column>
				<el-table-column label="职称" prop="titleName" width="95"></el-table-column>
				<el-table-column label="挂号费" width="90">
					<template #default="{row}"> ¥{{row.regFee}}</template>
				</el-table-column>
				<el-table-column label="就诊日期" prop="regDate" width="110"></el-table-column>
				<el-table-column label="时段" width="70">
					<template #default="{row}"> {{period_dict[row.regPeriod]}}</template>
				</el-table-column>
				<el-table-column label="类型" width="75">
					<template #default="{row}"> {{type_dict[row.regType]}}</template>
				</el-table-column>
				<el-table-column label="状态" width="85">
					<template #default="{row}">
						<el-tag v-if="row.status === '1'" type="warning">待就诊</el-tag>
						<el-tag v-else-if="row.status === '2'" type="success">已就诊</el-tag>
						<el-tag v-else type="info">已取消</el-tag>
					</template>
				</el-table-column>
				<el-table-column label="操作" width="175px" fixed="right">
					<template #default="{row}">
						<template v-if="row.status === '1'">
							<el-button type="primary" plain size="small" @click="finish(row.id)">完成就诊</el-button>
							<el-button type="danger" plain size="small" @click="cancel(row.id)">取消</el-button>
						</template>
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
	</div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { get, post } from '@/util/axios'
import { Search, Plus } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const patientName = ref('')
const regDate = ref('')
const status = ref('')
const data = reactive({
	records: [],
	totalPage: 0,
	totalRow: 0
})
const params = reactive({
	pageNo: 1,
	pageSize: 10,
	patientName: '',
	regDate: '',
	status: ''
})
const gender_dict = {
	'0': '未知',
	'1': '男',
	'2': '女'
}
const period_dict = {
	'1': '上午',
	'2': '下午'
}
const type_dict = {
	'1': '普通',
	'2': '预约'
}

getTableData()
function getTableData() {
	get('/sysRegistration/list', params, content => {
		data.records = content.records
		data.totalPage = content.totalPage
		data.totalRow = content.totalRow
	})
}
function search() {
	params.patientName = patientName.value
	params.regDate = regDate.value
	params.status = status.value
	params.pageNo = 1
	getTableData()
}
function add() {
	router.push('/registration/add')
}
function finish(id) {
	ElMessageBox.confirm('确定该患者已完成就诊吗?', '提示', {
		type: 'warning',
		cancelButtonText: '取消',
		confirmButtonText: '确定'
	}).then(() => {
		post('/sysRegistration/finish', { id: id }, content => {
			getTableData()
		})
	}).catch(() => {})
}
function cancel(id) {
	ElMessageBox.confirm('确定要取消该挂号吗?', '提示', {
		type: 'warning',
		cancelButtonText: '取消',
		confirmButtonText: '确定'
	}).then(() => {
		post('/sysRegistration/cancel', { id: id }, content => {
			getTableData()
		})
	}).catch(() => {})
}
</script>

<style scoped lang="scss">
</style>
