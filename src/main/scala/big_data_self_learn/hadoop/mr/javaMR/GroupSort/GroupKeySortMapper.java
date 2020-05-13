package big_data_self_learn.hadoop.mr.javaMR.GroupSort;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class GroupKeySortMapper extends Mapper<LongWritable, Text,GroupKeySortBean, NullWritable> {
    private GroupKeySortBean key;

    //数据的输入格式：
    //order_id\torder_count\torder_aomou
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //按照每一行就行构造bean对象
        String strValue = value.toString();

        String[] split = strValue.split(" ", -1);

        this.key=new GroupKeySortBean(Long.parseLong(split[0]),
                Long.parseLong(split[1]),
                Long.parseLong(split[2]));

        context.write(this.key,NullWritable.get());
    }
}
