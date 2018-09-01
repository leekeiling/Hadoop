
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
class KnnInstance
{
		  public double[] attributeset;//存放样例属性
		  public String lable;//存放样例标签
		  public String id; //索引
		  public  KnnInstance(String line)
		  {
				String[] splited = line.split("\t");
				attributeset = new double[splited.length-2];
				id = splited[0] + "";
				for(int i=1;i<attributeset.length;i++)
				{
					attributeset[i-1] = Double.parseDouble(splited[i]);  
				}
				lable = splited[splited.length-1] + "";      
		  }
		  public double[] getFeatures()
		  {
			   return attributeset; 
		  }
		  public String getLabel()
		  {
				return lable;
		  }
		  public String getId(){
			  return id;
		  }
}