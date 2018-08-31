### 

## Hadoop Multi Node Cluster的安装

承接上文的SingleNode Cluster的安装，现在把上文的单个结点作为master结点，然后复制虚拟机得到2个slaver结点，master和slavers组成hdfs架构。

#### 修改master结点的相关配置

编辑hostname主机名

```c++
vim /etc/hostname
```

<center>
    <img src="C:\Users\USER\Desktop\1.png"/>
</center>

1. 编辑core-site.xml

   设置HDFS的默认名称，当使用命令或程序获取HDFS时，可使用此名称。之前single node因为只有一台计算机，所以设置namenode位置为localhost即可，但是现在有多态计算机，必须指定主机名。

   ```c++
   vim /usr/local/hadoop/etc/hadoop/core-site.xml
   ```

   <center>
       <img src="C:\Users\USER\Desktop\2.png"/>
   </center>

2. 编辑yarn-site.xml

   - 设置ResourceManager 主机与NodeManager的连接地址
   - 设置ResourceManager与ApplicationMaster的连接地址
   - 设置ResourceManager与客户端的连接地址

   <center>
       <img src="C:\Users\USER\Desktop\3.png"/>
   </center>

3. 编辑mapred-site.xml

   ```
   vim /usr/local/hadoop/etc/hadoop/mapred-site.xml
   ```

   <center>
       <img src="C:\Users\USER\Desktop\4.png"/>
   </center>

4. 编辑hdfs-site.xml

   master没有datanode，所以删除原来的datanode设置。

   <center>
       <img src="C:\Users\USER\Desktop\5.png"/>
   </center>

5. 编辑master文件

   ```
   vim /usr/local/hadoop/etc/hadoop/masters		
   ```

   master文件告诉hadoop系统哪一台服务器是NameNode

   <center>
       <img src="C:\Users\USER\Desktop\6.png"/>
   </center>

6. 编辑slaves文件

   slaves文件告诉Hadoop系统哪些服务器是DataNode

   ```
   vim /usr/local/hadoop/etc/hadoop/slaves	
   ```

   <center>
       <img src="C:\Users\USER\Desktop\7.png"/>
   </center>

#### 复制master服务器到slaver1, slaver2

复制完成后设置如下

1. 修改hostname，改为slaver1、slaver2

2. 修改两个服务器的hdfs-site.xml

   slaver1和slaver2仅有datanode，所以需删除原来的namenode设置，添加datanode设置。

   <center>
       <img src="C:\Users\USER\Desktop\9.png"/>
   </center>

3. 编辑3个服务器的hosts文件

   ```
   vim /etc/hosts
   ```

   <center>
       <img src="C:\Users\USER\Desktop\8.png"/>
   </center>

   hosts文件为每一个服务器的ip地址添加别名，用ssh登录其他服务器时可以用域名代替ip地址。比如，登录slaver1服务器可以使用这样子的命令：

   ```
   ssh slaver1
   ```

#### master连接到slaver1、slaver2创建HDFS目录

通过ssh登录到slaver1和slaver2，删除hdfs所有目录，创建DataNode存储目录。

```
ssh slaver1
rm -rf /usr/local/hadoop/hadoop_data/hdfs
mkdir -p /usr/local/hadoop/hadoop_data/hdfs/datanode
exit
ssh slaver2
rm -rf /usr/local/hadoop/hadoop_data/hdfs
mkdir -p /usr/local/hadoop/hadoop_data/hdfs/datanode
exit
```

回到master服务器，格式化NameNode HDFS目录

**注意：只可以在master服务器上格式化**

```
hadoop namenode -format	
```

#### 启动Hadoop Multi Node Cluster

```
start-all.sh
```

#### 通过jps查看各个服务器的运行情况

<center>
    <img src="C:\Users\USER\Desktop\11.png"/><img src="C:\Users\USER\Desktop\12.png"/><img src="C:\Users\USER\Desktop\10.png"/>
</center>



