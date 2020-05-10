package big_data_self_learn.hadoop.mr.javaMR.splitLearn;
import java.io.IOException;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class SequenceFileMapper extends Mapper<Text, BytesWritable, Text, BytesWritable>{
    @Override
    protected void map(Text key, BytesWritable value,            Context context)        throws IOException, InterruptedException {

        context.write(key, value);
    }
}
