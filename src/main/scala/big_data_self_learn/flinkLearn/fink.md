

##### datastream中的api
有多种数据源，展示的是两种自定义的source，分别为是否是执行并行度的区别，两种分别实现不同的借口即可

实例代码中演示的都是java版本，如果是使用的scala开发的话，注意引入的包名是
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.api.scala._

相应的依赖如pom中的scala所示。

### 获取各种env
//设定Flink运行环境，如果在本地启动则创建本地环境，如果是在集群上启动，则创建集群环境 
StreamExecutionEnvironment.getExecutionEnvironment 
//指定并行度创建本地执行环境 
StreamExecutionEnvironment.createLocalEnvironment(5) 
//指定远程JobManagerIP和RPC端口以及运行程序所在jar包及其依赖包 
StreamExecutionEnvironment.createRemoteEnvironment("JobManagerHost",6021,5, "/user/application.jar")

### 需要引入的包的差异
StreamExecutionEnvironment 
Scala开发接口在org.apache.flink.streaming.api.scala包中，
Java开发接口在org.apache.flink.streaming.api. java包中；
ExecutionEnvironment 
Scala接口在org.apache.flink.api.scala包中，
Java开发接口则在org.apache.flink.api.java包中。

### 顺序说明
flink_wc 入门基础

DataStreamApi部分：source，trans，sink
source：readTextFile()，socketTextStream(),fromCollection(),自定义（kafka属于这种）
trans：map，filter等
sink：wirteAsText,print(),自定义（包含一些内置的connector）

MyNoParalleSource 最大并行为1
MyParalleSource   多个并行度的
MyRichparalleSource 带资源开关的
MyPartiton+StreamingDeoWithMyPartitions  自定义分区和Demo

###几个基础的概念
job manager（JVM 进程） 是集群的master，类似yarn的Nodemanager
task manger （JVM进程） 是集群的slave，类似NodeManager
task slot 是资源的抽象cpu+disk=I/o，类似hadoop的container，是在task manger中被划分出来的，在总的监控页面有显示总的task slots
task 是任务的抽象，按照设置的并行度的进行数据的
这是在flink的standaolne的模式下的几个概念，其他的模式下是按照这个进行的迁移

### 关于并行度的设置
设置并行度的优先级，最高是代码中写明的，然后是提交时候的设置的，最下是集群的默认的和申请到的核心的数据

### flink运行时架构
运行时组件：
主要有四个大块：
jobmanager：主要的控制逻辑
taskmanager：真正的干活的，里面有各种的solt
dispacher：进行通信和UI展示
resourcemanager：资源管理器

slot是类似的内存和计算的集合：每个taskmannager的solt*taskmanager的数目=总的solt的数目

### flink on yarn
前提是（需要在FLINK_HOME/lib下有hadoop的支持的jar包--flink-shaded-hadoop2-uber-1.7.2.jar,这个是flink1.8版本之前的官网提供的下载，无论是session模式还是job模式）
两种模式：
1.session模式：预先起来一个flink集群即保持一个session，且长期存在的，等job来；启动seeesin之后，提交任务的命令和flink的standalone
2.job模式：job提交之后才产生一个可以运行flink的运环境，提交时候加入参数-m yarn-cluster

### 提交的方式和取消的方式
flink standalone模式提交可以使用web提交，8081端口，也可以使用的命令行的形式进行数据的提交
flink standalone模式停止：flink run -p 1 -c org.jindi.App ./newDataBaseMinc-1.0-SNAPSHOT.jar

flink on yarn 模式：flink run -m yarn-cluster  -d  -c org.jindi.App ./newDataBaseMinc-1.0-SNAPSHOT.jar
flink cancel -m 127.0.0.1:8081 357591171dfcca2eea09de 按照cancel方式进行停止，这个jobid是提交时候获取到的

### 运行时架构
#### 运行时组件
Job Manager：控制每一个应用的主进程，checkpoint，管理调度；接受提交上来的job
分析处理流程，结构JobGraph生成执行图，分成各种task交给各个TaskManager，同时会和Resource  Manager进行数据的交互
TaskManager：把自己的资源分成slot，给ResourceManager进行报告，每个slot上执行task
ResourceManager：slot资源管理，可以接入更多资源对接yarn等等，当然yarn中的也是包含resource manager
Dispatcher：分发器

#### on yarn 的job的大体提交过程
1.app 提交给dispatcher，jar包提交到hdfs上（client本身就进行了graph的优化）
2.dispatcher 提交给job manager（这个是提交的才启动起来）
3.job manager 切分为执行图，知道多少slot，向resource manager申请资源
4.resource manager 去Task manager 划分 工作的slots，包含反向注册给job manager
5.job manager分配task到slots的，执行和监控整个job的执行任务。

#### flink任务调度

#### flink函数都提供一个rich版本，在这个函数中增加了open close等
这个函数是实现获取上下文的操作的



