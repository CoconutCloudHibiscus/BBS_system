<template>
  <div class="board-manage">
    <h2>板块管理</h2>

    <div class="board-form">
      <h3>{{ editingId ? '编辑板块' : '添加板块' }}</h3>
      <a-form layout="inline">
        <a-form-item>
          <a-input v-model:value="form.name" placeholder="板块名称" style="width: 200px;" />
        </a-form-item>
        <a-form-item>
          <a-input v-model:value="form.description" placeholder="板块描述" style="width: 300px;" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSave">保存</a-button>
          <a-button v-if="editingId" style="margin-left: 8px;" @click="cancelEdit">取消</a-button>
        </a-form-item>
      </a-form>
      <div v-if="formError" class="error-text" style="margin-top: 8px;">{{ formError }}</div>
    </div>

    <a-divider />

    <a-table :columns="columns" :data-source="boards" :pagination="false" row-key="id">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'action'">
          <a-button type="link" @click="startEdit(record)">编辑</a-button>
          <a-button type="link" danger @click="confirmDelete(record)">删除</a-button>
        </template>
      </template>
    </a-table>

    <a-modal v-model:open="deleteModalVisible" title="确认删除" @ok="handleDelete" ok-text="确认" cancel-text="取消">
      <p>删除板块将同时删除该板块下所有帖子，确定要删除吗？</p>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getBoards, createBoard, updateBoard, deleteBoard } from '@/api/board'

const boards = ref([])
const form = reactive({ name: '', description: '' })
const formError = ref('')
const editingId = ref(null)
const deleteModalVisible = ref(false)
const deletingId = ref(null)

const columns = [
  { title: '名称', dataIndex: 'name', key: 'name' },
  { title: '描述', dataIndex: 'description', key: 'description' },
  { title: '操作', key: 'action', width: 180 }
]

async function fetchBoards() {
  try {
    const res = await getBoards()
    boards.value = res.data || []
  } catch (e) {
    // ignore
  }
}

function startEdit(record) {
  editingId.value = record.id
  form.name = record.name
  form.description = record.description
  formError.value = ''
}

function cancelEdit() {
  editingId.value = null
  form.name = ''
  form.description = ''
  formError.value = ''
}

async function handleSave() {
  if (!form.name.trim()) {
    formError.value = '请输入板块名称'
    return
  }
  try {
    if (editingId.value) {
      await updateBoard(editingId.value, { name: form.name, description: form.description })
    } else {
      await createBoard({ name: form.name, description: form.description })
    }
    cancelEdit()
    fetchBoards()
  } catch (e) {
    formError.value = e.message || '操作失败'
  }
}

function confirmDelete(record) {
  deletingId.value = record.id
  deleteModalVisible.value = true
}

async function handleDelete() {
  try {
    await deleteBoard(deletingId.value)
    fetchBoards()
  } catch (e) {
    // ignore
  }
  deleteModalVisible.value = false
}

onMounted(() => {
  fetchBoards()
})
</script>

<style scoped>
.board-manage {
  max-width: 900px;
}

.board-manage h2 {
  font-size: 20px;
  margin-bottom: 16px;
}

.board-form {
  padding: 16px;
  background: #fafafa;
  border-radius: 6px;
  margin-bottom: 8px;
}

.board-form h3 {
  font-size: 15px;
  margin-bottom: 12px;
}

.error-text {
  color: #ff4d4f;
  font-size: 13px;
}
</style>
