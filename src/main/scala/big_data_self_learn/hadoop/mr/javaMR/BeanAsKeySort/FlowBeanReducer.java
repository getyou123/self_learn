package big_data_self_learn.hadoop.mr.javaMR.BeanAsKeySort;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

//输入的为map的数据输出 输出为数据的输出的格式同时按照了key进行了排序
public class FlowBeanReducer extends Reducer<FlowBeanAsKey,Text,Text,FlowBeanAsKey> {
    @Override
    protected void reduce(FlowBeanAsKey key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        for(Text value:values){
            context.write(value,key);
        }
    }
}
