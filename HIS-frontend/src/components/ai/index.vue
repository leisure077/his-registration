<template>
	<div class="ai-chat">
		<transition name="ai-panel">
			<section v-if="open" class="ai-panel">
				<header class="ai-header">
					<div class="ai-title">
						<span class="ai-logo">AI</span>
						<div>
							<h3>智能就医助手</h3>
							<p>科室推荐 · 医生查询 · 挂号记录</p>
						</div>
					</div>
					<div class="ai-actions">
						<button type="button" @click="clear">清空</button>
						<button type="button" class="ai-close" @click="open = false">×</button>
					</div>
				</header>

				<div ref="listRef" class="ai-messages">
					<div v-for="(item, index) in messages" :key="index" class="ai-row" :class="item.role">
						<div v-if="item.content" class="ai-bubble">{{ item.content }}</div>
					</div>
					<div v-if="waiting" class="ai-row assistant">
						<div class="ai-bubble ai-typing"><i></i><i></i><i></i></div>
					</div>
					<div v-if="messages.length === 1 && !sending" class="ai-quick">
						<button v-for="question in quickQuestions" :key="question" type="button" @click="ask(question)">
							{{ question }}
						</button>
					</div>
				</div>

				<footer class="ai-input">
					<textarea
						v-model="input"
						rows="2"
						placeholder="描述症状或提问，Enter 发送，Shift + Enter 换行"
						@keydown="onKeydown"
					></textarea>
					<button type="button" class="ai-send" :disabled="sending || !input.trim()" @click="send">
						发送
					</button>
				</footer>
			</section>
		</transition>

		<button type="button" class="ai-fab" :class="{ active: open }" @click="open = !open">
			{{ open ? '×' : 'AI' }}
		</button>
	</div>
</template>

<script setup>
import { computed, nextTick, ref, watch } from 'vue'
import { _axios } from '@/util/axios'
import { clearChatStorage, loadMemoryId, loadMessages, saveMessages } from '@/util/aiStorage'

const open = ref(false)
const input = ref('')
const sending = ref(false)
const listRef = ref(null)

const WELCOME = '您好，我是智能就医助手。可以帮您推荐科室、查询医生和挂号费、查询挂号记录。'

const messages = ref(initMessages())
const memoryId = loadMemoryId()

// 消息变化时写入当前用户的本地存储
watch(messages, value => saveMessages(value), { deep: true })

function initMessages() {
	const saved = loadMessages()
	return saved.length ? saved : [{ role: 'assistant', content: WELCOME }]
}

// 等待首个 token 时显示打字动画，开始输出后由气泡内容接管
const waiting = computed(() => {
	if (!sending.value) {
		return false
	}
	const last = messages.value[messages.value.length - 1]
	return !last || !last.content
})

const quickQuestions = [
	'我头痛挂什么科？',
	'心内科有哪些医生？',
	'怎么查询挂号记录？',
	'挂号费是怎么算的？'
]

function scrollToBottom() {
	nextTick(() => {
		if (listRef.value) {
			listRef.value.scrollTop = listRef.value.scrollHeight
		}
	})
}

function formOf(data) {
	const form = new FormData()
	Object.keys(data).forEach(key => form.append(key, data[key]))
	return form
}

async function send() {
	const text = input.value.trim()
	if (!text || sending.value) {
		return
	}
	messages.value.push({ role: 'user', content: text })
	messages.value.push({ role: 'assistant', content: '' })
	const reply = messages.value[messages.value.length - 1]
	input.value = ''
	sending.value = true
	scrollToBottom()

	let failed = false
	try {
		const response = await fetch(streamUrl(), {
			method: 'POST',
			body: formOf({ memoryId, message: text })
		})
		if (!response.ok || !response.body) {
			throw new Error('HTTP ' + response.status)
		}
		const reader = response.body.getReader()
		const decoder = new TextDecoder()
		const separator = /\r?\n\r?\n/
		let buffer = ''
		while (true) {
			const { value, done } = await reader.read()
			if (done) {
				break
			}
			buffer += decoder.decode(value, { stream: true })
			let matched = separator.exec(buffer)
			while (matched) {
				failed = handleEvent(buffer.slice(0, matched.index), reply) || failed
				buffer = buffer.slice(matched.index + matched[0].length)
				matched = separator.exec(buffer)
			}
		}
		if (!failed && !reply.content) {
			reply.content = '抱歉，没有收到回答，请稍后重试。'
		}
	} catch (error) {
		console.dir(error)
		if (!reply.content) {
			reply.content = '网络异常或 AI 服务超时，请稍后重试。'
		}
	} finally {
		sending.value = false
		scrollToBottom()
	}
}

// 解析单个 SSE 事件，返回是否已收到后端错误提示
function handleEvent(event, reply) {
	const data = event
		.split('\n')
		.filter(line => line.startsWith('data:'))
		.map(line => line.slice(5).trim())
		.join('')
	if (!data) {
		return false
	}
	let payload
	try {
		payload = JSON.parse(data)
	} catch (error) {
		return false
	}
	if (payload.content) {
		reply.content += payload.content
		scrollToBottom()
		return false
	}
	if (payload.error) {
		reply.content = '抱歉，' + payload.error
		return true
	}
	return false
}

function streamUrl() {
	return (_axios.defaults.baseURL || '') + '/sysChat/stream'
}

function ask(question) {
	if (sending.value) {
		return
	}
	input.value = question
	send()
}

async function clear() {
	try {
		await _axios.post('/sysChat/clear', formOf({ memoryId }), { timeout: 15000 })
	} catch (error) {
		console.dir(error)
	}
	messages.value = [{ role: 'assistant', content: '会话已清空，可以开始新的提问。' }]
	clearChatStorage()
}

