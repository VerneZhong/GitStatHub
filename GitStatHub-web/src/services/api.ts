import axios from 'axios'

// Spring Boot バックエンドアドレス
const baseURL =
    process.env.NODE_ENV === 'production'
        ? 'https://api.gitStatHub.com' // ✅ 本番環境のアドレス
        : 'http://localhost:8080'; // ✅ 開発環境アドレス

const api = axios.create({
    baseURL: baseURL, // APIのベースURLを設定
    timeout: 10000, // リクエストタイムアウトを10秒に設定
})

// interceptor
api.interceptors.request.use(config => {
    const token = localStorage.getItem('authToken')
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }
    return config
})

api.interceptors.response.use(
    response => response,
    error => {
        if (error.response && error.response.status === 403) {
            // 登录过期，清除 token 并跳转登录页
            localStorage.removeItem('authToken')
            window.location.href = '/login'
        }
        return Promise.reject(error)
    }
)

/**
 * リポジトリを取得する
 */
export const getRepos = async () => {
    try {
        const res = await api.get('/api/repo/repos')
        return res.data
    } catch (err) {
        if (axios.isAxiosError(err)) {
            console.error(`Failed to fetch repos:`, err.response?.data || err.message)
        } else {
            console.error('Failed to fetch repos:', err)
        }
        throw err
    }
}

export const queryRepos = async (username: string) => {
    try {
        const res = await api.get(`/api/repo/queryRepos/${username}`)
        return res.data
    } catch (err) {
        if (axios.isAxiosError(err)) {
            console.error(`queryRepos failed:`, err.response?.data || err.message)
        } else {
            console.error('queryRepos failed:', err)
        }
        throw err
    }
}

/**
 * ユーザーの貢献度を取得する
 * @param username
 */
export const getContributions = async (username: string) => {
    try {
        const res = await api.get(`/api/github/contributions/${username}`)
        return res.data
    } catch (err) {
        console.error(`Failed to fetch contributions for ${username}:`, err)
        throw err
    }
}

/**
 * 年度によるユーザーの貢献度を取得する
 * @param username
 * @param year
 */
export const getContributionsByYear = async (username: string, year: number) => {
    try {
        const res = await api.get('/api/github/contributionsByYear', {
            params: { username, year },
        })
        return res.data
    } catch (err) {
        if (axios.isAxiosError(err)) {
            console.error(`getContributionsByYear failed`, err.response?.data || err.message)
        } else {
            console.error('getContributionsByYear failed:', err)
        }
        throw err
    }
}

/**
 * ログインする
 * @param username
 * @param password
 */
export const login = async (username: string, password: string) => {
    try {
        const res = await api.post('/api/auth/login', { username, password })
        return res.data
    } catch (err) {
        if (axios.isAxiosError(err)) {
            console.error('login failed:', err.response?.data || err.message)
        } else {
            console.error('login failed:', err)
        }
        throw err
    }
}

/**
 * ログアウトする
 */
export const logout = async () => {
    try {
        const res = await api.post('/api/auth/logout')
        return res.data
    } catch (err) {
        if (axios.isAxiosError(err)) {
            console.error('logout failed:', err.response?.data || err.message)
        } else {
            console.error('logout failed:', err)
        }
        throw err
    }
}

/**
 * ログイン状態をチェックする
 */
export const checkLogin = async () => {
    try {
        const res = await api.get('/api/auth/check')
        return res.data // e.g. { loggedIn: true }
    } catch (err) {
        if (axios.isAxiosError(err)) {
            console.error('Check failed:', err.response?.data || err.message)
        } else {
            console.error('Check failed:', err)
        }
        throw err
    }
}

/**
 * 登録する
 */
export const register = async (username: string, password: string, email: string) => {
    try {
        const res = await api.post('/api/auth/register', {username, password, email})
        return res.data
    } catch (err) {
        if (axios.isAxiosError(err)) {
            console.error('Register failed:', err.response?.data || err.message)
        } else {
            console.error('Register failed:', err)
        }
        throw err
    }
}