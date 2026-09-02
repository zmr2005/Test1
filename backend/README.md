# CRM 后端（FastAPI + SQLAlchemy 2.0）

围绕「线索 → 客户 → 商机 → 成交」主线设计的 RESTful 后端脚手架。

## 目录结构

```
backend/
├── app/
│   ├── main.py                 # FastAPI 入口（CORS、路由挂载、健康检查）
│   ├── core/
│   │   ├── config.py           # 配置（pydantic-settings 读取 .env）
│   │   ├── database.py         # 引擎、会话、Base
│   │   └── security.py         # 密码哈希 + JWT
│   ├── models/                 # SQLAlchemy ORM 模型
│   │   ├── base.py             # 时间戳混入
│   │   ├── lead.py             # 线索
│   │   ├── customer.py         # 客户 + 联系人
│   │   ├── opportunity.py      # 商机
│   │   └── task.py             # 任务/待办/日报
│   ├── schemas/                # Pydantic 请求/响应模型
│   │   ├── common.py           # 分页 Page
│   │   └── lead.py
│   ├── api/
│   │   ├── deps.py             # 依赖注入（session、当前用户）
│   │   └── v1/
│   │       ├── router.py       # 路由聚合
│   │       └── endpoints/      # 各资源端点
│   │           ├── leads.py            # 完整 CRUD 范式
│   │           ├── customers.py
│   │           ├── opportunities.py
│   │           └── tasks.py
│   └── services/               # 业务逻辑
│       └── lead_service.py     # 查重 / 批量分配 / 转化
├── requirements.txt
└── .env.example
```

## 快速启动

```powershell
cd backend
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt

Copy-Item .env.example .env   # 修改数据库连接与 SECRET_KEY
python -m uvicorn app.main:app --reload
```

- 接口文档：http://localhost:8000/docs
- 健康检查：http://localhost:8000/health

## 说明

- 数据库默认配置为 MySQL（`pymysql`），可替换为 PostgreSQL / SQLite。
- 脚手架阶段未引入 Alembic 迁移，首次建表可在启动前执行：
  `python -c "from app.core.database import Base, engine; import app.models; Base.metadata.create_all(engine)"`
- `api/deps.py` 中 `get_current_user` 为占位实现，接入用户认证模块后替换为 JWT 解析。
