package big_data_self_learn.hadoop.mr.javaMR.BeanAsKeySort;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.spark.streaming.dstream.InternalMapWithStateDStream;

import java.io.IOException;

public class FlowBeanMapper extends Mapper<LongWritable, Text, FlowBeanAsKey, Text> {
    private FlowBeanAsKey Key;
    private Text val;

    //input的形式按照行来进行读取 数据输入的格式是 号码\t上传流量\t下载流量\t总流量
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

        String strVal = value.toString();
        String[] split = strVal.split(" ",-1);

        //val
        this.val=new Text(split[0]);
        //key
        this.Key=new FlowBeanAsKey(Integer.parseInt(split[1].toString()),
                Integer.parseInt(split[2].toString()));
        context.write(this.Key,this.val);
    }
}
