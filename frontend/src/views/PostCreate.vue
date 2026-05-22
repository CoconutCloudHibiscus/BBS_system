<template>
  <div class="post-create">
    <h2>发帖</h2>
    <a-form layout="vertical">
      <a-form-item label="标题">
        <a-input v-model:value="form.title" placeholder="请输入标题" />
        <div v-if="errors.title" class="error-text">{{ errors.title }}</div>
      </a-form-item>
      <a-form-item label="内容">
        <a-textarea v-model:value="form.content" placeholder="请输入内容" :rows="8" />
        <div v-if="errors.content" class="error-text">{{ errors.content }}</div>
      </a-form-item>
      <a-form-item label="板块">
        <a-select v-model:value="form.boardId" placeholder="请选择板块" style="width: 200px;">
          <a-select-option v-for="board in boards" :key="board.id" :value="board.id">
            {{ board.name }}
          </a-select-option>
        </a-select>
        <div v-if="errors.boardId" class="error-text">{{ errors.boardId }}</div>
      </a-form-item>
      <a-form-item>
        <a-button type="primary" @click="handleSubmit" :loading="submitting">发布</a-button>
        <a-button style="margin-left: 12px;" @click="navStore.showPostList()">取消</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useNavigationStore } from '@/stores/navigation'
import { createPost } from '@/api/post'
import { getBoards } from '@/api/board'

const navStore = useNavigationStore()

const form = reactive({ title: '', content: '', boardId: null })
const errors = reactive({ title: '', content: '', boardId: '' })
const boards = ref([])
const submitting = ref(false)

async function fetchBoards() {
  try {
    const res = await getBoards()
    boards.value = res.data || []
  } catch (e) {
    // ignore
  }
}

function validate() {
  errors.title = ''
  errors.content = ''
  errors.boardId = ''
  let valid = true
  if (!form.title.trim()) {
    errors.title = '请输入标题'
    valid = false
  }
  if (!form.content.trim()) {
    errors.content = '请输入内容'
    valid = false
  }
  if (!form.boardId) {
    errors.boardId = '请选择板块'
    valid = false
  }
  return valid
}

async function handleSubmit() {
  if (!validate()) return
  submitting.value = true
  try {
    await createPost({
      title: form.title,
      content: form.content,
      boardId: form.boardId
    })
    navStore.showPostList()
  } catch (e) {
    // ignore
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchBoards()
})
</script>

<style scoped>
.post-create {
  max-width: 900px;
}

.post-create h2 {
  font-size: 20px;
  margin-bottom: 24px;
}

.error-text {
  color: #ff4d4f;
  font-size: 13px;
  margin-top: 4px;
}
</style>
