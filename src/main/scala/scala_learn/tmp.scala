package scala_learn

import java.sql.Timestamp
import java.text.SimpleDateFormat

import java_learn.dateLearn.dateUtil.addDays

/**
 * @ProjectName: spark_learn 
 * @Package: scala_learn
 * @ClassName: tmp
 * @Author: guowanghao
 * @Description: ${description}
 * @Date: 2021/3/19 2:35 下午
 * @Version: 1.0
 */

object tmp {
  def main(args: Array[String]): Unit = {
    val pt: String = 20210317.toString
    val df = new SimpleDateFormat("yyyyMMdd")
    val begin = df.parse(pt)
    val end = df.parse(addDays(pt, 1, "yyyyMMdd"))
    val create_time_ : Timestamp = new Timestamp((begin.getTime + Math.random() * (end.getTime - begin.getTime)).toLong) //增量产出的时间
    val update_time_ : Timestamp = create_time_


  }
}
