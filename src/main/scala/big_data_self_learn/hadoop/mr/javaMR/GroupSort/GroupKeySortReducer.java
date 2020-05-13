package big_data_self_learn.hadoop.mr.javaMR.GroupSort;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

public class GroupKeySortReducer extends Reducer<GroupKeySortBean, NullWritable,GroupKeySortBean,IntWritable> {
    @Override
    protected void reduce(GroupKeySortBean key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {
        int sum=0;
        Iterator<NullWritable> it = values.iterator();
        while (it.hasNext()) {
            it.next();
            sum++;
        }
        context.write(key,new IntWritable(sum));//注意次此时输入的key是我们自己定义的”相同“的可以，他们的value都是NullWritable
        //实际是我们的定义的key的相同的方式，把数据进行了分组，只要他们的order_id相同就认为他们是一个组内的
        //而组内的数据是按照key的完整排序的过程，是按照order_id 然后order_amou进行的排序的。
        //所以如何进行分组和key的直接排序方式还是不一样的。
        //分组排序指定了哪些key是意义上的相同的
        //key是在 Map溢出的归并和Reduce拉取是合并时候的
    }
}
