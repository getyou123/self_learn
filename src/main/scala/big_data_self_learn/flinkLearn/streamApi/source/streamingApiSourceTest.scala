package big_data_self_learn.flinkLearn.streamApi.source

import java.util.Properties

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011

/**
 * create by hgw on 2020/12/5 4:42 下午
 *
 */

case class SensorReading(id:String,timestamp:Long,temperture:Double)

object streamingApiSourceTest {

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    //1。从集合中读取
    val readings = List(
      SensorReading("snensor1", 1544723632, 23.5),
      SensorReading("snensor31", 1544423432, 27.5),
      SensorReading("snensor51", 1544742532, 29.5)
    )
    val stream1 = env.fromCollection(readings)
//    stream1.print()
    /**
     * 3> SensorReading(snensor51,1544742532,29.5)
     * 2> SensorReading(snensor31,1544423432,27.5)
     * 1> SensorReading(snensor1,1544723632,23.5)
     */
    //数据的顺序是不确定的，默认使用的是系统的CPU的核心数

    //2。从文件中读取数据，转为case class然后，打印
    val sensor_file_path= "in/flinkIN/Senesor.txt"
    val stream2 = env.readTextFile(sensor_file_path)
    stream2.map(x=>x.split(",",-1))
      .map(x=>SensorReading(x(0),x(1).toLong,x(2).toDouble))
//      .print()
    /**
     * 1> SensorReading(snensor51,1544742532,29.5)
     * 3> SensorReading(snensor31,1544423432,27.5)
     * 6> SensorReading(snensor1,1544723632,23.5)
     */
    //数据的顺序不确定的

    //3。从kafka中读取数据，其实上面的都是这个的封装的，这个addsource是一个更一般的方式传入
    val prop=new Properties()
    prop.setProperty("bootstrap.servers","localhost:9092")
    prop.setProperty("group.id","group_id")
    val stream3 = env.addSource(new FlinkKafkaConsumer011[String]("test", new SimpleStringSchema(), prop))
    stream3.print()
    //这里的并行度是可以设置的

    //自定义source，参考另外的两个文件

    env.execute("source1")//注意流数据处理需要开始执行，对于有界流的执行完就退出了

  }
}
