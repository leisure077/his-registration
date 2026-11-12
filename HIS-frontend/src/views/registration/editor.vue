<template>
	<el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
		<el-divider content-position="left">患者信息</el-divider>
		<el-alert
			v-if="patientExist"
			:title="`身份证号已存在患者档案（${form.patientName}），将直接复用，无需重复保存`"
			type="success"
			:closable="false"
			style="margin-bottom: 15px;" />
		<el-row :gutter="20">
			<el-col :span="12">
				<el-form-item label="身份证号" prop="idCard">
					<el-input
						v-model="form.idCard"
						placeholder="请输入身份证号后自动识别"
						@blur="handleIdCard"
						@keydown.enter.exact="handleIdCard" />
				</el-form-item>
			</el-col>
			<el-col :span="12">
				<el-form-item label="患者姓名" prop="patientName">
					<el-input v-model="form.patientName" placeholder="请输入患者姓名" />
				</el-form-item>
			</el-col>
			<el-col :span="12">
				<el-form-item label="性别" prop="gender">
					<el-select v-model="form.gender" placeholder="请选择性别" style="width: 100%;">
						<el-option label="男" value="1"></el-option>
						<el-option label="女" value="2"></el-option>
						<el-option label="未知" value="0"></el-option>
					</el-select>
				</el-form-item>
			</el-col>
			<el-col :span="12">
				<el-form-item label="出生日期" prop="birthday">
					<el-date-picker
						v-model="form.birthday"
						type="date"
						value-format="YYYY-MM-DD"
						placeholder="选择出生日期"
						style="width: 100%;" />
				</el-form-item>
			</el-col>
			<el-col :span="12">
				<el-form-item label="联系电话" prop="phone">
					<el-input v-model="form.phone" placeholder="请输入联系电话" />
				</el-form-item>
			</el-col>
			<el-col :span="12">
				<el-form-item label="家庭住址" prop="address">
					<el-input v-model="form.address" placeholder="请输入家庭住址" />
				</el-form-item>
			</el-col>
		</el-row>

		<el-divider content-position="left">挂号信息</el-divider>
		<el-row :gutter="20">
			<el-col :span="12">
				<el-form-item label="就诊科室" prop="deptId">
					<el-select filterable v-model="form.deptId" placeholder="请选择科室" style="width: 100%;" @change="deptChange">
						<el-option v-for="item in deptList" :key="item.id" :label="item.deptName" :value="item.id"></el-option>
					</el-select>
				</el-form-item>
			</el-col>
			<el-col :span="12">
				<el-form-item label="就诊医生" prop="doctorId">
					<el-select filterable v-model="form.doctorId" placeholder="请先选择科室" style="width: 100%;" :disabled="!form.deptId">
						<el-option
							v-for="item in doctorList"
							:key="item.id"
							:label="`${item.realName}（${titleName(item.titleId)}）`"
							:value="item.id"></el-option>
					</el-select>
				</el-form-item>
			</el-col>
			<el-col :span="12">
				<el-form-item label="就诊日期" prop="regDate">
					<el-date-picker
						v-model="form.regDate"
						type="date"
						value-format="YYYY-MM-DD"
						placeholder="选择就诊日期"
						style="width: 100%;" />
				</el-form-item>
			</el-col>
			<el-col :span="12">
				<el-form-item label="就诊时段" prop="regPeriod">
					<el-select v-model="form.regPeriod" style="width: 100%;">
						<el-option label="上午" value="1"></el-option>
						<el-option label="下午" value="2"></el-option>
					</el-select>
				</el-form-item>
			</el-col>
			<el-col :span="12">
				<el-form-item label="挂号类型" prop="regType">
					<el-select v-model="form.regType" style="width: 100%;">
						<el-option label="普通挂号" value="1"></el-option>
						<el-option label="预约挂号" value="2"></el-option>
					</el-select>
				</el-form-item>
			</el-col>
			<el-col :span="12">
				<el-form-item label="挂号费">
					<el-input :model-value="`¥${regFee}`" readonly></el-input>
				</el-form-item>
			</el-col>
			<el-col :span="24">
				<el-form-item label="备注">
					<el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注"></el-input>
				</el-form-item>
			</el-col>
		</el-row>

		<el-form-item>
			<el-button type="primary" plain :icon="Save" @click="save">保存挂号</el-button>
		</el-form-item>
	</el-form>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import Save from '@@/icons/save'
