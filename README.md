
## 介绍

java 版本的redis server

命令：仅支持服务连接与五大基本类型相关的命令

存储：仅支持AOF日志

多路复用：支持 epoll，kqueue，select 默认优先级由高到低，同时支持本地和单路复用

强烈推荐使用单路select线程模型

#### 功能介绍

支持服务间的消息传递和数据共享

#### 架构简介

集群架构方式：客户端路由



####  如何连接？

redis-client 或者 redis-desktop-manager 都可以

###  压测结果

####  秒吞吐量

同样资源下 秒吞吐量是Redis的80%-95%

####  延迟



0.5%的可能延迟超过100ms小于1s

## 模块介绍
### datatype 
数据类型底层

### resp
byte[]数组解码编码方式

### command
指令解码、编码方式

### channel netty
tcp连接

### aof
实现aof持久化






