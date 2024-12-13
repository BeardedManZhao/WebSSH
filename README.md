# OnOn-WebSsh springBoot 服务器

> OnOn-WebSSH (On~On~ Lightweight WebSSH) enables SSH client operations within a web browser, supporting multi-user and
> multithreaded operations. It allows for specified SSH connections and supports SFTP as well as persistent SSH sessions.

OnOn-WebSsh (昂~昂~轻量级WebSSH) 可实现 网页 中的 ssh 客户端操作，支持多用户多线程操作, 支持指定ssh 连接, 支持sftp 以及
ssh 持久化.

官网：[访问官网页面](http://webssh.lingyuzhao.top:8080/about.html)

# 社区 qq 群

> 大家可以直接从这里进入来咨询作者和交流哦！！

![无标题](https://github.com/user-attachments/assets/0d33aa4c-099e-4ac1-9f0a-0ec48199da15)

## WebSsh

一个基于Java的WebSSH项目，使用 springBoot 做服务器，基于SSH协议，支持Linux、Windows、Mac等系统。其不需要任何外部依赖，具有非常快速的性能！！！

![image](https://github.com/user-attachments/assets/f4f75238-ddc8-4117-9400-70d09b422ecd)

## 下载

您可以[点击这里](https://github.com/BeardedManZhao/CodeBookWebSsh/releases/download/2024.12.14/WebSSH.jar) 下载网站服务包。

## 启动

项目导入IDEA后可以直接进行运行，没有任何外部依赖~~

**我（lingYuZhao）提供了在线使用网站**：[点击访问 WebSsh 在线](http://webssh.lingyuzhao.top:8080)

## 访问页面

浏览器访问：`http://<youServerIP>:8080` 可以直接开始输入参数以及使用！

## 写在最后

请注意，本库相较于其前身进行了大幅度的重构和改进（其前身代码不足以规范，浪费了大量的nio性能，且时间比较久远），几乎所有的代码都已更新,与其仅仅是前身的关系，不是同一个项目，因此在使用时请留意这些变化。尽管如此，我们依然尽可能地保留了原始库的使用方式，以降低用户的迁移成本和学习曲线。

本库的前身是由 [NoCortY/WebSSH GitHub链接](https://github.com/NoCortY/WebSSH)
开发的项目。遗憾的是，原作者已多年未对该仓库进行维护。现在，我们将接手继续维护和发展此项目，并致力于传承与改进它，为用户提供更优质的服务和支持。

## 更新日志

### 2024.12.19 版本发布

- 优化了配置文件中一些配置为空的时候的默认值设置，有效避免由于配置为null导致的程序无法启动的问题。
- 优化了前端提示
- 支持 ssh 配置的存储

### 2024.12.17 版本发布

- 新增文件上传与下载功能
- 新增安全模块，实现登录鉴权，可按照正则指定源IP和目标IP允许规则，默认规则是允许所有内网设备 ssh 到所有设备！
