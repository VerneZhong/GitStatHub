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

// Optional unified interceptor
api.interceptors.response.use(
    res => res,
    err => {
        console.error('API Error:', err)
        return Promise.reject(err)
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
        console.error('Failed to fetch repos:', err)
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
        console.error(`Failed to fetch contributions for ${username} in ${year}:`, err)
        throw err
    }
}