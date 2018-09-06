import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class Unique {

    public static class UniqueMapper extends Mapper<LongWritable, Text, Text, Text>{                           

        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String lineValue = value.toString();
            context.write(new Text(lineValue), new Text(""));
        }
    }

    public static class UniqueReducer extends Reducer<Text, Text, Text, Text> {
        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            context.write(key, new Text(""));
        }
    }

    public static void main(String[] args) throws Exception {
        
        String file_input_path = "/user/root/unique/input.txt";
	String file_output_path = "/user/root/unique/output";

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Unique");     

        job.setJarByClass(Unique.class);
        job.setMapperClass(UniqueMapper.class);
        job.setReducerClass(UniqueReducer.class);   

        FileInputFormat.addInputPath(job, new Path(file_input_path));
        FileOutputFormat.setOutputPath(job, new Path(file_output_path));

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        boolean isSuccess = job.waitForCompletion(true);

        System.exit(isSuccess ? 0 : 1);
    }
}
