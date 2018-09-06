public class KmeansPartional extends Partitioner<IntWritable, Text>{
 
	@Override
	public int getPartition(IntWritable key, Text value, int arg2) {
		if(key.get() == 0){
			return 0;
		}else if(key.get() == 1){
			return 1;
		}else{
		return 2;
		}
	}
 
}
