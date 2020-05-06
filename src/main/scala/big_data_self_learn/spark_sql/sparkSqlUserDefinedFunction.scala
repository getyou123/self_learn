package big_data_self_learn.spark_sql

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}

//包含使用sparkSql 处理数据的一般流程
  //读取数据（多种数据源）
  //转化成df
  //注册成表
  //执行sql语句返回结果


object sparkSqlUserDefinedFunction {

  def main(args: Array[String]): Unit = {

    //获取数据源
    val ss: SparkSession = SparkSession
      .builder()
      .appName("UDF")
      .master("local[2]")
      .getOrCreate()

    val sc = ss.sparkContext
    val empolyeeRdd: RDD[(Int, String, String, Int)] = sc.parallelize(Array((0, "xiaowang", "m", 186), (1, "waikai", "f", 187), (2, "xiaohua", "m", 189)))

    //转化成df：
    import ss.implicits._
    val emDf: DataFrame = empolyeeRdd.map(x=>(x._1,x._2,x._3,x._4)).toDF("id","name","sex","seat_num")

    //注册成表，主要createOrReplaceTempView和createGlobalTempView区别在global的会在下次启动session的时候
    //也是可用的，是全局的
    emDf.createOrReplaceTempView("employee")

    //执行sql语句返回DF数据
    val res1: DataFrame = ss.sql("SELECT * FROM employee")
    res1.show()

    //      +---+--------+---+--------+
    //      | id|    name|sex|seat_num|
    //      +---+--------+---+--------+
    //      |  0|xiaowang|  m|     186|
    //      |  1|  waikai|  f|     187|
    //      |  2| xiaohua|  m|     189|
    //      +---+--------+---+--------+

    //上述的都是基本的过程,下面延时基本的udf的使用方式

    //编写函数
    //注册函数
    //使用函数

    //1.注册实名的函数
    def addNameString(name:String): String ={
      "Name:".concat(name)
    }
    ss.udf.register("addName1",addNameString _)
    ss.sql("SELECT id,addName1(name) AS NAME,sex,(seat_num+1) AS NEW_SEAT FROM employee").show()
    //      +---+-------------+---+--------+
    //      | id|         NAME|sex|NEW_SEAT|
    //      +---+-------------+---+--------+
    //      |  0|Name:xiaowang|  m|     187|
    //      |  1|  Name:waikai|  f|     188|
    //      |  2| Name:xiaohua|  m|     190|
    //      +---+-------------+---+--------+

    //2.注册匿名函数
    ss.udf.register("addName2",(x:String)=>"Name".concat(x))
    ss.sql("SELECT id,addName1(name) AS NAME,sex,(seat_num+1) AS NEW_SEAT FROM employee").show()
    //效果和上述的实名的一样

  }

}
