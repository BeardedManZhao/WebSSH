# 如果国内小伙伴出现问题 可以执行：docker pull beardedmanzhao/jdk17 然后在构建！
FROM beardedmanzhao/jdk17

# 设置 apt 源为阿里云镜像源
RUN sed -i 's/archive.ubuntu.com/mirrors.aliyun.com/g' /etc/apt/sources.list
# 设置 Locale 为 zh_CN.UTF-8
RUN apt-get update && \
    apt-get install -y locales && \
    locale-gen zh_CN.UTF-8 && \
    update-locale LANG=zh_CN.UTF-8

# 安装中文字体
RUN apt-get install -y fonts-wqy-zenhei

# 设置环境变量
ENV LANG=zh_CN.UTF-8 \
    LANGUAGE=zh_CN:zh \
    LC_ALL=zh_CN.UTF-8

# 开始构建！
RUN mkdir "/opt/OnOnWebSsh/"

WORKDIR /opt/OnOnWebSsh

ADD ./target/WebSSH.jar ./app.jar
COPY ./src/main/resources/config ./config

# 开权限
RUN chmod -R 777 /opt/OnOnWebSsh

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "echo '欢迎~ 您如果需要修改配置，可以使用 exec 模式，进入到 【/opt/OnOnWebSsh/config】 目录进行修改~ 然后重启容器就可以啦' && java -Djava.security.egd=file:/dev/./urandom -Dspring.config.location=./config/ -jar ./app.jar"]