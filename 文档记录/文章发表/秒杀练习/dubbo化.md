## 版本和架构选择



docker + nacos 架构 （舍去zookeeper，nacos更像）

docker 版本  2.7

springboot 版本  2.6.1

分布式事务（tcc -transaction 1.73）



镜像源的坑，默认镜像源比阿里镜像源资源更多，网速不差的时候使用默认镜像源，springboot 版本  2.6.1使用默认镜像

```maven
    <mirror>
      <id>mirrorId</id>
      <mirrorOf>repositoryId</mirrorOf>
      <name>Human Readable Name for this Mirror.</name>
      <url>http://my.repository.com/repo/path</url>
    </mirror>
```





## 代码拆分

模块工作 

common 模块

```
<dependencyManagement>
    <dependencies>
        <!-- 服务公共工具包 -->
        <dependency>
            <groupId>com.travel.common</groupId>
            <artifactId>common</artifactId>
            <version>${common.version}</version>
        </dependency>
        .....
```

order模块

## 关于spring boot - pom文件继承

```maven\
//父pom项目
<parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.2.1.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    //dependencies import
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>2.2.1.RELEASE</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
```

使用这种方式就不用继承父模块，可以解决单继承的问题。这样就可以继承其他父模块，比如自己创建的父模块。

scope=import，type=pom表示在此pom中引入spring-boot-dependencies的pom的所有内容，注意只能在dependencyManagement中使用。
