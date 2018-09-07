import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class GlobalSort {

    public static class GlobalSortMapper extends Mapper<LongWritable, Text, IntWritable, IntWritable>{                           
        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String lineValue = value.toString();
            IntWritable keyInt = new IntWritable(Integer.parseInt(lineValue));
            IntWritable valInt = keyInt;
            context.write(keyInt, valInt);
        }
    }

    public static class GlobalSortReducer extends Reducer<IntWritable, IntWritable, IntWritable, NullWritable> {
        @Override
        public void reduce(IntWritable key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            for (IntWritable value : values)
                context.write(value, NullWritable.get());
        }
    }

     public static class GlobalSortPartitioner extends Partitioner<IntWritable, IntWritable> {
        @Override
        public int getPartition(IntWritable key, IntWritable value, int numPartitions) {
            int keyInt = Integer.parseInt(key.toString());
            if (keyInt < 10000) {
                return 0;
            } else if (keyInt < 20000) {
                return 1;
            } else {
                return 2;
            }
        }
    }
 

    public static void main(String[] args) throws Exception {
        
        String file_input_path = "/user/root/globalsort/input.txt";
	    String file_output_path = "/user/root/globalsort/output";

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "GlobalSort");     

        job.setJarByClass(GlobalSort.class);
        job.setMapperClass(GlobalSortMapper.class);
        job.setReducerClass(GlobalSortReducer.class);   
        job.setPartitionerClass(GlobalSortPartitioner.class);
        FileInputFormat.addInputPath(job, new Path(file_input_path));
        FileOutputFormat.setOutputPath(job, new Path(file_output_path));

        job.setMapOutputKeyClass(IntWritable.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(NullWritable.class);

        boolean isSuccess = job.waitForCompletion(true);

        System.exit(isSuccess ? 0 : 1);
    }
}
