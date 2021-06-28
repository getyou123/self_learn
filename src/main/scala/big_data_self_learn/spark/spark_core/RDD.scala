package big_data_self_learn.spark.spark_core

import org.apache.spark.{SparkConf, SparkContext}

/**
 * @ProjectName: DataBaseExport
 * @Package: com.jindi.spark_learn.spark_core
 * @ClassName: RDD
 * @Author: guowanghao
 * @Description:
 * @Date: 2021/6/25 2:17 下午
 * @Version: 1.0
 */
object RDD {

  // 一般spark程序的工作过程：
  //    //通过外部数据产生RDD，通过filter这样的转化操作产生新的RDD，通过行动操作触发spark优化后的并行计算，通过persist()实现持久化保存中间的需要进行重用的结果。
  //    //RDD操作：行动操作(不产生新的RDD)和转化操作(返回产生的新RDD)，filter这个操作虽然产生了新的RDD但是在对旧的RDD来说，在后面的程序中依然可以使用，
  //    //创建RDD：通过sc.parallelize产生；通过从外部数据读取(sc.textFile等)
  //    //RDD的惰性求值：产生的新的RDD，只有在进行行动操作的时候才会被真正的计算。
  //    //由于转化操作的存在，使得RDD之间存在着类似派生的关系，spark使用的系谱图来记录这些RDD之间的关系。常用来进行恢复数据等操作使用


