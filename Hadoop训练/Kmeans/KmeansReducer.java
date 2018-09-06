public class KMeansReducer extends Reducer<IntWritable, MyWritable, Text, NullWritable> {
 
	private String splitter ;
	private Pattern pattern;
	private String[] centerVec = null;
	private int k;
	
	private Logger log = LoggerFactory.getLogger(KMeansReducer.class);
	@Override
	protected void setup(Context context)
			throws IOException, InterruptedException {
		splitter = context.getConfiguration().get(SPLITTER);
		pattern = Pattern.compile(",");
		k= context.getConfiguration().getInt(K, 0);
		centerVec = new String[k];
	}
	
	
	@Override
	protected void reduce(IntWritable key, Iterable<MyWritable> values,
			Context arg2) throws IOException, InterruptedException {
		double[] sum=null;
		long  num =0;
		for(MyWritable value:values){
			int number = value.getNum();
			String[] valStr = pattern.split(value.getData().toString(), -1);
			if(sum==null){// 初始化
				sum=new double[valStr.length];
				addToSum(sum,valStr);// 第一次需要加上
			}else{
			//	对应字段相加
				addToSum(sum,valStr);
			}
			num += number;			
		}
		averageSum(sum,num);
		centerVec[key.get()]= format(sum);
	}
	private Text vec = new Text();
	/**
	 * 直接输出数组centerVec
	 */
	@Override
	protected void cleanup(Context context)
			throws IOException, InterruptedException {
		for(int i=0;i<centerVec.length;i++ ){
			
			vec.set(centerVec[i]);
			context.write(vec, NullWritable.get());
		}
	}
 
/**
 * 求平均值
 * @param sum
 * @param num
 */
	private void averageSum(double[] sum, long num) {
		//求平均值
		
		for(int i=0;i<sum.length;i++){
			sum[i]=sum[i]/num;
		}
	
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
 
/**
 * 格式化数组
 * 数组元素之间的分隔符采用splitter即可
 * @param sum
 * @return
 */
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
