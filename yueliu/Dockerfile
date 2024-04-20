# 使用 OpenJDK 11 作为基础镜像
FROM openjdk:11

# 将本地的target目录下的jar包复制到docker中的/app目录
COPY target/yueliu-1.0-SNAPSHOT-jar-with-dependencies.jar /app/yueliu.jar

# 设置工作目录
WORKDIR /app

# 定义容器启动命令
CMD ["java", "-jar", "yueliu.jar"]

