package com.tyc.mongoOplogMerge

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import com.alibaba.fastjson.JSONObject
import org.apache.spark.sql._
import org.apache.spark.sql.catalyst.encoders.RowEncoder
import org.apache.spark.sql.types._
import org.bson.BasicBSONDecoder

import scala.collection.mutable.ListBuffer

/**
 * @ProjectName: offline_warehouse
 * @Package: com.tyc.mongoOplogMerge
 * @ClassName: MongoIncMergeMain
 * @Author: haoguowang
 * @Description: 完成mongodb的增量和合并到全量生成新的全量的main工作
 * @Date: 2021/3/8 1:53 下午
 * @Version: 1.0
 */
object MongoIncMergeMain {

  /**
   * 获取某一天的变化的天数的日期
   *
   * @param dateStr 移动日期的起点
   * @param dayStep 移动几天 exam: -1 表示前一天 20200509 -1 输出为20200508
   * @param formatType
   * @return 指定日期类型的移动后的日期
   */
  def addDays(dateStr: String, dayStep: Int, formatType: String): String = {
    val stringDateFormat = new SimpleDateFormat(formatType)
    val calendar = Calendar.getInstance
    var date: Date = null
    try date = stringDateFormat.parse(dateStr)
    catch {
      case e: Exception =>
        e.printStackTrace()
    }
    calendar.setTime(date)
    calendar.add(Calendar.DATE, dayStep)
    date = calendar.getTime
    stringDateFormat.format(date)
  }

  /**
   * 增加小时的操作
   *
   * @param i
   * @param hh
   * @param pt
   */
  def addHour(hh: String, pt: String, i: Int): Tuple2[String, String] = {
    //构造新产出的分区标识
    val dateStr = pt.concat(hh)
    val df = new SimpleDateFormat("yyyyMMddHH")
    val calendar: Calendar = Calendar.getInstance()
    calendar.setTime(df.parse(dateStr))
    calendar.add(Calendar.HOUR_OF_DAY, i)
    val newPt = df.format(calendar.getTime).substring(0, 8)
    val newHh = df.format(calendar.getTime).substring(8)
    (newPt, newHh)
  }

  /**
   * 实现获取Json中的指定的Type的数据
   *
   * @param obj
   * @param schemaField
   * @return
   */
  def getFieldValue(obj: JSONObject, schemaField: Tuple2[String, DataType]): Any = {
    val field = "Value"
    schemaField._2.toString match {
      case "ByteType" => obj.getByteValue(field)
      case "ShortType" => obj.getShortValue(field)
      case "IntegerType" => obj.getIntValue(field)
      case "LongType" => obj.getLongValue(field)
      case "FloatType" => obj.getFloatValue(field)
      case "DoubleType" => obj.getDoubleValue(field)
      case "DecimalType" => obj.getBigDecimal(field)
      case "StringType" => obj.getString(field)
      case "DateType" => obj.getDate(field)
      case "TimestampType" => obj.getTimestamp(field)
      case "BooleanType" => obj.getBooleanValue(field)
      case "ArrayType" => obj.getJSONArray(field)
      //      case _ => obj.get(field)
    }
  }


  /**
   * 实现增量数据的转化
   *
   * @param ss
   * @param tableName
   * @param hh
   * @param pt
   */
  def incDataTrans(ss: SparkSession, incData: DataFrame, tableName: String, hh: String, pt: String) = {
    //按照小时进行更新增量数据表名称构造 这里考虑更通用化的构造方式可以
    val odsTableName = "ods_mg_certificate_online_".concat(tableName).concat("_hi")
    val (nPt, nHh) = addHour(hh, pt, -1)
    //读取ods层增量的表的结构
    val incDataRow1 = ss.sql(s"select * From ${odsTableName} where pt='${nPt}' and hh='${nHh}' limit 1")
    val encode: StructType = incDataRow1.drop("pt").drop("hh").schema
    val schema = encode.map(x => (x.name, x.dataType))
    val encoder = RowEncoder(encode)

    //解析origindb的层增量的数据格式
    //    val incData = ss.sql(s"select * From ${originDbTableName} where pt='${pt}' and hh='${hh}'")

    incData.rdd.map(x=>x.length).foreach(println(_))
    incData
//      .filter(x => {
////      val decoder: BasicBSONDecoder = new BasicBSONDecoder()
////      val valueDetailStr = x.getAs[String]("value_detail")
////      val valueDetailObj = decoder.readObject(valueDetailStr.getBytes)
////      val ns = valueDetailObj.get("ns").asInstanceOf[java.lang.String]
////      ns.split("\\.", -1)(1).equals(tableName)
//    })
      .map(x => {
      val decoder: BasicBSONDecoder = new BasicBSONDecoder()
      val valueDetailStr = x.getAs[Array[Byte]]("value_detail")
      val valueDetailObj = decoder.readObject(valueDetailStr)

      val o = valueDetailObj.get("o").asInstanceOf[org.bson.BasicBSONObject] // 行内容
      val op = valueDetailObj.get("op").asInstanceOf[java.lang.String] // 操作
      val ts = valueDetailObj.get("ts").asInstanceOf[org.bson.types.BSONTimestamp] // ts时间戳

        println(ts.toString)
      val nListBuffer = new ListBuffer[Any]
      for (i <- 0 until schema.length) {
        val field = schema(i)
        field._1 match {
          case "m_id" => {
            var flag = 0
            val it = o.toMap.entrySet().iterator
            while (it.hasNext && flag == 0) {
              val entry = it.next()
              if (entry.getKey.toString.equals("_id")) {
                nListBuffer.append(entry.getValue.toString)
              }
            }
            if (flag == 0) nListBuffer.append(null)
          }
          case "dw_is_del" => {
            op match {
              case "d" => {
                nListBuffer.append("1")
              }
              case _ => {
                nListBuffer.append("0")
              }
            }
          }
          case "dw_update_time"
          => {
            nListBuffer.append(ts.toString)
          }
          case "dw_opt_typ"
          => {
            op match {
              case "d" => {
                nListBuffer.append("DELETE")
              }
              case "i" => {
                nListBuffer.append("INSERT")
              }
              case "u" => {
                nListBuffer.append("UPDATE")
              }
            }
          }
          case _ => {
            var flag = 0
            val it = o.toMap.entrySet().iterator
            while (it.hasNext && flag == 0) {
              val entry = it.next()
              if (entry.getKey.toString.equals("_id")) {
                nListBuffer.append(entry.getValue.toString)
              }
            }
            if (flag == 0) nListBuffer.append(null)
          }
        }
      }
      Row.fromSeq(nListBuffer)
    }
    )(encoder).createOrReplaceTempView(odsTableName.concat("_tmp"))


    //解析出来的表插入到指定的分区
    val columns = schema.map(x => x._1).toArray.mkString(",")

    val result = ss.sql(
      s"""
         |select
         | ${columns},
         | ${pt},
         | ${hh}
         | from ${odsTableName.concat("_tmp")}
         |""".stripMargin)

    result.show()
    result.printSchema()

    ss.sql(
      s"""
         |select human,project from ${odsTableName.concat("_tmp")}
         |""".stripMargin).show


    //    ss.sql(
    //      s"""
    //         |insert overwrite table ${odsTableName}
    //         |select
    //         |${columns},
    //         | ${pt},
    //         | ${hh}
    //         | from ${odsTableName.concat("tmp")}
    //         |""".stripMargin)
  }

