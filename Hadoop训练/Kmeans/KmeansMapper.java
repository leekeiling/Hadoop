public class KMeansMapper extends Mapper<LongWritable, Text, IntWritable, MyWritable> {
	private Logger logger = LoggerFactory.getLogger(KMeansMapper.class);
	private String centerPathStr="";
	private String splitter ="";
	private int k;// 存储聚类中心个数
	private String[] centerVec= null; // 存储聚类中心向量
	
	@Override
	protected void setup(Context context)
			throws IOException, InterruptedException {
		centerPathStr = context.getConfiguration().get(Utils.CENTERPATH);
		splitter = context.getConfiguration().get(Utils.SPLITTER);
		k = context.getConfiguration().getInt(Utils.K, 0);
		centerVec  = new String[k];
		
		// TODO 读取聚类中心到数组 centerVec中
		Path path = new Path(centerPathStr);
		FSDataInputStream is=Utils.getFs().open(path);
		BufferedReader br=new BufferedReader(new InputStreamReader(is));
		String line="";
		int i=0;
		while( (line=br.readLine())!=null){
		 centerVec[i++]=line;
	}
		br.close();
		is.close();
 
	}
	
	private IntWritable ID = new IntWritable();
	private MyWritable mw = new MyWritable();
	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		int vecId = getCenterId(value.toString());
		
		ID.set(vecId);
		mw.setData(value.toString());
		context.write(ID, mw);
	}
	
	/**
	 * 计算当前行到聚类中心向量中距离最小的下标；
	 * @param string
	 * @return
	 */
	private int getCenterId(String line) {
		int type=-1;
		double min=Double.MAX_VALUE;
		double distance=0.0;
		for(int i=0;i<centerVec.length;i++){
			distance=Utils.calDistance(line,centerVec[i],splitter);
			if(distance<min){
				min=distance;
				type=i;
			}
		}
		return type;
	}
}