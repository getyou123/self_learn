package big_data_self_learn.hadoop.mr.javaMR.BeanAsKeySort;

import big_data_self_learn.hadoop.mr.javaMR.Basic_wc.WordcountDriver;
import big_data_self_learn.hadoop.mr.javaMR.Basic_wc.WordcountMapper;
import big_data_self_learn.hadoop.mr.javaMR.Basic_wc.WordcountReducer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class FlowBeanAsKeyDriver {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        // 1 获取配置信息以及封装任务
        Configuration configuration = new Configuration();
        Job job = Job.getInstance(configuration);

        // 2 设置jar路径
        job.setJarByClass(FlowBeanAsKeyDriver.class);

        // 3 设置map和reduce类
        job.setMapperClass(FlowBeanMapper.class);
        job.setReducerClass(FlowBeanReducer.class);

        // 4 设置map输出
        job.setMapOutputKeyClass(FlowBeanAsKey.class);
        job.setMapOutputValueClass(Text.class);

        // 5 设置最终输出kv类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FlowBeanAsKey.class);

        //用于设置reducer的个数的
        job.setNumReduceTasks(3);

        // 6 设置输入和输出路径
        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        // 7 提交
        boolean result = job.waitForCompletion(true);

        System.exit(result ? 0 : 1);
    }
}
