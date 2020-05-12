package big_data_self_learn.hadoop.mr.javaMR.splitLearn;
import java.io.IOException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

// 定义类继承FileInputFormat
public class WholeFileInputformat extends FileInputFormat<Text, BytesWritable>{
    //这里只是指定了能不能进行切分
    //切分的大小还是128MB
    @Override
    protected boolean isSplitable(JobContext context, Path filename) {
        return false;
    }//因为文件本身很小的所以这里指定的其实就是

    @Override
    public RecordReader<Text, BytesWritable> createRecordReader(InputSplit split, TaskAttemptContext context)    throws IOException, InterruptedException {
        WholeRecordReader recordReader = new WholeRecordReader();//自己定的recordreader，并返回这个recordreader
        recordReader.initialize(split, context);//再返回之前进行的操作
        return recordReader;
    }
}