/**
 * AI 聊天记录本地存储（按用户隔离）
 *
 * 存储键：ai_chat:<userId>:messages、ai_chat:<userId>:memory_id
 * 登录接入：登录成功后调用 setChatUser(用户ID)，退出登录调用 setChatUser(null)。
 * 当前登录页尚未实现，未登录时统一归入 guest 用户。
 */
const USER_KEY = 'ai_chat_user'
const GUEST_USER = 'guest'
const LEGACY_MESSAGES_KEY = 'ai_chat_messages'
const LEGACY_MEMORY_KEY = 'ai_chat_memory_id'

let migrated = false

export function setChatUser(userId) {
	if (userId === undefined || userId === null || userId === '') {
		localStorage.removeItem(USER_KEY)
	} else {
		localStorage.setItem(USER_KEY, String(userId))
	}
}

export function getChatUser() {
	return localStorage.getItem(USER_KEY) || GUEST_USER
}

function prefix() {
	return 'ai_chat:' + getChatUser()
}

function messagesKey() {
	return prefix() + ':messages'
}

function memoryKey() {
	return prefix() + ':memory_id'
}

/**
 * 旧版本用的是全局键，首次读取时迁移到 guest 名下，避免历史记录丢失。
 */
function migrateLegacy() {
	if (migrated) {
		return
	}
	migrated = true
	if (getChatUser() !== GUEST_USER) {
		return
	}
	try {
		const legacyMessages = localStorage.getItem(LEGACY_MESSAGES_KEY)
		if (legacyMessages && !localStorage.getItem(messagesKey())) {
			localStorage.setItem(messagesKey(), legacyMessages)
		}
		const legacyMemory = localStorage.getItem(LEGACY_MEMORY_KEY)
		if (legacyMemory && !localStorage.getItem(memoryKey())) {
			localStorage.setItem(memoryKey(), legacyMemory)
		}
		localStorage.removeItem(LEGACY_MESSAGES_KEY)
		localStorage.removeItem(LEGACY_MEMORY_KEY)
	} catch (error) {
		console.dir(error)
	}
}

export function loadMessages() {
	migrateLegacy()
	try {
		const saved = JSON.parse(localStorage.getItem(messagesKey()) || '[]')
		if (!Array.isArray(saved)) {
			return []
		}
		return saved.filter(item => item && item.role && typeof item.content === 'string')
	} catch (error) {
		console.dir(error)
		return []
	}
}

export function saveMessages(messages) {
	try {
		localStorage.setItem(messagesKey(), JSON.stringify(messages))
	} catch (error) {
		console.dir(error)
	}
}

export function loadMemoryId() {
	migrateLegacy()
	const key = memoryKey()
	let id = localStorage.getItem(key)
	if (!id) {
		id = getChatUser() + '-' + Date.now() + '-' + Math.random().toString(36).slice(2, 8)
		try {
			localStorage.setItem(key, id)
		} catch (error) {
			console.dir(error)
		}
	}
	return id
}

/**
 * 只清空当前用户的聊天记录，保留 memoryId，后端会话由 /sysChat/clear 负责清空。
 */
export function clearChatStorage() {
	try {
		localStorage.removeItem(messagesKey())
	} catch (error) {
		console.dir(error)
	}
}
