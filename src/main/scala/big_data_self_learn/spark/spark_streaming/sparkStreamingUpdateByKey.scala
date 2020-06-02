package big_data_self_learn.spark.spark_streaming

/*
有状态和无状态的转化，这个是在存储了所有的从收集开始到当前的手机周期的效果叠加
 */
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.dstream.ReceiverInputDStream


object sparkStreamingUpdateByKey {

  //定义了对于相同的key的更新方法
  //注意这里如果返回了None的话就是销毁这个key对应的数据
  def updateFunc: (Seq[Int], Option[Int]) => Some[Int] = (valuesAll:Seq[Int], state:Option[Int])=>{
    //参数的意义，Seq[Int]是同一个key的所有的values，因为是wordcount所以所有的values构成Seq[Int]
    //而维护的state是到目前为止这个key的value是多少，初次时候可能没有值所以是Option
    val currentAdd=valuesAll.foldLeft(0)(_+_)
    val oldState=state.getOrElse(0)
    Some(oldState+currentAdd)

  }

  def main(args: Array[String]): Unit = {

    val conf: SparkConf = new SparkConf().setMaster("local[2]").setAppName("wordcount")
    val ssc = new StreamingContext(conf,Seconds(3))
    //注意设置checkpoint
    ssc.checkpoint("chek2")

    val Dstream: ReceiverInputDStream[String] = ssc.socketTextStream("localhost",56783)

    Dstream.flatMap(_.split(" "))
      .map((_,1))
      .updateStateByKey[Int](updateFunc)
      .print()

    ssc.start()
    ssc.awaitTermination()
  }
}
