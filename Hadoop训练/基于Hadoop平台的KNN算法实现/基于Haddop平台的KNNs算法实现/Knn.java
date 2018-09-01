import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Knn {
	//读取验证集、测试集，计算测试集与训练集之间的样本，打印出k个 测试集样本字符串与对应的标签
	public static class KnnMap extends Mapper<LongWritable, Text, Text, Text> {
		
		public ArrayList<KnnInstance> train = new ArrayList<KnnInstance>();       //存储训练集
		public int k = 5; //k值

		@Override
		//读取训练集
		protected void setup(				
			Mapper<LongWritable, Text, Text, Text>.Context context)
		
			throws IOException, InterruptedException {
				// TODO Auto-generated method stub
				// super.setup(context);
				FileSystem fs = null;
				try {
					fs = FileSystem.get(new URI("hdfs://192.168.142.130:9000"), new Configuration());
				} 
				catch (Exception e) {
						
				}
				FSDataInputStream fi = fs.open(new Path(				//获取训练文本
						"hdfs://192.168.142.130:9000/input/traindata.txt")); //文件路径
				BufferedReader bf = new BufferedReader(new InputStreamReader(fi));
				String line = bf.readLine(); //读取每行文本
				while (line != null) {
					KnnInstance sample = new KnnInstance(line);
					train.add(sample); //放入训练集中
					line = bf.readLine();
				}
			}

		@Override
		protected void map(LongWritable key, Text value, Context context)//value是测试集样本
				throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			// super.map(key, value, context);
			ArrayList<Double> distance = new ArrayList<Double>(k); //存k个距离
			ArrayList<String> trainlabel = new ArrayList<String>(k); //存k个距离对应的标签
			//初始化
			for (int i = 0; i < k; i++) {
				distance.add(Double.MAX_VALUE); //初始化k个距离为最大值
				trainlabel.add(String.valueOf("-1.0")); //初始化k个标签为-1.0
			}
			
			KnnInstance test = new KnnInstance(value.toString());//读取测试集样本
			
			Double[] list = new Double[train.size()];//new
			String[] label = new String[train.size()];	
			
			//计算测试集样本和每个训练集样本的距离
			String id = test.getId(); //测试集索引
			for (int i = 0; i < train.size(); i++) {
				double dis = Distance(train.get(i).getFeatures(), 
						test.getFeatures());			
				list[i] = dis;
				label[i] = train.get(i).getLabel() + "";
			/*	//比较，如果小于第j个距离，则替换。缺点：会替换掉很小但位于前排的距离？？
				for (int j = 0; j < k; j++) {
					if (dis < (Double) distance.get(j)) {
						distance.set(j, dis); //更新第j个最小为当前距离
						trainlabel.set(j, train.get(i).getLabel() + ""); //更新第j个标签为当前训练样本对应的标签
						break;
					}
				}*/
			}
			
			for(int i=0; i<train.size(); i++) {
				for(int j=i; j<train.size(); j++) {
					if(list[i]>list[j]) {
						Double temp = list[i];
						String temps = label[i];
						list[i] = list[j];
						label[i] = label[j]; 
						list[j] = temp;
						label[j] = temps;
					}
				}
			}
			
			for(int i=0; i<k; i++) {
				distance.set(i, list[i]); 
				trainlabel.set(i, label[i]);
			}

			
			//结果应该是 有k个测试集样本+预测标签：  测试集样本1、测试集样本-1、测试集样本-1……
			for (int i = 0; i < k; i++) {
				context.write(new Text(id),
						new Text(trainlabel.get(i) + ""));
			}
		}
		//计算距离的公式
		private double Distance(double[] a, double[] b) {
			// TODO Auto-generated method stub
			double sum = 0.0;
			for (int i = 0; i < a.length; i++) {
				sum += Math.pow(a[i] - b[i], 2);
			}
			return Math.sqrt(sum); //欧式距离
		}
	}
	
	//
	public static class KnnReducer extends
			Reducer<Text, Text, Text, NullWritable> {
		@Override
		protected void reduce(Text k, Iterable<Text> values, Context context) //k为测试集文本，values为预测队列
				throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			// super.reduce(arg0, arg1, arg2);
			ArrayList<String> l = new ArrayList<String>();
			
			for (Text t : values) {
				l.add(t.toString());
			}
			//l为预测队列（-1、1、1、-1……）
			String predict = Predict(l); //根据预测值队列，选出出现次数最多的，作为预测值
			context.write(new Text(k.toString() + "\t" + predict),
					NullWritable.get());
		}
		//输入预测队列，输出最多的预测值
		private String Predict(ArrayList<String> arr) {
			
			// TODO Auto-generated method stub
			
			HashMap<String, Double> tmp = new HashMap<String, Double>(); //map映射，预测值->出现次数
			
			for (int i = 0; i < arr.size(); i++) {
				//原先存在该预测值
				if (tmp.containsKey(arr.get(i))) {
					double frequence = tmp.get(arr.get(i)) + 1; //更新频率
					tmp.remove(arr.get(i)); //移除原先的 （不能直接put更新么？？）
					tmp.put((String) arr.get(i), frequence); //重新插入
				}
				//原先没有该预测值
				else
					tmp.put((String) arr.get(i), new Double(1)); //加入map中
			}
			
			Set<String> s = tmp.keySet(); //键值的集合

			Iterator it = s.iterator();
			double lablemax = Double.MIN_VALUE; //初始化最大出现次数lablemax为最小值
			String predictlable = null; //预测值
			while (it.hasNext()) {
				String key = (String) it.next();
				Double lablenum = tmp.get(key);
				//找次数最大的预测值
				if (lablenum > lablemax) {
					lablemax = lablenum;
					predictlable = key;
				}
			}
			return predictlable;
		}
	}
	

	public static void main(String[] args) throws IOException,
			ClassNotFoundException, InterruptedException {
		FileSystem fs = FileSystem.get(new Configuration());

		Job job = new Job(new Configuration());
		job.setJarByClass(Knn.class); //通过传入的class 找到job的jar包

		FileInputFormat.setInputPaths(job, new Path(args[0]));
		job.setMapperClass(KnnMap.class); //设置map class
		job.setMapOutputKeyClass(Text.class); //设置map输出key的类型为text
		job.setMapOutputValueClass(Text.class); //设置map输出value的类型为text

		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		job.setReducerClass(KnnReducer.class); //设置reduce class
		job.setOutputKeyClass(Text.class); //设置reduce输出key的类型为text
		job.setOutputValueClass(NullWritable.class); 

		job.waitForCompletion(true);

	}

}

