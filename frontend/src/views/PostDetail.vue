<template>
  <div class="post-detail" v-if="post">
    <h2 class="post-title">{{ post.title }}</h2>
    <div class="post-meta">
      <span v-if="post.authorDeleted" style="color:#ff4d4f;">已注销用户</span>
      <span v-else>{{ post.authorName }}</span>
      · {{ post.createdAt }}
    </div>
    <div class="post-content">{{ post.content }}</div>
    <div class="post-actions">
      <a-button v-if="canDelete" type="link" danger @click="handleDelete">删除</a-button>
      <a-button type="link" @click="navStore.showPostList()">返回</a-button>
    </div>

    <a-divider />

    <div class="comment-section">
      <h3>评论</h3>
      <div class="comment-list">
        <div v-for="comment in comments" :key="comment.id" class="comment-item">
          <span v-if="comment.authorDeleted" class="comment-author-deleted">已注销用户</span>
          <span>{{ comment.content }}</span>
        </div>
        <div v-if="comments.length === 0" class="empty-comments">暂无评论</div>
      </div>
      <div class="comment-input">
        <a-input
          v-model:value="newComment"
          placeholder="写评论..."
          @pressEnter="handleAddComment"
        />
        <a-button type="primary" @click="handleAddComment" style="margin-left: 8px;">发送</a-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useUserStore } from '@/stores/user'
import { useNavigationStore } from '@/stores/navigation'
import { getPostDetail, deletePost } from '@/api/post'
import { getComments, createComment } from '@/api/comment'

const userStore = useUserStore()
const navStore = useNavigationStore()

const post = ref(null)
const comments = ref([])
const newComment = ref('')

const canDelete = computed(() => {
  if (!post.value) return false
  return userStore.isAdmin || post.value.userId === userStore.userInfo?.id
})

async function fetchPost() {
  if (!navStore.selectedPostId) return
  try {
    const res = await getPostDetail(navStore.selectedPostId)
    post.value = res.data
  } catch (e) {
    console.error('获取帖子详情失败:', e)
  }
}

async function fetchComments() {
  if (!navStore.selectedPostId) return
  try {
    const res = await getComments(navStore.selectedPostId)
    comments.value = res.data || []
  } catch (e) {
    console.error('获取评论失败:', e)
  }
}

async function handleDelete() {
  try {
    await deletePost(navStore.selectedPostId)
    navStore.showPostList()
  } catch (e) {
    console.error('删除帖子失败:', e)
  }
}

async function handleAddComment() {
  if (!newComment.value.trim()) return
  const content = newComment.value.trim()
  newComment.value = ''
  try {
    await createComment(navStore.selectedPostId, { content })
    fetchComments()
  } catch (e) {
    console.error('评论失败:', e)
  }
}

watch(() => navStore.selectedPostId, () => {
  if (navStore.selectedPostId) {
    fetchPost()
    fetchComments()
  }
}, { immediate: true })
</script>

<style scoped>
.post-detail {
  max-width: 900px;
}

.post-title {
  font-size: 22px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.post-meta {
  font-size: 13px;
  color: #999;
  margin-bottom: 16px;
}

.post-content {
  font-size: 15px;
  line-height: 1.8;
  color: #333;
  white-space: pre-wrap;
  margin-bottom: 16px;
}

.post-actions {
  display: flex;
  gap: 8px;
}

.comment-section h3 {
  font-size: 16px;
  margin-bottom: 12px;
}

.comment-list {
  margin-bottom: 16px;
}

.comment-item {
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
  font-size: 14px;
  color: #333;
  line-height: 1.6;
}

.comment-author-deleted {
  color: #ff4d4f;
  font-size: 12px;
  margin-right: 8px;
}

.empty-comments {
  color: #999;
  font-size: 14px;
  padding: 16px 0;
}

.comment-input {
  display: flex;
  align-items: center;
}
</style>
