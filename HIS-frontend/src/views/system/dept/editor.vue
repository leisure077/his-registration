<template>
	<el-form ref="formRef" :model="wyform" :rules="rules" label-width="80px">
		<el-form-item label="科室名称" prop="deptName">
			<el-input v-model="wyform.deptName" placeholder="请输入科室名称"></el-input>
		</el-form-item>
		<el-form-item label="科室编码" prop="deptCode">
			<el-input v-model="wyform.deptCode" placeholder="请输入科室编码"></el-input>
		</el-form-item>
		<el-form-item label="类别" prop="deptType">
			<el-select v-model="wyform.deptType" placeholder="请选择科室类别">
				<el-option label="临床科室" value="1"></el-option>
				<el-option label="行政科室" value="2"></el-option>
				<el-option label="医技科室" value="3"></el-option>
			</el-select>
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
if (props.id) {
	get('/sysDept/getInfo', { id: props.id }, content => {
		wyform.deptName = content.deptName
		wyform.deptCode = content.deptCode
		wyform.deptType = content.deptType
		wyform.id = props.id
	})
} 
const wyform = reactive({
	deptName: '',
	deptCode: '',
	deptType: ''
})
function check(column, value, cb) {
	get('/sysDept/check', { column, value, id: props.id }, content => {
		if (content) {
			cb()
		} else {
			cb(new Error())
		}
	})
}
const rules = reactive({
	deptName: [
		{ required: true, message: '请输入科室名称', trigger: 'blur' },
		{ max: 100, message: '科室名称最大100个字符', trigger: 'blur' },
		{ validator: (role, value, cb) => { check('dept_name', value, cb) }, message: '该科室名称已经存在', trigger: 'blur' }
	],
	deptCode: [
		{ required: true, message: '请输入科室编码', trigger: 'blur' },
		{ max: 50, message: '科室编码最大50个字符', trigger: 'blur' },
		{ validator: (role, value, cb) => { check('dept_code', value, cb) }, message: '该科室编码已经存在', trigger: 'blur' }
	],
	deptType: [
		{ required: true, message: '请选择科室类别', trigger: 'change' }
	]
})
const formRef = ref()
function save () {
	formRef.value.validate(valid => {
		if (valid) {
			post(props.id ? '/sysDept/update' : '/sysDept/add', wyform, content => {
				emits('update:show', false)
				emits('getTableData')
			})
		}
	})
}
</script>

<style scoped lang="scss">
</style>