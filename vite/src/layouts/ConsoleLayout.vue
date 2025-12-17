<template>
  <a-layout class="layout">
    <a-layout-sider breakpoint="lg" collapsedWidth="0" class="sider" theme="light">
      <div class="logo">EasyVerify 控制台</div>
      <a-menu
        mode="inline"
        :selectedKeys="selectedKeys"
        :items="menuItems"
        @click="onMenuClick"
      />
    </a-layout-sider>

    <a-layout>
      <a-layout-header class="header">
        <div class="header-left">控制台</div>
        <div class="header-right">
          <a-space>
            <a-button type="text" @click="handleLogout">
              <LogoutOutlined />
              退出
            </a-button>
          </a-space>
        </div>
      </a-layout-header>

      <a-layout-content class="content">
        <router-view />
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup lang="ts">
import { computed, h } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { DashboardOutlined, LogoutOutlined } from '@ant-design/icons-vue'
import type { MenuProps } from 'ant-design-vue'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const menuItems = computed<MenuProps['items']>(() => [
  {
    key: '/dashboard',
    icon: () => h(DashboardOutlined),
    label: '总览',
  },
])

const selectedKeys = computed(() => [route.path])

const onMenuClick: MenuProps['onClick'] = ({ key }) => {
  if (key !== route.path) {
    router.push(key as string)
  }
}

const handleLogout = () => {
  authStore.clearToken()
  router.push('/login')
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
}

.sider {
  border-right: 1px solid #f0f0f0;
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 16px;
  color: #1677ff;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  padding: 0 24px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.06);
}

.content {
  margin: 16px;
  padding: 16px;
  min-height: 360px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.03);
}
</style>


