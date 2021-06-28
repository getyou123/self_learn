package com.jindi.spark_learn.spark_sql

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

object UDF extends UserDefinedAggregateFunction {
  //输入的数据的类型
  override def inputSchema: StructType = StructType(StructField("age", IntegerType) :: Nil) //注意返回的数据的类型是structFiled的集合StructType

  //进行计算需要的维护的聚合用的变量
  override def bufferSchema: StructType = StructType(
    StructField("age_sum", LongType, false) ::
      StructField("count", LongType, false)
      :: Nil)

  //返回的数据的类型
  override def dataType: DataType = LongType

  //相同的数据是否输出相同
  override def deterministic: Boolean = true

  //初始化
  override def initialize(buffer: MutableAggregationBuffer): Unit = {
    buffer(0) = 0L //分别是总和和计数
    buffer(1) = 0L
  }

  //如果进行对于新数据来进行更新
  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    //前面定义的数据的输入是一个域，虽然这里表示的还是用的Row类型
    if (!input.isNullAt(0)) {
      buffer(0) = buffer.getLong(0) + input.getInt(0)
      buffer(1) = buffer.getLong(1) + 1
    }
  }

  //不同的executor之间的合并
  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    buffer1(0) = buffer1.getLong(0) + buffer2.getLong(0)
    buffer1(1) = buffer1.getLong(1) + buffer2.getLong(1)
  }

  //计算返回的数据的值
  override def evaluate(buffer: Row): Any = {
    buffer.getLong(0) / buffer.getLong(0)
  }
}

object sparkSqlUDAF {

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
    val emDf: DataFrame = empolyeeRdd.map(x => (x._1, x._2, x._3, x._4)).toDF("id", "name", "sex", "seat_num")

    //注册成表，主要createOrReplaceTempView和createGlobalTempView区别在global的会在下次启动session的时候
    //也是可用的，是全局的
    emDf.createOrReplaceTempView("employee")

    ss.udf.register("idAge", UDF)
    ss.sql("SELECT idAge(id) AS avg_id FROM employee").show()
  }
}
