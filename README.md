## 项目声明
基于https://hub.fastgit.org/qiurunze123/aircrafttravel该项目的练习项目，自己搭建在线环境，用来学习代码。
## 项目代码逻辑
![Image text](https://raw.fastgit.org/luozijing/miaoShaPractice/main/miaosha1/jpg/%E7%A7%92%E6%9D%80%E6%B5%81%E7%A8%8B.jpg)
## 云环境docker搭建

本人购买的是tx云 2c4g轻量服务器，1200G流量，使用体验来说一般，唯一的好处就是便宜，一年差不多70左右，三年使用期限，对于小型开发项目还是够用的。安装的操作的系统的自带的docker的CentOS7.6-Docker20，在管理容器方面较为方便。服务器使用过程中注意流量的控制，监控流量防止某些部分程序一直偷流量，超出流量后是要计费的，费用0.8/G。

搭建的开发环境的话使用的是Docker Compose部署SpringBoot应用，Docker Compose云环境中自带，很方便的就直接准备镜像了。

镜像文件可以到github项目空间去下载。

![image-20220103225810519](C:\Users\煎饼果子\AppData\Roaming\Typora\typora-user-images\image-20220103225810519.png)

直接在docker环境中运行以下命令运行应用环境，redis的配置文件记得放在对应目录下

```shell
docker-compose -f docker-compose-env.yml up -d
```

常用的docker命令有如下，也可以使用云管理平台管理容器

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
```

## 应用构建

刷下应用的sql，然后clone下代码，打包下项目，配置docker 镜像地址，和需要打包镜像的模块，本项目中需要打包的是web模块，其他的是公共包。maven pom.xml配置如下：

```xml
  <plugin>
                <groupId>com.spotify</groupId>
                <artifactId>docker-maven-plugin</artifactId>
                <version>${docker.maven.plugin.version}</version>
                <executions>
                    <execution>
                        <id>build-image</id>
                        <phase>package</phase>
                        <goals>
                            <goal>build</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <imageName>miaosha1/${project.artifactId}:${project.version}</imageName>
                    <dockerHost>${docker.host}</dockerHost>
                    <baseImage>java:8</baseImage>
                    <entryPoint>["java", "-jar", "-Dspring.profiles.active=prod","/${project.build.finalName}.jar"]
                    </entryPoint>
                    <resources>
                        <resource>
                            <targetPath>/</targetPath>
                            <directory>${project.build.directory}</directory>
                            <include>${project.build.finalName}.jar</include>
                        </resource>
                    </resources>
                </configuration>
            </plugin>
```

应用打包后的docker.yml配置也在项目中可以找到，启动后应用即可访问。

## 构建中的问题

解决zookeeper 防火墙状态下启动容器

```shell
firewall-cmd --zone=public --add-port=2181/tcp --permanent
firewall-cmd --zone=public --add-port=2888/tcp --permanent
firewall-cmd --zone=public --add-port=3888/tcp --permanent
firewall-cmd --zone=public --add-port=9080/tcp --permanent
重启docker 网络状态发生改变
```

连不上docker 客户端，尝试将环境的防火墙关闭，并编辑docker 文件，开启相应端口，重启docker。打完镜像后记得打开防火墙，以及关闭对应端口。

```shell
vi /usr/lib/systemd/system/docker.service
#修改为
ExecStart=/usr/bin/dockerd -H tcp://0.0.0.0:2375 -H unix://var/run/docker.sock
```

解决mysal外部访问数据乱码

```shell
 docker exec -it mysql mysql -u root -p'root'
SET NAMES 'utf8';
```

mysql  密码兼容问题，网上很多都是'root'@'localhost'，外网连接正确的是

```
ALTER USER 'root' IDENTIFIED WITH mysql_native_password BY 'xx';
```





