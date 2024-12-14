# 如果国内小伙伴出现问题 可以执行：docker pull beardedmanzhao/jdk17 然后在构建！
FROM beardedmanzhao/jdk17
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
VOLUME /tmp
ADD ./target/WebSSH.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]