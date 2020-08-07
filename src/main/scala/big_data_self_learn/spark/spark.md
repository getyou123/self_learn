#spark
### 一些简单基础
优缺点：快，易用，通用
所有内置的软件栈：（注意底层的部署模式）
提交的命令：
### spark-core
起点是sparkContext
对比MR的优点
RDD特点：基于内存（表现） 弹性（血缘，从新计算分区，checkpoint，自动内存和磁盘切换，Task计算失败重试） 分布式 
RDD创建
两种算子：action（底层出发runjob） transformation
map：
mapPartitions()
flatMap
### spark-sql
起点是SparkSeesion
spark-sql是专门用于处理结构化的，类似于HIVE处理结构话的数据的形式，
编程的核心抽象是DataFrame
DF = RDD[ ROW ] +Schema
SparkSql负责将sql转化成RDD的操作交给集群，也是支持HQL的
三种数据抽象之间的转化：
RDD是只是数据
DF在RDD基础上增加schema像是二维的表，所以可以写sql
DS在表的基础上增加每个行为一个对象

优点：DF也是懒惰执行的，底层对于RDD的执行进行了优化（谓词下推），并且数据按照二进制存在off-head中摆脱了GC限制
劣势：缺少安全性检查，运行时候报错

主要使用SQL风格的，不使用DSL风格的语法。
在获取DF之后注册成表，通过ss.sql获取结果
转化方式见big_data_self_learn.spark.spark_sql.sparkSqlDataTypeTransform
用户自定义函数：普通的直接注册即可（注意是实名的函数和匿名的不一样的写法big_data_self_learn.spark.spark_sql.sparkSqlUserDefinedFunction），
            UDAF的继承类big_data_self_learn.spark.spark_sql.sparkSqlUDAF

数据的加载和保存的过程：
    支持的数据源很多，通用的是read.format.load 和write.format.save 支持追加覆写等
特殊的hive表：开启hq支持，复制hive-site.xml到sparkconf目录下,实现连接存在的hive数据库，注意增加mysql的jar包
JDBC
客户端spark-sql的使用。
### spark-streaming
起点是SparkStreamingContext
实现原理上就是定时采集并封装成RDD，交给Executor进行执行。
原理和对比：Flink 真实时 spark-streaming是微批次
核心的编程抽象是DStream，里面是一个或者是多个RDD，离散数据流。
所以对于DSTREAM的操作api和RDD的很类似，也是懒惰执行
消费的方式2种：基于receiver的和基于direct的,receiver的是每个executor通过自己的receiver拉取到自己的内存中，然后运算，
direct则是自己维护offset每个executor直接对应一个partition，也不需要WAL，小号的资源更少。
#### 有状态RDD
updateStateByKey算子，本身的流式处理是无状态的，但是这个算子可以维护一个状态RDD，对于每个采集周期内的
#### 创建DTREAM的两种方式的对比
首先是按照简单的wc
#### checkpoint设置
只有用到状态转移操作或者需要无数据丢失恢复错误Driver时候才是需要设置checkpoint。
主要有元数据的checkpoint 和 数据的checkpoint
#### 高级数据源之kafka
比较新的对接kafka版本的官方文档：http://spark.apache.org/docs/latest/streaming-kafka-0-10-integration.html
对接kakfa有两种方式：
基于receiver的和基于direct的两种，receiver是直接包装了kafka的高阶消费者api（自动按时提交还是手动自己维护offset区分高低阶api）
direct的底层自己封装了kafka提供的低阶api，所以即可以提供自己维护有可以提供手动维护的版本。direct又分两种，一种是创建时候传入offset的，一种是不传入的。
程序说明：
08kafka的三个程序对应于kafka0.8版本的，010的对应高版本；每个版本分为高阶api（程序不维护offset，交给各自的zk或者kafka本地），
08的版本加了演示如何设置checkpoint和从checkpoint恢复ssc，08手动提交offset到zk，010手动offset提交kafka本地，010把offset维护在hbase;
#### 重点一job的提交过程

#### spark内核
sparkcontext中的内容：DAGSchduler TaskScheduker SchedulerBackend(YArnSchedulerBackend实例) HeartBeatReceiver
提交的jod的action入口。然后按照这个进行查看，之后按照ctrl+b进行查看。

