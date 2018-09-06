public class KmeansCombiner extends Reducer<IntWritable, MyWritable, IntWritable, MyWritable>{
	private String splitter ;
	private Pattern pattern;
	@Override
	protected void setup(Context context)
			throws IOException, InterruptedException {
		splitter = context.getConfiguration().get(SPLITTER);
		pattern = Pattern.compile(",");
	}
	/***
	 * map1 -> (0,"1.1,1.2,1.3"),(0,"1.3,1.2,1.4"),(1,"4.6,5.7,8.8")
	 * combiner1 -> (0,"2.4,2.4,2.7") , (1,"4.6,5.7,8.8")
	 * map2 -> (0,"2.1,2.4,1.2"),(2,"12.1,11.1,13.2"),(2,"14.1,12.3,15.2")
	 * combiner2 ->(0,"2.1,2.4,1.2"),(2,"26.2,23.4,28.4")
	 * 因此 combiner传值给reducer的时候需要传递当前类别的个数
	 * 
	 */
	MyWritable result = new MyWritable();
	@Override
	protected void reduce(IntWritable key, Iterable<MyWritable> values,
			Context context)
			throws IOException, InterruptedException {
		double[] sum=null;
		long  num =0;
		for(MyWritable value:values){
			String[] valStr = pattern.split(value.getData().toString(), -1);
			if(sum==null){// 初始化
				sum=new double[valStr.length];
				addToSum(sum,valStr);// 第一次需要加上
			}else{
			//	对应字段相加
				addToSum(sum,valStr);
			}
			num++;			
		}
		result.setData(format(sum));
		result.setNum((int) num);
		context.write(key, result);
	}
	
	/**
	 * 对应字段相加
	 * @param sum
	 * @param valStr
	 */
		private void addToSum(double[] sum, String[] valStr) {
			//  实现功能
			for(int i=0;i<sum.length;i++){
				sum[i]+=Double.parseDouble(valStr[i]);
			}
 
		}
		private String format(double[] sum) {
			//完善功能
			String str="";
			for(int i=0;i<sum.length;i++){
				if(i==0){
					str=str.concat(String.valueOf(sum[i]));
				}else{
					str=str.concat(splitter+String.valueOf(sum[i]));
				}
			}
			return str;
		}
}
