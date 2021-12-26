## 项目声明
基于https://hub.fastgit.org/qiurunze123/aircrafttravel该项目的练习项目，自己搭建在线环境，用来学习代码。
## 项目代码逻辑
![Image text](https://raw.fastgit.org/luozijing/miaoShaPractice/main/miaosha1/jpg/%E7%A7%92%E6%9D%80%E6%B5%81%E7%A8%8B.jpg)
## 搭建项目环境

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

docker 常用命令

```shell
docker container ls #查看容器
docker start gitlab #运行容器
docker exec -it gitlab /bin/bash #进入容器
docker ps -l #查看已经启动的容器, -l 是显示最新创建的容器包括所有状态
docker rm 5e9b198b3dcb      # 删除nginx容器
docker logs mysql #查看mysql日志
docker inspect #查看镜像详情
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
-e DUBBO_IP_TO_REGISTRY=localhost \
-e admin.registry.address=zookeeper://localhost:2181 \
-e admin.config-center=zookeeper://localhost:2181 \
-e admin.metadata-report.address=zookeeper://localhost:2181 \
--restart=always \
docker.io/apache/dubbo-admin
docker exec -it dubbo-admin /bin/bash  #启动docker admin
#DUBBO_IP_TO_REGISTRY能获取外网zookeeper 地址
```

启动redis

```shell
docker pull redis:latest
docker run -itd \
-p 26379:6379 \
--name redis \
-v /mydata/redis/data:/data \
-d redis redis-server /data/redis.conf --appendonly yes --requirepass "luozijing@2021"
docker exec -it redis /bin/bash  #启动redis
docker exec -it redis redis-cli -a "luozijing@2021" #进入redis
```

## 启动项目
进行账号注册（随便注册体验），和体验秒杀功能，项目体验地址
![Image text](https://raw.fastgit.org/luozijing/miaoShaPractice/main/miaosha1/jpg/%E7%BA%BF%E4%B8%8A%E6%88%AA%E5%9B%BE.PNG)



