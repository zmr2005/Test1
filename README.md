# CRM 线索管控模块

销售过程管理（CRM）系统，覆盖「板块3：客户管理」五大模块：线索池、客户池、客户档案、商机管理、日程管理。围绕「线索 → 客户 → 商机 → 成交」全生命周期设计。

## 技术栈

| 端   | 技术                                                 |
| --- | -------------------------------------------------- |
| 后端  | Spring Boot 3.2.5 · MyBatis-Plus 3.5.7 · JDK 17    |
| 数据库 | H2 内存库（默认，零依赖验证）· 可切 MySQL                         |
| 前端  | Vue 3 · Vite 5 · TypeScript · Element Plus · Pinia |

## 目录结构

```
Test1/
├── backend/                  # SpringBoot 后端
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/crm/     # controller / service / mapper / entity / dto / common
│       └── resources/
│           ├── application.yml   # 数据源配置（当前 H2）
│           ├── schema.sql        # H2 建表脚本
│           └── data.sql          # 种子数据
├── frontend/                 # Vue3 前端
│   └── src/                  # api / views / router / layout
├── .tools/                   # 绿色版 JDK + Maven（已下载，不入库）
├── 数据表结构.md
├── schema-mysql.sql          # MySQL 建表脚本（切回 MySQL 用）
└── CRM线索管控模块-需求文档.md
```

## 环境准备

本机已下载绿色版 JDK 17 与 Maven 到 `.tools` 目录（无需安装）：

- JDK：`.tools\jdk\jdk-17.0.20.1+1`

- Maven：`.tools\maven\apache-maven-3.9.16`

> 若你已安装 JDK/Maven/IntelliJ IDEA，可跳过绿色版，直接用自己的环境运行。

## 启动后端

后端当前使用 **H2 内存数据库**，启动时自动建表并灌入种子数据，无需 MySQL。

在项目根目录打开 PowerShell 执行：

```powershell
# 1. 指定绿色版 JDK
$env:JAVA_HOME = "c:\Users\Mrun\Desktop\Test1\.tools\jdk\jdk-17.0.20.1+1"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

# 2. 用绿色版 Maven 启动
cd backend
..\.tools\maven\apache-maven-3.9.16\bin\mvn.cmd spring-boot:run
```

启动成功后：

- 接口地址：<http://localhost:8080>

- 健康检查：<http://localhost:8080/health>

- H2 控制台：<http://localhost:8080/h2-console（JDBC> URL 填 `jdbc:h2:mem:crm`，用户名 `sa`，密码留空）

### 运行 CrmApplication 的其他方式

- **IntelliJ IDEA**：用 IDEA 打开 `backend` 目录，找到 `com.crm.CrmApplication`，右键 Run。

- **打包成 Jar 运行**：

```powershell
$env:JAVA_HOME = "c:\Users\Mrun\Desktop\Test1\.tools\jdk\jdk-17.0.20.1+1"
cd backend
..\.tools\maven\apache-maven-3.9.16\bin\mvn.cmd clean package -DskipTests
$env:JAVA_HOME\bin\java -jar target\crm-backend-0.1.0.jar
```

## 启动前端

```powershell
cd frontend
npm install        # 首次需执行
npm run dev        # http://localhost:5173
```

前端 `/api` 请求已通过 Vite 代理转发到后端 `:8080`，刷新页面即可看到种子数据。

## 数据库切换（H2 ↔ MySQL）

**当前默认 H2**（内存库，重启即重置），适合快速验证逻辑。

切回 MySQL：

1. 执行 `schema-mysql.sql` 建库建表：`mysql -u root -p < schema-mysql.sql`
2. 修改 `backend/src/main/resources/application.yml` 的 `spring.datasource` 为 MySQL 连接，并删除 `spring.sql.init` 段（避免重复执行建表脚本）
3. 重启后端

## 功能模块与接口

| 模块   | 主要接口                                                                                                                                                      |
| ---- | --------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 线索   | `GET/POST /leads`、`PUT/DELETE /leads/{id}`、`POST /leads/batch-assign`、`POST /leads/duplicate-check`、`POST /leads/{id}/restore`、`DELETE /leads/{id}/purge` |
| 客户   | `GET /customers`、`POST /customers/{id}/reclaim`（认领）、`/recycle`（回收公海）、`/transfer`（移交）                                                                      |
| 客户档案 | `GET/POST /customers/{id}/follow-ups`（跟进日志）、`GET/POST /customers/{id}/contacts`（联系人）                                                                      |
| 商机   | `GET/POST /opportunities`、`PUT /opportunities/{id}/stage`（阶段流转）                                                                                           |
| 日程   | `GET/POST /tasks`、`PUT /tasks/{id}/done`                                                                                                                  |

> 当前用户通过请求头 `X-User-Id` 传递（默认 0），接入登录认证后替换为 token 解析。

## 常见问题

- **前端报 500 / 数据加载失败**：后端未启动。先启动后端，再刷新页面。

- **`mvn`** **不是内部命令**：用 `..\.tools\maven\...\bin\mvn.cmd` 完整路径，或确认已设置 `JAVA_HOME`。

- **端口被占用**：修改 `application.yml` 的 `server.port` 或 `vite.config.ts` 的 `server.port`。