function onKeydown(event) {
	if (event.key === 'Enter' && !event.shiftKey && !event.isComposing) {
		event.preventDefault()
		send()
	}
}
</script>

<style scoped>
.ai-chat {
	position: fixed;
	right: 28px;
	bottom: 28px;
	z-index: 3000;
}

.ai-fab {
	width: 56px;
	height: 56px;
	border: none;
	border-radius: 50%;
	background: linear-gradient(135deg, #1d4ed8, #2563eb 55%, #22d3ee);
	color: #fff;
	font-size: 16px;
	font-weight: 700;
	cursor: pointer;
	box-shadow: 0 10px 26px rgba(37, 99, 235, 0.35);
	transition: transform 0.2s;
}

.ai-fab:hover {
	transform: translateY(-2px) scale(1.03);
}

.ai-fab.active {
	font-size: 24px;
	font-weight: 400;
}

.ai-panel {
	position: absolute;
	right: 0;
	bottom: 72px;
	display: flex;
	flex-direction: column;
	width: 370px;
	height: 520px;
	max-height: calc(100vh - 140px);
	overflow: hidden;
	background: rgba(255, 255, 255, 0.94);
	border: 1px solid rgba(255, 255, 255, 0.8);
	border-radius: 18px;
	box-shadow: 0 22px 56px rgba(20, 40, 90, 0.24);
	backdrop-filter: blur(20px);
}

.ai-header {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 14px 16px;
	color: #fff;
	background: linear-gradient(120deg, #1d4ed8, #2563eb 60%, #22d3ee);
}

.ai-title {
	display: flex;
	align-items: center;
	gap: 10px;
}

.ai-logo {
	display: flex;
	align-items: center;
	justify-content: center;
	width: 34px;
	height: 34px;
	border-radius: 50%;
	background: rgba(255, 255, 255, 0.22);
	font-size: 13px;
	font-weight: 700;
}

.ai-title h3 {
	margin: 0;
	font-size: 15px;
	font-weight: 600;
}

.ai-title p {
	margin: 2px 0 0;
	font-size: 11px;
	opacity: 0.85;
}

.ai-actions {
	display: flex;
	align-items: center;
	gap: 6px;
}

.ai-actions button {
	padding: 3px 8px;
	border: none;
	border-radius: 6px;
	background: rgba(255, 255, 255, 0.2);
	color: #fff;
	font-size: 12px;
	cursor: pointer;
}

.ai-actions button:hover {
	background: rgba(255, 255, 255, 0.32);
}

.ai-close {
	font-size: 16px !important;
	line-height: 1;
}

.ai-messages {
	flex: 1;
	display: flex;
	flex-direction: column;
	gap: 10px;
	padding: 14px;
	overflow-y: auto;
	background: #f4f8ff;
}

.ai-row {
	display: flex;
}

.ai-row.user {
	justify-content: flex-end;
}

.ai-bubble {
	max-width: 82%;
	padding: 9px 12px;
	border-radius: 12px;
	background: #fff;
	color: #303133;
	font-size: 13px;
	line-height: 1.65;
	white-space: pre-wrap;
	word-break: break-word;
	box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
	border-bottom-left-radius: 4px;
}

.ai-row.user .ai-bubble {
	background: #2563eb;
	color: #fff;
	border-bottom-left-radius: 12px;
	border-bottom-right-radius: 4px;
}

.ai-typing {
	display: flex;
	gap: 4px;
	padding: 13px 14px;
}

.ai-typing i {
	width: 6px;
	height: 6px;
	border-radius: 50%;
	background: #9db4d4;
	animation: ai-blink 1.2s infinite ease-in-out;
}

.ai-typing i:nth-child(2) {
	animation-delay: 0.2s;
}

.ai-typing i:nth-child(3) {
	animation-delay: 0.4s;
}

@keyframes ai-blink {
	0%,
	80%,
	100% {
		opacity: 0.3;
		transform: translateY(0);
	}
	40% {
		opacity: 1;
		transform: translateY(-3px);
	}
}

.ai-quick {
	display: flex;
	flex-wrap: wrap;
	gap: 8px;
	padding: 2px;
}

.ai-quick button {
	padding: 6px 10px;
	border: 1px solid #cfe0f8;
	border-radius: 999px;
	background: #fff;
	color: #2563eb;
	font-size: 12px;
	cursor: pointer;
	transition: all 0.15s;
}

.ai-quick button:hover {
	border-color: #2563eb;
	background: #2563eb;
	color: #fff;
}

.ai-input {
	display: flex;
	align-items: flex-end;
	gap: 8px;
	padding: 10px 12px;
	border-top: 1px solid #eef1f6;
	background: #fff;
}

.ai-input textarea {
	flex: 1;
	max-height: 92px;
	padding: 8px 10px;
	border: 1px solid #dfe5ee;
	border-radius: 10px;
	outline: none;
	resize: none;
	font-family: inherit;
	font-size: 13px;
	line-height: 1.5;
}

.ai-input textarea:focus {
	border-color: #2563eb;
}

.ai-send {
	height: 38px;
	padding: 0 14px;
	border: none;
	border-radius: 10px;
	background: #2563eb;
	color: #fff;
	font-size: 13px;
	cursor: pointer;
}

.ai-send:hover:not(:disabled) {
	background: #1f6ae0;
}

.ai-send:disabled {
	cursor: not-allowed;
	opacity: 0.5;
}

.ai-panel-enter-active,
.ai-panel-leave-active {
	transition:
		opacity 0.2s,
		transform 0.2s;
	transform-origin: bottom right;
}

.ai-panel-enter-from,
.ai-panel-leave-to {
	opacity: 0;
	transform: scale(0.92) translateY(8px);
}
</style>
