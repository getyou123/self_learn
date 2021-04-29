package big_data_self_learn.flinkLearn.tableapiAndSql

import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.scala._
import org.apache.flink.table.api.{DataTypes, EnvironmentSettings, Table}
import org.apache.flink.table.descriptors.{FileSystem, OldCsv, Schema}

/**
 * create by hgw on 2020/12/27 3:16 下午
 *
 */
object tableSInkBasicLearn {

  def main(args: Array[String]): Unit = {
    /**
     * 一、配置tableEnv执行环境
     * 分成老版本和新版本的 planner（旧版本和blink的版本）
     * 分成流式查询和批量查询
     */
    //1.使用老版本planner + stream + 基于StreamTableEnvironment
    val StreamEnv: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    val settingsForOldPlannerStreamEnv = EnvironmentSettings.newInstance()
      .useOldPlanner()
      .inStreamingMode()
      .build()
    val OldPlannerStreamTableEnv = StreamTableEnvironment.create(StreamEnv, settingsForOldPlannerStreamEnv)

    /**
     * 二、从文件中获取数据并注册成表
     *
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

    /**
     * 三、对于表的table api或者是sql 的转化操作
     * 包含table api或者是sql的结果转化成 DataStream
     * DataStream转化成Table
     */
      //简单的转化操作
//    val sql_1 =
//      """
//        |select id,`timestamp` from sensorTableFromFile
//        |""".stripMargin
//    val res2 = tableEnv.sqlQuery(sql_1)
//    val table1 = tableEnv.from("sensorTableFromFile")
//    table1.select("id,temp").toAppendStream[(String,String)].print()


    //集合操作
      val table1 = tableEnv.from("sensorTableFromFile")
    table1.groupBy('id)
      .select('id,'id.count as 'count)
      .toAppendStream[(String,Int)]
      .print("group by")
//这是一个计数的过程，是包含udate的操作的，所以写入的的sink也是需要支持update的操作的文件系统



    /**
     * 四、构建tableSink作为数据输出
     */

//    //1。输出到文件的操作
//    tableEnv.connect(new FileSystem().path("out/sqlout.txt"))
//      .withFormat(new OldCsv)
//      .withSchema(new Schema() //定义所有的字段的名称
//        .field("id", DataTypes.STRING())
//        .field("timestamp", DataTypes.STRING())).createTemporaryTable("outputTable")
//    res2.insertInto("outputTable")

    StreamEnv.execute("table sink test")

  }
}
