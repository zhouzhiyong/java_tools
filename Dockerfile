# 第一个阶段：使用 Maven 镜像进行构建
FROM maven:3.6.3-openjdk-11 AS builder

# 设置工作目录
WORKDIR /app

# 将整个项目目录复制到容器中的工作目录
COPY . .

# 安装依赖的本地JAR包到Maven本地仓库
RUN mvn install:install-file -Dfile=/app/dep/Time-NLP-1.0.0.jar -DgroupId=org.apache.maven.plugins -DartifactId=maven-source-plugin -Dversion=1.0 -Dpackaging=jar

# 在/app/yueliu目录下进行项目构建
WORKDIR /app/yueliu
RUN mvn clean package

# 第二个阶段：使用 OpenJDK 11 作为基础镜像，只复制构建好的JAR文件
FROM openjdk:11
WORKDIR /app
COPY --from=builder /app/yueliu/target/yueliu-1.0-SNAPSHOT-jar-with-dependencies.jar /app/yueliu.jar
CMD ["java", "-jar", "yueliu.jar"]

