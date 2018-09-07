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


public class SingleJoin {

    public static class SingleJoinMapper extends Mapper<LongWritable, Text, Text, Text>{                           

        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String lineValue = value.toString();
            StringTokenizer tokenizer = new StringTokenizer(lineValue);
            String[] values = new String[2];
            int i = 0;
            while (tokenizer.hasMoreTokens()) {
                String wordValue = tokenizer.nextToken();
                values[i] = wordValue;
                i++;
            }   
            //左表用0标识     
            context.write(new Text(values[1]), new Text("0" + values[0]));
            //右表用1标识
            context.write(new Text(values[0]), new Text("1" + values[1]));
        }
    }

    public static class SingleJoinReducer extends Reducer<Text, Text, Text, Text> {
        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            String[] grandsons = new String[10];
            String[] grandparents = new String[10];
            int grandson_num = 0;
            int grandparent_num = 0;
            for(Text value: values)
            {
                String name = value.toString();
                if(name.charAt(0)=='0')
                {
                    grandsons[grandson_num] = name.substring(2);
                    grandson_num++;
                }
                else
                {
                    grandparents[grandparent_num] = name.substring(2);
                    grandparent_num++;
                }
            }
            //笛卡尔积
            for(int j = 0; j < grandson_num; j++)
            {
                for(int i = 0; i < grandparent_num; i++)
                {
                    context.write(new Text(grandsons[j]), new Text(grandparents[i]));
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        
        String file_input_path = "/user/root/single/input.txt";
	    String file_output_path = "/user/root/single/output/";  
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "SingleJoin");     

        job.setJarByClass(SingleJoin.class);
        job.setMapperClass(SingleJoinMapper.class);
        job.setReducerClass(SingleJoinReducer.class);   

        FileInputFormat.addInputPath(job, new Path(file_input_path));
        FileOutputFormat.setOutputPath(job, new Path(file_output_path));

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        boolean isSuccess = job.waitForCompletion(true);

        System.exit(isSuccess ? 0 : 1);
    }
}
