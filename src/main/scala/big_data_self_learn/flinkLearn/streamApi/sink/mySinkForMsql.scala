//package big_data_self_learn.flinkLearn.streamApi.sink
//
//import java.util.Properties
//
//import org.apache.flink.api.common.serialization.SimpleStringSchema
//import org.apache.flink.streaming.api.functions.sink.RichSinkFunction
//import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
//import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011
//
///**
// * create by hgw on 2020/12/14 8:49 下午
// *
// */
//
//object mySinkForMsql {
//  def main(args: Array[String]): Unit = {
//    val env = StreamExecutionEnvironment.getExecutionEnvironment
//    val prop=new Properties()
//    prop.setProperty("bootstrap.servers","localhost:9092")
//    prop.setProperty("group.id","group_id")
//    val stream3 = env.addSource(new FlinkKafkaConsumer011[String]("test", new SimpleStringSchema(), prop))
//    stream3.print()
//  }
//
//}
