public class Alljobs {
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String[] KmeansArgs = new String[]{
					"hdfs://master:8020/user/Administrator/Kmeans/k.csv", //原始数据
					"hdfs://master:8020/user/Administrator/Kmeans/k_all", // 输出各个数据的类别
					"3", // 聚几类
					",", // 原始数据分隔符
					"20",// 迭代次数
					"0.5", // 误差阈值
					"0" //start: 0-> 初始化聚类中心，1 -> 计算新的聚类中心  2 -> 是否分类
		};
		String input = KmeansArgs[0];
		String output = KmeansArgs[1];
		int k = Integer.valueOf(KmeansArgs[2]);
		String splitter = KmeansArgs[3];
		int iteration = Integer.valueOf(KmeansArgs[4]);
		double delta = Double.parseDouble(KmeansArgs[5]);
		int start = Integer.valueOf(KmeansArgs[6]);
		int number = 0;
		// 1. 初始化聚类中心向量(SampleJob)
		int ret = -1;
		String fileStr = "iter";
		switch (start){
		case 0 :first(input, output, k, ret);
		case 1: number = updateKmeans(input, output, k, 
				splitter, delta, ret, iteration);
		case 2: if(start == 2){
			number = readLastFile(output,fileStr)-1;
			}
			classify(number, input, output, k, 
				splitter, iteration);
		default: break;
		}
	}

	public static void first(String input,String output,
			int k,
			int ret) throws Exception{
		String[] job1Args = new String[]{
				input,
				output+"/iter0",
				String.valueOf(k)
		};
		ret = ToolRunner.run(Utils.getConf(),new Driver.MyDriver(), job1Args);
		if(ret != 0){
			System.err.println("sample job failed!");
			System.exit(-1);
		}
	}

	// 2. 循环Kmeans，更新聚类中心
	public static int updateKmeans(String input,String output,
				int k,String splitter,double delta,
				int ret,int iteration) throws Exception{ 
		int num = 0;
		for(int i=0;i<iteration;i++){
			String[] jobArgs = new String[]{
				input, // input
				output+"/iter"+(i+1),  //当前聚类中心
				splitter, // splitter
				String.valueOf(k),
				output+"/iter"+i+"/part-r-00000" // 上一次聚类中心
			};
			ret = ToolRunner.run(Utils.getConf(), new KMeansDriver(), jobArgs);
			if(ret != 0){
				System.err.println("kmeans job failed!"+":"+i);
				System.exit(-1);
			}
			if(!Utils.shouldRunNextIteration(output+"/iter"+i+"/part-r-00000",output+"/iter"+(i+1)+"/part-r-00000",
				delta,splitter)){
					num = i+1;
					break;
			}
		}
		return num;
	}

	// 3. 分类
	public static void classify(int num,String input,String output,
			int k,String splitter,
			int iteration) throws IOException, ClassNotFoundException, InterruptedException{
		if (num == 0) {
			num = iteration;
		}
		Configuration conf = Utils.getConf();
		conf.set(SPLITTER, splitter );
		conf.set(CENTERPATH, output+"/iter"+num+"/part-r-00000");
		conf.setInt(K, k);
		Job job = Job.getInstance(conf,"classify");
		job.setMapperClass(Classify.KMeansMapper.class);
		job.setPartitionerClass(Classify.KmeansPartional.class);
		job.setReducerClass(Classify.ClassifyReducer.class);
		job.setMapOutputKeyClass(IntWritable.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(Text.class);
		job.setNumReduceTasks(k);
		FileInputFormat.addInputPath(job, new Path(input));
		Path out =new Path(output+"/clustered");
	    FileOutputFormat.setOutputPath(job,out);
	    if(Utils.getFs().exists(out)){
	    	Utils.getFs().delete(out, true);
	    }
	    System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

	public static int readLastFile(String output,String fileStr) throws IOException{
		Path path = new Path(output);
		FileStatus[] fs = Utils.getFs().listStatus(path);
		int num = 0;
		for(int i=0;i<fs.length;i++){
			if(fs[i].getPath().getName().startsWith(fileStr)){
				num++;
			}
		}
		return num;	
	}
}
