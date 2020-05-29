spark-sql是专门用于处理结构化的，类似于HIVE处理结构话的数据的形式，
编程的核心抽象是DataFrame
DF = RDD[ROW] +Schema
SparkSql负责将sql转化成RDD的操作交给集群，也是支持HQL的
三种数据抽象之间的转化：
RDD是只是数据
DF在RDD基础上增加schema像是二维的表，所以可以写sql
DS在表的基础上增加每个行为一个对象

优点：DF也是懒惰执行的，底层对于RDD的执行进行了优化（谓词下推），并且数据按照二进制存在off-head中摆脱了GC限制
劣势：缺少安全性检查，运行时候报错