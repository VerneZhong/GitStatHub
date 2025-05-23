# 🚀 GitStatHub - GitHub プロジェクト統計・可視化ツール | GitHub Statistics & Visualization Tool

🌸 **GitStatHub** は、GitHub API を利用して、指定したユーザーの情報を統計・可視化する Web アプリケーションです。ポートフォリオとして公開可能で、ユーザーの技術力を見える化します。

🌸 **GitStatHub** is a web application that fetches and visualizes GitHub statistics for a given user. It provides clear insights into a user's development activity, ideal for portfolio presentation.

---

## 📷 Demo | デモ
> Demo準備中（後で Netlify や Vercel にデプロイリンクを貼り付け可能）  
> _Demo coming soon (deploy to Netlify/Vercel recommended)_

---

## ✨ Features | 主な機能

- 📊 GitHub のユーザー情報・リポジトリ情報を取得・表示  
  Fetch and display GitHub user and repository info

- 🧠 使用言語別のリポジトリ数やスター数をグラフ化  
  Visualize repo count & stars by programming language

- 📅 コミット数や貢献履歴の可視化（緑格子対応予定）  
  Display contribution timeline (GitHub-style heatmap in progress)

- 🧑‍💻 ポートフォリオ風プロフィールページの公開機能  
  Public portfolio-style GitHub profile presentation

---

## 🛠 Tech Stack | テクノロジースタック

| Category | 技術 |
|----------|------|
| Backend | Spring Boot (Java) |
| Frontend | Vue.js |
| Database | MySQL |
| Visualization | ECharts |
| API | GraphQL API |

---

## 📦 Project Structure | プロジェクト構成（例）

```bash
gitstathub/
├── GitStatHub-Backend/               # Spring Boot backend
│   └── src/
├── GitStatHub-web/                   # Vue frontend
│   └── src/
├── README.md
└── ...
```


🚧 現在は GitHub トークンを通じて「自分自身の情報」のみ取得可能です。  
🧭 今後、任意の GitHub ユーザー名を入力して統計できる機能を追加予定です。

🔍 Currently, the app can only access **your own GitHub data** via personal token.  
We plan to add support for analyzing **any public GitHub username** in the future.