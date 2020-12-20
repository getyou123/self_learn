package big_data_self_learn.flinkLearn.streamApi.transform
import org.apache.flink.streaming.api.scala._

/**
 * create by hgw on 2020/12/6 12:11 下午
 * part1 简单的数据流处理，对应的是流式数据处理的转化操作
 */
object SimpleTransform{
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //map
    val readings = List(1,2,3)
    val stream1 = env.fromCollection(readings).map(x=>x+1)
    //flatmap
    //filter
    env.execute("source1")//注意流数据处理需要开始执行，对于有界流的执行完就退出了

  }
}
