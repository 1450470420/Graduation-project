<template>
  <div class="login-container">
    <!-- 左侧装饰区域 -->
    <div class="login-left">
      <!-- 使用网络真实校园图片作为背景 -->
      <div class="left-content">
        <div class="brand-logo">🏃</div>
        <h1 class="brand-title">校园跑腿服务平台</h1>
        <p class="brand-desc">智能化校园跑腿管理系统<br/>让校园生活更便捷</p>
        <div class="features">
          <div class="feature-item">
            <span class="feature-icon">📦</span>
            <span>代取快递 · 高效快速</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">🛒</span>
            <span>代买服务 · 省心省力</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">🚀</span>
            <span>代送物品 · 贴心便捷</span>
          </div>
        </div>
        <div class="stats-row">
          <div class="stat-item">
            <div class="stat-num">1000+</div>
            <div class="stat-label">注册用户</div>
          </div>
          <div class="stat-item">
            <div class="stat-num">5000+</div>
            <div class="stat-label">完成订单</div>
          </div>
          <div class="stat-item">
            <div class="stat-num">98%</div>
            <div class="stat-label">好评率</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧登录表单区域 -->
    <div class="login-right">
      <div class="login-box">
        <div class="login-header">
          <h2>管理员登录</h2>
          <p>欢迎回来，请登录您的账号</p>
        </div>

        <el-form 
          ref="loginFormRef" 
          :model="loginForm" 
          :rules="rules" 
          class="login-form"
          @keyup.enter="handleLogin"
        >
          <el-form-item prop="username">
            <el-input 
              v-model="loginForm.username" 
              placeholder="请输入用户名"
              size="large"
              clearable
            >
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="password">
            <el-input 
              v-model="loginForm.password" 
              type="password"
              placeholder="请输入密码"
              size="large"
              show-password
              clearable
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item>
            <el-button 
              type="primary" 
              size="large" 
              class="login-btn"
              :loading="loading"
              @click="handleLogin"
            >
              {{ loading ? '登录中...' : '立即登录' }}
            </el-button>
          </el-form-item>
        </el-form>

        <div class="login-tips">
          <p>默认账号：admin / 123456</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { adminApi } from '@/api/index.js'

const router = useRouter()
const loginFormRef = ref()
const loading = ref(false)

// 登录表单数据
const loginForm = reactive({
  username: '',
  password: ''
})

// 表单验证规则
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// 处理登录
const handleLogin = async () => {
  try {
    await loginFormRef.value.validate()
    loading.value = true
    const res = await adminApi.login(loginForm)
    // 保存管理员信息到sessionStorage
    sessionStorage.setItem('adminInfo', JSON.stringify(res.data))
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch (e) {
    // 错误已在拦截器中处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

/* 左侧装饰区 */
.login-left {
  flex: 1;
  background: linear-gradient(135deg, #8B1A1A 0%, #C0392B 50%, #E74C3C 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.login-left::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: url('https://images.unsplash.com/photo-1562774053-701939374585?w=1200&auto=format&fit=crop') center/cover;
  opacity: 0.15;
}

.left-content {
  position: relative;
  z-index: 1;
  text-align: center;
  color: #fff;
  padding: 40px;
}

.brand-logo {
  font-size: 72px;
  margin-bottom: 20px;
  filter: drop-shadow(0 4px 8px rgba(0,0,0,0.3));
}

.brand-title {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 16px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.3);
}

.brand-desc {
  font-size: 16px;
  opacity: 0.9;
  line-height: 1.8;
  margin-bottom: 40px;
}

.features {
  display: flex;
  flex-direction: column;
  gap: 14px;
  margin-bottom: 40px;
  text-align: left;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  background: rgba(255,255,255,0.15);
  backdrop-filter: blur(10px);
  padding: 12px 20px;
  border-radius: 50px;
  font-size: 15px;
}

.feature-icon {
  font-size: 22px;
}

.stats-row {
  display: flex;
  justify-content: space-around;
  gap: 20px;
}

.stat-item {
  text-align: center;
}

.stat-num {
  font-size: 28px;
  font-weight: 700;
}

.stat-label {
  font-size: 13px;
  opacity: 0.8;
  margin-top: 4px;
}

/* 右侧登录表单 */
.login-right {
  width: 480px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fafafa;
}

.login-box {
  width: 380px;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-header h2 {
  font-size: 28px;
  color: #2c2c2c;
  font-weight: 700;
  margin-bottom: 8px;
}

.login-header p {
  color: #888;
  font-size: 14px;
}

.login-form {
  margin-bottom: 20px;
}

.login-form .el-form-item {
  margin-bottom: 20px;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 10px;
  padding: 4px 12px;
}

.login-form :deep(.el-input__wrapper:hover),
.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #C0392B inset;
}

.login-btn {
  width: 100%;
  height: 48px;
  border-radius: 10px;
  font-size: 16px;
  background: linear-gradient(135deg, #C0392B, #E74C3C);
  border: none;
  letter-spacing: 2px;
}

.login-btn:hover {
  background: linear-gradient(135deg, #A93226, #C0392B);
  transform: translateY(-1px);
  box-shadow: 0 4px 15px rgba(192,57,43,0.4);
}

.login-tips {
  text-align: center;
  color: #aaa;
  font-size: 13px;
  padding: 12px;
  background: #f5f5f5;
  border-radius: 8px;
}

@media (max-width: 768px) {
  .login-left { display: none; }
  .login-right { width: 100%; }
}
</style>
