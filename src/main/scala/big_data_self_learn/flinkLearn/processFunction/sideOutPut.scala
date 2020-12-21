package big_data_self_learn.flinkLearn.processFunction

import big_data_self_learn.flinkLearn.stateProgram.SensorReadingWithTimeStamp
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.util.Collector

/**
 * create by hgw on 2020/12/21 4:04 下午
 * 实现侧输出流，实现分流的操作，同时可以是格式的转化
 */
object sideOutPut {
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

    /**
     * 无参数的
     * 按照高温进行分流
     */
    //实现自定process function进行分流
    val sidoutput1 = dataStream.process(new SplitTempProcessor1) //这个是无参的构造
    //获取测流
    val highTag = new OutputTag[SensorReadingWithTimeStamp]("high")
    val highTemp = sidoutput1.getSideOutput(highTag)
    highTemp.print()


    /**
     * 有参数的
     * 按照不同的传感器进行分流
     */
    val sensor1Tag = new OutputTag[SensorReadingWithTimeStamp]("sensor1")
    val sensor2Tag = new OutputTag[SensorReadingWithTimeStamp]("sensor2")
    val sensor3Tag = new OutputTag[SensorReadingWithTimeStamp]("sensor3")

    val sideoutput2 = dataStream.process(new SplitTempProcessor2(sensor1Tag, sensor2Tag, sensor3Tag))

    val sensor1DateStream = sideoutput2.getSideOutput(sensor1Tag)
    sensor1DateStream.print()

    env.execute("side out put example")
  }
}

/**
 * 这里实现的是按照温度进行分流
 * 核心是按照tag写入或者是回到主流中
 * 比spit强大因为可以进行数据的格式的转化
 */
class SplitTempProcessor1 extends ProcessFunction[SensorReadingWithTimeStamp, SensorReadingWithTimeStamp] { //输入和输出操作类型，分流的时候两者是一样的
  override def processElement(i: SensorReadingWithTimeStamp, context: ProcessFunction[SensorReadingWithTimeStamp, SensorReadingWithTimeStamp]#Context, collector: Collector[SensorReadingWithTimeStamp]): Unit = {
    //定一个侧数据流tag
    val highTag = new OutputTag[SensorReadingWithTimeStamp]("high")
    if (i.temperature > 32) {
      context.output(highTag, i) //测流输出
    } else {
      collector.collect(i) //主流
    }
  }
}

/**
 * 有参数的测流数据处理函数
 *
 * @param tag1
 * @param tag2
 */
class SplitTempProcessor2(tag1: OutputTag[SensorReadingWithTimeStamp], tag2: OutputTag[SensorReadingWithTimeStamp], tag3: OutputTag[SensorReadingWithTimeStamp]) extends ProcessFunction[SensorReadingWithTimeStamp, SensorReadingWithTimeStamp] {
  override def processElement(i: SensorReadingWithTimeStamp, context: ProcessFunction[SensorReadingWithTimeStamp, SensorReadingWithTimeStamp]#Context, collector: Collector[SensorReadingWithTimeStamp]): Unit = {
    if (i.name.equals("sensor1")) { //侧流
      context.output(tag1, i)
    } else if (i.name.equals("sensor2")) { //侧流
      context.output(tag2, i)
    } else if (i.name.equals("sensor3")) { //侧流
      context.output(tag3, i)
    } else {
      collector.collect(i) //主流
    }
  }
}