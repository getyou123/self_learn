package big_data_self_learn.flinkLearn.streamApi.sink

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011

/**
 * create by hgw on 2020/12/14 8:40 下午
 *
 */
object kafkaSink {
    def main(args: Array[String]): Unit = {
      val env = StreamExecutionEnvironment.getExecutionEnvironment
      env.setParallelism(1)

      val stream1 = env.fromCollection(List(
        1,
        3, 4,
        1, 4, 5
      ))
      val stream2 = stream1.map(x => x.toString)
      //添加kafka的sink
      stream2.addSink(new FlinkKafkaProducer011[String]("localhost:9092","test",new SimpleStringSchema()))
      env.execute("kafka sink")
    }
}
