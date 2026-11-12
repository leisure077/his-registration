import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useLoadingStore = defineStore('loading', () => {
	const loading = ref(0)
	function open() {
		loading.value ++
	}
	function close() {
		loading.value --
	}
	return { loading, open, close }
})

function readUser() {
	try {
		return JSON.parse(localStorage.getItem('his_user'))
	} catch (error) {
		return null
	}
}

export const useUserStore = defineStore('user', () => {
	const user = ref(readUser())
	function setUser(value) {
		user.value = value
		localStorage.setItem('his_user', JSON.stringify(value))
	}
	function logout() {
		user.value = null
		localStorage.removeItem('his_user')
	}
	return { user, setUser, logout }
})
