


# RedisServer

Redis在实际开发中登场率极高，本项目是java 版本的redis server。
在redis-benchmark测试下秒吞吐量能达到Redis的80%-95%,0.5%的可能延迟超过100ms小于1s。


# 项目功能
- 命令：支持服务连接与五大基本类型string、list、set、zset、hash相关的命令

- 存储：支持AOF持久化日志

- 多路复用：支持 epoll，kqueue，select 默认优先级由高到低，同时支持本地和单路复用
，强烈推荐使用单路select线程模型

- 过期策略：采用懒惰删除


#### 架构简介

集群架构方式：客户端路由



####  如何连接？

redis-client 或者 redis-desktop-manager 都可以





# 项目中文件
- datatype 数据类型底层

- resp byte[]数组解码编码方式

- command 指令的具体实现

- channel netty的tcp连接
- clink 实现基于socket的tcp连接
- heartbeat 自定义心跳包实现
- resp 指令的序列化与反序列化
- aof 实现aof持久化

## 存储的具体表现

![img_1.png](img_1.png)

# 待优化
- 还未实现集群和主从模式
