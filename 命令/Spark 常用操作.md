## Spark 常用操作

### aggregateByKey

- 函数原型：`aggregateByKey(zeroValue, seqFunc, combFunc, numPartitions=None)`
- 参数与aggregate相同
- 根据key进行合并
- 上例稍加改动可以完成一个wordcounts

```
sc.parallelize(["hello world", "hello morning"])\
.flatMap(lambda line: line.split())\
.map(lambda letter: (letter, 1)).aggregateByKey(0, lambda x,y: y+x, lambda x,y: x+y)\
.collect()
# [(1, 1), (1, 2), (2, 1), (2, 2)]
```

------

### cartesian

- 返回两个rdd的笛卡儿积

```
rdd1 = sc.parallelize([1, 2])
rdd2 = sc.parallelize([3, 4, 5])
rdd1.catesian(rdd2).cellect()
# [(1, 1), (1, 2), (2, 1), (2, 2)]
```

------

### glom

- 将一个一维横向列表，划分为多个块

```
sc.parallelize([1,2,3,4,5], 1).collect()
# [1, 2, 3, 4, 5]
sc.parallelize([1,2,3,4,5], 1).glom().collect()
# [[1, 2, 3, 4, 5]]
sc.parallelize([1,2,3,4,5], 2).glom().collect()
# [[1, 2], [3, 4, 5]]
```

------

### coalesce

- 将多个块组合成n个大的列表

```
sc.parallelize([1,2,3,4,5], 3).coalesce(2).glom().collect()
# [[1], [2, 3, 4, 5]]
sc.parallelize([1,2,3,4,5], 3).coalesce(2).collect()
# [1, 2, 3, 4, 5]
sc.parallelize([1,2,3,4,5], 3).glom().collect()
# [[1], [2, 3], [4, 5]]
```

------

### cogroup

- 函数原型：`cogroup(other, numPartitions=None)`
- 按key聚合后，求两个RDD的并集。

```
x = sc.parallelize([("a", 1), ("b", 4)])
y = sc.parallelize([("a", 2)])
map((lambda (x,y): (x, (list(y[0]), list(y[1])))), sorted(list(x.cogroup(y).collect())))
# [('a', ([1], [2])), ('b', ([4], []))]
```

------

### collectAsMap

- 将rdd数据按KV对形式返回

```
sc.parallelize([(1,2), (3,4)]).collectAsMap()
# {1: 2, 3: 4}
sc.parallelize([(1, (2, 6666)), (3, 4)]).collectAsMap()
# {1: (2, 6666), 3: 4}
```

------

### combineByKey

- 函数原型：`combineByKey(createCombiner, mergeValue, mergeCombiners, numPartitions=None)`
- 根据key进行

------

### count

- 返回rdd中元素的数目

```
sc.parallelize([2,3,4]).count()
# 3
```

------

### countByKey

- 按key聚合后计数

```
rdd = sc.parallelize([("a", 1), ("b", 1), ("a", 1)])
rdd.countByKey().items()
# [('a', 2), ('b', 1)]
```

------

### countByValue

- 按value聚合后再计数

```
sc.parallelize(["hello", "world", "hello", "china", "hello"]).countByValue().items()
# [('world', 1), ('china', 1), ('hello', 3)]
```

------

### countApprox

- countApprox(timeout, confidence=0.95) 貌似在公司版本中还未提供 count的一个升级版（实验中），当超过timeout时，返回一个未完成的结果。

```
rdd = sc.parallelize(range(1000), 10)
rdd.countApprox(1000, 1.0)
# 1000
```

------

### distinct

- distinct(numPartitions=None) 返回rdd中unique的元素

```
sorted(sc.parallelize([1, 1, 2, 3]).distinct().collect()
# [1, 2, 3]
```

------

### filter

- 过滤一个RDD中，其每一行必须瞒住filter的条件

```
rdd = sc.parallelize([1, 2, 3, 4, 5])
rdd.filter(lambda x: x%2==0).collect()
# [2, 4]
```

------

### first

- 返回rdd中的第一个元素

```
sc.parallelize([2, 3, 4]).first()
```

------

### flatMap

- flatMap(f, preservesPartitioning=False) 返回rdd中的所有元素，并把flatMap中返回的列表拉平。

```
rdd = sc.parallelize([2, 3, 4])
rdd.flatMap(lambda x: range(1, x)).collect()
# [1, 1, 1, 2, 2, 3]
```

------

### flatMapValues

- 同flatMap，但按照key进行flat，并最终拉平。

```
x = sc.parallelize([("a", ["x", "y", "z"]), ("b", ["p", "r"])])
def f(x): return x
x.flatMapValues(f).collect()
# [('a', 'x'), ('a', 'y'), ('a', 'z'), ('b', 'p'), ('b', 'r')]
```

------

### fold

- fold(zeroValue, op) 聚合RDD的每一个分区，最后再合并计算，每一个函数默认值为"zeroValue"。 op(t1,t2)函数可以更改t1并且将更改后的t1作为返回值返回以减少对象内存占用。切记不可个性t2的值。

```
def add(x,y): return x+y
sc.parallelize([1, 2, 3, 4, 5]).fold(0, add)
# 15
```