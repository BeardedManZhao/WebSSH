# OnOn-WebSsh springBoot 服务器

> OnOn-WebSSH (On~On~ Lightweight WebSSH) enables SSH client operations within a web browser, supporting multi-user and
> multithreaded operations. It allows for specified SSH connections and supports SFTP as well as persistent SSH sessions.

OnOn-WebSsh (昂~昂~轻量级WebSSH) 可实现 网页 中的 ssh 客户端操作，支持多用户多线程操作, 支持指定ssh 连接, 支持sftp 以及
ssh 持久化.

官网：[访问官网页面](http://webssh.lingyuzhao.top:8082/about.html)

# 社区 qq 群

> 大家可以直接从这里进入来咨询作者和交流哦！！

![无标题](https://github.com/user-attachments/assets/0d33aa4c-099e-4ac1-9f0a-0ec48199da15)

## WebSsh

一个基于Java的WebSSH项目，使用 springBoot 做服务器，基于SSH协议，支持Linux、Windows、Mac等系统。其不需要任何外部依赖，具有非常快速的性能！！！

![image](https://github.com/user-attachments/assets/f4f75238-ddc8-4117-9400-70d09b422ecd)

**我（lingYuZhao）提供了在线使用网站**：[点击访问 WebSsh 在线](http://webssh.lingyuzhao.top:8082)

## 关于手动部署

手动部署操作将更加的节省性能，其将直接运行在服务器上！不过需要使用 java 17 来运行~

### 1. 下载 jar 包

> 如果有 java8 的需求，可以直接在 `pom.xml` 中为 springBoot 降级！但这会降低一些性能哦！因为 java17 的性能比 java8 更高。

您可以[点击这里](https://github.com/BeardedManZhao/WebSSH/releases) 下载网站服务包。

### 2. 启动服务包

直接使用 `java -jar WebSSH.jar` 进行运行，没有任何外部依赖~~

## 关于 docker 部署

本章节之前讲解的是使用 SpringBoot jar 包来部署这个服务，事实上，我们也可以使用 docker 来部署这个服务。

### 1. 使用 docker 部署

```shell
docker pull beardedmanzhao/webssh && docker run -d -p 8080:8080 beardedmanzhao/webssh
```

### 2. 使用 docker 镜像部署

#### 下载镜像

可以直接前往包管理仓库中下载名为 `webssh.iso.tar` 的文件，然后使用如下命令即可。
```shell
docker import webssh.iso.tar beardedmanzhao/webssh && docker run -d -p 8080:8080 beardedmanzhao/webssh
```

### 访问页面

浏览器访问：`http://<youServerIP>:8080` 可以直接开始输入参数以及使用！

## 配置文件完整格式

如果您是 docker 环境，您可以在配置文件目录中修改配置，若您是 本地环境，您可以根据下面的操作来创建您的自定义配置文件！

```yaml
server:
  port: 8080

spring:
  # 配置 HTTP 多部分文件上传
  servlet:
    # 文件上传相关配置 由于浏览器到服务器是 http 因此需要在这里配置
    multipart:
      # 指定临时文件的存储位置
      # 注意：此目录必须存在，并且应用程序应具有写入权限
      location: /opt/OnOnWebSsh/temp
      # 指定临时文件的位置（与location配置相同）
      # 这个配置在较新版本的Spring中可能不是必需的
      # temp-location: /opt/OnOnWebSsh/temp
      # 单个文件的最大大小
      max-file-size: 1024MB
      # 整个请求中所有文件的总大小的最大值，默认值为多部分数据的最大大小
      max-request-size: 1030MB
      # 是否启用文件大小检查，默认为true
      enabled: true
      # 当文件大小超过这个阈值时，才会使用临时文件存储
      file-size-threshold: 4MB
  web:
    resources:
      static-locations: classpath:/static/

logging:
  level:
    root: INFO
  file:
    name: logs/webssh.log
  pattern:
    console: '%d{yyyy-MM-dd HH:mm:ss} - %msg%n'
    file: '%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n'

# OnOn WebSSH 配置
on-on-web-ssh:
  # 文件进度打印相关
  file-progress-monitor-config:
    # 文件上传时，每上传多少字节就打印一次进度 这里是 8MB
    update-threshold: 8388608
  # 安全设置
  secure-config:
    # 源IP地址匹配模式
    src-ip-pattern: "(10(\\.[0-9]{1,3}){3}|172\\.(1[6-9]|2[0-9]|3[0-1])(\\.[0-9]{1,3}){2}|192\\.168(\\.[0-9]{1,3}){2})"
    # 目标IP地址匹配模式 默认为空，表示不限制
    # dest-ip-pattern: "(10(\\.[0-9]{1,3}){3}|172\\.(1[6-9]|2[0-9]|3[0-1])(\\.[0-9]{1,3}){2}|192\\.168(\\.[0-9]{1,3}){2})"
  # 数据持久化设置 这会存储一些 ssh 信息
  storage-config:
    # 启用持久化模块 如果不启用 则全程将不会进行ssh数据的记录 (但是全程可以读取哦!) 若启用则代表所有成功连接的 ssh 配置都会被记录！
    enable-writer: true
    # 持久化目录
    storage-dir: storage
  # 定时设置
  scheduled:
    # 设置 ssh 信息存储的时间间隔
    ssh-storage:
      interval: 3600000

```

您可以使用下面的命令来指定配置文件的启动

```shell
java -Dspring.config.location=xxx.yaml -jar WebSSH.jar
```

## 写在最后

请注意，本库相较于其前身进行了大幅度的重构和改进（其前身代码不足以规范，浪费了大量的nio性能，且时间比较久远），几乎所有的代码都已更新,与其仅仅是前身的关系，不是同一个项目，因此在使用时请留意这些变化。尽管如此，我们依然尽可能地保留了原始库的使用方式，以降低用户的迁移成本和学习曲线。

[本库 (BeardedManZhao/WebSSH)](https://github.com/BeardedManZhao/WebSSH) 的前身是由 [NoCortY/WebSSH GitHub链接](https://github.com/NoCortY/WebSSH)
开发的项目。遗憾的是，原作者已多年未对该仓库进行维护。现在，我们将接手继续维护和发展此项目，并致力于传承与改进它，为用户提供更优质的服务和支持。

## 更新日志

### 2025.06.11 版本发布

- 修复了单行无法自动换行（超过行长度将回到行首覆盖字符）的问题

### 2025.06.10 版本发布

- 修复了样式问题，之前的样式是依赖码本录的，码本录的样式修改会导致此库的样式出现问题 因此我们在这个版中修复了此问题！

### 2024.12.19 版本发布

- 优化了配置文件中一些配置为空的时候的默认值设置，有效避免由于配置为null导致的程序无法启动的问题。
- 优化了前端提示
- 支持 ssh 配置的存储

### 2024.12.17 版本发布

- 新增文件上传与下载功能
- 新增安全模块，实现登录鉴权，可按照正则指定源IP和目标IP允许规则，默认规则是允许所有内网设备 ssh 到所有设备！
