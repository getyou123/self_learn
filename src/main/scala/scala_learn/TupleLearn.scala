package scala_learn

/**
 * @ProjectName: spark_learn 
 * @Package: scala_learn
 * @ClassName: Tuple
 * @Author: guowanghao
 * @Description: ${description}
 * @Date: 2021/3/18 8:29 下午
 * @Version: 1.0
 */
object TupleLearn {
  def main(args: Array[String]): Unit = {
    val t = (1, 23)
    println(t._1)

    //遍历
    for (it<-t.productIterator){
      println(it)
    }


  }
}
