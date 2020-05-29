统计各个手机号的上行流量和下行流量以及总流量之和
数据的格式：
7 	13560436666	120.196.100.99		1116		 954			200
id	手机号码		网络ip			上行流量  下行流量     网络状态码

把指定的数据结构放在val的前提是这个对象实现了Writable接口，注意实现的时候写的顺序和读取的
顺序是一致的
在集群上能运行：
hadoop jar  spark_learn-1.0-SNAPSHOT.jar self_learn.hadoop.mr.javaMR.BeanAsKey.FlowsumDriver /user/guowanghao/data/input /user/guowanghao/data/output/res.txt
