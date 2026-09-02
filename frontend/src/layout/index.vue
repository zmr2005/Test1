<template>
  <el-container class="layout">
    <el-aside width="200px">
      <div class="logo">CRM 线索管控</div>
      <el-menu :default-active="$route.path" router>
        <el-menu-item index="/leads">
          <el-icon><Collection /></el-icon>
          <span>线索池</span>
        </el-menu-item>
        <el-menu-item index="/customers">
          <el-icon><User /></el-icon>
          <span>客户池</span>
        </el-menu-item>
        <el-menu-item index="/opportunities">
          <el-icon><TrendCharts /></el-icon>
          <span>商机管理</span>
        </el-menu-item>
        <el-menu-item index="/schedule">
          <el-icon><Calendar /></el-icon>
          <span>日程管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header>{{ $route.meta.title }}</el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { Calendar, Collection, TrendCharts, User } from '@element-plus/icons-vue'
</script>

<style scoped>
.layout {
  height: 100vh;
}
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
}
.el-header {
  display: flex;
  align-items: center;
  border-bottom: 1px solid var(--el-border-color-light);
  font-weight: 600;
}
.el-main {
  background: var(--el-fill-color-lighter);
}
</style>
