### Centos 下载安装Hadoop

1. 安装java

   ```c++
   yum install java		
   ```

2. 安装并注册ssh服务

   ```c++
   下载sshd
   yum install openssh*
   注册使用服务
   systemctl enable sshd
   service sshd start
   开启防火墙的22端口
   firewall-cmd --zone=public add-port=22/tcp --permanent
   service firewalld restart
   ```

3. 产生ssh key进行后续身份验证

   ```c++
   ssh-keygen -t dsa -P '' -f ~/.ssh/id_dsa
   ```

4. 将产生的key放置到许可文件中

   ```c++
   cat ~/.ssh/id_dsa.pub >> ~/.ssh/authorized_keys
   ```

5. 下载hadoop

   ```c++
   wget https://archive.apache.org/dist/hadoop/common/hadoop-2.6.0/hadoop-2.6.0.tar.gz
   ```

6. 解压缩到/usr/local/hadoop目录

7. 设置环境变量

   编辑 ~/.bashrc, 后面添加如下配置，其中JavaHome是jdk的安装路径，需要根据自己的实际情况安装。

   编辑完之后 source ~/.bashrc生效

   ```c++
   export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.181-3.b13.el7_5.x86_64/jre
   export HADOOP_HOME=/usr/local/hadoop
   export PATH=$PATH:$HADOOP_HOME/bin
   export PATH=$PATH:$HADOOP_HOME/sbin
   export HADOOP_MAPRED_HOME=$HADOOP_HOME
   export HADOOP_COMMON_HOME=$HADOOP_HOME
   export HADOOP_HDFS_HOME=$HADOOP_HOME
   export YARN_HOME=$HADOOP_HOME
   export HADOOP_COMMON_LIB_NATIVE_DIR=$HADOOP_HOME/lib/native
   export HADOOP_OPTS="-Djava.library.path=$HADOOP_HOME/lib"
   export JAVA_LIBRARY_PATH=$HADOOP_HOME/lib/native:$JAVA_LIBRARY_PATH
   ```

8. 编辑 /usr/local/hadoop/etc/hadoop/hadoop-env.sh，修改JAVAHOME设置，改为jdk的安装路径

9. 修改/usr/local/hadoop/etc/haddop/core-site.xml，设置HDFS的默认名称

   ```c++
   <configuration>
   <property>
   	<name>fs.default.name</name>
   	<value>hdfs://localhost:9000</value>i
   </property>
   </configuration>
   ```

10. 编辑/usr/local/hadoop/etc/hadoop/yarn-site.xml, 设置MapReduce的相关配置

   ```c++
   <configuration>
   
   <!-- Site specific YARN configuration properties -->
   <property>
   	<name>yarn.nodemanager.aux-services</name>
   	<value>mapreduce_shuffle</value>
   </property>
   
   <property>
   	<name>yarn.nodemanager.aux-services</name>
   	<value>org.apache.hadoop.mapred.ShuffleHandler</value>
   </property>
   </configuration>
   ```

11. 修改上一个目录下的mapred.site.xml。

    首先复制模板文件：有mapred-site.xml.template至mapred-site.xml。

    然后编辑mapred-site.xml，设置mapreduce框架为yarn

    ```c++
    <configuration>
    <property>
    	<name>mapreduce.framework.name</name>
    	<value>yarn</value>
    </property>
    </configuration>
    ```

12. 编辑hdfs-site.xml

    内容是：

    设置blocks副本备份数量

    设置NameNode数据存储目录

    设置DataNode数据存储

    ```c++
    <configuration>
    <property>
    	<name>dfs.replication</name>
    	<value>3</value>
    </property>
    <property>
    	<name>dfs.namenode.name.dir</name>
    	<value> file:/usr/local/hadoop/hadoop_data/hdfs/datanode</value>
    </property>
    <property>
    	<name>dfs.datanode.data.dir</name>
    	<value>file:/usr/local/hadoop/hadoop_data/hdfs/datanode</value>
    </property>
    </configuration>
    ```

13. 创建并格式化HDFS目录

    创建Name Node数据存储目录

    mkdir -p /usr/local/hadoop/hadoop_data/hdfs/namenode

    创建DataNode数据存储目录

    mkdir -p /usr/local/hadoop/hadoop_data/hdfs/datanode

14. 将HDFS格式化

    ```c++
    hadoop namenode -format
    ```

15. 启动hadoop

    ```
    start-all.sh
    ```

