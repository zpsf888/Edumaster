<template>
  <div class="ai-support">
    <div class="header">
      <button class="back-btn" @click="goBack">
        返回课程
      </button>
      <h1>AI智能答疑</h1>
    </div>

    <div class="chat-container">
      <div class="chat-messages" ref="chatContainer">
        <div v-for="message in messages" :key="message.id" 
          :class="['message', message.type]">
          <div class="avatar">
            <img :src="message.type === 'ai' ? '/ai-avatar.png' : '/user-avatar.png'" 
              :alt="message.type === 'ai' ? 'AI助手' : '我'">
          </div>
          <div class="message-content">
            <div class="message-header">
              <span class="name">{{ message.type === 'ai' ? 'AI助手' : '我' }}</span>
              <span class="time">{{ message.time }}</span>
            </div>
            <div class="message-text">{{ message.content }}</div>
          </div>
        </div>
      </div>

      <div class="input-area">
        <div class="suggestion-chips" v-if="showSuggestions">
          <button 
            v-for="suggestion in suggestions" 
            :key="suggestion"
            class="suggestion-chip"
            @click="useQuestion(suggestion)"
          >
            {{ suggestion }}
          </button>
        </div>
        <div class="input-container">
          <textarea 
            v-model="userInput" 
            placeholder="输入你的问题..."
            @keyup.enter.prevent="sendMessage"
            rows="2"
          ></textarea>
          <button class="send-button" @click="sendMessage" :disabled="!userInput.trim()">
            <i class="fas fa-paper-plane"></i>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'

interface Message {
  id: number;
  type: 'user' | 'ai';
  content: string;
  time: string;
}

export default defineComponent({
  name: 'AISupport',
  setup() {
    const route = useRoute()
    const router = useRouter()
    const messages = ref<Message[]>([])
    const userInput = ref('')
    const chatContainer = ref<HTMLElement | null>(null)
    const showSuggestions = ref(true)

    const suggestions = [
      '这节课的重点内容是什么？',
      '如何理解组件化开发？',
      '课程中遇到的问题怎么解决？',
      '有没有相关的实战案例？'
    ]

    const goBack = () => {
      router.back()
    }

    const scrollToBottom = async () => {
      await nextTick()
      if (chatContainer.value) {
        chatContainer.value.scrollTop = chatContainer.value.scrollHeight
      }
    }

    const useQuestion = (question: string) => {
      userInput.value = question
      showSuggestions.value = false
    }

    const sendMessage = async () => {
      if (!userInput.value.trim()) return

      // 添加用户消息
      messages.value.push({
        id: Date.now(),
        type: 'user',
        content: userInput.value,
        time: new Date().toLocaleTimeString()
      })

      const userQuestion = userInput.value
      userInput.value = ''
      await scrollToBottom()

      // 模拟AI响应
      setTimeout(() => {
        messages.value.push({
          id: Date.now(),
          type: 'ai',
          content: generateAIResponse(userQuestion),
          time: new Date().toLocaleTimeString()
        })
        scrollToBottom()
      }, 1000)
    }

    const generateAIResponse = (question: string): string => {
      // 这里可以替换为实际的AI响应逻辑
      const responses = [
        '这是一个很好的问题！让我为您详细解答...',
        '根据课程内容，我建议您可以...',
        '这个问题的关键点在于...',
        '您可以参考以下示例...'
      ]
      return responses[Math.floor(Math.random() * responses.length)]
    }

    onMounted(() => {
      // 添加欢迎消息
      messages.value.push({
        id: Date.now(),
        type: 'ai',
        content: '你好！我是你的AI学习助手，有什么问题我可以帮你解答吗？',
        time: new Date().toLocaleTimeString()
      })
    })

    return {
      messages,
      userInput,
      chatContainer,
      showSuggestions,
      suggestions,
      goBack,
      sendMessage,
      useQuestion
    }
  }
})
</script>

<style scoped>
.ai-support {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  display: flex;
  align-items: center;
  gap: 2rem;
  margin-bottom: 2rem;
}

.header h1 {
  color: #2c5282;
  margin: 0;
}

.back-btn {
  padding: 0.8rem 1.2rem;
  background-color: #f7fafc;
  border-radius: 8px;
  color: #3182ce;
  cursor: pointer;
  transition: all 0.3s ease;
}

.back-btn:hover {
  background-color: #e6f3ff;
  transform: translateX(-5px);
}

.chat-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 2rem;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.message {
  display: flex;
  gap: 1rem;
  max-width: 80%;
}

.message.user {
  margin-left: auto;
  flex-direction: row-reverse;
}

.avatar img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.message-content {
  background-color: #f7fafc;
  padding: 1rem;
  border-radius: 12px;
  position: relative;
}

.message.user .message-content {
  background-color: #3182ce;
  color: white;
}

.message-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
}

.message.user .message-header {
  color: #e6f3ff;
}

.message-text {
  line-height: 1.6;
}

.input-area {
  padding: 1.5rem;
  border-top: 1px solid #e2e8f0;
  background-color: #ffffff;
}

.suggestion-chips {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
  flex-wrap: wrap;
}

.suggestion-chip {
  padding: 0.5rem 1rem;
  background-color: #e6f3ff;
  color: #3182ce;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
}

.suggestion-chip:hover {
  background-color: #3182ce;
  color: white;
}

.input-container {
  display: flex;
  gap: 1rem;
  align-items: flex-end;
}

textarea {
  flex: 1;
  padding: 0.8rem;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  resize: none;
  font-size: 1rem;
  line-height: 1.5;
}

.send-button {
  padding: 0.8rem 1.5rem;
  background-color: #3182ce;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.send-button:hover:not(:disabled) {
  background-color: #2c5282;
}

.send-button:disabled {
  background-color: #cbd5e0;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .ai-support {
    padding: 1rem;
  }

  .message {
    max-width: 90%;
  }

  .suggestion-chips {
    gap: 0.5rem;
  }

  .suggestion-chip {
    padding: 0.4rem 0.8rem;
    font-size: 0.8rem;
  }
}
</style> 