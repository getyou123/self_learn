package big_data_self_learn.kafka.consumer

import java.util
import java.util.Properties

import org.apache.kafka.clients.consumer.{ConsumerRecords, KafkaConsumer, OffsetAndMetadata, OffsetCommitCallback}
import org.apache.kafka.common.TopicPartition

object kafkaConsumerHighApiWithOutAutoCommitOffset {


  def main(args: Array[String]): Unit = {

    //配置参数
    val props = new Properties()
    props.put("bootstrap.servers", "10.39.222.28:9092")
    props.put("group.id", "console-consumer-32141231")
    //是否自动提交offset
    props.put("enable.auto.commit", "false")
    //kv的反序列化类
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")

    //构造消费者
    val consumer = new KafkaConsumer[String,String](props)
    //订阅topics
    consumer.subscribe(util.Arrays.asList("data_company"))

    //进行轮询的消费数据
    while (true) {
      val records: ConsumerRecords[String, String] = consumer.poll(100)
      var it=records.iterator()
      while(it.hasNext) {
        val record=it.next()
        println(s"offset = ${record.offset}, key = ${record.key}, value = ${record.value}, timestamp= ${record.timestamp}")
      }
//      consumer.commitSync()//同步提交,当前的线程会被阻塞直到提交成功，这个是对于offset管理比较安全的方式，会开启自动重试的方式
      consumer.commitAsync(new OffsetCommitCallback(){//异步提交的方式，不保证一定成功提交
        override def onComplete(offsets: util.Map[TopicPartition,
          OffsetAndMetadata], exception: Exception) = {
          if(exception!=null){
            println("Commit offset error "+offsets)
          }else{
            print("")
          }
        }
      })//异步提交的方式

      //手动提交消费的offset前提是将props中的自动提交和自动提交时间关闭
    }
    consumer.close()
  }
}
