---
title: codex-cli
---

# Codex CLI

## Windows 安装

```powershell
powershell -ExecutionPolicy ByPass -c "irm https://chatgpt.com/codex/install.ps1 | iex"
```

## Debian 安装

```bash
curl -fsSL https://chatgpt.com/codex/install.sh | sh
source ~/.bashrc
```


# Install (macOS, Linux, WSL)
```
curl https://cursor.com/install -fsS | bash
```

# 安装 (Windows PowerShell)
```
irm 'https://cursor.com/install?win32=true' | iex
```

# Run interactive session
```
agent
```

## Codewhale(deepseak tui)

- 安装文档 https://github.com/Hmbown/CodeWhale/blob/main/README.zh-CN.md


- 命令 `npm install -g codewhale`  安装报错不用管  能 `codewhale`  就行
- `codewhale auth set --provider deepseek   # or export ANTHROPIC_API_KEY, etc.
- `codewhale                                # open the TUI`
- `codewhale exec "fix the failing test"    # headless`
- `codewhale web                            # local browser client on 127.0.0.1


## 每日重置脚本

```bat
@echo off
setlocal EnableExtensions
rem Codex 每日重置：发送「你好」开启新 CLI 会话
rem 配合 Windows 计划任务，每天东八区 07:00 / 12:00 / 17:00 各触发一次
chcp 65001 >nul 2>&1
set "PROJECT_ROOT=%~dp0..\.."
cd /d "%PROJECT_ROOT%" || exit /b 1
for %%I in ("%CD%") do set "PROJECT_ROOT=%%~fI"
set "LOG_DIR=%PROJECT_ROOT%\.codex\logs"
if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"
set "LOG_FILE=%LOG_DIR%\daily-reset.log"
set "CODEX_EXE=C:\Users\liuyunhui\AppData\Local\Programs\OpenAI\Codex\bin\codex.exe"
echo [%date% %time%] start >> "%LOG_FILE%"
if not exist "%CODEX_EXE%" (
    echo [%date% %time%] [ERROR] 未找到: %CODEX_EXE% >> "%LOG_FILE%"
    exit /b 1
)
echo 你好| "%CODEX_EXE%" exec --ephemeral -C "%PROJECT_ROOT%" - >> "%LOG_FILE%" 2>&1
echo [%date% %time%] end, exit=%ERRORLEVEL% >> "%LOG_FILE%"
echo. >> "%LOG_FILE%"
exit /b %ERRORLEVEL%
```

