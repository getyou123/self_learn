package big_data_self_learn.flinkLearn.windowApi

import big_data_self_learn.flinkLearn.streamApi.transform.num_count
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.assigners.{EventTimeSessionWindows, TumblingEventTimeWindows}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow

/**
 * create by hgw on 2020/12/16 6:07 下午
 * 窗口的类型分为timeWindow和countWindow
 * 分别是按照定时的时间间隔和单个key的元素的个数达到一个级别（而不是输入的总的元素的个数）触发计算操作
 * 这里的使用的时间语义都是默认的时间语义都是处理时间
 */
object Window {

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    /**
     * nc -lk 9999
     * 先开起来
     */

    val stream1 = env.socketTextStream("localhost",9999).map(x=>num_count(x.toInt,1)).keyBy(_.num)


    /**
     * 时间滚动窗口 rolling time window
     * 窗口和窗口之间没有重叠部分，timeWindow是keyedStream的方法
     *
     */
    val window1: WindowedStream[num_count, Int, TimeWindow] = stream1.timeWindow(Time.seconds(5))
    val res1 = window1.reduce((x, y) => num_count(x.num, x.count + y.count))
    res1.print()

    /**
     * 时间滑动窗口
     * 窗口之间可能存在重叠，一般常见的需求是每隔5min测算过去10min内的平均数等
     */
    val window2 = stream1.timeWindow(Time.seconds(10), Time.seconds(5))
    val res2 = window2.reduce((x, y) => num_count(x.num, x.count + y.count))
    res2.print()

    /**
     * countWindow 滚动窗口
     * 比如设置的count的数据为5，那么按照 23 4 2 4 5 6 7 4 4都不会有任何的产出，但是在输入一个4那么就会数据key=4的数据的计算结果，之后再次输入5个4之后才会产出数据，因为是滑动窗口
     * 各个窗口之间没有重叠部分，countWindow是keyedSteam的方法
     * 这个其实是在看最近的count个元素的触发计算的情况
     */
    val res3 = stream1.countWindow(5).reduce((x, y) => num_count(x.num, x.count + y.count))
    res3.print()

    /**
     * countWindow 滑动窗口
     * 需要设置滑动的sliding_size和window_size，需要只看单个key上的操作，下面写的是每来一个key（来一个就达到了sliding，就会触发计算，看的window是单个key上的5个的元素的）
     * 一般是计算最近满足count个数的key的情况，比如计算最近的key的最近五次的value的总和
     */
    val res4 = stream1.countWindow(5, 1).reduce((x, y) => num_count(x.num, x.count + y.count))
    res4.print()

    /**
     * 会话窗口 session 这个是基于时间的
     * 会话窗口只能是基于时间的，如果某个session是超时之后的了，那么就会down掉这个连接
     */
    val res5 = stream1.window(EventTimeSessionWindows.withGap(Time.seconds(10))).reduce((x, y) => num_count(x.num, x.count + y.count))
    res5.print()

    /**
     * 更加一般化操作都是从window出发，后面指定窗口的类型
     * 比如stream1.window(TumblingEventTimeWindows)等，也指明了使用的是哪种时间语义
     * 其中指定的窗口的类型都是一些window assigner，其实和上面的几个window是一样的，上面的timeWindows是这个的实现
     * 还可以指定时间窗口的offset，用于指定为几点-几点的准确的一个小时（比如用的是9点09到10点09的窗口，而不是程序启动的时候的那个时刻）校准时区
     * 有这种需求的需要必须要使用 offset使用的是底层api的
     *
     */

    /**
     * 窗口中的数据是一条一条增加的，但是触发的计算可以是单条数据，
     * 也可以是收集到了窗口的所有的数据的在触发计算操作
     * window操作是按照针对的keyedStream的，
     * windowAll是这对非keyedStream的，后面可以接上reduce，因为窗口本身就是一批数据的概念了，这个也是需要一个window assigner
     * 同一个数据可以属于不同的窗口，窗口属于是一个bucket的，这样理解比较好，一个数据发送到不同的窗口中
     * window的计算也是按照watermark来触发的
     */

    env.execute("window test")
  }

}
