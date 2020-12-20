package big_data_self_learn.flinkLearn.streamApi.source;


import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

/**
 * 相比于parallelSource增加open和close方法，都是执行一次，可以进行资源的链接
 */
public class MyRichParalleSource extends RichParallelSourceFunction<Long> {
    private Long count=1L;
    private boolean isRuning=true;
    /**
     * 在这里产出无限的数据
     * @param sourceContext
     * @throws Exception
     */
    @Override
    public void run(SourceContext<Long> sourceContext) throws Exception {
        while (isRuning){
            sourceContext.collect(count);
            count++;
            Thread.sleep(1000);
        }
    }

    /**
     * 执行cancel的操作的时候会调用这个函数
     */
    @Override
    public void cancel() {
        isRuning=false;

    }

    /**
     * 申请资源执行一次
     * @param parameters
     * @throws Exception
     */
    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
    }

    @Override
    public void close() throws Exception {
        super.close();
    }
}
