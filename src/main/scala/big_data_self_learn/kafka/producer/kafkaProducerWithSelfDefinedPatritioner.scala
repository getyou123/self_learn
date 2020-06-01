package big_data_self_learn.kafka.producer

import java.util
import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, Partitioner, ProducerRecord}
import org.apache.kafka.common.Cluster

/*
  实现向指定的分区写数据
 */
object kafkaProducerWithSelfDefinedPatritioner {


  class selfDefinedPartitioner extends Partitioner{//注意继承的父类，使用的时候是在props中设置使用的类全类名
    //主要的逻辑在这里，参数意义，topic（所以可以实现对不同的topic的自定义不同的partitioner），keyObject，keyByte，valueObject,valueBytes，这些bytes是序列化之后的形式，cluster的原信息（比如一个topic的所有的额partitions数目）
    override def partition(s: String, o: Any, bytes: Array[Byte], o1: Any, bytes1: Array[Byte], cluster: Cluster): Int = {
      0//返回的数值为int类型，指明将何种数据存储到那个分区
    }
    //这个是实现资源关闭等等
    override def close(): Unit = {}
    //这个可以实现给核心的逻辑的那个函数提供需要兑取的一些配置文件的信息
    override def configure(map: util.Map[String, _]): Unit = {}
  }


  def main(args: Array[String]): Unit = {

    //生成的配置配置信息
    val props=new Properties()

    // Kafka服务端的主机名和端口号，和kafka中控制台的broker-list一样
    props.put("bootstrap.servers", "192.168.3.107:9092")
    // 等待所有副本节点的应答
    props.put("acks", "all")//ack的机制保证了producer的程序生产 -1（all） 0（所有的操作不需要回复） 1（只要求leader回复）
    // 消息发送最大尝试次数 ，发送失败重试次数。0次是失败了就不再次发送了
    props.put("retries", "0")
    // 一批消息处理大小，数据大小变成这么大的时候就发送
    props.put("batch.size", "16384")
    // 请求延时，如果数据没那么大，但是过了这么多时间就需要发送出去生产的数据
    props.put("linger.ms", "1")
    // 发送缓存区内存大小
    props.put("buffer.memory", "33554432")
    // key序列化
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    // value序列化
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    //设置使用的自定义的分区器
    props.put("partitioner.class","test.kafka.kafkaProducerWithSelfDefinedPatritioner")

    val producer = new KafkaProducer[String, String](props)//生产者生产数据的kv的类型

    //无回调函数的版本
    var i = 0
    while(i < 50){
      //发送的数据是ProducerRecord，指定
      producer.send(
        new ProducerRecord[String, String]("test", i.toString, "hello world-" + i)
      )
      i += 1
    }

    producer.close()
  }
}
