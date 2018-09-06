## Hadoop Map Reduce & YARN

分布式框架，机器之间彼此通信协同工作，以高度分布式共同存储和处理大量数据。

Map 任务 在输入数据的子集上调用 map 函数。在完成这些调用后，reduce 任务 开始在 map 函数所生成的中间数据上调用 reduce 任务，生成最终的输出。 map 和 reduce 任务彼此单独运行，这支持并行和容错的计算。

### MapShuffle 原理

<center>
    <img src = "6"/>
</center>

### ReduceShuffle原理

<center>
    <img src = "7"/>
</center>



##### 原MapReduce框架的不足

- JobTracker是集群事务的集中处理点，存在单点故障
- JobTracker需要完成的任务太多，既要维护job的状态又要维护job的task的状态，造成过多的资源消耗
- 在taskTracker端，用map/reduce task作为资源的表示过于简单，没有考虑到CPU、内存等资源情况，当把两个需要消耗大内存的task调度到一起，很容易出现OOM
- 把资源强制划分为map/reduce slot,当只有map task时，reduce slot不能用；当只有reduce task时，map slot不能用，容易造成资源利用不足。

##### 解决可伸缩性问题

在 Hadoop MapReduce 中，JobTracker 具有两种不同的职责：

- 管理集群中的计算资源，这涉及到维护活动节点列表、可用和占用的 map 和 reduce slots 列表，以及依据所选的调度策略将可用 slots 分配给合适的作业和任务
- 协调在集群上运行的所有任务，这涉及到指导 TaskTracker 启动 map 和 reduce 任务，监视任务的执行，重新启动失败的任务，推测性地运行缓慢的任务，计算作业计数器值的总和，等等

为单个进程安排大量职责会导致重大的可伸缩性问题，尤其是在较大的集群上，JobTracker 必须不断跟踪数千个 TaskTracker、数百个作业，以及数万个 map 和 reduce 任务。相反，TaskTracker 通常近运行十来个任务，这些任务由勤勉的 JobTracker 分配给它们。

为了解决可伸缩性问题，一个简单而又绝妙的想法应运而生：我们减少了单个 JobTracker 的职责，将部分职责委派给 TaskTracker，因为集群中有许多 TaskTracker。在新设计中，这个概念通过将 JobTracker 的双重职责（集群资源管理和任务协调）分开为两种不同类型的进程来反映。

