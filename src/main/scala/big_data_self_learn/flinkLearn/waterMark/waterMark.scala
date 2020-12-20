package big_data_self_learn.flinkLearn.waterMark

import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.spark.sql.execution.streaming.FileStreamSource.Timestamp

/**
 * create by hgw on 2020/12/20 1:54 下午
 * 本质上watermark描述的数据处理的进度，因为数据本身就是流，有先后顺序的
 * 本质上waterMark也是一条插入到数据流中的数据，用于触发窗口的计算
 * 而allowedLateness是保留window一段时间（这个时间也是按照watermark的进度来的，如果waterMark超过了window的end+allowedLateness的话，直接关闭窗口allowedLateness默认值是0。/）
 * 时间语义上有三种数据时间：
 * 事件事件（这个是时间需要流式数据中夹杂这时间戳信息），进入flink的时间，处理时间（这个是不设置的时候的默认的时间语义）
 */

case class SensorReading(name: String, temperature: Int, eventTime: Timestamp) //数据中需要包含时间戳，作为事件时间

object waterMark {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    /**
     * 设置时间语义
     * 三种之一
     */
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime) //数据中需要包含所属的时间，这个需要配合数据格式，这个的乱序会产生业务逻辑的错误等
    //    env.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime)//进入flink的时间点，这个很少用，虽然到了处理阶段还是会出现乱序的情况，最多就是按照事件时间一样的处理
    //    env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime)//默认时间是处理时间点，这个处理时间就谈不上什么乱序了，因为处理时间就是处理时候才获得的

    /**
     * 设置waterMark和事件时间
     * 正确的处理来到的乱序事件需要waterMark机制和window来共同的处理
     * window的计算是waterMark来触发的
     * watermark本质上一条插入到数据流中的消息记录
     * window本质上一个bucket（监控了来的每条数据是不是属于这个bucket，window中的数据都已经完全了就触发计算）
     *
     */

    /**
     * 1。不会产生乱序的数据流，数据流自身就保证了不乱序
     * 举个例子：如果我们设置的是按照处理时间的时间语义来算的话，那么数据就是非乱序的
     * 属于排好顺序的数据流
     */

    val stream1 = env.fromCollection(List(
      SensorReading("1", 23, 1608445793),
      SensorReading("2", 24, 1608445879),
      SensorReading("3", 24, 1608445912),
      SensorReading("3", 22, 1608445600)
    ))
    stream1.assignAscendingTimestamps(x=>x.eventTime*1000)//如果数据已经是非乱序的，直接设置事件时间即可

    /**
     * 2。如果数据是乱序来的
     * 那么可以给一个乱序的程度，这个要和业务耦合
     * 同时设置一下事件时间
     */
    stream1.assignTimestampsAndWatermarks(
      new BoundedOutOfOrdernessTimestampExtractor[SensorReading](Time.seconds(4)) {//乱序程度的时延设置位4seconds
        override def extractTimestamp(t: SensorReading): Timestamp = t.eventTime * 1000//时间戳需要是13位的
      }
    )

    /**
     * 3。个人认为：处理时间不存在乱序情况
     */

    stream1.keyBy(_.name)
      .timeWindow(Time.seconds(4))
      .allowedLateness(Time.minutes(1))
      .sideOutputLateData()
      .reduce((x,y)=>SensorReading(x.name,x.temperature,0))

    /**
     * 总：给数据流设置waterMark其实返回的是一个TimeStampAssigner
     * 这个类型可以是周期性质的，可以在    env.getConfig.setAutoWatermarkInterval()中设置waterMark插入到数据流中
     * 前面的assignTimestampsAndWatermarks和assignAscendingTimestamps都是这种周期性的插入到数据流中的，默认时间是200ms
     *
     * 另外一种没有周期性质的规律的：略
     *
     * 所以对于乱序数据的处理是这样的：
     * 设置waterMark：设置一个乱序的最大的程度时间界限，设置好数据中获取到的事件时间即可 assignTimestampsAndWatermarks
     * 设置window的等待时延
     *
     * watermark机制需要结合window来一起梳理:
     * 数据保留了最大的已经接收到的数据的时间戳
     */





  }
}
