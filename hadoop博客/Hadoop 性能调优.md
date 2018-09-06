## Hadoop 性能调优

**一个通用的原则是给shuffle过程分配尽可能大的内存**，当然你需要确保map和reduce有足够的内存来运行业务逻辑。因此在实现Mapper和Reducer时，应该尽量减少内存的使用，例如避免在Map中不断地叠加。

### map优化：

在map端，避免写入多个spill文件可能达到最好的性能，一个spill文件是最好的， 减少IO消耗



### Reduce优化：

在reduce端，如果能够让所有数据都保存在内存中，可以达到最佳的性能。



通用优化：

Hadoop默认使用4KB作为缓冲，这个算是很小的，可以通过`io.file.buffer.size`来调高缓冲池大小。