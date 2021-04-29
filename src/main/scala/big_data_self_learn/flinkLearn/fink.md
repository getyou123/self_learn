

##### DataStream中的api
有多种数据源，展示的是两种自定义的source，分别为是否是执行并行度的区别，两种分别实现不同的接口即可

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


#### process function
获取时间，获取watermark
使用计时器
侧输出流用于数据的分流
状态编程支持

#### flink的容错机制
检查点本质是一条信息，多个task流入的消息中被flink插入了很多的这类消息，而且他们是同一个逻辑时间点插入的，看成是一中checkpoint barrier
检查点的出发点就是对于同一个数据的处理完成之后所有的任务的状态进行快照，所以检查点就是一个消息，告诉任务需要进行检查点操作了，需要将状态（常见的就是消费的offset存在稳定的介质中了）
保存点本质上和检查点算法是一致的，但是需要web操作触发或者程序中设置，不是有flink自身的触发的，保存点一般来说是服务于版本升级或者是AB测试等。

#### 状态一致性
fink内部是通过checkpoint的机制保证了内存处理的exactly once的，原理是所有的任务对同一个数据处理完了之后进行checkpoint（可以参考一下容错机制）
端到端的状态一致性：一致性保障是最差的那个一致性保障来决定的
 source：可以要求记录从某个位置开始读取的，可以设置数据读取的位置
 sink：实现幂等性（做多次更做一次的结果是一样的，ES，hbase等）或者事务性（要么操作都成功要么都失败）



### 主表删除 51379 全维度测试公司  amac_manager
DELETE FROM amac_manager where id = 51379
## 主表插入数据使用的是全维度测试公司的 company_id
INSERT INTO amac_manager (id,company_id,manager_name,deleted) 
VALUES (51379,198582870,'全维度测试有限责任公司',0);
## 查询获取 manager_id 
SELECT * FROM amac_manager where manager_name = '全维度测试有限责任公司'
## 主表UPDATE 操作
UPDATE amac_manager SET reg_no='P1000000' where id =51379 AND manager_name = '全维度测试有限责任公司'
UPDATE amac_manager SET reg_date='1998-01-09' where id =51379 AND manager_name = '全维度测试有限责任公司'
UPDATE amac_manager SET est_date='1998-01-09' where id =51379 AND manager_name = '全维度测试有限责任公司'
UPDATE amac_manager SET office_addr='黑龙江省哈尔滨市666号' where id =51379 AND manager_name = '全维度测试有限责任公司'
UPDATE amac_manager SET reg_capital='1,000' where id =51379 AND manager_name = '全维度测试有限责任公司'
UPDATE amac_manager SET pay_capital='1,000' where id =51379 AND manager_name = '全维度测试有限责任公司'
UPDATE amac_manager SET company_nature='内资企业' where id =51379 AND manager_name = '全维度测试有限责任公司'
UPDATE amac_manager SET pay_capital_scale='100%' where id =51379 AND manager_name = '全维度测试有限责任公司'
UPDATE amac_manager SET org_type='私募证券投资基金管理人' where id =51379 AND manager_name = '全维度测试有限责任公司'
UPDATE amac_manager SET is_vip='是' where id =51379 AND manager_name = '全维度测试有限责任公司'
UPDATE amac_manager SET vip_type='观察会员' where id =51379 AND manager_name = '全维度测试有限责任公司'
UPDATE amac_manager SET become_vip_date='2013-06-28' where id =51379 AND manager_name = '全维度测试有限责任公司'
UPDATE amac_manager SET boss_name='李雷' where id =51379 AND manager_name = '全维度测试有限责任公司'
UPDATE amac_manager SET boss_qua='资格认定' where id =51379 AND manager_name = '全维度测试有限责任公司'
UPDATE amac_manager SET amac_last_updatetime='2018-02-27' where id =51379 AND manager_name = '全维度测试有限责任公司'
UPDATE amac_manager SET amac_last_updatetime='2018-02-27' where id =51379 AND manager_name = '全维度测试有限责任公司'
UPDATE amac_manager SET deleted=1 where id =51379 AND manager_name = '全维度测试有限责任公司'
UPDATE amac_manager SET deleted=0 where id =51379 AND manager_name = '全维度测试有限责任公司'

