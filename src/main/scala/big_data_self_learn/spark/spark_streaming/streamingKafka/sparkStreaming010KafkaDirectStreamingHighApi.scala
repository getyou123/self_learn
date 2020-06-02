package big_data_self_learn.spark.spark_streaming.streamingKafka

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.{Seconds, StreamingContext}

object sparkStreaming010KafkaDirectStreamingHighApi {

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
      PreferConsistent,//
      Subscribe[String, String](topics, kafkaParams)
    )
    val IDstream: DStream[(String, String)] = stream.map(record => (record.key, record.value))

    IDstream.print()

    ssc.start()
    ssc.awaitTermination()
  }
}
