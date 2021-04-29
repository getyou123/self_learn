package scala_learn

/**
 * @ProjectName: spark_learn 
 * @Package: scala_learn
 * @ClassName: List
 * @Author: guowanghao
 * @Description: ${description}
 * @Date: 2021/3/18 8:32 下午
 * @Version: 1.0
 */
object ListLearn {
  def main(args: Array[String]): Unit = {

    val l1 = List(1, 2, 3)
    println(l1(1))

    for(i<-l1){
      println(i)
    }

    // 追加
    val l2 = Nil
    val l3=l2:+90
    println(l3)

  }
}