### 子表 amac_manager_boss_record
## 查询子表中的id 
SELECT * FROM amac_manager_boss_record where manager_id = 51379 # 查无数据 自己造
INSERT INTO amac_manager_boss_record (manager_id,unit,deleted,boss_name) 
VALUES (51379,'全维度测试有限责任公司',0,'小明');
SELECT * FROM amac_manager_boss_record where manager_id=51379 AND unit='全维度测试有限责任公司'
## 子表改 
UPDATE amac_manager_boss_record SET time_scope='2014.12 - ' where id =8421565 AND unit = '全维度测试有限责任公司'
UPDATE amac_manager_boss_record SET position='投资总监' where id =8421565 AND unit = '全维度测试有限责任公司'
UPDATE amac_manager_boss_record SET unit='上海元点投资管理合伙企业（有限合伙）' where id =8421565 AND unit = '全维度测试有限责任公司'
### 注意此时应该按照ID进行删除操作  

### 子表 amac_manager_product
## 查询子表中的id 
SELECT * FROM amac_manager_product where manager_id = 51379 # ID 为 212554
DELETE FROM amac_manager_product where id = 212554
INSERT INTO amac_manager_product (id,manager_id,fund_name,fund_no,est_date,filing_date,filing_stage,fund_type,money_type,manager_name,manager_type,trustee_name,operation_status,fund_last_updatetime,special_msg,monthly_report,half_year_report,year_report,quarterly_report,source_url,deleted) 
VALUES (212554,51379,'全维度测评投资基金合伙企业（有限合伙','SD0000','2012-06-26','2014-03-17','暂行办法实施前成立的基金','股权投资基金','人民币现钞','国药资本管理有限公司','受托管理',	'全维度股份有限公司','正在运作',	'2017-06-16',NULL,'应披露0条，未披露0条；',	'应披露2条，未披露0条；','应披露3条，未披露0条；','应披露0条，未披露0条；',NULL,0);
## 子表改 
UPDATE amac_manager_product SET est_date='2014.12 - ' where id =212554 AND manager_id = 51379
UPDATE amac_manager_product SET est_date='2012-06-26' where id =212554 AND manager_id = 51379
UPDATE amac_manager_product SET trustee_name='华泰证券股份有限公司' where id =212554 AND manager_id = 51379
UPDATE amac_manager_product SET trustee_name='全维度股份有限公司' where id =212554 AND manager_id = 51379

### 子表 amac_manager_staff
## 查询子表中的id 
SELECT * FROM amac_manager_staff where manager_id = 51379 # ID 为 132193
DELETE FROM amac_manager_staff where id = 132193 and manager_id = 51379
INSERT into amac_manager_staff (id,manager_id,e_name,position,qualification,deleted)
VALUES (132193,51379,'小红','总经理','是（资格认定',0)
## 子表改 
UPDATE amac_manager_staff SET e_name='小明' where id =132193 AND manager_id = 51379
# 还未改回来 UPDATE amac_manager_staff SET e_name='小红' where id =132193 AND manager_id = 51379



#### company_own_tax
SELECT * FROM company_own_tax where name LIKE '%全维度测试%' # 3351518
## 删除
DELETE FROM company_own_tax where id = 3351518
## 插入
INSERT INTO company_own_tax 
(id,`name`,base,tax_id_number,legalperson_name,person_id_name,person_id_number,location	,tax_category,own_tax_amount,taxpayer_type,publish_date,own_tax_balance,new_own_tax_balance,reg_type,department,source,oss_location,type,is_deleted)
VALUES (3351518,'全维度测试有限责任公司','hlj','912301997029403XXP','小明','身份证','非公示项',NULL,'城建税','1253.00',NULL,NULL,'1253.00',NULL,NULL,'黄浦区税务局',NULL,NULL,NULL,0)
## UPDATE
UPDATE company_own_tax SET legalperson_name='李雷' where id =3351518 AND `name` = '全维度测试有限责任公司'


###