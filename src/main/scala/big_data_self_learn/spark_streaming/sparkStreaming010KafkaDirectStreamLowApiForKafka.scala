package big_data_self_learn.spark_streaming

/*
 这个是利用kafka中的自带的topic的存储着offset，可以使用续点消费
 */
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.{CanCommitOffsets, HasOffsetRanges, KafkaUtils}
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.{Seconds, StreamingContext}

/*
实现从kafka获取 offset并继续消费然后将最新的offset更新到kafka中的__consumer的topic中
 */
object sparkStreaming010KafkaDirectStreamLowApiForKafka {

  def main(args: Array[String]): Unit = {


    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("high")
    val ssc = new StreamingContext(conf,Seconds(4))

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "192.168.3.107:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "GP2020",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val topics = Array("first")
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,//本地的策略LocationStragies，描述缓存executors上缓存消费者和kafka的数据之间的关系
      // 有三种取值：PreferConsistent(希望在executors上均匀的分布任务)，PreferBrokers（kafka和spark的executors存在交叉希望更倾向于在），PreferFixed（最差在数据发生倾斜的时候进行执行的映射）
      Subscribe[String, String](topics, kafkaParams)
    )

    stream.map(r=>(r.key(),r.value())).print()

    stream.foreachRDD { rdd =>
      val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges

      // some time later, after outputs have completed
      stream.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges)
    }
    ssc.start()
    ssc.awaitTermination()

  }

}
