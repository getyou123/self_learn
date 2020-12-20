package big_data_self_learn.flinkLearn.streamApi.transform

import org.apache.flink.streaming.api.scala._

/**
 * create by hgw on 2020/12/6 12:16 下午
 * 键控流转换首先进行keyby操作，然后在接上聚合操作算子，属于数据流式处理的滚动聚合，
 * 需要注意的是程序需要自己保持key的有限行，否则产生了无限的值域的话会导致程序无限的大
 * key也可以传入的一个表达式返回一个分组的key，这个和map然后在根据某个字段进行key也是一致的
 * sum
 * min 返回值
 * max
 * minBy 返回元素
 * maxBy
 * reduce 这个是按照指定的方式进行数据的聚合但是前提是数据已经按照key进行了keyed的，但是这个reduce是不会更改数据的格式的
 */

case class num_count(num: Int, count: Int)

object keyedStream {
  def main(args: Array[String]): Unit = {
    //指的就是将数据分流处理，其实同一个key的数据在同一个分区，但是同一个分区内的数据的key可以是不同的
    //通过keyby实现从DStream转化成keyedStream
    //keyby+聚合算子属于流处理方式中的滚动聚合，对于来的数据更新相应的统计信息，其实这个数据最好在map阶段就定好最终的形式，意思是滚动聚合的前提是这个数据已经被map过了
    //然后在reduce的时候其实就是一个+或者是比较的操作即可，因为reduce是不能定义最终的数据的形式的

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    val list = List(1, 2, 3, 4, 3, 4, 3)

    val stream1 = env.fromCollection(list)
      .map(x => (x, x + 2))
      .map(x => num_count(x._1, x._2))


    println("stream2-----")
    val stream2 = stream1.keyBy("num") //这里等同于keyBy(0),或者是keyBy(_.num)，逻辑上效果是把不同的key分区到不到分区中，然后对这个分区中的数据进行滚动的聚合
      //      .minBy("count") //sum也可以指定的filed的一个数值
      .reduce((x, y) => num_count(x.num, x.count + y.count))
    stream2.print()

    //
    //    /**
    //     * 6> num_count(1,3)
    //     * 8> num_count(3,5)
    //     * 1> num_count(4,6)
    //     * 1> num_count(4,12)
    //     * 8> num_count(3,10)
    //     * 8> num_count(3,15)
    //     * 8> num_count(2,4)
    //     */
    //    //这里数据的反馈像是一个按照统计的流的最终的结果产出的过程,8核心下的一种情况
    //    //可以通过设置并行度为1然后实现按照顺序的产
    //同时可以是看出这个是更新一个产出一个分区的临时统计结果，在数据上表现的就是更改了一个产生一个结果（也是结果中的一个部分，更新操作）

    println("stream3--------")
    val stream3 = stream1.keyBy(_.num).filter(_.count > 2)
    stream3.print()

    /**
     * stream 4 使用reduce方式进行数据的聚合操作
     * 注意reduce是不更改数据的格式的，
     * 这是实现是按照sensor进行数据的计数
     */
    val steam4 = stream1.map(x => (x, 1)).keyBy(_._1).reduce((x, y) => (x._1, x._2 + y._2))
    steam4.print()

    env.execute("keyedstream")


  }
}