  def incDataMerge(ss: SparkSession, tableName: String, date: String, pt: String) = {


    //构造输入输出的数据表
    val odsTableName = "ods_mg_certificate_online_".concat(tableName).concat("da")
    val originDbTableName = "origindb_mg_certificate_online_".concat(tableName).concat("_hi")

    val odsTableDf = ss.sql(s"select * from ${
      odsTableName
    } where pt ='${
      pt
    }'")
    val originDbTableDF = ss.sql(s"select * from ${
      originDbTableName
    } where pt ='${
      pt
    }'")

    //

  }

  def main(args: Array[String]): Unit = {
    require(args.length == 5, "please input right args with: fullSnapshot1 incData(json formatted) fullSnapshot2")
    //    val spark = SparkSession
    //      .builder()
    //      .appName("MongoIncMergeMain")
    //      .config("spark.sql.broadcastTimeout", 20 * 60)
    //      .config("spark.sql.crossJoin.enabled", true)
    //      .config("odps.exec.dynamic.partition.mode", "nonstrict")
    //      .config("spark.sql.catalogImplementation", "odps")
    //      .getOrCreate()




    val tableName = args(0) // 需要进行更新的数据流的表 w_human
    val incDataTableName = args(1) //总的增量的表名称
    val mergeFlag = args(2).toInt //  0-> 增量merge 1-> 隔天的全量merge
    val pt = args(3) //业务日期时间 20210310
    val hh = args(4) //业务小时 09

    //    //读取需要解析的增量表
    //    val incDf = spark.sql(s"select * from ${incDataTableName} where pt=${pt} and hh=${hh}")

    val spark = SparkSession
      .builder()
      .appName("MongoIncMergeMain")
      .master("local[1]")
      .getOrCreate()
    //    val incDf = spark.sparkContext.textFile("in/3.log")
    //      .map(_.split("\t", -1))
    //      .map(x => (x(0).substring(1, x(0).length - 1), x(1), x(2), x(3), x(4), x(5)))
    //      .toDF("value_detail", "partition_detail", "offset_detail", "timestamp_detail", "pt", "hh"
    //      )


    import spark.implicits._
    val incDf = spark.sparkContext.textFile("in/3.log")
      //      .map(_.split("\t", -1))
      .toDF("value_detail")


    incDf.show()
    incDf.printSchema()
    println(incDf.count())

    val ods_hi = spark.sparkContext.textFile("in/3.log")
      .map(_.split("\t", -1))
      .map(x =>
        (x(0), x(1), x(2), x(3), x(4), x(5), x(6), x(7), x(8), x(7), x(10), x(11), x(12), x(13),
          x(14), x(15).toString, x(16).toString))
      .toDF("m_id",
        "credit_code",
        "company_name",
        "company_id",
        "uuid", "quzzzg_id",
        "human",
        "project",
        "updatetime",
        "crawledtime",
        "isdelete",
        "source",
        "dw_is_del",
        "dw_update_time",
        "dw_opt_type",
        "pt",
        "hh"
      )
    ods_hi.createOrReplaceTempView("ods_mg_certificate_online_w_company_hi")
    //进行匹配的操作
    mergeFlag match {
      case 0 => {
        incDataTrans(spark, incDf, tableName, hh, pt)
      }
      case 1 => {
      }
    }

    spark.close()
  }
}