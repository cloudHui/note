---
title: tools
---

# 开发工具

## Nginx

### Vim

```vim
:set nu
```

## 常用命令

```bash
start nginx
nginx -s stop
nginx -s reload
nginx -t
tasklist | findstr nginx
```

## 配置示例

```nginx
worker_processes  1;
error_log  logs/error.log;

events {
    worker_connections  1024;
}

http {
    include       mime.types;
    default_type  application/octet-stream;
    sendfile        on;
    keepalive_timeout  65;

    server {
        listen       80;
        server_name  localhost;

        location / {
            root   html;
            index  index.html index.htm;
        }

        location /headicons/ {
            alias   D:/kingdom/headicons/;
            autoindex on;
            autoindex_exact_size off;
            autoindex_localtime on;
        }

        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
    }
}
```

## 相关笔记

- SVN 操作见 [测试服](工作服务器.md)。
- GM 命令见 [开发工具](tools.md)。
- Resin 命令见 [服务器运维](服务器vp.md)。

## Resin

- Resin 命令已并入服务器运维主文件：[服务器运维](服务器vp.md)。

# GIT
- amend 可以修改提交描述
## GM 相关命令

## 任务与消息

```text
gamesendmsg 910010005 7 17 21 1 普通商品购买
gamesendmsg 910010005 12 27 27 7 金币商店购买
gamesendmsg 910010004 12 26 完成普通商店购买
gamesendmsg 910010005 8 9 10 激活任务
gamesendmsg 910010005 15 45 45 激活跑商任务
gamesendmsg 910010012 5 采集
gamesendmsg 910010009 领取奖励
gamesendmsg 910010014 收集资源
gamesendmsg 910010005 1 26 加资源
```

## 关卡与摄像机

```text
C2SLevelClaimFocusReward
onGMCameraSetDistanceAndFarPlane 400 3000
onGMCameraSetDistanceAndFarPlane 1000 3000
gametask print enemy level
gametask print enemy room
```

## 说明

- `gamesendmsg` 的参数顺序保持现有项目约定。
- `onGMCameraSetDistanceAndFarPlane` 用于调客户端显示。

## 清理 Java MCP Server 进程

## 用途

用于在 Windows 上查找并强制结束名称中包含 `Mcp` 的 Java 进程。适合 MCP Server 卡住、端口占用、重复启动时使用。

## 前置条件

- 已安装 JDK，并且 `jps` 命令可用。
- 在 `cmd` 或 `.bat` 文件中执行。
- 如果进程权限较高，需要用管理员身份运行。

## BAT 脚本

保存为 `clear-mcp-server.bat`：

```bat
@echo off
chcp 65001 >nul 2>&1

echo [INFO] 查找 Mcp*Server.jar 进程...

for /f "tokens=1" %%a in ('jps -l ^| findstr /i "Mcp"') do (
    echo [KILL] PID %%a
    taskkill /F /PID %%a >nul 2>&1
)

echo [INFO] 剩余 MCP 进程:
jps -l | findstr /i "Mcp" || echo    (无)

pause
```

## 单行排查命令

```bat
jps -l | findstr /i "Mcp"
```

## 注意

- `%%a` 是 `.bat` 文件里的写法；如果直接在命令行手动执行 `for`，要改成 `%a`。
- `^|` 是 bat 里的管道转义，不能省略。
- `taskkill /F` 会强制结束进程，执行前确认筛选条件 `Mcp` 不会误伤其他 Java 程序。


