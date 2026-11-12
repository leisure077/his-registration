<template>
	<div class="page">
		<div class="page-hero">
			<div>
				<h2>挂号登记</h2>
				<p class="sub">录入患者信息，选择科室与医生，完成本次挂号</p>
			</div>
			<el-button plain :icon="ArrowLeft" @click="backToList">返回列表</el-button>
		</div>

		<div class="glass-card editor-card">
			<registration-editor @update:show="handleClose" />
		</div>
	</div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import RegistrationEditor from './editor'

const router = useRouter()

function backToList() {
	router.push('/registration')
}

// 表单保存成功后触发 update:show(false)，返回挂号列表
function handleClose(visible) {
	if (visible === false) {
		backToList()
	}
}
</script>

<style scoped lang="scss">
</style>
