import axios from 'axios'

const api = axios.create({
    baseURL: 'http://localhost:8080', // Spring Boot 后端地址
})

export const getRepos = async () => {
    const res = await api.get('/api/repo/repos')
    return res.data
}

export const getContributionCalendar = (username: string) => {
    return api.get('/api/github/contributions', {
        params: { username }
    })
}

export const getContributions = async (username: string) => {
    const res = await api.get(`/api/github/contributions/${username}`)
    return res.data
}