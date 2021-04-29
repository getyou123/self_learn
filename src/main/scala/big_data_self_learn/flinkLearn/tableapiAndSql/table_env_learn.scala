package big_data_self_learn.flinkLearn.tableapiAndSql

import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.EnvironmentSettings
import org.apache.flink.table.api.scala.StreamTableEnvironment

/**
 * create by hgw on 2020/12/27 2:28 下午
 * 构建env的方式
 */
object table_env_learn {
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
    //
    //    //2.老版本planner + batch + 基于BatchTableEnvironment
    //    val batchEnv = ExecutionEnvironment.getExecutionEnvironment
    //    val oldPlannerBatchTableEnv = BatchTableEnvironment.create(batchEnv)

    //    //3.blink planner + stream + 基于StreamTableEnvironment
    //    val StreamEnv: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    //    val settingsForBlinkPlannerStreamEnv = EnvironmentSettings.newInstance()
    //      .useBlinkPlanner()
    //      .inStreamingMode()
    //      .build()
    //    val bsTableENnv = StreamTableEnvironment.create(StreamEnv, settingsForBlinkPlannerStreamEnv)

    //    //4.blink planner + batch + 基于TableEnvironment
    //    val settingsForBlinkPlannerBatchEnv = EnvironmentSettings.newInstance()
    //      .useBlinkPlanner()
    //      .inBatchMode()
    //      .build()
    //    val bbTableENnv = TableEnvironment.create(settingsForBlinkPlannerBatchEnv)
  }
}
