## Spark四种运行模式

转载：http://blog.cheyo.net/29.html

### 介绍

- 本地模式



Spark单机运行，一般用于开发测试。

- Standalone模式

构建一个由Master+Slave构成的Spark集群，Spark运行在集群中。

- Spark on Yarn模式

Spark客户端直接连接Yarn。不需要额外构建Spark集群。

- Spark on Mesos模式

Spark客户端直接连接Mesos。不需要额外构建Spark集群。

#### 启动方式: spark-shell.sh(Scala)

spark-shell通过不同的参数控制采用何种模式进行。 涉及两个参数：

```c++
--master MASTER_URL         spark://host:port, mesos://host:port, yarn, or local.
--deploy-mode DEPLOY_MODE   Whether to launch the driver program locally ("client") or
                            on one of the worker machines inside the cluster ("cluster")
                            (Default: client).
```

–master参数用于指定采用哪种运行模式。
对于Spark on Yarn模式和Spark on Mesos模式还可以通过 –deploy-mode参数控制Drivers程序的启动位置。

- 进入本地模式：

  ```c++
  ./spark-shell --master local
  ./spark-shell --master local[2]  # 本地运行,两个worker线程,理想状态下为本地CPU core数
  ```

- 进入Standalone模式：

  ```c++
  ./spark-shell --master spark://192.168.1.10:7077
  ```

  备注：测试发现MASTER_URL中使用主机名替代IP地址无法正常连接(hosts中有相关解析记录)，即以下命令连接不成功

  ```c++
  ./spark-shell --master spark://ctrl:7077  # 连接失败
  ```

- Spark on Yarn模式

  ```c++
  ./spark-shell --master yarn
  ./spark-shell --master yarn-client
  #不支持这种模式
  #./spark-shell --master yarn-cluster
  ./spark-shell --master yarn --deploy-mode client
  #不支持这种模式
  #./spark-shell --master yarn --deploy-mode cluster
  ```

- Spark on Mesos模式：

  ```c++
  ./spark-shell --master mesos://host:port
  ./spark-shell --master mesos://host:port --deploy-mode client
  ./spark-shell --master mesos://host:port --deploy-mode cluster
  ```

  #### 启动方式: pyspark(Python)

  参数及用法与Scala语言的spark-shell相同，比如：

  ```c++
  pyspark --master local[2]
  ```




