package big_data_self_learn.spark.spark_sql

import org.apache.spark.sql.types._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{StructField, StructType}
import org.apache.spark.sql.{Row, SparkSession}

object sparkSqlDataTypeTransform {

  case class empolyee (id:Int,name:String,sex:String,seat_num:Int)//注意case class的定位的位置
  // 注意：case class必须定义在main方法之外；否则会报错

  def main(args: Array[String]): Unit = {
    //程序新的入口
    val ss: SparkSession = SparkSession
      .builder()
      .appName("UDF")
      .master("local[2]")
      .getOrCreate()
    //一般來説程序的主要过程是

    val sc = ss.sparkContext
    val empolyeeRdd: RDD[(Int, String, String, Int)] = sc.parallelize(Array((0, "xiaowang", "m", 186), (1, "waikai", "f", 187), (2, "xiaohua", "m", 189)))


    //rdd to df 1:
    import ss.implicits._ //注意这个是实例对象的,这个是实现隐式转化的前提
    empolyeeRdd.toDF("id", "name", "sex", "seat_num").show() //简单的tuple转化成DF
    //      +---+--------+---+--------+
    //      | id|    name|sex|seat_num|
    //      +---+--------+---+--------+
    //      |  0|xiaowang|  m|     186|
    //      |  1|  waikai|  f|     187|
    //      |  2| xiaohua|  m|     189|
    //      +---+--------+---+--------+

    //rdd to df 2:
    import ss.implicits._ //注意这个是实例对象的,这个是实现隐式转化的前提
    empolyeeRdd.map(x=>empolyee(x._1,x._2,x._3,x._4)).toDF().show()
    //      +---+--------+---+--------+
    //      | id|    name|sex|seat_num|
    //      +---+--------+---+--------+
    //      |  0|xiaowang|  m|     186|
    //      |  1|  waikai|  f|     187|
    //      |  2| xiaohua|  m|     189|
    //      +---+--------+---+--------+

    //rdd to df 3:
    val schema = StructType(
      Array(
        StructField("id", IntegerType,nullable=true),//是否可以设置成空的
        StructField("name", StringType),//注意这里的type是org.apache.spark.sql.types包下面的
        StructField("sex", StringType),
        StructField("seat_num", IntegerType)
      )
    )
    //通过createDf的api来设置
    ss.createDataFrame(empolyeeRdd.map(x=>Row(x._1,x._2,x._3,x._4)),schema).show()
    //      +---+--------+---+--------+
    //      | id|    name|sex|seat_num|
    //      +---+--------+---+--------+
    //      |  0|xiaowang|  m|     186|
    //      |  1|  waikai|  f|     187|
    //      |  2| xiaohua|  m|     189|
    //      +---+--------+---+--------+


    //对于rdd[row]某个域的访问方式如下：
    ss.createDataFrame(empolyeeRdd.map(x=>Row(x._1,x._2,x._3,x._4)),schema)
    .map(x=>x.getAs[String]("sex")).show()
    //    +-----+
    //    |value|
    //    +-----+
    //    |    m|
    //    |    f|
    //    |    m|
    //    +-----+


    //rdd to ds:
    empolyeeRdd.map(x=>empolyee(x._1,x._2,x._3,x._4)).toDS().show()
    //      +---+--------+---+--------+
    //      | id|    name|sex|seat_num|
    //      +---+--------+---+--------+
    //      |  0|xiaowang|  m|     186|
    //      |  1|  waikai|  f|     187|
    //      |  2| xiaohua|  m|     189|
    //      +---+--------+---+--------+
    // 对于ds的访问可以通过属性的方式
    empolyeeRdd.map(x=>empolyee(x._1,x._2,x._3,x._4)).toDS().map(x=>x.sex).show()
    //    +-----+
    //    |value|
    //    +-----+
    //    |    m|
    //    |    f|
    //    |    m|
    //    +-----+

    //df to rdd :
    val Df2rdd1: RDD[Row] = ss.createDataFrame(empolyeeRdd.map(x => Row(x._1, x._2, x._3, x._4)), schema)
      .rdd
    Df2rdd1.foreach(println(_))
    //      [0,xiaowang,m,186]
    //      [1,waikai,f,187]
    //      [2,xiaohua,m,189]

    //ds to rdd:
    val ds2rdd1: RDD[empolyee] = empolyeeRdd.map(x=>empolyee(x._1,x._2,x._3,x._4)).toDS().rdd
    ds2rdd1.foreach(println(_))
    //    empolyee(0,xiaowang,m,186)
    //    empolyee(1,waikai,f,187)
    //    empolyee(2,xiaohua,m,189)

    //df to ds:
    ss.createDataFrame(empolyeeRdd.map(x => Row(x._1, x._2, x._3, x._4)), schema)
      .as[empolyee].show()
    //      +---+--------+---+--------+
    //      | id|    name|sex|seat_num|
    //      +---+--------+---+--------+
    //      |  0|xiaowang|  m|     186|
    //      |  1|  waikai|  f|     187|
    //      |  2| xiaohua|  m|     189|
    //      +---+--------+---+--------+


    //所以综上，对于rdd中的元素是row，可以通过r.getAs[String]("sex")或者r.getAs[String](1)的形式
    //对于rdd中的元素是case class对象的时候直接使用r.sex就可以实现访问

  }
}
