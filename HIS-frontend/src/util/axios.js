import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useLoadingStore } from '@/stores'
const loadingStore = useLoadingStore()
export const _axios = axios.create({
	baseURL: 'http://127.0.0.1:18080'
})
export function request(url, method, data, callback) {
	const args = {
		url: url,
		method: method
	}
	if (method === 'POST') {
		const formData = new FormData()
		for (const key in data) {
			formData.append(key, data[key])
		}
		args.data = formData
	} else {
		args.params = data
	}
	loadingStore.open()
	_axios.request(args).then(response => {
		if (response.data.code === 200) {
			if (response.data.message) {
				ElMessage.success(response.data.message)
			}
			callback(response.data.content)
		} else {
			ElMessage.error(response.data.message)
		}	
	}).catch(error => {
		console.dir(error)
		ElMessage.error('网络错误')
	}).finally(() => {
		loadingStore.close()
	})
}
export function get(url, data, callback) {
	request(url, 'GET', data, callback)
}
export function post(url, data, callback) {
	request(url, 'POST', data, callback)
}
