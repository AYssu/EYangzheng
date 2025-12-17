<template>
  <div class="login-wrapper">
    <a-card class="login-card" :bordered="false">
      <div class="title">管理员登录</div>
      <a-form layout="vertical" :model="formState" @submit.prevent="handleSubmit">
        <a-form-item label="用户名" name="loginName" :rules="[{ required: true, message: '请输入用户名' }]">
          <a-input
            v-model:value="formState.loginName"
            size="large"
            placeholder="请输入管理员用户名"
            :prefix="h(UserOutlined)"
          />
        </a-form-item>
        <a-form-item label="密码" name="password" :rules="[{ required: true, message: '请输入密码' }]">
          <a-input-password
            v-model:value="formState.password"
            size="large"
            placeholder="请输入密码"
            :prefix="h(LockOutlined)"
          />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit" block size="large" :loading="loading">
            登录
          </a-button>
        </a-form-item>
      </a-form>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, h } from 'vue'
import { message } from 'ant-design-vue'
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'
import { useRouter } from 'vue-router'
import { login } from '../api/admin'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)

const formState = reactive({
  loginName: '',
  password: '',
})

const handleSubmit = async () => {
  if (!formState.loginName || !formState.password) {
    message.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const { data } = await login({
      loginName: formState.loginName,
      password: formState.password,
    })
    const token = data?.data
    if (data?.code === 200 && token) {
      authStore.setToken(token)
      message.success('登录成功')
      router.push('/')
    } else {
      message.error(data?.message || '用户名或密码错误')
    }
  } catch (err) {
    // 错误提示已在拦截器中处理
    console.error(err)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-wrapper {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f0f5ff, #f9f0ff);
  padding: 24px;
}

.login-card {
  width: 360px;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.08);
  border-radius: 12px;
}

.title {
  font-size: 22px;
  font-weight: 600;
  margin-bottom: 16px;
  text-align: center;
}
</style>

