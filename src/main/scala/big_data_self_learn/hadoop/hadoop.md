# hadoop
### 基础入门
主要有运行模式：
本地（只配置了hadoop_HOME），伪分布式（集群中只有自己一台，算是一个完整的环境了吧），完全分布式（多台机器组成一个集群）
完全分布式的搭建中的一些重要文件：（基础就是ssh和JAVA_HOME）
site.xml四个文件：core（集群连接的8020端口） hdfs（临时文件路径） yarn（Rm地址） mapred
env.sh四个文件：hadoop yarn mapred
slaves文件
格式化namenode和启动hdfs。
### hdfs 
优缺点：廉价机上，不适合实时，小文件等
块大小：128MB默认，太大和太小的情况。
hdfs shell操作
hdfs的api操作，上传下载（api操作不能删除恢复）
写数据的流程画图（注意抛出已经存在异常，切块、机架感知怎么选节点，建立IO流通道，先内存在落盘）
读数据的流程画图（本地client拼接）
NN和2NN:Fsimage和Edits加载到内存 画图
2NN合并两者的触发机制checkpoint点检查（时间间隔或者edits多） 尽量NN和2NN不放在一个节点上
NN故障，从2NN恢复
安全模式 就是加载Fsimage 和 edits的过程，只响应读取
NameNode多目录配置：为了是增加可靠性，多个目录之间是相同的
DN工作机制（心跳时间每3秒 10min+30s认为丢失可以设置，数据完整性crc数据校验等）
DN多目录（多个目录是不一样的）
2.x新特性（回收站 快照 小文件存档 集群之间的拷贝）
服役新节点和退役旧结点
### mr
优缺点：不适合实时，不擅长流计算，不擅长图计算，编程简单，PB级离线数据
核心编程思想：map阶段结束才开始reduce阶段
三大进程：Mrap，maptask，reduceTask
mr编程规范：driver的步骤
需求更高级
序列化不选java的原因（java太重，还有一些别的信息被序列化了header等）紧凑快速
自定义bean作为val:BeanAsVal/readme.txt，实现Writable接口，必须有空参数构造，序列的顺序必须一致（传输过程是队列）
切片源码（FileInputFormat 关键在于切片的大小max(minsize,min(blockSize,Long.MaxValue))） blockSize 128MB集群 32MB本地 129MB切成一片1.1.倍
mrjob提交源码（哪里打断点，1.org.apache.hadoop.mapreduce.Job.waitForCompletion 2.
提交的三部分内容切片信息，jar包，job.xml 3.am的类型）
MapTask源码
ReduceTask源码
切片机制：splitLearn/readme.txt 切片多少决定了MapTask的多少
环形缓冲区的数据的格式（）（）（）（）| （k）（v） 默认的是HashPartitioner，分区多少个是默认使用NumReducer，环形缓冲区内无序，只有在溢写时候对分区内按照key字典序排序
MapTask的工作机制：五个步骤（input或者是read map方法 collect(分区 分区内排序) spill merge（溢写分区内归并））
ReduceTask工作机制：四个步骤（copy merge GroupSort reduce方法 ）
M_R总的图。
NumReducer和分区数的关系（0个没有reduceTask 1个默认或者是全局汇总的要求 和分区数不对齐异常 多余）
按照某准规则对所有的数据进行排序：全排序，Bean作为key,定义排序规则，https://www.jianshu.com/p/f4abe558fa76
某种分区内有序，区内排序：Bean作为key同时定义分区规则（注意设置出来和分区数相同的reducer个数）
分组排序reducer端的排序：定义什么样的数据被分成一组：比如order_sku被拆分了，怎么样求每个订单中的最大的金额。思路：定义key为order+amou，排序为order生amou降序   
        然后分组认为order同的key在一个组内，输出组内的第一个即可。
shuffle工作机制：从map方法之后，reduce方法之前。
MapTask的ReduceTask的并行度设置
Comnioner的使用优点，哪些情况不能使用
自定义outputFormat
ReduceJoin 和 MapJoin：参考MRJoin 包
mr做ETL的一个模板（只有map没有reduce，map中使用一个方法判定是不是合法，不合法的直接return，合法的在write）
关于数据压缩：可以进行压缩的位置，一开始map的输入，map的输出（shuffle大小，适合LZO和Snappy），reducer的输出（为了减少存储占用）。主要的一个是不是支持split（）
优化方法：（1）数据输入端：合并小文件可以使用CombinerFileInputFormat（2）map阶段:环形缓冲区大小和比例，增大触发溢写条件io.sort.mb
 io.spill.percent,同时环形缓冲区的数据也不是溢写一次就合并的，增大次数io.sort.factor，combiner的使用可以考虑，在不影响业务逻辑的情况下
 （3）reducer阶段： 拉取所有同一分区时候的buffer设置大点，减少写磁盘;map reduce共存，reduce提前开始运算;
 （4）IO 考虑压缩
 （5）数据倾斜 自定义分区；抽样一下看看数据分布情况，增加随机数；Map端join；Map端加上combine
 HDFS小文件优化：
    (1)最根本时候采集就合并
    (2)小文件归档hadoop archive -archiveName [] -p [] []:
    hadoop archive -archiveName ar.har -p /hbase /har
    (3)输入格式CombineFileInputFormat
    (4)JVM重用：用JVM重用技术来提高性能，那么可以将mapred.job.reuse.jvm.num.tasks设置成大于1的数。这表示属于同一job的顺序执行的task可以共享一个JVM，也就是说第二轮的map可以重用前一轮的JVM，而不是第一轮结束后关闭JVM，第二轮再启动新的JVM。
TopN: 
    map端每个MapTask维护一个大顶堆，ReduceTask拉取过来可以实现
    Reduce就一个Reducetask实现全排序
    分组的组内的TopN:定义key 的bean的两个排序准则，然后group sort之后取出来前N
### yarn
联系提交流程画图AM RM NM
调度器：
FIFO 只有一个队列，一个时刻只有一个job在跑
Capacity 多个队列每个队列有容量上限，综合哪一个最闲，同一个时刻有队列那么多的任务在跑 Apache默认的是这个
Fair 多个队列，每个中选择缺额最大的，同一个时刻有至少队列那么多的任务在跑 CDH默认是这个版本的
推测执行：前提是最多只有一个备份，任务0.05% 算法：按照当前其他任务的平均进行推算结束时刻如果比现在这个进度更快就开
但是避免数据倾斜，避免数据库连接等。

