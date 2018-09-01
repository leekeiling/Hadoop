## Hadoop简介

### HDFS文件存储架构

1. 文件分割

   文件会分割成多个block存储在不同结点中

2. 区块副本

   每个block会复制多份存储在不同结点中，当有一块损坏时，副本可以用来恢复数据。

3. 机架感知

   每个机架包含若干个datanode结点，每个datanode结点在其他机架都保存有副本，因此任何机架出现故障可以保证恢复数据，提高网络性能。


### Hadoop Yarn资源管理架构

1. 在Client客户端，用户会向Resource Manager请求执行计算（或执行任务）

2. 在NameNode会有Resource Manager统筹管理运算请求

3. 在其他DataNode会有NodeManger负责运行，以及监督每一个任务


### Spark Cluster模式架构

1. DriverProgram是Spark程序，在Spark程序中必须定义SparkContext，是应用程序的入口

2. SparkContext通过Cluster Manger管理集群，群集中包含多个Worker Node，在每个Worker Node都有Executor负责执行任务。
