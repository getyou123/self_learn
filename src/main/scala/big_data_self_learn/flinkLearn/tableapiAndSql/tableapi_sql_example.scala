package big_data_self_learn.flinkLearn.tableapiAndSql

import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api._
import org.apache.flink.table.api.scala._
import org.apache.flink.table.descriptors.{FileSystem, Kafka, OldCsv, Schema}
import org.apache.spark.sql.execution.streaming.FileStreamSource.Timestamp

/**
 * create by hgw on 2020/12/26 10:23 下午
 * 简单介绍程序的基本结构
 * 读取数据转化为dataStream
 * 转化成table
 * 执行sql
 * 转回成dataStream
 */
case class SensorReadingForTableExample(name: String, temperature: Int, eventTime: Timestamp)

object tableapi_example {
  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    env.setParallelism(1)

    val stream1 = env.fromCollection(List(
      SensorReadingForTableExample("1", 23, 1608445793),
      SensorReadingForTableExample("2", 24, 1608445879),
      SensorReadingForTableExample("3", 24, 1608445912),
      SensorReadingForTableExample("3", 22, 1608445600)
    ))
    stream1.assignAscendingTimestamps(x => x.eventTime * 1000) //如果数据已经是非乱序的，直接设置事件时间即可

    /**
     * 所有的步骤总结为：
     * 注册一个表的执行环境 val tableEnv  = （这个区分是stream的还是batch）（区分planner版本）
     * 创建一个输入表（可以从其他的数据流进行转换成表）（基于上一个获取的环境数据的输入的方式）
     * 注册一个输出表
     * table APi就是DQL
     * SQL是就是专门的SQL语句支持的
     * 获得到最后的TABLE之后INSERT INTO
     * 所以最核心的数据抽象是Table
     */

    //    //1。获取表的执行环境,流式查询
    //    val tableEnv = StreamTableEnvironment.create(env)
    //
    //    //2。获取一个表：从数据流中创建一张表；从外部系统创建创建一个表
    //    val table = tableEnv.fromDataStream(stream1)
    //
    //    //3。执行DQL或者SQL等操作
    //    /**
    //     * 一种是基于table这类型的表的
    //     */
    //    val res1 = table.select("name,temperature")
    //      .filter("name == '3'")
    //    res1.toAppendStream[(String, Int)].print("DQL") //两个字段类型的结果
    //
    //    /**
    //     * 另外一种是基于env中的注册好的表的
    //     */
    //    tableEnv.createTemporaryView("stream1", table)
    //    val sql = "select name,temperature from stream1 where name = '3'"
    //    val res2: Table = tableEnv.sqlQuery(sql)
    //    res2.toAppendStream[(String, Int)].print("SQL") //两个字段类型结果

    /**
     * 一、配置tableEnv执行环境
     * 分成老版本和新版本的 planner（旧版本和blink的版本）
     * 分成流式查询和批量查询
     */
    //1.使用老版本planner + stream + 基于StreamTableEnvironment
    val settingsForOldPlannerStreamEnv = EnvironmentSettings.newInstance()
      .useOldPlanner()
      .inStreamingMode()
      .build()
    val OldPlannerStreamTableEnv = StreamTableEnvironment.create(env, settingsForOldPlannerStreamEnv)
    //
    //    //2.老版本planner + batch + 基于BatchTableEnvironment
    //    val batchEnv = ExecutionEnvironment.getExecutionEnvironment
    //    val oldPlannerBatchTableEnv = BatchTableEnvironment.create(batchEnv)

    //3.blink planner + stream + 基于StreamTableEnvironment
    //    val settingsForBlinkPlannerStreamEnv = EnvironmentSettings.newInstance()
    //      .useBlinkPlanner()
    //      .inStreamingMode()
    //      .build()
    //    val bsTableENnv = StreamTableEnvironment.create(env, settingsForBlinkPlannerStreamEnv)

    //    //4.blink planner + batch + 基于TableEnvironment
    //    val settingsForBlinkPlannerBatchEnv = EnvironmentSettings.newInstance()
    //      .useBlinkPlanner()
    //      .inBatchMode()
    //      .build()
    //    val bbTableENnv = TableEnvironment.create(settingsForBlinkPlannerBatchEnv)

    /**
     * 二、 创建表
     * 创建表其实在catalog中注册了一个属于某个库中的某个表结构
     */

    val tableEnv = OldPlannerStreamTableEnv

    //1。从文件系统中的文件中注册表
    val filePath = "in/flinkIN/Senesor.txt"
    tableEnv.connect(new FileSystem().path(filePath))
      .withFormat(new OldCsv()) //csv格式,注意flink连接的必须要使用flink-csv的版本
      .withSchema(new Schema() //定义所有的字段的名称
        .field("id", DataTypes.STRING())
        .field("timestamp", DataTypes.STRING())
        .field("temp", DataTypes.STRING())
      ).createTemporaryTable("sensorTableFromFile")

    //从环境中的注册的表中获取Table
    val table1: Table = tableEnv.from("sensorTableFromFile")
    //    table1.toAppendStream[(String, String, String)].print()

//    //2。从kafka去读数据转化成表
//    tableEnv.connect(new Kafka()
//      .version("0.11.1")
//    .topic("test")
//    .property("zookeeper.connect","localhost:2181")
//    .property("bootstrap.server","localhost:9092"))
//      .withFormat(new OldCsv)
//      .withSchema(new Schema() //定义所有的字段的名称
//        .field("id", DataTypes.STRING())
//        .field("timestamp", DataTypes.STRING())
//        .field("temp", DataTypes.STRING())
//      ).createTemporaryTable("sensorTableFromKafka")


    /**
     * 三、转化操作按照 DQL或者SQL风格，分别对应table api 和 flink sql
     * 包括Table 和 DataStream[ case class ]的转换，字段可以单独指出来或者是指定名称
     *
     */

    //1。table api
//    val resTable1 = table1.select("id").filter("temp<100")
    //2. flink sql的实现
    val sql =
      """
        |select id from sensorTableFromFile
        |""".stripMargin
    val resTable2 = tableEnv.sqlQuery(sql)

    // Table 和 DataStream[case class] 的转化
//    tableEnv.fromDataStream()//同时可以指定数据的顺序as
//    tableEnv.from("")//获取table

    resTable2.toAppendStream[(String)].print("SQL out")


    env.execute("table api test")
  }


}
