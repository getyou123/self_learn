package big_data_self_learn.spark.spark_streaming

import kafka.serializer.StringDecoder
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.dstream.{InputDStream, ReceiverInputDStream}
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.Option


/*
对于kafka的数据源，写消费者的api分为高级和低级的api，两者的区别在于offset的维护是在消费者维护还是
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
// note :总结来看kafka的高阶的api是不传入offset的，低级的api是传入ofset的这是两者的区别。两者的差异在于KafkaUtils.createDirectStream
// 时候是否传入offset

// 对于kafka的消费过程的参数：首先接受到队列中的数据类型是kv对，这在接收的Dstream和创建接收器时候就要指明其中的泛型
// 消费的参数至少包括：所属的group，消费时候的连接地址（0.9版本为分界，前面连接zk后面连接kafka集群），订阅的主题topics
