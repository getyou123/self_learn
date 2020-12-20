package big_data_self_learn.flinkLearn
import org.apache.flink.api.scala.ExecutionEnvironment
import  org.apache.flink.api.scala._


/**
 * 批处理的wc 最终没有环境的execute，最重要的数据的类型1 dataset
 */
object flink_wc_batch {
  def main(args: Array[String]): Unit = {

    val inputPath = "data/1.txt"
    val env = ExecutionEnvironment.getExecutionEnvironment  //创建执行的环境env，上下文


    val text = env.readTextFile(inputPath)
    val counts = text.flatMap(_.split("\\W+"))
      .filter(_.nonEmpty)
      .map((_,1))
      .groupBy(0)
      .sum(1)

    counts.print()
  }
}




