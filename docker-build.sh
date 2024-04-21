#!/usr/bin/env bash

VERSION=0.1.0
VERSION_DATE=$(date "+%Y%m%d%H%M")

docker build -t registry.cn-beijing.aliyuncs.com/dengtaai/java-tools:$VERSION .
docker tag registry.cn-beijing.aliyuncs.com/dengtaai/java-tools:$VERSION registry.cn-beijing.aliyuncs.com/dengtaai/java-tools:$VERSION_DATE
docker tag registry.cn-beijing.aliyuncs.com/dengtaai/java-tools:$VERSION registry.cn-beijing.aliyuncs.com/dengtaai/java-tools:latest

docker push registry.cn-beijing.aliyuncs.com/dengtaai/java-tools:$VERSION
docker push registry.cn-beijing.aliyuncs.com/dengtaai/java-tools:$VERSION_DATE
docker push registry.cn-beijing.aliyuncs.com/dengtaai/java-tools:latest