import { get, post } from '@/util/axios'

const emits = defineEmits(['update:show', 'getTableData'])

const formRef = ref()
const patientExist = ref(false)
const deptList = ref([])
const doctorList = ref([])
const titles = ref([])

// 获取本地今天的日期，格式 YYYY-MM-DD
function today() {
	const now = new Date()
	const month = String(now.getMonth() + 1).padStart(2, '0')
	const day = String(now.getDate()).padStart(2, '0')
	return `${now.getFullYear()}-${month}-${day}`
}

const form = reactive({
	idCard: '',
	patientName: '',
	gender: '0',
	birthday: '',
	phone: '',
	address: '',
	deptId: null,
	doctorId: null,
	regDate: today(),
	regPeriod: '1',
	regType: '1',
	remark: ''
})

const selectedDoctor = computed(() => doctorList.value.find(item => item.id === form.doctorId))
const regFee = computed(() => {
	const doctor = selectedDoctor.value
	if (!doctor) {
		return 0
	}
	const title = titles.value.find(item => item.id === doctor.titleId)
	return title ? title.regFee : 0
})

function titleName(titleId) {
	const title = titles.value.find(item => item.id === titleId)
	return title ? title.titleName : ''
}

// 加载科室、医生、职称字典
loadSelects()
function loadSelects() {
	get('/sysDept/listAll', {}, content => {
		deptList.value = content || []
	})
	get('/sysTitle/listAll', { }, content => {
		titles.value = content || []
	})
}

// 切换科室后清空医生
function deptChange() {
	form.doctorId = null
	get('/sysUser/getByDept', { deptId: form.deptId }, content => {
		doctorList.value = content || []
	})
}

// 根据身份证号识别患者是否存在，并解析生日、性别
function handleIdCard() {
	const id = (form.idCard || '').trim()
	if (!/^(\d{15}|\d{17}[\dXx])$/.test(id)) {
		return
	}
	// 18位身份证：7-14位出生日期，第17位奇男偶女
	if (id.length === 18) {
		form.birthday = `${id.substr(6, 4)}-${id.substr(10, 2)}-${id.substr(12, 2)}`
		form.gender = Number(id.substr(16, 1)) % 2 === 1 ? '1' : '2'
	} else {
		// 15位身份证：7-12位出生日期（19xx），第15位奇男偶女
		form.birthday = `19${id.substr(6, 2)}-${id.substr(8, 2)}-${id.substr(10, 2)}`
		form.gender = Number(id.substr(14, 1)) % 2 === 1 ? '1' : '2'
	}
	// 查询患者档案，存在则自动回填其他信息
	get('/sysRegistration/patient', { idCard: id }, content => {
		if (content && content.id) {
			patientExist.value = true
			form.patientName = content.patientName || ''
			form.gender = content.gender || form.gender
			form.birthday = content.birthday || form.birthday
			form.phone = content.phone || ''
			form.address = content.address || ''
		} else {
			patientExist.value = false
		}
	})
}

watch(() => form.idCard, value => {
	if (!value) {
		patientExist.value = false
	}
})

const rules = reactive({
	idCard: [
		{ required: true, message: '请输入身份证号', trigger: 'blur' },
		{ pattern: /^(\d{15}|\d{17}[\dXx])$/, message: '身份证号格式不正确', trigger: 'blur' }
	],
	patientName: [
		{ required: true, message: '请输入患者姓名', trigger: 'blur' },
		{ max: 50, message: '患者姓名最大50个字符', trigger: 'blur' }
	],
	phone: [
		{ pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
	],
	deptId: [
		{ required: true, message: '请选择就诊科室', trigger: 'change' }
	],
	doctorId: [
		{ required: true, message: '请选择就诊医生', trigger: 'change' }
	],
	regDate: [
		{ required: true, message: '请选择就诊日期', trigger: 'change' }
	]
})

function save() {
	formRef.value.validate(valid => {
		if (!valid) {
			return
		}
		// 过滤空值，避免后端日期/数值转换异常
		const payload = {}
		Object.keys(form).forEach(key => {
			const value = form[key]
			if (value !== null && value !== undefined && value !== '') {
				payload[key] = value
			}
		})
		post('/sysRegistration/add', payload, content => {
			emits('update:show', false)
			emits('getTableData')
		})
	})
}
</script>

<style scoped lang="scss">
</style>
