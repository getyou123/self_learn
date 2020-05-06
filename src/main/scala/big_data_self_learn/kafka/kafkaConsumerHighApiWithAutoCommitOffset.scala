package big_data_self_learn.kafka

import java.util
import java.util.Properties

import org.apache.kafka.clients.consumer.{ConsumerRecords, KafkaConsumer}

import scala.collection.JavaConversions._

object kafkaConsumerHighApiWithAutoCommitOffset {

  def main(args: Array[String]): Unit = {

    //配置参数,使用简单的map或者props都是可以的，这些参数如果记不起来，可以用的一个consumerConfig对象
    val props = new Properties()
    props.put("bootstrap.servers", "192.168.3.107:9092")
    props.put("group.id", "test")
    //是否自动提交offset
    props.put("enable.auto.commit", "true")
    //自动提交的延时，数据读出来之后过一秒提交，所以存在重读消费的可能性。尽量不使用高级的api来操作
    props.put("auto.commit.interval.ms", "1000")
    //kv的反序列化类
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    //是否是从最开始的位置开始消费，重置最新的还是最早的offset
    props.put("auto.offset.reset", "earliest") //能找到的最早的位置，相当于shell中的--from-beginning，这个可能是从最开始的位置，也可能只是从7天前
    //同时也有另外的默认的选项是latest，表示是从程序开始运行的时候算的offset，程序起来了才开始进行算offset消费

    //构造消费者
    val consumer = new KafkaConsumer[String, String](props)
    //订阅topics
    consumer.subscribe(util.Arrays.asList("test"))

    //进行轮询的消费数据，获取到的数据的kv对是，consumerRecord类型
    while (true) {
      val records: ConsumerRecords[String, String] = consumer.poll(100)
      for (record <- records) {
        println(s"offset = ${record.offset}, key = ${record.key}, value = ${record.value}")
      }
    }
    //注意这里没哟提交offset导致只能处理程序运行时候发送的数据
    consumer.close()
  }

}
