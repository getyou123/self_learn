package big_data_self_learn.flinkLearn.waterMark

import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time

/**
 * create by hgw on 2020/12/20 3:32 下午
 *
 */
object timedelayProcess {
  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)//设置事件时间
    val inputStream: DataStream[String] = env.socketTextStream("localhost", 9999)
    val outputTag = new OutputTag[SensorReading]("side")
    val dataStream = inputStream
      .map(data => {
        val dataArray = data.split(",")
        SensorReading(dataArray(0).trim, dataArray(1).trim.toLong, dataArray(2).trim.toDouble)
      }).assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor[SensorReading](Time.seconds(2)) {
      override def extractTimestamp(element: SensorReading): Long = {
        element.timestamp * 1000 //毫秒 Long
      }
    })
    val minStream: DataStream[SensorReading] = dataStream.keyBy(_.id)
      //  .window( SlidingEventTimeWindows.of(Time.seconds(10),Time.seconds(2)))
      .timeWindow(Time.seconds(10))
      .allowedLateness(Time.seconds(4))//窗口存留期
      .sideOutputLateData(outputTag)
      .minBy("temperature")
    dataStream.print()
    minStream.print()
    minStream.getSideOutput(outputTag).print()
    env.execute("delay demo")
  }
}

case class SensorReading(id: String, timestamp: Long, temperature: Double)

/**
 * 总的说明：
 * watermark：事件时间+延时，触发window的计算，正常的走这条线
 * allowedLateness 是为了延长window的存留时间
 * sideOutPut是最后兜底操作，所有过期延迟数据，指定窗口已经彻底关闭了，就会把数据放到侧输出流
 */

