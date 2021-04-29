package big_data_self_learn.flinkLearn.tableapiAndSql

import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.scala._
import org.apache.flink.table.api.{DataTypes, EnvironmentSettings, Table}
import org.apache.flink.table.descriptors.{FileSystem, OldCsv, Schema}
import org.apache.spark.sql.execution.streaming.FileStreamSource.Timestamp

case class SensorReadingForTableSourceTest(name: String, temperature: Int, eventTime: Timestamp)
/**
 * create by hgw on 2020/12/27 2:33 下午
 *
 */
object tableSourceBasicLearn {

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
    //      ).createTemporaryTable("sensorTableFromKa")

    //从注册的表中的获取到Table,转化成dataStream
    val table = tableEnv.from("sensorTableFromFile")//
//    table.toAppendStream[(String,String,String)].print()//import org.apache.flink.table.api.scala._ 才能转化

    /**
     * 三、对于表的table api或者是sql 的转化操作
     * 包含table api或者是sql的结果转化成 DataStream
     * DataStream转化成Table
     */
    val res1: Table = table.select("id").filter("id =='snensor51'")
//    res1.toAppendStream[(String)].print("table api out")

    val sql_1 =
      """
        |select id from sensorTableFromFile
        |""".stripMargin
    val res2 = tableEnv.sqlQuery(sql_1)
//    res2.toAppendStream[(String)].print("sql out")

    val stream1 = StreamEnv.fromCollection(List(
      SensorReadingForTableExample("1", 23, 1608445793),
      SensorReadingForTableExample("2", 24, 1608445879),
      SensorReadingForTableExample("3", 24, 1608445912),
      SensorReadingForTableExample("3", 22, 1608445600)
    ))

    val DataStream2Table = tableEnv.fromDataStream(stream1)//转化成Table
    tableEnv.createTemporaryView("DataStream",DataStream2Table)//Table注册成临时的view，在这个层面上是可以通过env执行sql的了
    val sql_2 =
      """
        |select name from DataStream where name = '3'
        |""".stripMargin
    val res3 = tableEnv.sqlQuery(sql_2)
    res3.toAppendStream[(String)].print("from dataStream")

    StreamEnv.execute("source test example")

  }
}
