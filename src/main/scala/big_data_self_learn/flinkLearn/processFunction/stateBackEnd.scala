package big_data_self_learn.flinkLearn.processFunction

import org.apache.flink.runtime.state.filesystem.FsStateBackend
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
     * 设置一种状态后端，是否使用异步快照的方式
     */
    //        env.setStateBackend(new MemoryStateBackend(1000,false))
    env.setStateBackend(new FsStateBackend("hdfs://namenode:40010/flink", false))

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
