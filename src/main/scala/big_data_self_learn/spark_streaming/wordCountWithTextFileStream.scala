package big_data_self_learn.spark_streaming

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Seconds, StreamingContext}

object wordCountWithTextFileStream {

  def main(args: Array[String]): Unit = {

    // create sparkconf
    val conf: SparkConf = new SparkConf().setAppName("wordcount").setMaster("local[*]")
    // create saprkstream with spark conf to collect data each 4 seconds
    val ssc = new StreamingContext(conf,Seconds(4))
    // use api to collect
    val DstreamString: DStream[String] = ssc.textFileStream("in")
    // word count
    DstreamString.flatMap(_.split(" ")).map((_,1))
      .reduceByKey(_+_)
      .print()

    //start and wait
    ssc.start()
    ssc.awaitTermination()
  }
}

// note: 启动之后放入两次文件才有结果
// note: 主要借鉴的是这个程序的框架
