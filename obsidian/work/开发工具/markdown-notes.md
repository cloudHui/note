---
title: markdown-notes
---

# Markdown / Obsidian 速查

## 当前笔记已经在用的格式

### 文档属性

放在文件最上方，用来记录标题、标签等元数据。

```yaml
---
title: resin
tags:
  - server
  - command
---
```

### 标题层级

```markdown
## 二级标题
### 三级标题
```

建议：

- `#` 只放一个，作为整篇笔记标题。
- `##` 用来分大类，比如 `SSH`、`SVN`、`Nginx`。
- `###` 用来放具体操作或小节。

### 代码块

当前笔记里已经用了 `bash`、`text`、`sql`、`nginx`、`vim` 等代码块。

````markdown
```bash
svn log -l 10 -v
svn update -r 1234
```

```sql
SELECT * FROM role LIMIT 10;
```

```text
这里放不能确定语言的命令、日志、步骤。
```
````

常用语言标记：

- `bash`：Linux / macOS Shell 命令。
- `bat`：Windows 批处理脚本。
- `powershell`：PowerShell 命令。
- `sql`：数据库脚本。
- `nginx`：Nginx 配置。
- `vim`：Vim 命令。
- `text`：普通文本、日志、GM 命令。

### 内联代码

用于命令名、路径、文件名、参数名。

```markdown
执行 `nginx -t` 检查配置。
进入 `/data/resin/bin` 后运行 `./resinctl start`。
```

### 列表

无顺序记录用 `-`：

```markdown
- 先检查配置。
- 再重启服务。
- 最后看日志。
```

有严格顺序的操作用数字：

```markdown
1. 备份旧文件。
2. 停止服务。
3. 更新代码。
4. 启动服务。
```

### 缩进

列表下方继续写说明时，缩进两个或四个空格都可以，建议统一用四个空格。

```markdown
- Nginx
    - `nginx -t`：检查配置。
    - `nginx -s reload`：重载配置。
```

代码块放在列表下面时，也要缩进：

````markdown
1. 查看进程：

    ```bash
    tasklist | findstr nginx
    ```
````

## 链接写法

### 普通链接

```markdown
[OpenAI](https://openai.com)
```

### 本地文件链接

```markdown
[查看开发工具笔记](dev-tools.md)
[查看服务器运维笔记](../server-ops/server-ops.md)
```

### Obsidian 双链

```markdown
[[dev-tools]]
[[server-ops]]
[[dev-tools]]
[[dev-tools]]
```

### 链接到标题

```markdown
[[server-ops#SSH 与面板]]
[[dev-tools#Nginx]]
```

### 显示别名

```markdown
[[server-ops#SSH 与面板|SSH 配置记录]]
[[dev-tools#Nginx|Nginx 常用命令]]
```

## 高级笔记写法

### 任务清单

```markdown
- [ ] 检查进程
- [ ] 备份配置
- [x] 更新完成
```

### 表格

```markdown
| 场景 | 命令 | 说明 |
| --- | --- | --- |
| 检查 Nginx | `nginx -t` | 验证配置是否正确 |
| 重载 Nginx | `nginx -s reload` | 不完全停止服务 |
```

### 引用

```markdown
> 这里记录重要提醒、风险、来源说明。
```

### 折叠块

```markdown
> [!note]- 点击展开
> 这里放较长的日志、历史命令或临时记录。
```

### 提醒块

```markdown
> [!warning]
> `taskkill /F` 会强制结束进程，执行前确认筛选条件。
```

常用类型：

- `[!note]`：普通说明。
- `[!tip]`：技巧。
- `[!warning]`：风险提醒。
- `[!danger]`：高风险操作。

### 标签

```markdown
#server #nginx #java #mcp
```

建议优先放在 frontmatter 的 `tags` 中，正文里只在需要快速检索时使用。

## 推荐记录模板

### 命令笔记模板

````markdown
---
title: 工具名
tags:
  - command
---

## 用途

一句话说明这个命令解决什么问题。

## 常用命令

```bash
command --help
```

## 操作步骤

1. 第一步。
2. 第二步。
3. 第三步。

## 注意

- 记录风险。
- 记录前置条件。
- 不直接记录明文密码、Token、私钥。
````

### 服务器操作模板

````markdown
---
title: 服务名 运维记录
tags:
  - server
  - ops
---

## 路径

```text
/data/server/xxx
```

## 启停

```bash
sh start
sh stop
```

## 更新步骤

1. 备份。
2. 停服。
3. 更新。
4. 启服。
5. 看日志。

## 回滚

```bash
cp xxx.bak xxx
```
````

## Obsidian 常用快捷键

> 快捷键可能被自定义改掉；以下是常见默认或建议绑定。

| 操作 | 快捷键 |
| --- | --- |
| 命令面板 | `Ctrl + P` |
| 快速打开文件 | `Ctrl + O` |
| 全局搜索 | `Ctrl + Shift + F` |
| 当前文件搜索 | `Ctrl + F` |
| 新建笔记 | `Ctrl + N` |
| 保存 | `Ctrl + S` |
| 加粗 | `Ctrl + B` |
| 斜体 | `Ctrl + I` |
| 插入链接 | `Ctrl + K` |
| 切换阅读 / 编辑视图 | `Ctrl + E` |

## 推荐操作习惯

- 命令统一放进代码块，不要只写在正文里。
- 路径、文件名、命令参数用内联代码包起来。
- 一次性操作用编号步骤，零散命令用无序列表。
- 高风险命令前加 `[!warning]` 或 `[!danger]`。
- 不在笔记里保存明文密码、API Token、私钥；确实要记，只写存放位置或用途。
- 中文文件建议统一保存为 UTF-8，避免出现乱码。


