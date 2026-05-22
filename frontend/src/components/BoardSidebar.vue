<template>
  <div class="board-sidebar">
    <a-menu mode="inline" :selected-keys="selectedKeys" @click="handleMenuClick">
      <a-menu-item key="all">全部</a-menu-item>
      <a-menu-item v-for="board in boards" :key="String(board.id)">{{ board.name }}</a-menu-item>
    </a-menu>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useNavigationStore } from '@/stores/navigation'
import { getBoards } from '@/api/board'

const navStore = useNavigationStore()

const boards = ref([])

const selectedKeys = computed(() => {
  if (navStore.selectedBoardId) {
    return [String(navStore.selectedBoardId)]
  }
  return ['all']
})

function handleMenuClick({ key }) {
  if (key === 'all') {
    navStore.showPostList()
  } else {
    navStore.showPostList(Number(key))
  }
}

async function fetchBoards() {
  try {
    const res = await getBoards()
    boards.value = res.data || []
  } catch (e) {
    // ignore
  }
}

onMounted(() => {
  fetchBoards()
})
</script>

<style scoped>
.board-sidebar {
  padding-top: 8px;
}

.board-sidebar :deep(.ant-menu) {
  background: #f5f5f5;
  border-right: none;
}

.board-sidebar :deep(.ant-menu-item) {
  margin: 0;
}
</style>
