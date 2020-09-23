package big_data_self_learn.spark.spark_sql

import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.hadoop.hbase.client.{Connection, ConnectionFactory, Result, ResultScanner, Scan, Table}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.hadoop.hbase.mapreduce.TableInputFormat

object sparksql_hbase {

  def main(args: Array[String]): Unit = {
    val ss: SparkSession = SparkSession
      .builder()
      .appName("habse_saperk_sql")
      .master("local[2]")
      //      .enableHiveSupport()//使用hql的前提是开始hive支持，同时在spark_home的conf文件夹下可以到hive-site.xml
      .getOrCreate()

    val hbaseConf = HBaseConfiguration.create()
    val tablename="Student"
    hbaseConf.set("hbase.zookeeper.quorum","localhost")  //设置zooKeeper集群地址，也可以通过将hbase-site.xml导入classpath，但是建议在程序里这样设置
    hbaseConf.set("hbase.zookeeper.property.clientPort", "2181")       //设置zookeeper连接端口，默认2181
    hbaseConf.set(TableInputFormat.INPUT_TABLE, tablename)

    val sc=ss.sparkContext

    //先读取为RDD在转化成df
    val hbaseRDD: RDD[(ImmutableBytesWritable, Result)] = sc.newAPIHadoopRDD(hbaseConf,
      classOf[TableInputFormat],
      classOf[ImmutableBytesWritable],
      classOf[Result])

    hbaseRDD.foreach { case (_, result) =>
      //获取行键
      val key = Bytes.toString(result.getRow)
      //通过列族和列名获取列
      val name = Bytes.toString(result.getValue("StuInfo".getBytes, "Age".getBytes))
      val age = Bytes.toString(result.getValue("StuInfo".getBytes, "Name".getBytes))

      println("Row key:" + key + "\tStuInfo.Age:" + name + "\tStuInfo.Name:" + age)
    }
    val rowRDD = hbaseRDD.map(x => {
      (
        Bytes.toString(x._2.getRow),
        Bytes.toString(x._2.getValue("StuInfo".getBytes, "Name".getBytes)),
        Bytes.toString(x._2.getValue("StuInfo".getBytes, "Sex".getBytes)),
        Bytes.toString(x._2.getValue("StuInfo".getBytes, "Age".getBytes))
      )
    })

    import ss.implicits._
    val df = rowRDD.toDF("key", "NAME", "SEX", "AGE")

    df.createOrReplaceTempView("waybillInfo")//注册表
    val sqldf = ss.sql("select * from waybillInfo")
      sqldf.show()
    }


}
