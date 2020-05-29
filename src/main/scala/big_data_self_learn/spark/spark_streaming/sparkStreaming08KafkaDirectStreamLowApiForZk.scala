package big_data_self_learn.spark.spark_streaming

/*
 注意版本的事情，kafkacluster这个是从zk中读出offset的，但是如果你的集群是kafka0-11中是将offset存储在
 topic中的，所以无论怎么获取获取都是没有然后被重新置位0
 */

/*
相应的pom的link
    <!-- 会按照正式集群使用的kafka的版本进行设置的目前这个是简单的0.8的版本的-->
    <dependency>
      <groupId>org.apache.spark</groupId>
      <artifactId>spark-streaming-kafka-0-8_2.11</artifactId>
      <version>2.1.1</version>
    </dependency>
 */

import kafka.common.TopicAndPartition
import kafka.message.MessageAndMetadata
import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka.{HasOffsetRanges, KafkaCluster, KafkaUtils, OffsetRange}
import org.apache.spark.streaming.kafka.KafkaCluster.Err
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.mutable

object sparkStreaming08KafkaDirectStreamLowApiForZk {//这个程序时间offset保存到zk中的

  //通过kafkaCluster和topic和group获取到维护的offset的参数
  def getOffset(kafkaCluster: KafkaCluster, group: String, topic: Set[String]): Map[TopicAndPartition, Long] = {
    //返回的结果
    var resultMap = new mutable.HashMap[TopicAndPartition,Long]()
    // 从topic中获取partition
    val topicAndPatitionsEither: Either[Err, Set[TopicAndPartition]] = kafkaCluster.getPartitions(topic)

    // 根据topic分区获取的结果，这里由于一些topic可能更不不存在的情况进行分类
    if (topicAndPatitionsEither.isRight) {
      //如果topic的分区存在的话获取到其中的数据
      val topicAndpartitions: Set[TopicAndPartition] = topicAndPatitionsEither.right.get
      val topicAndPartitionToLongEither: Either[Err, Map[TopicAndPartition, Long]] = kafkaCluster.getConsumerOffsets(group,topicAndpartitions)
      if (topicAndPartitionToLongEither.isRight) {//可以获取到的topic的消费情况
        val topicAndPartitionToLong: Map[TopicAndPartition, Long] = topicAndPartitionToLongEither.right.get//到这里是获取到了指定group对指定topic的offset
        //获取其中的数据或者使用++=
//        for (elem<- topicAndPartitionToLong){
//          resultMap+=elem
//        }
        resultMap++=topicAndPartitionToLong
      }else{//一些topic可能从来就没有被消费过或者无法获取到消费的offset，为他们赋值到最初的offset，注意如果这个topic获取不到offset那么他的
        for(elem<-topicAndpartitions){
          resultMap+=(elem->0L)//注意map中的kv的写法，这个是为每一个topic的offset赋值为初始的0
        }
      }
      }
    resultMap.toMap
  }

  def setOffset(kafkaCluster:KafkaCluster,groupid: String ,IDstream: InputDStream[String]) = {


    // 处理数据之后的offset是存在stream中的，消息中存储了offset
    IDstream.foreachRDD (rdd => {
      var partitionToLong = new mutable.HashMap[TopicAndPartition, Long]()
      val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges //这个是这个topic有多少分区就有多少的offset
      for (offsetOfPatition <- offsetRanges) { //取出一个分区的offset
        //offsetOfPatition.untilOffset表征了这个offset的消费的进度
        partitionToLong += (offsetOfPatition.topicAndPartition() -> offsetOfPatition.untilOffset) //存储到
      }
      kafkaCluster.setConsumerOffsets(groupid, partitionToLong.toMap)
    })

  }

