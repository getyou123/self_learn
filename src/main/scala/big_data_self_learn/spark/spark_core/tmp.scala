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
      val log1rdd = sc.textFile("in/1.log").map(x => x.split("\t", -1)).map(x => (x(0),x(1).toInt))

      val log2rdd = sc.textFile("in/2.log")
        .map(x=>(x,1000))

      val log23dd = sc.textFile("in/3.log")
        .map(x=>x.split(" "))
        .map(x=>(x(0),x(1)))

      log2rdd.leftOuterJoin(log1rdd)
        .map(x=>(x._1,x._2._2 match {
          case Some(y)=>y
          case _=>x._2._1
        }))
        .leftOuterJoin(log23dd)
        .map(x=>(x._1,x._2._1,x._2._2 match {
          case Some(y)=>y
          case _=>""
        }))
        .map(x=>x._3+"."+x._1+"\t"+(x._2>1000 match {
          case true =>x._2
          case _=>1000
        }))
        .foreach(println(_))
    }
}
