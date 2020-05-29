package big_data_self_learn.spark.spark_sql

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object wang {

  def main(args: Array[String]): Unit = {

    //有多种数据源和数据的格式的可以使用在ss中

    val ss: SparkSession = SparkSession
      .builder()
      .appName("UDF")
      .master("local[*]")
      .getOrCreate()

    val RDD1: RDD[String] = ss.sparkContext.textFile("data/2020.5.5学员信息表.CSV")
    val RDD2: RDD[String] = ss.sparkContext.textFile("data/望京1.CSV")

    println(RDD2.count())

    val RDD12 = RDD1.map(_.split(",", -1))
      .filter(x => x.length == 7)
      .map(x => (x(0), (x(0), x(1), x(2), x(3), x(4), x(5), x(6))))

    println(RDD12.first())

    val RDD22 = RDD2.map(_.split(",", -1))
      .filter(x => x.length == 9)
      .filter(x => x(1).length > 0)
      .map(x => (x(1), (x(0), x(1), x(2), x(3), x(4), x(5), x(6), x(7)))) //name

    println(RDD22.first())

    val ResRDD = RDD22.leftOuterJoin(RDD12)
      .map(x => (x._2._1, x._2._2 match {
        case Some(info) => (info._4, info._6, info._7)
        case _ => ("", "", "")
      }))
      .map(x => Array(x._1._1, x._1._2, x._1._3, x._1._4, x._1._5, x._1._6, x._1._7, x._1._8,
        if (x._2._1.toString.split("/", -1).length > 2) x._2._1.split("/", -1)(1) else x._2._1,
        x._2._2,
        x._2._3))
      .map(x => x.mkString(","))
      .foreach(println(_))
    //        .saveAsTextFile("data/out.csv")
    //    println(ResRDD)
  }
}
