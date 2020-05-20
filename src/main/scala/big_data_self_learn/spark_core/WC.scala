package big_data_self_learn.spark_core

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object WC {
  def main(args: Array[String]): Unit = {
    val ss: SparkSession = SparkSession
      .builder()
      .appName("UDF")
      .master("local[2]")
      //      .enableHiveSupport()//使用hql的前提是开始hive支持，同时在spark_home的conf文件夹下可以到hive-site.xml
      .getOrCreate()

    val sc: SparkContext = ss.sparkContext

    val value: RDD[(String, Int)] = sc.textFile(args(0).toString())
      .flatMap(x => x.split(" ", -1))
      .map(x => (x, 1))
      .reduceByKey(_ + _)

    println(value.count())

    ss.close()
  }

}
