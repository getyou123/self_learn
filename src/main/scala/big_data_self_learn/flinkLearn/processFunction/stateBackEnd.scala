package big_data_self_learn.flinkLearn.processFunction

import java.util.concurrent.TimeUnit

import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.apache.flink.api.common.time.Time
import org.apache.flink.runtime.state.filesystem.FsStateBackend
import org.apache.flink.streaming.api.CheckpointingMode
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment


/**
 * create by hgw on 2020/12/21 1:43 下午
 *
 */
object stateBackEnd {
  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    /**
     * 状态后端是所有的算子状态（算子的中间结果）和键控状态（某个键的一些中间结果）的集合管理插件，状态看成是中间的结果
     * 有三种状态后端：
     * MemoryStateBackend：内存级别 JOBManager的堆上,默认
     * FsStateBackEnd：文件系统，本地状态存储在TaskManager的堆上，远程存储在远程文件系统上
     * RocksDbStateBackend：存在本地的RocksDB上（这个是FS中本地内存存不下的使用，本地存储的状态太多的情况下是使用这种方式的）
     */

    /**
     * checkpoint是所有的task都被设置到处理完同一个数据（遇到barrier的的状态保存）
     * checkpoint这个操作是jobManager做的往远程写状态的操作，本质是操作，但都是需要task返回状态数据
     * jobManager通过插入barrier来实现通知Task作自己的状态保存，并收集所有的task的状态进行存盘，可以设置收集多个（所有状态）
     */

    /**
     * 设置一种状态后端，是否使用异步快照的方式
     * 异步快照方式就是写checkpoint到远程文件系统的的时候还可以接着处理下面的数据，内存留一份用于真正的checkpoint，另外的作其他的状态修改响应，
     * 异步就是不用等checkpoint完成写入到远程文件就响应下面的状态修改
     * false表示是同步的，一个checkpoint没做完不会响应下一个checkpoint请求
     */
    //        env.setStateBackend(new MemoryStateBackend(1000,false))
    env.setStateBackend(new FsStateBackend("hdfs://namenode:40010/flink", false))

    /**
     * 配置checkpoint，
     * 所以checkpoint是所有的task的状态的存储，同一个的数据处理的之后的状态组成了需要进行存储的checkpoint
     */
    env.enableCheckpointing(1000) //开启checkpoint，多久触发一次checkpoint
    env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE) //设置checkpoint模式，默认是ExactlyONCE，要求快的时候可以使用at_lest_onc60
    env.getCheckpointConfig.setCheckpointTimeout(60000) //job manager存FS的时候超时时间长度，写入到远程文件系统的超时间设置
    //主要前三个配置
    env.getCheckpointConfig.setMaxConcurrentCheckpoints(2) //前一个checkpoint没做完后面的checkpoint就来了最多响应几个checkpoint，就是job Manger没做完上一个checkpoint的还能插入几个barrier
    env.getCheckpointConfig.setMinPauseBetweenCheckpoints(500) //checkpoint 前一个结束和后一个开始的的至少的时间长度间隔

    /**
     * 重启策略配置，一共三种：
     * 固定延迟重启策略（最常用）
     *
     */
    env.setRestartStrategy(RestartStrategies.fixedDelayRestart(
      3, // 重启次数
      Time.of(10, TimeUnit.SECONDS) // 延迟时间间隔
    ))

    /**
     * 保存点，手动给出的checkpoint操作
     * 程序升级（bug 修改）
     * flink的版本更改升级
     * AB测试
     * 但是目前来看savepoint的处理要求有点多，算子不能变，状态的名称不能变这些都有不少的限制了
     */
    /**
     * 使用RocksDbStateBackend引入依赖
     * <dependency>
     * <groupId>org.apache.flink</groupId>
     * <artifactId>flink-statebackend-rocksdb_${scala.binary.version}</artifactId>
     * <version>1.8.0</version>
     * </dependency>
     */
  }
}
