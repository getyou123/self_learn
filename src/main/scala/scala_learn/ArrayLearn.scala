package scala_learn

/**
 * @ProjectName: spark_learn 
 * @Package: scala_learn
 * @ClassName: ArrayLearn
 * @Author: guowanghao
 * @Description: ${description}
 * @Date: 2021/3/18 7:34 下午
 * @Version: 1.0
 */

import scala.collection.mutable.ArrayBuffer
object ArrayLearn {
  def main(args: Array[String]): Unit = {

    //定长
    val Arr0 = Array(1, 2, 3)
    Arr0.foreach(println(_))

    val Arr1= new Array[String](4)
    Arr1(1)="dfsad"
    Arr1.foreach(println(_))

    //变长
    val Arr2 = new ArrayBuffer[Int]()
    Arr2.append(3)
    Arr2.remove(0)
    Arr2(0)=9
    Arr2.foreach(println(_))

    //二维
    val Arr3 = Array.ofDim[Int](3, 6)
    for(it<-Arr3){
      for(ite<-it)
        println(ite)
    }

    // 类型转Java List  import scala.collection.JavaConversions.bufferAsJavaList



  }
}
