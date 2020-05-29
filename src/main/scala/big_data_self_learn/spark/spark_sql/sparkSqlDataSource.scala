package big_data_self_learn.spark.spark_sql

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}


//演示多种sparkSql的数据源，从不同的数据源来获取DF
object sparkSqlDataSource {

  def main(args: Array[String]): Unit = {

    //有多种数据源和数据的格式的可以使用在ss中

    val ss: SparkSession = SparkSession
      .builder()
      .appName("UDF")
      .master("local[2]")
//      .enableHiveSupport()//使用hql的前提是开始hive支持，同时在spark_home的conf文件夹下可以到hive-site.xml
      .getOrCreate()

    //读取正常的text的文件：
    val textRDD: RDD[String] = ss.sparkContext.textFile("data/1.txt")
    import ss.implicits._
    textRDD.map(_.split(" "))
      .filter(_.length==4)
      .map(x=>(x(0),x(1),x(2),x(3)))
      .toDF("id","name","sex","seat_num")
      .show()

    //更加通用的load read方式
    ss.read.format("text").load("data/1.txt").show()

    //读取单行满足json的文件

    ss.read.format("json").load("data/ex.json").show()
    //或者可以进一步简写为：ss.read.json("data/ex.json")


    //读取parquet的文件的时候可以直接使用read.load,因为这个格式是默认的支持的数据格式不用再次指定format

    //连接和使用hive：
    ss.sql("select * from emp").show()


    //连接jdbc的方法：

//    ss.read.jdbc("",)//连接jdbc的四条件url,表名


  }
}
