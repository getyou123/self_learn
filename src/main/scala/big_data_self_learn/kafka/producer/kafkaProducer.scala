package big_data_self_learn.kafka.producer

import java.util.Properties

import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}

object kafkaProducer {

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

    val producer = new KafkaProducer[String, String](props)//生产者生产数据的kv的类型

    //无回调函数的版本，且无get则是发完即忘的模式
//    var i = 0
//    while(i < 50) {
//      //发送的数据是ProducerRecord，可以看出来producerRecrd的api中有五种方式，可以指明写入的partition
//      //也可以指明key，或者是没有key的时候就是使用轮询的
//      producer.send(//这里开启的是sender的线程的，如果为了实现同步的话的，使用send().get()
//        new ProducerRecord[String, String]("test", i.toString, "hello world-" + i)
//      )
//      i += 1
//    }
//    producer.close()
//  }
  //有回调函数版本，有回调函数就是异步发送的
  var i=100
  while (i<200){
    producer.send(
      new ProducerRecord[String,String]("test",i.toString,"he"),
      new Callback(){
        @Override
        override def onCompletion(metadata: RecordMetadata, exception: Exception) = {
          if(exception==null){//无异常
            println(metadata.topic()+"\t"+metadata.partition()+"\t"+metadata.offset())
          }else{
            exception.printStackTrace()
          }
        }
      })
    i+=1
  }
    producer.close()
  }
}
