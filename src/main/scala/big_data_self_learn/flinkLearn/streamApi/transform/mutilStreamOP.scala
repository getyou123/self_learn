package big_data_self_learn.flinkLearn.streamApi.transform

import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}

/**
 * create by hgw on 2020/12/6 11:38 下午
 * 多流数据的处理，包含流分流和合流
 * 分流：split然后select，把数据中一部分按照某些特征进行数据的分流
 *
 */
object mutilStreamOP {

  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    val list = List(1, 2, 3, 4, 3, 4, 3)

    /**
     * 分流操作：
     * 通过split+select进行分流，但是目前来看这个操作已经过时
     */
    val stream1 = env.fromCollection(list)
    val stream2 = stream1.map(x => (x, x.toString + "yes"))
      .split(x => if (x._1 > 3) Seq("large") else Seq("small")) //实现按照是不是大于3进行数据的分流操作，返回的数据类型是splitStream
      .select("large") //在进行split之后按照select进行选择出来，返回的数据类型是dataStream

    val stream3 = stream1.map(x => (x, x.toString + "yes"))
      .split(x => if (x._1 > 3) Seq("large") else Seq("small")) //实现按照是不是大于3进行数据的分流操作，返回的数据类型是splitStream
      .select("small") //在进行split之后按照select进行选择出来，返回的数据类型是dataStream

    stream2.print()

    /**
     * 合流操作：
     * 不同的数据类型的合流使用connect+comap
     */

    val stream4: DataStream[(Int, Int)] = stream1.map(x => (x, x + 1))
    val stream5 = stream1.map(x => (x, x.toString + "yes"))
    val stream6: ConnectedStreams[(Int, Int), (Int, String)] = stream4.connect(stream5) //注意此时的数据的类型
    val stream7: DataStream[Int] = stream6.map( //注意这里的map需要对于两个流的数据的处理操作
      firstElem => firstElem._1,
      secondElem => secondElem._1
    )
    stream7.print()

    /**
     * 合流操作：
     * 相同的数据类型的流使用union即可实现合流操作
     */

    val stream8: DataStream[(Int, Int)] = stream1.map(x => (x, x + 1))
    val stream9: DataStream[(Int, Int)] = stream1.map(x => (x * x, x + 1))
    val stream10: DataStream[(Int, Int)] = stream8.union(stream9)
    stream10.print()
    env.execute("splitStream")


  }

}