  def main(args: Array[String]): Unit = {

    // spark Core 中的核心数据抽象 RDD 血缘 分区 task重试

    // 回忆 ： 部署模式，角色任务

    /**
     * spark core 程序入口 sc
     */
    val conf = new SparkConf().setAppName("appName").setMaster("local[1]")
    val sc = new SparkContext(conf)

    /**
     * 创建 RDD
     */
    val RDD1 = sc.parallelize(Array(1, 3, 4), 2) // RDD1.glom().collect()
    val RDD2 = sc.makeRDD(Array("1 2", "3 4", "5 6")) // 底层用的parallelize
    val textFileRDD = sc.textFile("file:///home/guowanghao/1.txt") // 本地或者其他文件系统的，em hdfs://master:port/path

    // 其他数据库 文件系统 各种文件格式的数据 自行百度 HABSE，mysql，HDFS，ES等


    /**
     * trans && act ； 普通RDD & pair RDD
     */

    //非pair RDD的操作：普通简单的一些算子
    RDD1.take(1) //take(10)  获取指定数目的元素rdd;
    RDD1.filter(x => x == 1) //filter(x=>x>10) 针对各个元素过滤掉返回值是false的元素;
    RDD1.map(x => x + 1) //map(x=>(x,1)) 针对各个元素进行操作;
    RDD2.flatMap(x => x.split(" ")) //    flatMap(x=>x.split(" ")) 针对各个元素，单个元素可能返回值不止一个
    RDD1.count() //count() 对元素进行计数；
    RDD1.first() //first() 返回第一个元素；
    RDD1.collect() // collect() 整个的元素返回驱动器, 消耗巨大；
    RDD1.sample(false, 0.5) // 无放回抽样，每个元素被抽到的概率为0.5：fraction=0.5 sample() 对rdd进行取样，结果非确定的;
    RDD1.distinct() //distinct() 使集合中元素唯一;
    RDD1.reduce((x, y) => x + y) // reduce((x,y)=>x+y) 对所有的元素进行归约,比如对RDD中所有元素求和；
    RDD1.aggregate((0, 0))((acc, value) => (acc._1 + value, acc._2 + 1), (acc1, acc2) => (acc1._1 + acc2._1, acc1._2 + acc2._2))
    //分别是初始值(0,0)，(acc,value)本地节点运行是遇到value如何操作，（acc1，acc2）在多个累加器之间合并操作;
    RDD1.foreach(x => println(x + 1)) //    foreach(x=>x+1) 可以在不返回驱动器的情况下操作每一个元素;

    /**
     * 集合操作：
     */

    // union(other) 取两个RDD的并集
    val union_rdd1 = sc.parallelize(List("a", "b", "c"))
    val union_rdd2 = sc.parallelize(List("d", "b", "c"))
    union_rdd1.union(union_rdd2).foreach(println(_))
    // substract(other) 差集，在前者而不在后者的元素
    union_rdd1.subtract(union_rdd2).foreach(println(_))
    // intersection(other)取两个RDD的交集
    union_rdd1.intersection(union_rdd2).foreach(println(_))
    // cartesian(other) 两个RDD 的笛卡尔积
    union_rdd1.cartesian(union_rdd2).foreach(println(_))

    /**
     * RDD的持久化
     */

    //    多次重复计算同一个RDD的消耗可以通过持久化的操作减少多次重复计算，持久化有不同的级别，
    //    用法：rdd.persist(StorageLevel.DISK_ONLY)，持久化是在将数据存在执行器进程的缓存中的，也可以使用unpersist()解除持久化操作

    /**
     * 单个 pair RDD的操作
     */

    //    reduceByKey((x,y)=>x+y) 对相同key的元素操作,分区内可进行操作
    //      groupBykey() 按照key进行分组 不能带函数，仅仅是分区，reduceBykey
    val words = Array("one", "two", "two", "three", "three", "three")
    val wordPairsRDD = sc.parallelize(words).map(word => (word, 1))
    val wordCountsWithReduce = wordPairsRDD.reduceByKey(_ + _)
    val wordCountsWithGroup = wordPairsRDD.groupByKey().map(t => (t._1, t._2.sum))
    wordCountsWithGroup.foreach(print(_))
    wordCountsWithReduce.foreach(println(_))


    // combineByKey() 合并具有相同的key
    val RDD3 = sc.makeRDD(Array(("A", 2), ("A", 1), ("A", 3), ("B", 1), ("B", 2), ("C", 1)))
    val collect: Array[(String, String)] = RDD3.combineByKey(
      (v: Int) => v + "_", // 数据的map
      (c: String, v: Int) => c + "@" + v, //同一分区内数据如何合并
      (c1: String, c2: String) => c1 + "$" + c2 // 分区之间的数据的如何reduce
    ).collect

    for (elem <- collect) {
      println(elem._1, "--", elem._2)
    }
    //(A,--,2_$1_$3_)
    //(B,--,1_$2_)
    //(C,--,1_)

    //  mapValues() 对所有的values进行相同的操作
    val RDD4 = sc.makeRDD(Array(("A", 2), ("A", 1), ("A", 3), ("B", 1), ("B", 2), ("C", 1)))
    RDD4.mapValues(x => x + 1).foreach(println(_))

    //  flatMapValues()
    val RDD5 = sc.makeRDD(Array(("A", "T Y C"), ("A", "J IN D"), ("B", "J H U"), ("B", "H U B "), ("C", "R O U")))
    RDD5.flatMapValues(x => x.split(" ", -1)).foreach(println(_))

    //   keys() 获取所有的keys
    val RDD6 = sc.makeRDD(Array(("A", "T Y C"), ("A", "J IN D"), ("B", "J H U"), ("B", "H U B "), ("C", "R O U")))
    RDD6.keys.foreach(println(_))

    //      values() 获取所有的values
    val RDD7 = sc.makeRDD(Array(("A", "T Y C"), ("A", "J IN D"), ("B", "J H U"), ("B", "H U B "), ("C", "R O U")))
    RDD7.values.foreach(println(_))

    //      sortByKey() 根据key进行排序 更强大的是sortBy
    val RDD8 = sc.makeRDD(Array(("A", "T Y C"), ("A", "J IN D"), ("B", "J H U"), ("B", "H U B "), ("C", "R O U")))
    RDD8.sortBy(_._2).collect().foreach(println(_))
    RDD8.sortBy(_._1).collect().foreach(println(_))
    RDD8.sortByKey().collect().foreach(println(_))

    //      lookup(key) 返回给定的键对相应的所有的值
    val RDD9 = sc.makeRDD(Array(("A", "T Y C"), ("A", "J IN D"), ("B", "J H U"), ("B", "H U B "), ("C", "R O U")))
    RDD9.lookup("A")

    //      collectAsMap() 结果以映射表的形式返回
    val RDD10 = sc.makeRDD(Array(("A", "T Y C"), ("A", "J IN D"), ("B", "J H U"), ("B", "H U B "), ("C", "R O U")))
    RDD10.collectAsMap()

    //      countBykey() 对每个键分别计数
    val RDD11 = sc.makeRDD(Array(("A", "T Y C"), ("A", "J IN D"), ("B", "J H U"), ("B", "H U B "), ("C", "R O U")))
    RDD11.countByKey()

    /**
     * 针对两个pairRDD的转化操作
     */

    val union_rdd1_p = sc.parallelize(List("a", "b", "c")).map(x => (x, 2))
    val union_rdd2_p = sc.parallelize(List("d", "b", "c")).map(x => (x, 1))

    //    subtractBykey() 在key上的差集操作,
    union_rdd1_p.subtractByKey(union_rdd2_p).foreach(println(_))

    //      join() 根据key进行内连接
    union_rdd1_p.join(union_rdd2_p).foreach(println(_))

    //    rightOuterJoin
    //    leftOuterJoin
    union_rdd1_p.leftOuterJoin(union_rdd2_p).foreach(println(_))
    //    cogroup 将相同的key进行分组
    union_rdd1_p.cogroup(union_rdd2_p).foreach(println(_))
    union_rdd1_p.cogroup(union_rdd2_p, union_rdd2_p).foreach(println(_)) // 多个


  }
}
