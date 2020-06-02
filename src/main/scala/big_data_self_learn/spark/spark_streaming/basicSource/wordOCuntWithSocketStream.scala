package big_data_self_learn.spark.spark_streaming.basicSource

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.ReceiverInputDStream
import org.apache.spark.streaming.{Seconds, StreamingContext}

object wordOCuntWithSocketStream {

  def main(args: Array[String]): Unit = {

    val conf: SparkConf = new SparkConf().setMaster("local[2]").setAppName("wordcount")
    val ssc = new StreamingContext(conf,Seconds(3))

    val Dstream: ReceiverInputDStream[String] = ssc.socketTextStream("localhost",56783)

    Dstream.flatMap(_.split(" "))
      .map((_,1))
      .reduceByKey(_+_)
      .print()

    ssc.start()
    ssc.awaitTermination()
  }
}
