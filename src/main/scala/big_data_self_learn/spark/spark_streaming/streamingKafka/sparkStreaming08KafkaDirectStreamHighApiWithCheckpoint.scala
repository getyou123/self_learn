package big_data_self_learn.spark.spark_streaming.streamingKafka

import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

object sparkStreaming08KafkaDirectStreamHighApiWithCheckpoint {


  def getNewStreamingContext(checkpointPath:String):StreamingContext={//注意这里的逻辑是获取之后进行处理并进行保存和返回

    val conf: SparkConf = new SparkConf().setAppName("kafkaHigh02").setMaster("local[*]")
    val ssc = new StreamingContext(conf,Seconds(3))
    ssc.checkpoint(checkpointPath)//看源码的逻辑好像只是设置了checkpoint的路径，没有马上就网里面存储ssc的状态


    //进行实际的消费
    val broker_list:String="192.168.3.107:9092"// 如果是老版本的话用的是zk的
    val topic:String="first"
    val group:String="GP2020"
    val deserialization="org.apache.kafka.common.serialization.StringDeserializer" //指定反序列化的方法

    //之所以封装成map是因为createDirectStream时候第二个参数需要
    val kafkapara:Map[String,String]=Map(
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

    ssc//返回ssc对象

  }

  def main(args: Array[String]): Unit = {

    val checkpointPath:String="chk"
    // 使用kafka并设置ssc的checkpoint可以实现消费者从上次的offset处进行消费
    // 原理上是kafka的消费的offset是被存储在ssc streamingcontext中的额，在设置好checkpoint之后
    // 获取ssc的方式不是一味重新创建而是可以冲checkpoint中进行读取，从而实现从上次的消费过程来集训消费

    val ssc: StreamingContext = StreamingContext.getActiveOrCreate(checkpointPath,()=>getNewStreamingContext(checkpointPath))
    ssc.start()
    ssc.awaitTermination()
  }

}
