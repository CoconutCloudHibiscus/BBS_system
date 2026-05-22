<template>
  <div class="login-container">
    <div class="login-card">
      <h2 class="login-title">企业BBS系统</h2>
      <a-tabs v-model:activeKey="activeTab" centered>
        <a-tab-pane key="login" tab="登录">
          <a-form layout="vertical">
            <a-form-item label="账号">
              <a-input v-model:value="loginForm.username" placeholder="请输入账号" @pressEnter="handleLogin" />
              <div v-if="loginErrors.username" class="error-text">{{ loginErrors.username }}</div>
            </a-form-item>
            <a-form-item label="密码">
              <a-input-password v-model:value="loginForm.password" placeholder="请输入密码" @pressEnter="handleLogin" />
              <div v-if="loginErrors.password" class="error-text">{{ loginErrors.password }}</div>
            </a-form-item>
            <div v-if="loginErrors.general" class="error-text" style="margin-bottom: 16px;">{{ loginErrors.general }}</div>
            <a-form-item>
              <a-button type="primary" block :loading="loginLoading" @click="handleLogin">登录</a-button>
            </a-form-item>
          </a-form>
        </a-tab-pane>
        <a-tab-pane key="register" tab="注册">
          <a-form layout="vertical">
            <a-form-item label="账号">
              <a-input v-model:value="registerForm.username" placeholder="请输入账号" />
              <div v-if="registerErrors.username" class="error-text">{{ registerErrors.username }}</div>
            </a-form-item>
            <a-form-item label="密码">
              <a-input-password v-model:value="registerForm.password" placeholder="请输入密码" />
              <div v-if="registerErrors.password" class="error-text">{{ registerErrors.password }}</div>
            </a-form-item>
            <a-form-item label="昵称">
              <a-input v-model:value="registerForm.nickname" placeholder="请输入昵称" />
              <div v-if="registerErrors.nickname" class="error-text">{{ registerErrors.nickname }}</div>
            </a-form-item>
            <div v-if="registerErrors.general" class="error-text" style="margin-bottom: 16px;">{{ registerErrors.general }}</div>
            <a-form-item>
              <a-button type="primary" block :loading="registerLoading" @click="handleRegister">注册</a-button>
            </a-form-item>
          </a-form>
        </a-tab-pane>
      </a-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useNavigationStore } from '@/stores/navigation'
import { login as loginApi, register as registerApi } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()
const navStore = useNavigationStore()

const activeTab = ref('login')
const loginLoading = ref(false)
const registerLoading = ref(false)

const loginForm = reactive({ username: '', password: '' })
const loginErrors = reactive({ username: '', password: '', general: '' })

const registerForm = reactive({ username: '', password: '', nickname: '' })
const registerErrors = reactive({ username: '', password: '', nickname: '', general: '' })

function validateLogin() {
  loginErrors.username = ''
  loginErrors.password = ''
  loginErrors.general = ''
  let valid = true
  if (!loginForm.username.trim()) {
    loginErrors.username = '请输入账号'
    valid = false
  }
  if (!loginForm.password.trim()) {
    loginErrors.password = '请输入密码'
    valid = false
  }
  return valid
}

function validateRegister() {
  registerErrors.username = ''
  registerErrors.password = ''
  registerErrors.nickname = ''
  registerErrors.general = ''
  let valid = true
  if (!registerForm.username.trim()) {
    registerErrors.username = '请输入账号'
    valid = false
  }
  if (!registerForm.password.trim()) {
    registerErrors.password = '请输入密码'
    valid = false
  }
  if (!registerForm.nickname.trim()) {
    registerErrors.nickname = '请输入昵称'
    valid = false
  }
  return valid
}

async function handleLogin() {
  if (!validateLogin()) return
  loginLoading.value = true
  try {
    const res = await loginApi({
      account: loginForm.username,
      password: loginForm.password
    })
    userStore.login(res.data.token, res.data.user)
    navStore.showPostList()
    router.push('/main')
  } catch (e) {
    loginErrors.general = e.message || '登录失败'
  } finally {
    loginLoading.value = false
  }
}

async function handleRegister() {
  if (!validateRegister()) return
  registerLoading.value = true
  try {
    const res = await registerApi({
      account: registerForm.username,
      password: registerForm.password,
      nickname: registerForm.nickname
    })
    userStore.login(res.data.token, res.data.user)
    navStore.showPostList()
    router.push('/main')
  } catch (e) {
    registerErrors.general = e.message || '注册失败'
  } finally {
    registerLoading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: #f0f2f5;
}

.login-card {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.login-title {
  text-align: center;
  margin-bottom: 24px;
  font-size: 24px;
  color: #333;
}

.error-text {
  color: #ff4d4f;
  font-size: 13px;
  margin-top: 4px;
}
</style>
