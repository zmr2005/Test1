# CRM 线索管控模块（板块3：客户管理）

软件工程2505郑棉润

销售过程管理系统，覆盖「板块3：客户管理（CRM 线索管控模块）」的五大功能，围绕「**线索 → 客户 → 商机 → 成交**」全生命周期设计。

## 一、功能模块（对应板块要求）

### 1. 线索池

- 多渠道线索统一汇入、集中管理

- 线索标签、来源渠道标记

- 批量筛选、分配、导出

- 线索查重（手机号/邮箱精确 + 公司名模糊）

- 线索回收站（软删除、恢复、彻底删除）

### 2. 客户池

- 客户保护、防撞客（同客户唯一负责人）

- 销售间客户移交

- 公海池管理（长期未跟进自动回收、认领、回收）

- 客户跟进时效提醒

### 3. 客户档案

- 客户建档，存储企业基础资料、联系人信息

- 跟进沟通记录沉淀（邮件/社媒/报价/日志，统一时间线）

- 每次跟进可新增日志备注

- 全生命周期跟进记录可回溯

### 4. 商机管理

- 新建商机，记录询盘、意向产品、预算、成交进度

- 商机阶段：初步接触 → 报价 → 谈判 → 成交 / 失败

- 商机进度看板、商机到期提醒

### 5. 日程管理

- 团队工作日志（每日日报、拓客任务）

- 工作计划日历化（日历待办）

- 管理者查看团队计划与完成进度

- 任务提醒

## 二、技术栈

| 端   | 技术                                                 |
| --- | -------------------------------------------------- |
| 后端  | Spring Boot 3.2.5 · MyBatis-Plus 3.5.7 · JDK 17    |
| 数据库 | H2 内存库（默认，零依赖验证）· 可切 MySQL                         |
| 前端  | Vue 3 · Vite 5 · TypeScript · Element Plus · Pinia |

## 三、目录结构

```
Test1/
├── backend/                  # SpringBoot 后端
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/crm/
│       │   ├── controller/   # 5 个 RESTful 控制器
│       │   ├── service/      # 业务接口 + 实现
│       │   ├── mapper/       # MyBatis-Plus Mapper
│       │   ├── entity/       # ORM 实体
│       │   ├── dto/          # 请求对象（含校验）
│       │   └── common/       # 统一响应、异常处理
│       └── resources/
│           ├── application.yml   # 数据源配置（当前 H2）
│           ├── schema.sql        # H2 建表脚本
│           └── data.sql          # 种子数据
├── frontend/                 # Vue3 前端
│   └── src/
│       ├── api/              # 接口封装（axios）
│       ├── views/            # 五大模块页面
│       ├── router/           # 路由
│       └── layout/           # 侧边栏布局
├── .tools/                   # 绿色版 JDK + Maven（已下载，不入库）
├── 数据表结构.md
├── schema-mysql.sql          # MySQL 建表脚本（切回 MySQL 用）
└── CRM线索管控模块-需求文档.md
```

## 四、环境准备

本机已下载绿色版 JDK 17 与 Maven 到 `.tools` 目录（无需安装）：

- JDK：`.tools\jdk\jdk-17.0.20.1+1`

- Maven：`.tools\maven\apache-maven-3.9.16`

> 若已安装 JDK/Maven/IntelliJ IDEA，可跳过绿色版，直接用自身环境。

## 五、启动后端

后端默认使用 **H2 内存数据库**，启动时自动建表并灌入种子数据，无需 MySQL。

在项目根目录打开 PowerShell：

```powershell
# 1. 指定绿色版 JDK
$env:JAVA_HOME = "c:\Users\Mrun\Desktop\Test1\.tools\jdk\jdk-17.0.20.1+1"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

# 2. 用绿色版 Maven 启动（注意用 & 调用 .cmd）
cd backend
& "..\.tools\maven\apache-maven-3.9.16\bin\mvn.cmd" spring-boot:run
```

启动成功后：

- 接口地址：<http://localhost:8080>

- 健康检查：<http://localhost:8080/health>

- H2 控制台：<http://localhost:8080/h2-console（JDBC> URL 填 `jdbc:h2:mem:crm`，用户名 `sa`，密码留空）

### 运行 CrmApplication 的其他方式

- **IntelliJ IDEA**：用 IDEA 打开 `backend` 目录，运行 `com.crm.CrmApplication`。

- **打包 Jar 运行**：

```powershell
$env:JAVA_HOME = "c:\Users\Mrun\Desktop\Test1\.tools\jdk\jdk-17.0.20.1+1"
cd backend
& "..\.tools\maven\apache-maven-3.9.16\bin\mvn.cmd" clean package -DskipTests
& "$env:JAVA_HOME\bin\java.exe" -jar target\crm-backend-0.1.0.jar
```

## 六、启动前端

```powershell
cd frontend
npm install        # 首次需执行
npm run dev        # http://localhost:5173
```

前端 `/api` 请求经 Vite 代理转发到后端 `:8080`，刷新页面即可看到种子数据。

## 七、数据库切换（H2 ↔ MySQL）

**当前默认 H2**（内存库，重启即重置）。

切回 MySQL：

1. 执行建库建表：`mysql -u root -p < schema-mysql.sql`
2. 修改 `backend/src/main/resources/application.yml` 的 `spring.datasource` 为 MySQL 连接，并移除 `spring.sql.init` 段
3. 重启后端

## 八、接口清单

| 模块   | 接口                                                                                                                                                            |
| ---- | ------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 线索   | `GET/POST /leads`、`GET/PUT/DELETE /leads/{id}`、`POST /leads/batch-assign`、`POST /leads/duplicate-check`、`POST /leads/{id}/restore`、`DELETE /leads/{id}/purge` |
| 客户   | `GET /customers`、`GET /customers/{id}`、`POST /customers/{id}/reclaim`（认领）、`/recycle`（回收公海）、`/transfer`（移交）                                                    |
| 客户档案 | `GET/POST /customers/{id}/follow-ups`（跟进日志）、`GET/POST /customers/{id}/contacts`（联系人）                                                                          |
| 商机   | `GET/POST /opportunities`、`GET /opportunities/{id}`、`PUT /opportunities/{id}/stage`（阶段流转）                                                                     |
| 日程   | `GET/POST /tasks`、`PUT /tasks/{id}/done`                                                                                                                      |

> 当前用户通过请求头 `X-User-Id` 传递（默认 0），接入登录认证后替换为 token 解析。

## 九、常见问题

- **前端报 500 / 数据加载失败**：后端未启动。先启动后端，再刷新页面。

- **`mvn`** **不是内部命令**：用 `& "..\.tools\maven\...\bin\mvn.cmd"` 完整路径，并确认已设置 `JAVA_HOME`。

- **端口被占用**：修改 `application.yml` 的 `server.port`，或 `vite.config.ts` 的 `server.port`。

- **Maven 下载依赖慢**：可在 Maven `conf/settings.xml` 配置阿里云镜像加速。

