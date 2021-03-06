package big_data_self_learn.hadoop.mr.javaMR.splitLearn;

import big_data_self_learn.hadoop.mr.javaMR.BeanAsVal.FlowBean;
import big_data_self_learn.hadoop.mr.javaMR.BeanAsVal.FlowCountMapper;
import big_data_self_learn.hadoop.mr.javaMR.BeanAsVal.FlowCountReducer;
import big_data_self_learn.hadoop.mr.javaMR.BeanAsVal.FlowsumDriver;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.CombineFileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class CombainSplitDriver {
    public static void main(String[] args) throws IllegalArgumentException, IOException, ClassNotFoundException, InterruptedException {

// 输入输出路径
//        args = new String[] { "e:/input/inputflow", "e:/output1" };

        // 1 获取配置信息，获取job对象实例
        Configuration configuration = new Configuration();
        Job job = Job.getInstance(configuration);

        // 6 指定本程序的jar包所在的本地路径
        job.setJarByClass(CombainSplitDriver.class);//防止多个相同的任务

        // 2 指定本业务job要使用的mapper/Reducer业务类
        job.setMapperClass(FlowCountMapper.class);
        job.setReducerClass(FlowCountReducer.class);

        // 3 指定mapper输出数据的kv类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(FlowBean.class);

        //分片机制选择CombineFileInputFormat把小文件进行合并 并设置合并时候的文件的大小
        job.setInputFormatClass(CombineFileInputFormat.class);//
        CombineFileInputFormat.setMaxInputSplitSize(job,4094304);

        // 4 指定最终输出的数据的kv类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FlowBean.class);

        // 5 指定job的输入原始文件所在目录
        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        // 7 将job中配置的相关参数，以及job所用的java类所在的jar包， 提交给yarn去运行
        boolean result = job.waitForCompletion(true);
        System.exit(result ? 0 : 1);
    }
}