  //从ZK获取offset
  def getOffsetFromZookeeper(kafkaCluster: KafkaCluster, kafkaGroup: String, kafkaTopicSet: Set[String]): Map[TopicAndPartition, Long] = {

    // 创建Map存储Topic和分区对应的offset
    val topicPartitionOffsetMap = new mutable.HashMap[TopicAndPartition, Long]()

    // 获取传入的Topic的所有分区
    // Either[Err, Set[TopicAndPartition]]  : Left(Err)   Right[Set[TopicAndPartition]]
    val topicAndPartitions: Either[Err, Set[TopicAndPartition]] = kafkaCluster.getPartitions(kafkaTopicSet)

    // 如果成功获取到Topic所有分区
    // topicAndPartitions: Set[TopicAndPartition]
    if (topicAndPartitions.isRight) {
      // 获取分区数据
      // partitions: Set[TopicAndPartition]
      val partitions: Set[TopicAndPartition] = topicAndPartitions.right.get

      // 获取指定分区的offset
      // offsetInfo: Either[Err, Map[TopicAndPartition, Long]]
      // Left[Err]  Right[Map[TopicAndPartition, Long]]
      val offsetInfo: Either[Err, Map[TopicAndPartition, Long]] = kafkaCluster.getConsumerOffsets(kafkaGroup, partitions)

      if (offsetInfo.isLeft) {

        // 如果没有offset信息则存储0
        // partitions: Set[TopicAndPartition]
        for (top <- partitions)
          topicPartitionOffsetMap += (top -> 0L)
      } else {
        // 如果有offset信息则存储offset
        // offsets: Map[TopicAndPartition, Long]
        val offsets: Map[TopicAndPartition, Long] = offsetInfo.right.get
        for ((top, offset) <- offsets)
          topicPartitionOffsetMap += (top -> offset)
      }
    }
    print(topicPartitionOffsetMap.toMap)
    topicPartitionOffsetMap.toMap
  }


  def offsetToZookeeper(kafkaCluster:KafkaCluster,groupid: String ,IDstream: InputDStream[String])={

    IDstream.foreachRDD {
      rdd =>
        // 获取DStream中的offset信息
        // offsetsList: Array[OffsetRange]
        // OffsetRange: topic partition fromoffset untiloffset
        val offsetsList: Array[OffsetRange] = rdd.asInstanceOf[HasOffsetRanges].offsetRanges

        // 遍历每一个offset信息，并更新Zookeeper中的元数据
        // OffsetRange: topic partition fromoffset untiloffset
        for (offsets <- offsetsList) {
          val topicAndPartition = TopicAndPartition(offsets.topic, offsets.partition)
          // ack: Either[Err, Map[TopicAndPartition, Short]]
          // Left[Err]
          // Right[Map[TopicAndPartition, Short]]
          print(Map((topicAndPartition, offsets.untilOffset)))
          val ack: Either[Err, Map[TopicAndPartition, Short]] = kafkaCluster.setConsumerOffsets(groupid, Map((topicAndPartition, offsets.untilOffset)))
          if (ack.isLeft) {
            println(s"Error updating the offset to Kafka cluster: ${ack.left.get}")
          } else {
            println(s"update the offset to Kafka cluster: ${offsets.untilOffset} successfully")
          }
        }
    }

  }

  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("high")
    val ssc = new StreamingContext(conf,Seconds(4))

    val broker_list:String="192.168.3.107:9092"// 如果是老版本的话用的是zk的
    val topic:String="second"
    val group:String="GP2020"
    val deserialization="org.apache.kafka.common.serialization.StringDeserializer" //指定反序列化的方法

    //之所以封装成map是因为createDirectStream时候第二个参数需要
    val kafkapara:Map[String,String]=Map(
//      "zookeeper.connect"->"192.168.3.107:2181",
      org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG->group,
      org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG->broker_list,
      org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG->deserialization,
      org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG->deserialization
    )

    //createDirectStream中的第三个参数时候需要,由于是程序自己维护的offset所以需要进行读取
    //获取offset的过程需要new一个kafkacluster的对象
    val kafkaCluster = new KafkaCluster(kafkapara)//需要的参数只有一个kafkapara参数
    val fromOffset:Map[TopicAndPartition,Long]=
      getOffsetFromZookeeper(kafkaCluster,group,Set(topic))

    //低阶api是程序自身维护offset，正确区分高级api和低级api还有，设置checkpoint的实现继续消费的情况

    val IDstream: InputDStream[String] =
      KafkaUtils.createDirectStream[String,String,StringDecoder,StringDecoder,String](ssc,//五个参数的意义分别是k，V和两者的接码，之后那个是消息的格式
      kafkapara,
      fromOffset,
      (message:MessageAndMetadata[String,String]) => message.message()//对应第五个泛型参数
    )
    IDstream.print()

    // 设置处理之后的offset的函数
    offsetToZookeeper(kafkaCluster,topic,IDstream)


    ssc.start()
    ssc.awaitTermination()
  }

}
