package big_data_self_learn.kafka

import java.util
import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerInterceptor, ProducerRecord, RecordMetadata}

/*
拦截器在kafka0.10版本之后才有，使用的作用就是在生产消息的时候对产生写入的消息加以限制或者修改
 */


class selfDefinedInterceptor extends  ProducerInterceptor[String,String]{

  var successRcordCnt:Int=0//在类中维护变量，用于统计多少条信息发送成功
  //主要逻辑,用于对producer中的发送的数据格式等进行修改
  override def onSend(record: ProducerRecord[String, String]): ProducerRecord[String, String] = {
    var newRecval=record.value()
    newRecval+=" times"
    new ProducerRecord[String,String](record.topic(),record.partition(),record.key(),newRecval)
  }

  //这个是在成功之后返回存储的record的元数据的信息
  override def onAcknowledgement(metadata: RecordMetadata, exception: Exception): Unit ={
    if (metadata != null){
      successRcordCnt+=1
    }
  }

  //结束时候的数据输出
  override def close(): Unit = {
    println("成功输入的消息条数是："+successRcordCnt)
  }

  override def configure(configs: util.Map[String, _]): Unit = {

  }
}



object kafkaProducerInterceptor {

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

    //添加拦截器,拦截器存在顺序性，新建一个array依次添加
    var interceptorArr=new util.ArrayList[String]()
    interceptorArr.add("test.kafka.selfDefinedInterceptor")
    props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG,interceptorArr)

    val producer = new KafkaProducer[String, String](props)//生产者生产数据的kv的类型

    //无回调函数的版本
    var i = 0
    while(i < 50) {
      //发送的数据是ProducerRecord，可以看出来producerRecrd的api中有五种方式，可以指明写入的partition
      //也可以指明key，或者是没有key的时候就是使用轮询的
      producer.send(//这里开启的是sender的线程的，如果为了实现同步的话的，使用send().get()
        new ProducerRecord[String, String]("test", i.toString, "hello world-" + i)
      )
      i += 1
    }
    producer.close()
  }

}
