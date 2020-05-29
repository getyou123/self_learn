package big_data_self_learn.spark.spark_streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}

object sparkStreamingWindowsOperation {

  def main(args: Array[String]): Unit = {

    val conf: SparkConf = new SparkConf().setMaster("local[2]").setAppName("wordcount")
    val ssc = new StreamingContext(conf,Seconds(3))

    val Dstream: ReceiverInputDStream[String] = ssc.socketTextStream("localhost",56783)
    //最基本的形成window的过程是
    Dstream.window(Seconds(2*3),Seconds(2*3))//形成window，分别是窗口长度和滑动步长

    val value: DStream[(String, Int)] = Dstream.flatMap(_.split(" "))
      .map((_, 1))

    value.reduceByKeyAndWindow(
      (x:Int,y:Int)=>x+y,
        (x:Int,y:Int)=>x-y,//在窗口发生重合的时候，如果步长小于窗口长度这种情况下会发生两次的窗口的重合
      //这个时候对于需要抛弃的key对应的那些采集周的数据怎么处理，前面那个是对于新来的那些窗口中的采集周期中的数据如何处理
        Seconds(6),
        Seconds(3),
      4,
        (x:(String,Int))=>(x._2>0))
      .print()

    ssc.start()
    ssc.awaitTermination()

  }
}
