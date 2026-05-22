<template>
  <a-layout class="main-layout">
    <a-layout-header class="main-header">
      <div class="header-left">企业BBS系统</div>
      <div class="header-right">
        <span v-if="userStore.isAdmin" class="header-link" @click="navStore.showBoardManage()">板块管理</span>
        <span class="header-link" @click="navStore.showPostCreate()">发帖</span>
        <a-popover v-model:open="nicknameVisible" trigger="click" placement="bottom">
          <template #content>
            <div style="display: flex; gap: 8px; align-items: center;">
              <a-input v-model:value="newNickname" size="small" style="width: 150px;" />
              <a-button size="small" type="primary" @click="handleUpdateNickname">确认</a-button>
            </div>
            <div v-if="nicknameError" class="error-text" style="margin-top: 4px;">{{ nicknameError }}</div>
          </template>
          <span class="header-link">{{ userStore.userInfo?.nickname || '用户' }}</span>
        </a-popover>
        <span class="header-link" @click="handleLogout">注销</span>
        <span class="header-link header-link-danger" @click="showDeleteModal = true">删除账户</span>
      </div>
    </a-layout-header>
    <a-layout>
      <a-layout-sider class="main-sider" :width="220">
        <BoardSidebar />
      </a-layout-sider>
      <a-layout-content class="main-content">
        <PostList v-if="navStore.currentView === 'postList'" />
        <PostDetail v-else-if="navStore.currentView === 'postDetail'" />
        <PostCreate v-else-if="navStore.currentView === 'postCreate'" />
        <BoardManage v-else-if="navStore.currentView === 'boardManage'" />
      </a-layout-content>
    </a-layout>
    <a-modal v-model:open="showDeleteModal" title="确认删除" @ok="handleDeleteAccount" ok-text="确认" cancel-text="取消">
      <p>确定要删除账户吗？此操作不可恢复。</p>
    </a-modal>
  </a-layout>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useNavigationStore } from '@/stores/navigation'
import { logout as logoutApi } from '@/api/auth'
import { deleteAccount, updateNickname } from '@/api/user'
import BoardSidebar from '@/components/BoardSidebar.vue'
import PostList from '@/views/PostList.vue'
import PostDetail from '@/views/PostDetail.vue'
import PostCreate from '@/views/PostCreate.vue'
import BoardManage from '@/views/BoardManage.vue'

const router = useRouter()
const userStore = useUserStore()
const navStore = useNavigationStore()

const nicknameVisible = ref(false)
const newNickname = ref('')
const nicknameError = ref('')
const showDeleteModal = ref(false)

function handleUpdateNickname() {
  if (!newNickname.value.trim()) {
    nicknameError.value = '昵称不能为空'
    return
  }
  updateNickname({ nickname: newNickname.value.trim() }).then(() => {
    userStore.updateNickname(newNickname.value.trim())
    nicknameVisible.value = false
    nicknameError.value = ''
    newNickname.value = ''
  }).catch(e => {
    nicknameError.value = e.message || '修改失败'
  })
}

function handleLogout() {
  logoutApi().catch(() => {})
  userStore.logout()
  router.push('/login')
}

async function handleDeleteAccount() {
  try {
    await deleteAccount()
  } catch (e) {
    // ignore
  }
  userStore.logout()
  router.push('/login')
  showDeleteModal.value = false
}
</script>

<style scoped>
.main-layout {
  min-height: 100vh;
}

.main-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #001529;
  padding: 0 24px;
  height: 56px;
  line-height: 56px;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
}

.header-left {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-link {
  color: #fff;
  font-size: 14px;
  cursor: pointer;
  white-space: nowrap;
}

.header-link:hover {
  color: #40a9ff;
}

.header-link-danger:hover {
  color: #ff4d4f;
}

.main-sider {
  background: #f5f5f5;
  margin-top: 56px;
  position: fixed;
  left: 0;
  top: 56px;
  bottom: 0;
  overflow-y: auto;
}

.main-content {
  margin-top: 56px;
  margin-left: 220px;
  padding: 24px;
  background: #fff;
  min-height: calc(100vh - 56px);
}

.error-text {
  color: #ff4d4f;
  font-size: 13px;
}
</style>
