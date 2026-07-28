


``` bash
 看git 仓库大小 https://api.github.com/repos/cloudHui/Server
ssh -i LightsailDefaultKey-ap-northeast-1.pem ec2-user@3.112.193.21
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

