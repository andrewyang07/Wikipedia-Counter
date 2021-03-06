import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WikipediaPopular {

    public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable> {
        private Text date = new Text();
        private Text lang = new Text();
        private Text title = new Text();
        private Text requestedTimes = new Text();
        private Text size = new Text();

        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException, NumberFormatException {
            StringTokenizer itr = new StringTokenizer(value.toString());

            // date and time
            date.set(itr.nextToken());

            // language type of the wiki page
            lang.set(itr.nextToken());

            // title of the wiki page
            title.set(itr.nextToken());

            // how many time the page was visited
            requestedTimes.set(itr.nextToken());

            // the byte size of requests, we can ignore that
            size.set(itr.nextToken());

            // filter out unwanted records
            if (lang.toString().equals("en") || title.toString().equals("Main_Page")
                    || title.toString().startsWith("Special:")) {
                context.write(date, new IntWritable(0));
                continue;
            }

            context.write(date, new IntWritable(Integer.parseInt(requestedTimes.toString())));
        }
    }

    public static class GetLargestIntReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable result = new IntWritable();

        public void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {
            int max = 0;

            // iterates over all results from the mapper
            // and find the records with most visited times
            for (IntWritable val : values) {
                max = Math.max(max, val.get());
            }
            result.set(max);
            IntWritable value = new IntWritable(max);
            context.write(key, value);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "wikipedia popular");
        job.setJarByClass(WikipediaPopular.class);
        job.setMapperClass(TokenizerMapper.class);
        job.setCombinerClass(GetLargestIntReducer.class);
        job.setReducerClass(GetLargestIntReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
