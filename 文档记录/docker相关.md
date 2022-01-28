## 12.05  学习日志

### docker 

启动MySQL

```shell
docker pull mysql #拉mysql 镜像
docker run -p 3306:3306 --name mysql \
-v /mydata/mysql/log:/var/log/mysql \
-v /mydata/mysql/data:/var/lib/mysql \
-v /mydata/mysql/conf:/etc/mysql \
-v /mydata/mysql/mysql-files:/var/lib/mysql-files \
-e MYSQL_ROOT_PASSWORD=root  \
-d mysql:8.0 #启动容器，默认没密码
docker exec -it mysql mysql -u root -p'root' #连接mysql
docker exec -it mysql /bin/bash  #启动mysql
```



启动RabbitMQ

```shell
docker pull rabbitmq:3.7.15
docker run -p 5672:5672 -p 15672:15672 --name rabbitmq \
-d rabbitmq:3.7.15
docker exec -it rabbitmq /bin/bash
rabbitmq-plugins enable rabbitmq_management #启动后，开启管理后台
```

启动Zookeeper

```shell
docker pull zookeeper:3.4.14
docker run -d \
-p 2181:2181 \
-p 3888:3888 \
-p 2888:2888 \
-v /mydata/zookeeper/data/:/data/ \
--name=zookeeper  \
--privileged zookeeper
docker exec -it zookeeper zkCli.sh #启动zookeeper 客户端
```

启动dubbo-admin

```shell
docker pull apache/dubbo-admin
docker run -d \
--name dubbo-admin \
-v /mydata/dubbo/data:/data \
-p 9600:8080 \
-e DUBBO_IP_TO_REGISTRY=81.69.254.72 \
-e admin.registry.address=zookeeper://81.69.254.72:2181 \
-e admin.config-center=zookeeper://81.69.254.72:2181 \
-e admin.metadata-report.address=zookeeper://81.69.254.72:2181 \
--restart=always \
docker.io/apache/dubbo-admin
docker exec -it dubbo-admin /bin/bash  #启动docker admin
#DUBBO_IP_TO_REGISTRY能获取外网zookeeper 地址
```

启动redis

```shell
docker pull redis:latest
docker run -itd \
-p 26379:26379 \
--name redis \
-v /mydata/redis/data:/data \
-d redis redis-server /data/redis.conf --appendonly yes --requirepass "luozijing@2021"
docker exec -it redis /bin/bash  #启动redis
docker exec -it redis redis-cli -h 127.0.0.1 -p 26379 -a "luozijing@2021" #进入redis
```

docker 解决客户端问题--腾讯云环境

最好关闭防火墙，开打包镜像 

https://blog.csdn.net/weiwai/article/details/118680694?spm=1001.2101.3001.6650.1&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7Edefault-1.nonecase&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7Edefault-1.nonecase

部署秒杀

```shell
docker run -p 9080:9080 --name miaosha2 \
--link mysql:mydb \
--link redis:myredis \
--link zookeeper:myzookeeper \
--link rabbitmq:myrabbitmq \
-v /etc/localtime:/etc/localtime \
-v /mydata/app/miaosha1/logs:/var/logs \
-d 38758c9ac9a5
docker exec -it miaosha2 /bin/bash  #启动docker 
```





解决zookeeper 防火墙状态下开启 docker

```shell
firewall-cmd --zone=public --add-port=2181/tcp --permanent
firewall-cmd --zone=public --add-port=2888/tcp --permanent
firewall-cmd --zone=public --add-port=3888/tcp --permanent
firewall-cmd --zone=public --add-port=9080/tcp --permanent
重启docker 网络状态发生改变
```





docker 常用命令

```shell
docker container ls #查看容器
docker start gitlab #运行容器
docker exec -it gitlab /bin/bash #进入容器
docker ps -l #查看已经启动的容器, -l 是显示最新创建的容器包括所有状态
docker rm 5e9b198b3dcb      # 删除nginx容器
docker logs mysql #查看mysql日志
docker inspect #查看镜像详情
docker rmi imageId #删除镜像
docker images #查看镜像
docker ps -a #查看所有容器

docker-compose -f docker-compose-env.yml up -d
docker-compose -f docker-compose-app.yml up -d
```

linux 

```shell
iptraf-ng #查看网卡流量详情
systemctl stop firewalld.service
systemctl restart docker
systemctl stop docker
docker start mysql;docker start zookeeper;docker start rabbitmq;docker start redis;docker start miaosha2
```

