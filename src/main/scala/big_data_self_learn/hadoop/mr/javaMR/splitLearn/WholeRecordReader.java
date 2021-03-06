package big_data_self_learn.hadoop.mr.javaMR.splitLearn;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class WholeRecordReader extends RecordReader<Text, BytesWritable>{

    private Configuration configuration;
    private FileSplit split;
    private boolean isProgress= true;
    private BytesWritable value = new BytesWritable();
    private Text k = new Text();

    //注意这传的是split而不是整的文件的内容
    //一般来说的这个split中包含这属于那个文件，文件的路径path，这是基本的信息
    @Override
    public void initialize(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
        this.split = (FileSplit)split;
        configuration = context.getConfiguration();
    }

    //核心函数
    @Override
    public boolean nextKeyValue() throws IOException, InterruptedException {
        if (isProgress) {//这个标志位用来告诉是不是成功的读取了这个的split的信息

            // 1 定义缓存区
            byte[] contents = new byte[(int)split.getLength()];
            FileSystem fs = null;
            FSDataInputStream fis = null;
            try {
                // 2 获取文件系统
                fs = FileSystem.get(configuration);
                // 3 读取数据
                Path path = split.getPath();
                fis = fs.open(path);
                // 4 读取文件内容
                IOUtils.readFully(fis, contents, 0, contents.length);
                // 5 输出文件内容
                value.set(contents, 0, contents.length);

                // 6 获取文件路径及名称
                String name = split.getPath().toString();

                // 7 设置输出的key值
                k.set(name);

            } catch (Exception e) {
            }finally {
                IOUtils.closeStream(fis);
            }
            isProgress = false;
            return true;
        }
        return false;
    }

    @Override
    public Text getCurrentKey() throws IOException, InterruptedException {
        return k;
    }

    @Override
    public BytesWritable getCurrentValue() throws IOException, InterruptedException {
        return value;
    }

    @Override
    public float getProgress() throws IOException, InterruptedException {
        return 0;
    }

    @Override
    public void close() throws IOException {
    }
}