---
title: 服务器 VP 运维
---

# 服务器运维

## SSH 与面板

## SSH

```text
ssh-rsa [请在这里粘贴你.pub文件里那一长串密钥内容] cloudsunnyshine
```

```bash
ssh-keygen -t ed25519 -f cloudsunnyshine -C "cloudsunnyshine"
chown -R cloudsunnyshine:cloudsunnyshine /home/cloudsunnyshine/.ssh
chmod 700 /home/cloudsunnyshine/.ssh
chmod 600 /home/cloudsunnyshine/.ssh/authorized_keys
passwd cloudsunnyshine
usermod -aG sudo cloudsunnyshine
ssh -vi cloudsunnyshine cloudsunnyshine@35.212.136.223
sudo tail -f /var/log/auth.log
```
```
mkdir -p ~/.ssh
chmod 700 ~/.ssh
echo 'ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAACAQCorYCXbv7NhNucVTl31P57TF1lYaA6mqsLZZoQCdMphuggzwbl9fz41YrFm2R7hb3efJtnkHQ0pszieiyukM6624OqSbbdRv0iJx/WGhQNUn6u9NMohssvvIgJZfinAofJHXVq0HfzdfBmF+qXK8Tv2Ya+/bYnVbMAwV0AMBLWQNd8hhq/y06lB+haha8gGZKJ+AhCuz8xcFlOgDlX6HSQ5Z3KBgJWzom4j5YK8qXcDq/JHlyAkbC6E+oLBfYapLbaeMC0CewYurJCmrI6tku71/BSsuJz9MK7asFRh4dWprHWxwO/4MAUbS0W8lVT+2I2Po+VSnH6po7CEc1KUwVmCPMpC2GcoM7MApu1/qy5Z76N4r6bAhgpycvCE1QtDHItK7votd7AVTQbfg7vVctsksjNC96V91zPSLPxZBfVfB4Zk36IsVffyZ0mZwwyyCchPJlwdWpGvLV5cT/T2CXEDkdfIw8JnYsjNlJszRq6RqqVAMncD5kUNtAShJg1JhGUdwvyzKB6XzYDoz/qnYtiDgyfeeJm8/dhkBNZk8MO9/CqZYAZM8tiUQa+SfeA7VfSDPkqJm0GPa+AEXLN07x2m6TJGWr6Q6xw7q/MqM1cqSjnAg9lXJmcb+mmb1m76deOZQpFaMgzka4UmeubjzAcSjdP96RQ5lZph8OiwsDbRw== liuyunhui' >> ~/.ssh/authorized_keys
chmod 600 ~/.ssh/authorized_keys
```
## 面板

```text
Panel Installation Complete!
Username: ql4g0AZXhF
Password: nqGwtQUyKa
Port: 9563
WebBasePath: KxCE6a1P4IPOUYz3zx
Database: SQLite (/etc/x-ui/x-ui.db)
Access URL: https://35.212.136.223:9563/KxCE6a1P4IPOUYz3zx
API Token: 2zDM1HI5kgDnDbRXgr5IxgPot8Bciy9RJHfuFvVOCWD65mRo
```

## Swap 与 x-ui

## Swap

```bash
sudo dd if=/dev/zero of=/swapfile bs=1M count=2048
chmod 600 /swapfile
mkswap /swapfile
swapon /swapfile
```

## x-ui

```bash
bash <(curl -Ls https://raw.githubusercontent.com/mhsanaei/3x-ui/master/install.sh) v3.3.0
```



### 1. 创建脚本

执行命令：

Bash

```
vi /usr/local/bin/ddns.sh
```

粘贴以下精简内容（换成你的配置）：

Bash

```
#!/bin/bash
API_KEY="你的_X-API-Key"
API_SECRET="你的_X-API-Secret"
DOMAIN="bbroot.com"
SUB="home"

# 1. 获取当前公网 IP
IP=$(curl -s https://api.icanhazip.com | tr -d '[:space:]')
[ -z "$IP" ] && exit 1

# 2. 对比上一次的 IP，没变就退出
[ "$IP" == "$(cat /tmp/last_ip.txt 2>/dev/null)" ] && exit 0

# 3. 变了就同步到 DNSHE
res=$(curl -s -X POST "https://api005.dnshe.com/index.php?m=domain_hub&endpoint=subdomains&action=update" \
    -H "X-API-Key: $API_KEY" \
    -H "X-API-Secret: $API_SECRET" \
    -H "Content-Type: application/json" \
    -d "{\"domain\":\"$DOMAIN\",\"subdomain\":\"$SUB\",\"type\":\"A\",\"value\":\"$IP\"}")

# 4. 成功则记录 IP
[[ "$res" == *"success"* ]] && echo "$IP" > /tmp/last_ip.txt
```

### 2. 赋予权限

Bash

```
chmod +x /usr/local/bin/ddns.sh
```

### 3. 设置定时任务

输入 `crontab -e`，在最后添加一行（每5分钟执行一次）：

Plaintext

```
*/5 * * * * /usr/local/bin/ddns.sh
```


• 按 systemd 服务实时查看 CPU 和内存：

  sudo systemd-cgtop --delay=1 --depth=2 --order=cpu --cpu=percentage

  它本身每秒刷新，不需要 watch。

  如果一定要用 watch -n 1：

  watch -n 1 -c 'systemd-cgtop --batch --iterations=1 --depth=2 --order=cpu --cpu=percentage'

  仅查看具体业务服务：

  watch -n 1 -c "systemd-cgtop --batch --iterations=1 --depth=2 --cpu=percentage | grep -E 'family-learning|x-ui|nginx|fail2ban|ssh'"

  普通进程维度则用：

  watch -n 1 -c 'ps -eo pid,comm,%cpu,%mem,rss --sort=-%cpu | head -30'

  其中 RSS 单位是 KiB。
