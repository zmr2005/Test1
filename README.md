# CRM 线索管控系统

前后端分离项目脚手架，覆盖「板块3：客户管理（CRM 线索管控模块）」的五大模块。

- **后端**：Python + FastAPI + SQLAlchemy 2.0 + Pydantic v2（目录见 `backend/README.md`）
- **前端**：Vue 3 + Vite + TypeScript + Element Plus + Pinia（目录见 `frontend/README.md`）

## 总体结构

```
Test1/
├── backend/       # FastAPI 后端
└── frontend/      # Vue3 前端
```

## 启动顺序

1. 后端：`cd backend` → 安装依赖 → `python -m uvicorn app.main:app --reload`（:8000）
2. 前端：`cd frontend` → `npm install` → `npm run dev`（:5173）

前端 `/api` 请求会代理到后端 `:8000`，无需额外跨域配置。

## 已实现的脚手架能力

- 后端：线索完整 CRUD + 查重 + 批量分配 + 转化客户；客户/商机/任务端点骨架；分页响应；JWT 工具。
- 前端：路由 + 布局 + 请求封装 + 线索列表完整业务页 + 其余模块占位页。
