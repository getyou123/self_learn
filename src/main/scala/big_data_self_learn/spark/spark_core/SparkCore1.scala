package big_data_self_learn.spark.spark_core

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object SparkCore1 {

  def main(args: Array[String]): Unit = {

    val ss: SparkSession = SparkSession.builder().appName("sparkcore").master("local[1]").getOrCreate()

    val sc = ss.sparkContext

        val RDD1: RDD[String] = sc.parallelize(Array("1 3 ", "2 4 ", "3 4", "4"), 2)

        RDD1.map(x => x + "1")
//            .foreach(println(_))

        RDD1.mapPartitions(it => {
          var list: List[String] = List[String]()
          for (elem <- it) {
            list = list ::: (List(elem))
          }
          list.iterator
        }
        )
//          .foreach(println(_))


    RDD1.glom()
//      .foreach(x=>println(x.length))

    RDD1.flatMap(x=>x.split(" ",-1))
//      .foreach(println(_))


  }
}
