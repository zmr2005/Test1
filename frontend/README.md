# CRM 前端（Vue 3 + Vite + TS + Element Plus）

## 目录结构

```
frontend/
├── index.html
├── package.json
├── vite.config.ts            # 别名 @ -> src，/api 代理到后端
├── tsconfig.json
├── .env.development          # VITE_API_BASE
└── src/
    ├── main.ts               # 入口（Pinia、Router、Element Plus、图标注册）
    ├── App.vue
    ├── router/
    │   └── index.ts          # 路由表（含 4 大模块）
    ├── api/
    │   ├── request.ts        # axios 封装（拦截器）
    │   └── lead.ts           # 线索接口示例
    ├── types/
    │   └── index.ts          # 类型与枚举常量
    ├── layout/
    │   └── index.vue         # 侧边栏 + 顶部 + 内容区
    └── views/
        ├── lead/LeadList.vue         # 线索列表（完整范式：查询/分页/新建/转化）
        ├── customer/CustomerList.vue
        ├── opportunity/OpportunityBoard.vue
        └── schedule/Schedule.vue
```

## 快速启动

```powershell
cd frontend
npm install
npm run dev   # http://localhost:5173
```

## 说明

- `/api` 请求由 Vite 代理到 `http://localhost:8000`（见 `vite.config.ts`）。
- 页面路由懒加载，`LeadList.vue` 为业务页面范式，其余页面为占位，可按同样模式补齐。
- 鉴权：`request.ts` 已预留 `Authorization` 头注入，登录后写入 `localStorage.token` 即可。
