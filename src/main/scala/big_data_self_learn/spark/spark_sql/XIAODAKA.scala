package big_data_self_learn.spark.spark_sql

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object XIAODAKA {

  case class tb_habit(open_id:String,habit_id:String)extends Serializable
  case class user_habit_relation(habit_id:String,open_id:String) extends Serializable

  def main(args: Array[String]): Unit = {
    val ss: SparkSession = SparkSession
      .builder()
      .appName("XIAODAKA")
      .master("local[*]")
      .getOrCreate()


    import ss.implicits._
    //读取数据
    val habit_tb_DF= ss.sparkContext.textFile("E:\\workuse\\spark_learn\\in\\xiaodaka\\habit_.txt").map(x=>x.split(",",-1))
      .map(x=>tb_habit(x(0),x(1)))
      .toDF()

    val user_habit_relation_DF=ss.sparkContext.textFile("E:\\workuse\\spark_learn\\in\\xiaodaka\\user_habit_relatiion.txt").map(x=>x.split(",",-1))
      .map(x=>user_habit_relation(x(0),x(1)))
      .toDF()

    //注册成表
    habit_tb_DF.createOrReplaceTempView("tb_habit")
    user_habit_relation_DF.createOrReplaceTempView("user_habit_relation")

    //计算

    ss.sql("SELECT * FROM tb_habit").show()
    ss.sql("SELECT user_habit_relation.habit_id,user_habit_relation.open_id " +
      "FROM tb_habit " +
      "left outer join user_habit_relation ON (tb_habit.habit_id=user_habit_relation.habit_id)").show()
    ss.sql("SELECT " +
      "tb_habit.open_id,COUNT(DISTINCT(tb_habit.open_id)) habit_cnt," +
      "COUNT(DISTINCT(CASE WHEN user_habit_relation.open_id IS NOT NULL THEN user_habit_relation.open_id ELSE NULL END)) user_cnt " +
      "FROM tb_habit " +
      "left outer join user_habit_relation ON (tb_habit.habit_id=user_habit_relation.habit_id) " +
      "GROUP BY tb_habit.open_id").show()
//    ss.sql("SELECT " +
//      "* " +
//      "FROM (tb_habit left out join " +
//      "(SELECT habit_id,COUNT(*) habit_cnt FROM user_habit_relation GROUP BY habit_id) AS habit_id_count " +
//      "ON tb_habit.habit_id=habit_id_count.habit_id) GROUP BY tb_habit.open_id").show()







  }



}
