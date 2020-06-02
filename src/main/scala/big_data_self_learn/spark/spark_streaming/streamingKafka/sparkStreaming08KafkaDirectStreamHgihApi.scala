package big_data_self_learn.spark.spark_streaming.streamingKafka

import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/*
对于kafka的数据源，写消费者的api分为高级和低级的api，两者的区别在于offset的维护是在手动维护还是定时自动提交
对于kafka的版本，在0.9之后可以是通过连接kafka集群而不是zk来进行消费的

 */
object sparkStreaming08KafkaDirectStreamHgihApi {

  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("high")
    val ssc = new StreamingContext(conf,Seconds(4))

    val broker_list:String="192.168.3.107:9092"// 如果是老版本的话用的是zk的
    val topic:String="first"
    val group:String="GP2020"
    val deserialization="org.apache.kafka.common.serialization.StringDeserializer" //指定反序列化的方法

    //之所以封装成map是因为createDirectStream时候第二个参数需要
    val kafkapara:Map[String,String]=Map(//也是通过一个config类来帮助记忆
      org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG->group,
      org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG->broker_list,
      org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG->deserialization,
      org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG->deserialization
    )

    val IDstream: InputDStream[(String, String)] =
      KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc,
      kafkapara,
      Set(topic)
    )


    IDstream.flatMap(_._2.split(" "))
      .map((_,1))
      .reduceByKey(_+_)
      .print()

    ssc.start()
    ssc.awaitTermination()
  }

}
