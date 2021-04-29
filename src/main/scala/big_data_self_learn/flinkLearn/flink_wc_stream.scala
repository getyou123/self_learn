package big_data_self_learn.flinkLearn

import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}

/**
 * 流处理wc，最重要的数据抽象是DataStream
 */

object flink_wc_stream {

  def main(array: Array[String]): Unit ={

    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(2)//设置并行度，默认是本机的核心
    val text: DataStream[String] = env.socketTextStream( "localhost", 9000, '\n')

    //解析数据，前提是必须的是引入了隐士转化
    val wc=text.flatMap(x=>x.split(" "))
      .map(x=>(x,1))
      .keyBy(0)
      //    .timeWindow(Time.seconds(2),Time.seconds(1))
      .sum(1) //对当前分组的按照第二个元素求和

    wc.print()

    env.execute("stream_wc_basic")//这个需要一直开启的

  }
}

/**
 * 2> (不知道,1)
 * 3> (jiushishuo,1) 数字的表示执行在哪个并行子任务号码并行的任务的编号，print的时候都写成setParalle（1），也是在写入文件时候也是写并行度为
 * 数据分在哪个分区的子任务上，按照分区来算，默认的是hash
 * 上游和下游的并行度都是可以随意设置的，这些任务都是相互分离的，但是实际上就是把所有的都设置成一样的
 */