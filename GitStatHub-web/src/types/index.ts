// src/types/index.ts
export interface RepoInfo {
    id: number
    name: string
    description: string
    language: string
    stargazersCount: number
    updatedAt: string
}

export interface UserInfo {
    name: string
    avatarUrl: string
}