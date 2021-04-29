package big_data_self_learn.spark.spark_sql

/**
 * @ProjectName: spark_learn 
 * @Package: big_data_self_learn.spark.spark_sql
 * @ClassName: Bson
 * @Author: guowanghao
 * @Description: ${description}
 * @Date: 2021/3/12 10:35 上午
 * @Version: 1.0
 */

import java.io.{BufferedInputStream, File, FileInputStream}
import java.text.SimpleDateFormat
import java.util.Date

import com.alibaba.fastjson.serializer.SerializerFeature
import com.alibaba.fastjson.{JSON, JSONObject}
import org.bson.types.{BSONTimestamp, BasicBSONList}
import org.bson.{BasicBSONDecoder, BasicBSONObject}



object BsonTest {

  def main(args: Array[String]): Unit = {

    "2021-03-12 11:04:41" > "2021-03-12 00:00:00" match {
      case true=> println("da")
      case _=> println("xiao")
    }

    val decoder: BasicBSONDecoder = new BasicBSONDecoder()
    try {
      val FileInputStream = new FileInputStream(new File("in/3.log"))
      val inputStream = new BufferedInputStream(FileInputStream)


      while (inputStream.available() > 0) {
        val obj = decoder.readObject(inputStream)
        val Bsobej = new BasicBSONObject(obj.toMap)

        println("1.############")
        println(Bsobej.toString)

        println("2.############")
        println(Bsobej.get("ns"))
        val ns = Bsobej.get("ns")
        val aftObejns = ns.asInstanceOf[java.lang.String]
        println(aftObejns)


        println("3.############")
        println(Bsobej.get("ts"))
        val ts = Bsobej.get("ts")
        val aftObej: BSONTimestamp = ts.asInstanceOf[org.bson.types.BSONTimestamp]
        println(aftObej.getTime)
        val tsTmp=aftObej.getTime
        val tsDate=new Date(tsTmp*1000L)
        val df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        println(df.format(tsDate))

        println("4.############")
        println(Bsobej.get("op").getClass)
        val op = Bsobej.get("op")
        val aftObejop = op.asInstanceOf[java.lang.String]
        println(aftObejop)

        println("5.############")
//        println(Bsobej.get("o").getClass)
        val o = Bsobej.get("o").asInstanceOf[org.bson.BasicBSONObject]
        val map=o.toMap.asInstanceOf[java.util.LinkedHashMap[String,Object]]
//        println(o.getClass)
//        val aftObejo = o.asInstanceOf[org.bson.BasicBSONObject]
//        val it = aftObejo.toMap.entrySet().iterator
//        while (it.hasNext) {
//          val entry = it.next()
//          println(entry.getKey)
//          println(entry.getValue)
//          JSONObject.toJSONString(map)


        val nObject = new JSONObject(map)

        println(nObject.get("reg_info"))
        println(nObject.get("card_num"))
        println(nObject.get("_id").getClass)

        if(nObject.get("card_num").isInstanceOf[java.lang.String]){
          println(1)
        }


        println(JSON.toJSONString(nObject.get("reg_info"), SerializerFeature.WriteMapNullValue))
        println(JSON.toJSONString(nObject.get("card_num"), SerializerFeature.WriteMapNullValue))
        println(JSON.toJSONString(nObject.get("_id"), SerializerFeature.WriteMapNullValue))
        println(nObject.get("_id").toString)


      }
    }
  }
}
