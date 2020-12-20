package big_data_self_learn.flinkLearn.streamApi.source;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

/**
 * 自定义实现并行度为1的Source
 * 注意：
 * SourceFunction和SourceContext都需要指定数据类型（泛型）
 * 如果不指定，代码运行的时候会报错
 * Caused by: org.apache.Flink.api.common.functions.InvalidTypesException:
 * The types of the interface org.apache.Flink.streaming.api.functions.source.
 * SourceFunction could not be inferred.
 * Support for synthetic interfaces, lambdas, and generic or raw types is limited
 * at this point
 */
public class MyNoParalleSource implements SourceFunction<Long>{
  private long count=1L;
  private boolean isRuning=true;

  /**
   * 大部分时间在这里实现一个循环，循环产出数据
   * @param sourceContext
   * @throws Exception
   */
  @Override
  public void run(SourceContext<Long> sourceContext) throws Exception {//这调用的是无限循环的操作
    while (isRuning){
      sourceContext.collect(count);//这里将数据collct就可以发送出去
      count++;
      // 每一秒产生一个数据
      Thread.sleep(1000);
    }

  }
  /**
   * 执行cancel时候会执行这个函数
   */
  @Override
  public void cancel() {
    isRuning=false;
  }
}