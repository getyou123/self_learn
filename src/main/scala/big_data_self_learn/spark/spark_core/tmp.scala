package big_data_self_learn.spark.spark_core

import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession

/**
 * create by hgw on 2020/12/14 7:50 下午
 *
 */
object tmp {
    def main(args: Array[String]): Unit = {
      val ss: SparkSession = SparkSession
        .builder()
        .appName("WC")
        .master("local[2]")
        .getOrCreate()

      val sc: SparkContext = ss.sparkContext
      val log1rdd = sc.textFile("in/1.log")
        .map(x => x.split("\t", -1))
        .map(x => (x(0), x(1).toInt))

      val log2rdd = sc.textFile("in/2.log")
        .map(x=>x.trim)
        .map(x =>(x,1000))


      log2rdd.leftOuterJoin(log1rdd)
        .map(x=>(x._1,x._2._2.getOrElse(1000)))
        .map(x=>x._1+"\t"+x._2)
        .collect()
        .foreach(println(_))


    }
}
