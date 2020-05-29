#spark
###一些简单基础
优缺点：快，易用，通用
所有内置的软件栈：（注意底层的部署模式）
提交的命令：
###spark-core
对比MR的优点
RDD特点：基于内存（表现） 弹性（血缘，从新计算分区，checkpoint，自动内存和磁盘切换，Task计算失败重试） 分布式 
RDD创建
两种算子：action（底层出发runjob） transformation
map：
mapPartitions()
flatMap
