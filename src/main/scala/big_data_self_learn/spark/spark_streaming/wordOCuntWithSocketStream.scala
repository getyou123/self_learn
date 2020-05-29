package big_data_self_learn.spark.spark_streaming

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

//其实主要的变化是从哪里进行数据的收集
//使用netcat工具时候nc -lk port端口号
//win10 使用的命令是nc -l -p port端口号
//只有端口开始之后才有信息，否则会报错
// note: 本程序实现的是架空kafka中的指定的topic但是并不能实现消费者的断点继续消费