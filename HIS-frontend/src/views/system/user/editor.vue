<template>
	<el-form ref="formRef" :model="wyform" :rules="rules" label-width="90px">
		<el-form-item label="真实姓名" prop="realName">
			<el-input v-model="wyform.realName" placeholder="请输入真实姓名"></el-input>
		</el-form-item>
		<el-form-item label="密码" prop="password">
			<el-input v-model="wyform.password" show-password :placeholder="props.id ? '留空则不修改密码' : '请输入密码'"></el-input>
		</el-form-item>
		<el-form-item label="手机号" prop="phone">
			<el-input v-model="wyform.phone" placeholder="请输入手机号"></el-input>
		</el-form-item>
		<el-form-item label="邮箱" prop="email">
			<el-input v-model="wyform.email" placeholder="请输入邮箱"></el-input>
		</el-form-item>
		<el-form-item label="性别" prop="gender">
			<el-select v-model="wyform.gender" placeholder="请选择性别">
				<el-option label="男" value="1"></el-option>
				<el-option label="女" value="2"></el-option>
				<el-option label="未知" value="0"></el-option>
			</el-select>
		</el-form-item>
		<el-form-item label="所属科室" prop="deptId">
			<el-select filterable v-model="wyform.deptId" placeholder="请选择科室" style="width: 100%;">
				<el-option v-for="item in deptList" :key="item.id" :label="item.deptName" :value="item.id"></el-option>
			</el-select>
		</el-form-item>
		<el-form-item label="职称" prop="titleId">
			<el-select filterable v-model="wyform.titleId" placeholder="请选择职称" style="width: 100%;">
				<el-option v-for="item in titleList" :key="item.id" :label="item.titleName" :value="item.id"></el-option>
			</el-select>
		</el-form-item>
		<el-form-item label="工号" prop="employeeNo">
			<el-input v-model="wyform.employeeNo" placeholder="请输入工号"></el-input>
		</el-form-item>
		<el-form-item label="出生日期" prop="birthday">
			<el-date-picker v-model="wyform.birthday" value-format="YYYY-MM-DD" type="date" placeholder="选择日期" style="width: 100%;"></el-date-picker>
		</el-form-item>
		<el-form-item label="家庭住址" prop="address">
			<el-input v-model="wyform.address" placeholder="请输入家庭住址"></el-input>
		</el-form-item>
		<el-form-item label="医生简介" prop="intro">
			<el-input v-model="wyform.intro" type="textarea" :rows="3" placeholder="请输入医生简介（擅长治疗的疾病和症状）"></el-input>
		</el-form-item>
		<el-form-item>
			<el-button type="primary" plain :icon="Save" @click="save">保存</el-button>
		</el-form-item>
	</el-form>
</template>

<script setup>
import { ref, reactive } from 'vue'
import Save from '@@/icons/save'
import { post, get } from '@/util/axios'

const emits = defineEmits(['update:show', 'getTableData'])
const props = defineProps(['id'])

const wyform = reactive({
	realName: '',
	password: '',
	phone: '',
	email: '',
	gender: '0',
	deptId: null,
	titleId: null,
	employeeNo: '',
	birthday: '',
	address: '',
	intro: ''
})

// 科室列表
const deptList = ref([])
// 职称列表
const titleList = ref([])

// 加载科室和职称下拉数据
loadSelects()
function loadSelects() {
	get('/sysDept/listAll', {  }, content => {
		deptList.value = content
	})
	get('/sysTitle/listAll', {  }, content => {
		titleList.value = content
	})
}

// 编辑回显
if (props.id) {
	get('/sysUser/getInfo', { id: props.id }, content => {
		wyform.id = props.id
		wyform.realName = content.realName
		wyform.phone = content.phone
		wyform.email = content.email
		wyform.gender = content.gender
		wyform.deptId = content.deptId
		wyform.titleId = content.titleId
		wyform.employeeNo = content.employeeNo
		wyform.birthday = content.birthday
		wyform.address = content.address
		wyform.intro = content.intro
	})
}

// 唯一性校验
function check(column, value, cb) {
	if (!value) {
		cb()
		return
	}
	get('/sysUser/check', { column, value, id: props.id }, content => {
		if (content) {
			cb()
		} else {
			cb(new Error())
		}
	})
}

const rules = reactive({
	realName: [
		{ required: true, message: '请输入真实姓名', trigger: 'blur' },
		{ max: 50, message: '真实姓名最大50个字符', trigger: 'blur' }
	],
	password: [
		{ required: !props.id, message: '请输入密码', trigger: 'blur' },
		{ min: 6, max: 50, message: '密码长度6-50个字符', trigger: 'blur' }
	],
	phone: [
		{ validator: (role, value, cb) => { check('phone', value, cb) }, message: '该手机号已存在', trigger: 'blur' }
	],
	email: [
		{ validator: (role, value, cb) => { check('email', value, cb) }, message: '该邮箱已存在', trigger: 'blur' }
	],
	gender: [
		{ required: true, message: '请选择性别', trigger: 'change' }
	],
	deptId: [
		{ required: true, message: '请选择所属科室', trigger: 'change' }
	],
	titleId: [
		{ required: true, message: '请选择职称', trigger: 'change' }
	],
	employeeNo: [
		{ validator: (role, value, cb) => { check('employee_no', value, cb) }, message: '该工号已存在', trigger: 'blur' }
	]
})

const formRef = ref()
function save() {
	console.dir(wyform)
	formRef.value.validate(valid => {
		if (valid) {
			post(props.id ? '/sysUser/update' : '/sysUser/add', wyform, content => {
				emits('update:show', false)
				emits('getTableData')
			})
		}
	})
}
</script>

<style scoped lang="scss">
</style>
