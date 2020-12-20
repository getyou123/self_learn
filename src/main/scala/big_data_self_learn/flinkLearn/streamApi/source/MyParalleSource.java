package big_data_self_learn.flinkLearn.streamApi.source;

import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;

/**
 * 实现一个支持多个并行度的source
 */
public class MyParalleSource  implements ParallelSourceFunction<Long>{


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
}
