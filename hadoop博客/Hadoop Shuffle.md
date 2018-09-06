## Hadoop Shuffle

huffle的主要工作是从Map结束到Reduce开始之间的过程, 如下图

<center>
    <img src = "4"/>
</center>


**shuffle阶段又可以分为Map端的shuffle和Reduce端的shuffle。**

### Map 端shuffle

**spill**： Map端处理输入数据会产生中间结果，中间结果首先会写入内存缓冲区中，当缓冲区不够时，则将数据写入到磁盘，**这个过程较spill**。

**二次排序，多次合并**：写入磁盘前，首先根据数据所属的partition进行排序，然后每个partition中的数据再按key来排序。

**partition**的目是将记录划分到不同的Reducer上去，以期望能够达到负载均衡，以后的Reducer就会根据partition来读取自己对应的数据。

多次合并是将磁盘中的多个数据块合并成单独一块的过程。

### Reduce端的shuffle

Reduce端的shuffle主要包括三个阶段，copy、sort(merge)和reduce。

首先将map端的输出文件拷贝到Reduce端。每个Reducer处理一个或者多个partion。

接下来就是sort阶段，也成为merge阶段，因为这个阶段的主要工作是执行了归并排序。从Map端拷贝到Reduce端的数据都是有序的，所以很适合归并排序。最终在Reduce端生成一个较大的文件作为Reduce的输入。

最后就是Reduce过程了，在这个过程中产生了最终的输出结果，并将其写到HDFS上。

<center>
    <img src = "5"/>
</center>









