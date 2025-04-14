# GitStatHub Web（フロントエンド）

このプロジェクトは、自分の GitHub プロフィールおよびリポジトリを可視化・表示する Vue フロントエンドアプリケーションです。  
バックエンド（Spring Boot）と連携して、GitHub API から取得した情報を表示します。

---

## 🔧 使用技術

- [Vue 3](https://vuejs.org/)
- [TypeScript](https://www.typescriptlang.org/)
- [Vite](https://vitejs.dev/)
- [Axios](https://axios-http.com/)

---

## 🚀 セットアップ手順

以下のコマンドでプロジェクトを作成し、開発サーバーを起動できます。

```bash
npm create vite@latest gitstathub-web -- --template vue-ts
cd gitstathub-web
npm install
npm install axios
npm run dev