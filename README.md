# BBS 论坛系统

一个基于前后端分离架构的在线论坛系统，支持用户注册登录、板块管理、帖子发布与评论等核心功能。

## 系统架构

```
┌──────────────┐     HTTP/RESTful API     ┌──────────────┐
│   前端 (Vue)  │ ◄──────────────────────► │  后端 (Spring) │
│   Port 5173   │                          │   Port 8080   │
└──────────────┘                          └──────┬───────┘
                                                 │
                                                 │ JDBC
                                                 ▼
                                          ┌──────────────┐
                                          │   数据库       │
                                          │  MySQL / PG   │
                                          └──────────────┘
```

- **前端**：Vue 3 + Vite + Ant Design Vue + Pinia + Axios
- **后端**：Spring Boot 3.2 + MyBatis + JWT 认证
- **数据库**：MySQL（本地开发）/ PostgreSQL（部署环境）

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端框架 | Vue 3 (Composition API) |
| 构建工具 | Vite 5 |
| UI 组件库 | Ant Design Vue 4 |
| 状态管理 | Pinia |
| 路由 | Vue Router 4 |
| HTTP 客户端 | Axios |
| 后端框架 | Spring Boot 3.2.5 |
| ORM | MyBatis (注解方式) |
| 认证 | JWT (2小时过期) |
| 密码加密 | BCrypt |
| Java 版本 | 21 |

## 项目结构

```
BBSsys/
├── backend/                  # 后端 Spring Boot 项目
│   └── src/main/java/com/bbs/
│       ├── controller/       # 控制层 - 处理 HTTP 请求
│       ├── service/          # 业务层 - 业务逻辑处理
│       ├── mapper/           # 数据层 - MyBatis 数据访问
│       ├── entity/           # 实体类 - 数据模型定义
│       ├── config/           # 配置类 - CORS、Swagger 等
│       ├── interceptor/      # 拦截器 - JWT 认证拦截
│       ├── common/           # 公共类 - 统一响应、异常处理
│       └── util/             # 工具类 - JWT、Token 黑名单等
├── frontend/                 # 前端 Vue 项目
│   └── src/
│       ├── views/            # 页面组件
│       ├── components/       # 公共组件
│       ├── api/              # API 请求封装
│       ├── stores/           # Pinia 状态管理
│       └── router/           # 路由配置
├── sql/                      # 数据库脚本
│   ├── schema.sql            # 建表脚本
│   └── data.sql              # 初始数据
├── start.bat                 # 一键启动脚本
├── stop.bat                  # 一键停止脚本
└── .gitignore
```

## 访问系统

直接访问：**http://121.40.240.80**

## 本地部署

### 环境要求

- JDK 21+
- Node.js 18+
- MySQL 8.0+
- Maven 3.8+

### 启动步骤

1. 创建数据库并执行 SQL 脚本：

```bash
mysql -u root -p < sql/schema.sql
mysql -u root -p < sql/data.sql
```

2. 修改后端数据库配置（`backend/src/main/resources/application.yml`）

3. 一键启动：

```bash
start.bat
```

或手动分别启动：

```bash
# 后端
cd backend
mvnw.cmd spring-boot:run

# 前端
cd frontend
npm install
npm run dev
```

4. 访问 http://localhost:5173

### 停止服务

```bash
stop.bat
```

## 核心功能

- 用户注册 / 登录（JWT 认证）
- 板块浏览与管理
- 帖子发布、编辑、删除
- 帖子评论与回复
- 个人信息管理
