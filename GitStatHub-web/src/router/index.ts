// src/router/index.ts
import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/Login.vue'
import Main from "../views/Main.vue";

const routes = [
    {
        path: '/',
        redirect: '/login' // 默认跳转到 login
    },
    {
        path: '/login',
        name: 'Login',
        component: Login
    },
    {
        path: '/app',
        name: 'Main',
        component: Main,
        meta: { requiresAuth: true } // 需要登录的页面
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 👇 可选：路由守卫，未登录跳转到 login（简单示例）
router.beforeEach((to, from, next) => {
    const isLoggedIn = !!localStorage.getItem('authToken')
    if (to.meta.requiresAuth && !isLoggedIn) {
        next('/login')
    } else {
        next()
    }
})

export default router