package big_data_self_learn.hadoop.mr.javaMR.Basic_wc;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

//四个参数的意义是 偏移量,输入的数据的类型，输出的数据的key的类型，输出的数据的value的类型，分别为数据输入的kv和数据输出的kv

//关于java中的数据类型和hadoop中的序列化的对应的类型的关系：
/*
        Java类型	Hadoop Writable类型
        boolean	BooleanWritable
        byte	ByteWritable
        int	IntWritable
        float	FloatWritable
        long	LongWritable
        double	DoubleWritable
        String	Text
        map	MapWritable
        array	ArrayWritable
*/
//除了string其他都是增加了writeable

//这里的map的数据为什么是LongWritable, Text
public class WordcountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
    Text k = new Text();
    IntWritable v = new IntWritable(1);


    @Override
    protected void map(LongWritable key, Text value, Context context)	throws IOException, InterruptedException {

        // 1 获取一行 ，注意进行转化操作
        String line = value.toString();

        // 2 切割
        String[] words = line.split(" ");

        // 3 输出
        for (String word : words) {
            k.set(word);
            context.write(k, v);
        }
    }

    // 可以看出来的是text和string之间的转化的操作是v.toString()和k.set()
}
