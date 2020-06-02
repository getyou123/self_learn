package big_data_self_learn.spark.spark_streaming.basicSource

import java.io.{BufferedReader, InputStreamReader}
import java.net.Socket
import java.nio.charset.StandardCharsets

import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.ReceiverInputDStream
import org.apache.spark.streaming.receiver.Receiver
import org.apache.spark.streaming.{Seconds, StreamingContext}

class userDefinedReceiver(host:String,port:Int)//这个是构造这个接收器的参数
  extends Receiver[String](StorageLevel.MEMORY_AND_DISK_2){//继承的父类的构造函数，因为是接受String的接收器所以泛型写String，存储级别自定义
  //继承类的时候直接在子类名上使用alt+enter实现导入所有需要实现的抽象类方法
  override def onStart(): Unit = {
    new Thread("socket receiver"){
      override def run(): Unit ={
        receiveAndAct  //通过 thread来实现函数的调用 这个函数中就是主要的接收器的主要的逻辑处理
      }
    }.start()//注意开始进程
  }

  override def onStop(): Unit = {

  }

  def receiveAndAct()={//这个是实际的逻辑，从socket中的读取数据完成存储store

    //所以这个过程中得主要工作是store(String)，本次是从socket中的监听读取

    var socket:Socket=null
    var userinput:String=null

    try{
      socket=new Socket(host,port)
      val reader = new BufferedReader(new InputStreamReader(socket.getInputStream(),StandardCharsets.UTF_8))
      userinput=reader.readLine()
      while(userinput!=null && !isStopped()){//这个isStoped是父类中实现的
        store(userinput)//这里的userInptu是String类型对应于继承的类是String类型的
        userinput=reader.readLine()
      }
      reader.close()
      socket.close()
    }catch {
      case e:java.net.ConnectException=>restart("Error Connecting to "+host+":"+port,e)
      case t:Throwable=>restart("Error receiveing data",t)
    }
  }
}
//end of userDefinedReceiver

object selfDefinedReceiver {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("userDefinedReceiver").setMaster("local[*]")
    val ssc = new StreamingContext(conf,Seconds(4))


    //生成一个Receiver类对象,并设置成成数据的来源
    val userDefinedReceiver = new userDefinedReceiver("localhost",56783)
    val DStream: ReceiverInputDStream[String] = ssc.receiverStream(userDefinedReceiver)

    DStream.flatMap(_.split(" "))
      .map((_,1))
      .reduceByKey(_+_)
      .print()

    ssc.start()
    ssc.awaitTermination()
  }
}


