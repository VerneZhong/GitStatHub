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

router.beforeEach(async (to, from, next) => {
    const token = localStorage.getItem('authToken')
    if (to.meta.requiresAuth && !token) {
        next('/login')
    } else {
        next()
    }
})

export default router