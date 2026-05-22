<template>
  <div class="post-list">
    <div v-if="posts.length === 0 && !loading" class="empty-state">暂无帖子</div>
    <a-list v-else :data-source="posts" item-layout="vertical">
      <template #renderItem="{ item }">
        <a-list-item class="post-item" @click="navStore.showPostDetail(item.id)">
          <a-list-item-meta>
            <template #title>
              <span class="post-title">{{ item.title }}</span>
            </template>
            <template #description>
              <span class="post-meta">
                <span v-if="item.authorDeleted" style="color:#ff4d4f;">已注销用户</span>
                <span v-else>{{ item.authorName }}</span>
                · {{ item.createdAt }}
              </span>
            </template>
          </a-list-item-meta>
          <div class="post-summary">{{ item.summary }}</div>
        </a-list-item>
      </template>
    </a-list>
    <div v-if="hasMore" style="text-align: center; margin-top: 16px;">
      <a-button @click="loadMore">加载更多</a-button>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useNavigationStore } from '@/stores/navigation'
import { getPosts } from '@/api/post'

const navStore = useNavigationStore()

const posts = ref([])
const page = ref(1)
const hasMore = ref(true)
const loading = ref(false)

async function fetchPosts() {
  loading.value = true
  try {
    const params = { page: page.value, size: 20 }
    if (navStore.selectedBoardId) {
      params.boardId = navStore.selectedBoardId
    }
    const res = await getPosts(params)
    const list = res.data?.posts || []
    if (page.value === 1) {
      posts.value = list
    } else {
      posts.value = [...posts.value, ...list]
    }
    hasMore.value = list.length >= 20
  } catch (e) {
    console.error('获取帖子失败:', e)
  } finally {
    loading.value = false
  }
}

function loadMore() {
  page.value++
  fetchPosts()
}

watch(() => navStore.selectedBoardId, () => {
  page.value = 1
  posts.value = []
  fetchPosts()
})

watch(() => navStore.currentView, (val) => {
  if (val === 'postList') {
    page.value = 1
    posts.value = []
    fetchPosts()
  }
})

onMounted(() => {
  fetchPosts()
})
</script>

<style scoped>
.post-list {
  max-width: 900px;
}

.post-item {
  cursor: pointer;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.post-item:hover {
  background: #fafafa;
}

.post-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.post-meta {
  font-size: 13px;
  color: #999;
}

.post-summary {
  color: #666;
  font-size: 14px;
  margin-top: 8px;
  line-height: 1.6;
}

.empty-state {
  min-height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 14px;
}
</style>
