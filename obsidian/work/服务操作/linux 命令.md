


``` bash
 看git 仓库大小 https://api.github.com/repos/cloudHui/Server
ssh -i LightsailDefaultKey-ap-northeast-1.pem ec2-user@3.112.193.21
cd repos/Server
git push
cd ../../
sh repos/Server/scripts/ops.sh stop
rm -rf repos/Server/logs
sh repos/Server/scripts/ops.sh build-restart
```


# 🚀 SSH 连接远程服务器完整指南

## 1. 基础连接命令

### 标准连接
```bash
ssh username@remote_host

ssh -p port_number username@remote_host

ssh -i /path/to/private_key username@remote_host

chmod 600 /path/to/private_key

ssh-keygen -t rsa -b 4096

ssh-copy-id username@remote_host


假设你有一个运行在远程服务器（IP地址为`remote_host`）上的Web服务，监听在端口`8080`上，你想通过你的本地机器的端口`80`来访问这个服务。你可以使用以下命令来实现这一需求：

bash

`ssh -L 80:localhost:8080 user@remote_host`


# 手动同步
sudo ntpdate -u ntp.aliyun.com



watch -n1 -d '
echo "========== 各进程详情 =========="
ps -C java -C xray -o pid,comm,%cpu,%mem,rss --no-headers 2>/dev/null | awk '\''{
    cpu+=$3; mem+=$4; rss+=$5;
    # 截取进程名，只保留前10个字符
    name = substr($2, 1, 50);
    printf "%-8s %-12s CPU: %5.1f%%  MEM: %5.1f%%  RSS: %6.1f MB\n", $1, name, $3, $4, $5/1024
}
END {
    printf "\n========== 合计 ==========\n"
    printf "总CPU: %.1f%%   总MEM: %.1f%%   总RSS: %.1f MB\n", cpu, mem, rss/1024
}'\'


watch -n1 -d '
echo "========== 各进程详情 =========="
ps -C java -C xray -o pid,comm,%cpu,%mem,rss,cmd --no-headers 2>/dev/null | awk '\''{
    cpu+=$3; mem+=$4; rss+=$5;
    
    # 根据命令行关键词匹配服务名
    if ($2 == "java") {
        if ($6 ~ /center/) name = "center"
        else if ($6 ~ /gate/) name = "gate"
        else if ($6 ~ /lobby/) name = "lobby"
        else if ($6 ~ /game/) name = "game"
        else if ($6 ~ /web/) name = "web"
        else name = "java-other"
    } else if ($2 == "xray") {
        name = "xui"
    } else {
        name = substr($2, 1, 10)
    }
    
    printf "%-8s %-8s CPU: %5.1f%%  MEM: %5.1f%%  RSS: %6.1f MB\n", $1, name, $3, $4, $5/1024
}
END {
    printf "\n========== 合计 ==========\n"
    printf "总CPU: %.1f%%   总MEM: %.1f%%   总RSS: %.1f MB\n", cpu, mem, rss/1024
}'\'
```

# 看内存使用命令
``` bash
ps -p <PID> -o %cpu,rss,cmd | awk 'NR==1 {print $0 " (MB)"} NR>1 {printf "%.1f  %.2f MB  %s\n", $1, $2/1024, $3}'
```
