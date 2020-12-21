package big_data_self_learn.flinkLearn.stateProgram

import org.apache.flink.api.common.functions.RichFlatMapFunction
import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.util.Collector


/**
 * create by hgw on 2020/12/21 2:27 下午
 * 实现连续采集数据流时候的相邻两次的差值超过10度就报警的操作
 */

case class SensorReadingWithTimeStamp(name: String, temperature: Double, timestamp: Long)

object stateProgram {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    /**
     * nc -lk 9999
     * 先开起来
     */
    val socketStream = env.socketTextStream("localhost", 9999)

    env.setParallelism(1)
    //设置时间语义
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    //设置时间戳和watermark
    val dataStream: DataStream[SensorReadingWithTimeStamp] = socketStream.map(d => {
      val arr = d.split(",")
      SensorReadingWithTimeStamp(arr(0).trim, arr(1).trim.toDouble, arr(2).toLong)
    })
      .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor[SensorReadingWithTimeStamp](Time.seconds(2)) {
        override def extractTimestamp(t: SensorReadingWithTimeStamp) = t.timestamp * 1000
      })

    //温度变动超过10度报警
    val processStream: DataStream[String] = dataStream.keyBy(_.name)
      .process(new TempChangeAlert(10.0))
    //    dataStream.print()
    processStream.print()
    env.execute("test")
  }
}

/**
 * 需要使用更底层的api才能获取到键控的状态
 *
 * @param threshold
 */
class TempChangeAlert(threshold: Double) extends KeyedProcessFunction[String, SensorReadingWithTimeStamp, String] {
  /**
   * flink中对于键控的状态有三种：
   * ValueState （直接.value获取，Set操作是.update）
   * ListState，将状态表示为一组数据的列表（存多个状态）；（.get，.update，.add）
   * Collector中的数据是返回之后的DataStream的类型，这里拼成了一个string返回
   */
  //维护一个状态，记录上一个key的温度值，传入的参数是构造state的描述器,其实可以设置初始值但是不建议在这设置初始值，注意名称的必须要是唯一的
  lazy val lastTemp = getRuntimeContext.getState(new ValueStateDescriptor[Double]("tempState", classOf[Double]))

  override def processElement(value: SensorReadingWithTimeStamp,
                              ctx: KeyedProcessFunction[String, SensorReadingWithTimeStamp, String]#Context,
                              out: Collector[String]): Unit = {
    //取出上一个温度
    val lastTemperature = lastTemp.value()
    val diff = (lastTemperature - value.temperature).abs
    if (diff > threshold) {
      out.collect(value.name + "," + lastTemperature + "," + value.temperature)
    }
    lastTemp.update(value.temperature)
  }
}

/**
 * 输入数据样式：
 * sen1,12,1608533781
 * sen1,14,1608533981
 * sen1,15,1608534781
 * sen1,34,1608534981
 * sen1,23,1608535781
 *
 * 数据输出样式：
 * sen1,12,1608533781 （这里有一个数据输出为 sen1,0.0,12.0）
 * sen1,14,1608533981
 * sen1,15,1608534781
 * sen1,34,1608534981 （输出为sen1,15.0,34.0）
 * sen1,23,1608535781 （输出为sen1,34.0,23.0）
 */


//也可以参考下面的实现方式：
/**
 * 同样实现报警两个数据的值之间差值较大时候的报警操作
 * @param threshold
 */
class TempChangeAlert2(threshold: Double) extends RichFlatMapFunction[SensorReadingWithTimeStamp, (String, Double, Double)] {

  private var lastTemp: ValueState[Double] = _ //注意这里需要放在类中作为类的成员变量不能成为open的单独的局部变量

  override def open(parameters: Configuration): Unit = {
    lastTemp = getRuntimeContext.getState(new ValueStateDescriptor[Double]("tempState2", classOf[Double]))
  }

  override def flatMap(value: SensorReadingWithTimeStamp, out: Collector[(String, Double, Double)]): Unit = {
    //取出上一个温度
    val lastTemperature = lastTemp.value()
    val diff = (lastTemperature - value.temperature).abs
    if (diff > threshold) {
      out.collect((value.name, lastTemperature, value.temperature))
    }
    lastTemp.update(value.temperature)
  }


}