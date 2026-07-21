# Git 常用命令速查

## 1. 查看状态

```bash
git status          # 查看工作区和暂存区状态
git status -s       # 简洁模式
```

## 2. 添加文件到暂存区

```bash
git add filename    # 添加指定文件
git add .           # 添加当前目录所有变更
git add -A          # 添加所有变更（整个仓库）
git add -p          # 交互式选择性添加
```

## 3. 提交

```bash
git commit -m "提交信息"        # 提交暂存区
git commit -a -m "提交信息"     # 跳过暂存，直接提交已跟踪文件
git commit --amend -m "新信息"  # 修改最后一次提交
```

## 4. 查看差异

```bash
git diff             # 查看工作区未暂存的改动
git diff --staged    # 查看已暂存未提交的改动
git diff HEAD        # 查看所有改动（已暂存+未暂存）
git diff --no-pager  # 不分页显示（无需按q退出）
```

## 5. 查看提交历史

```bash
git log              # 完整日志
git log --oneline    # 简洁一行显示
git log --oneline -3 # 只显示最近3条
```

## 6. 撤销操作

```bash
git restore filename           # 丢弃工作区修改（危险）
git restore --staged filename  # 取消暂存（撤回 git add）
git reset HEAD filename        # 取消暂存（老命令）
git reset --soft HEAD~1        # 撤回提交，保留修改
git reset --hard HEAD~1        # 撤回提交，丢弃修改（危险）
```

## 7. 远程仓库

```bash
git push origin main    # 推送到远程
git pull origin main    # 拉取并合并
git fetch origin        # 仅拉取不合并
git clone <仓库地址>    # 克隆仓库
```

## 8. 分支操作

```bash
git branch              # 查看本地分支
git branch -a           # 查看所有分支（含远程）
git branch <分支名>     # 创建分支
git checkout <分支名>   # 切换分支
git checkout -b <分支名> # 创建并切换
git merge <分支名>      # 合并分支
git branch -d <分支名>  # 删除分支
```

## 9. 配置

```bash
git config --global user.name "你的名字"
git config --global user.email "你的邮箱"
git config --global core.quotepath false   # 修复中文乱码
git config --global core.pager cat         # 禁用分页器
```

## 10. 常用组合命令

```bash
# 完整提交流程
git status
git add .
git commit -m "提交说明"
git push origin main

# 拉取最新代码
git pull origin main

# 查看修改内容
git status
git diff
```