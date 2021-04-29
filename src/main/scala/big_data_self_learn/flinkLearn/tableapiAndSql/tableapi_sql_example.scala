package big_data_self_learn.flinkLearn.tableapiAndSql

import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api._
import org.apache.flink.table.api.scala._
import org.apache.flink.table.descriptors.{FileSystem, OldCsv, Rowtime, Schema}
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
      ).createTemporaryTable("sensorTableFromFile") //注册到catalog中

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

    //3。dataStream转化成Table
    val tableFromStream = tableEnv.fromDataStream(stream1)
    //    tableEnv.fromDataStream(stream1,'name as 'id)//可以对数据alias，规定数据的schema


    /**
     * 三、转化操作按照 DQL或者SQL风格，分别对应table api 和 flink sql
     * 包括Table 和 DataStream[ case class ]的转换，字段可以单独指出来或者是指定名称
     * 一般来说查询的反馈的信息也是一个table对象
     *
     */

    //1。table api
    //    val resTable1 = table1.select("id").filter("temp<100")
    //2. flink sql的实现
    val sql =
    """
      |select id,temp from sensorTableFromFile
      |""".stripMargin
    val resTable2 = tableEnv.sqlQuery(sql)

    /**
     * dataStream和table的转换
     * 临时表的创建
     */

    // Table 和 DataStream[case class] 的转化
    //    tableEnv.fromDataStream()//同时可以指定数据的顺序as，如上面所示,从Stream中获取Table转成Table
    //    tableEnv.from("")//从catalog中获取table
    //    tableEnv.createTemporaryView("tempViewFromTable",resTable2)//基于Table创建temporaryView注册到catalog中的
    //    tableEnv.createTemporaryView("tempViewFromStream",stream1,'name as 'id)//基于Stream创建temporaryView注册到catalog中的
    resTable2.toAppendStream[(String)].print("SQL out")

    /**
     * 四、输出表
     * 输出表其实是TableSink的实现
     * 也是和source一样在环境中加入这个TableSink
     * 然后通过某一个更新模式（APPEND，UPSERT，RETRACT）中的一种实现数据传输到外部系统
     * 这里可以把这个注册到环境中的表当作是一个外部数据系统的抽象
     * 实际的外部系统要和上面的更新模式相对应才能得到相应的数据操作的支持
     */

    //    //1。输出到文件
    //    tableEnv.connect(
    //      new FileSystem().path("out/table_out.txt")
    //    )
    //      .withFormat(new OldCsv())
    //      .withSchema(new Schema()
    //        .field("id", DataTypes.STRING())
    //        .field("temp", DataTypes.STRING())
    //      )
    //      .createTemporaryTable("outputTableOfFile") //注册到环境中的数据输出表
    //    resTable2.insertInto("outputTableOfFile") //这里是按照insert的更新模式进行的，文件这种是仅仅只支持追加模式的
    //
    //    //2。输出到Kafka
    //    tableEnv.connect(
    //      new Kafka()
    //        .version("0.11")
    //        .topic("test")
    //        .property("zookeeper.connect","localhost:2181")
    //        .property("bootstrap.servers","localhost:9092")
    //    ).withFormat(new OldCsv())
    //      .withSchema(new Schema()
    //      .field("id",DataTypes.STRING())
    //      .field("temp",DataTypes.STRING())
    //      )
    //      .createTemporaryTable("kafkaOutPutTable")//注册到环境中的数据输出表
    //    resTable2.insertInto("kafkaOutPutTable")//追加的方式输出到kafka中

    //3。数据输出到mysql，通过环境中的DDL语句创建环境中的，反正都是在环境中创建表
    val DDLsql_for_mysql_table_sink = {
      """
        |create table jdbcOutPutTable (
        | id varchar(255) not null,
        | temp varchar(255)
        | ) with (
        |  'connector.type' = 'jdbc',
        |  'connector.url' = 'jdbc://mysql://localhost:3306/test',
        |  'connector.table' = 'test',
        |  'connector.driver' = 'com.mysql.jdbc.Driver',
        |  'connector.username' = 'user',
        |  'connector.password' = '123456'
        |""".stripMargin

//      tableEnv.sqlUpdate(DDLsql_for_mysql_table_sink) //执行sql实现创建表
      resTable2.insertInto("jdbcOutPutTable") //
    }

    /**
     * 补充：flink sql 和table api下的时间语义赋值
     * 1。定义数据处理时间
     * 2。定义事件时间
     * 窗口
     */
    //定义处理时间1：从dataStream构造table的时候追加上processing time
    tableEnv.fromDataStream(stream1, 'name as 'id, 'pt.proctime) //只能在末尾schema中进行定义作为处理时间的定义
    //定义处理时间二：通过table schema的时候进行操作补充， * 前提是这个数据源头是实现了 DefinedProctimeAttribute 或者是 DefinedRowtimeAttribute，否则还是转化成流通过第一种方式来定义时间语义吧(转化成流之后再定义)
    tableEnv.connect(
      new FileSystem().path("in.txt")
    ).withFormat(new OldCsv())
      .withSchema(new Schema()
        .field("id", DataTypes.STRING())
        .field("temp", DataTypes.STRING())
        .field("pt", DataTypes.TIMESTAMP(3))//也是在schema中定义了一个不存在的列作为了时间属性，指定为处理时间，字段名称可以自己定义，精度为ms
        .proctime()
      ).createTemporaryTable("SchemaWithPtEnd")
    //定义处理时间三：通过DDL进行定义处理时间
    val DDLsql_for_pt =
      """
        |create table DDLCreateWithPtTable (
        | id varchar(255) not null,
        | ts bigint,
        | ps As PROCTIME()
        | ) with (
        | 'connector.type' = 'filesystem',
        | 'connector.path' = 'in.txt',
        | 'format.type' = 'csv'
        | )
        |""".stripMargin

    tableEnv.sqlUpdate(DDLsql_for_pt)

    //定义事件时间：
    //定义事件时间1：从dataStream构造table的时候追加上或者直接指定为处理时间
    tableEnv.fromDataStream(stream1, 'name as 'id, 'stamp.rowtime) //把某个字段指定为事件时间，前提是env中已经读取流的已经设置好了使用事件时间语义和watermark
    //定义事件时间二：通过table schema的时候进行操作补充， * 前提是这个数据源头是实现了 DefinedProctimeAttribute 或者是 DefinedRowtimeAttribute，否则还是转化成流通过第一种方式来定义时间语义吧(转化成流之后再定义)
    tableEnv.connect(
      new FileSystem().path("in.txt")
    ).withFormat(new OldCsv())
      .withSchema(new Schema()
        .field("id", DataTypes.STRING())
        .field("temp", DataTypes.STRING())
        .field("pt", DataTypes.TIMESTAMP(3))//也是在schema中定义了一个不存在的列作为了时间属性，指定为处理时间，字段名称可以自己定义，精度为ms
        .rowtime(
          new Rowtime()
            .timestampsFromField("TimeStamp")// 指定字段为事件时间
            .watermarksPeriodicBounded(1000)//周期性质的1000ms
        )
      ).createTemporaryTable("SchemaWithRtEnd")
    //定义事件时间三：通过DDL进行定义处理时间
    val DDLsql_for_rt =
      """
        |create table DDLCreateWithPtTable (
        | id varchar(255) not null,
        | ts bigint,
        | rt As TO_TIMESTAMP( FROM_UNIXTIME(ts) ),
        | watermark for rt as rt - interval '1' second
        | ) with (
        | 'connector.type' = 'filesystem',
        | 'connector.path' = 'in.txt',
        | 'format.type' = 'csv'
        | )
        |""".stripMargin

    tableEnv.sqlUpdate(DDLsql_for_rt)


      env.execute("table api test")
  }


}
