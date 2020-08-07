#kafka基础
最新的官方文档的地址：http://kafka.apache.org/
消息队列好处，异步，解耦，削峰，可恢复性，缓冲一般来说是生产大于消费的，速度不一致
消息队列的两种方式：一对一（双方都在线，消费完成就没了），一对多（接着分成两种，基于队列的推，基于消费者的拉kafka后者，消息还是在队列中的）也是发布订阅模式，kafka由消费者来决定消费的速度，消费者维护一个长轮询，而不是队列主动推。
kafka基础架构：producer（写到topic中的哪个分区由分区器来决定） consumer（按照cG的形式来进行消费） consumer——group（一个组去消费） broker（集群就是一个个的broker进程，最多维护这各个topic中的一个partition） topic（生产者和消费这都是面向一个topic） partition（topic中的一块信息，一个topic里面有多个topic）
leader（分区的副本的leader） follower(分区副本的follower) leader和follower是不在同一个broker上的。
生产者和消费者必须要是找leader的进行数据的交互的。
kafka中存的一段时间内的数据，如果过了这个设置时间的话就会把数据清除，有时效性。
topic划分了数据，topic分区提高并发的，负载均衡。
某个分区在同一个时刻只能由同一个消费者组内的一个消费者消费，实际是同一个时刻，同一个消费者组中只能有一个消费者
访问这个文件夹。消费者组用于提高消费能力，但是组内的数据是无序的，虽然叫队列。一般来说，消费者组中消费者数量和分区数相同，
这样每个分区被消费者组中的一个消费者消费，效率理论上是最高的。
重要的基础架构图：ipad上生产者追加发给topic上的多个分区，相邻数据不一定发送到了同一个分区，这个是由分区器来决定发到哪里去；消费者组中的多个消费者，每个最多拉取topic中的一个
分区，进行消费，消费的程度offset放在0.9之前是zk，0.9之后kafka本地topic中的__consumer_offset中存着。
确定版本方法：kafka_2.11-0.11.0.2.tgz scala版本是2.11 kafka的版本是0.11 
###toipic的基本命令：
####topic的增删改查：
可以使用kafka-topic.sh --help查询用法，因为是对于topic的层面的信息，所以必须要有zk。
./kafka-topics.sh --zookeeper 192.168.3.107:2181 --list
./kafka-topics.sh --zookeeper 192.168.3.107:2181 --create --topic again --replication-factor 1 --partitions 1 注意副本数不超过broker数区分于hdfs设置的分区数，那个是最大的副本数
./kafka-topics.sh --zookeeper 192.168.3.107:2181 --delete --topic again 会被标记删除，没有分区了就。
./kafka-topics.sh --topic first --describe --zookeeper 192.168.3.107 可以查看topic的详情
    Topic:first     PartitionCount:2        ReplicationFactor:1     Configs:
    Topic: first    Partition: 0    Leader: 0       Replicas: 0     Isr: 0
    Topic: first    Partition: 1    Leader: 0       Replicas: 0     Isr: 0
分区实质上是在指定的log下面生成了first-0的文件夹，这就是分区了。server.log日志也是很重要的。
#### 控制台生产者
./kafka-console-producer.sh --broker-list 192.168.3.107:9092 可以查询可以使用的命令
前提是zookeeper开着，kafka集群开着
生产 ./kafka-console-producer.sh --broker-list 192.168.3.107:9092 --topic first
尽量轮询的发送到各个机器上的topic的文件夹中
#### 控制台消费者
./kafka-console-consumer.sh --topic fisrt --bootstrap-server 192.168.3.107:9092(新的)
./kafka-console-consumer.sh --topic fisrt --zookeeper 192.168.3.107:2181(老的)
默认从最大的offset开始消费，--from-beginning 从头消费
####kafka压测
使用kafka自带的压测脚本进行压测，需要测试写入速度上限和消费数据速度，
使用kafka的自带的压测脚本可以实现kafka-producer-perf-test.sh:

### kafka中的底层存储机制
主要的文件在配置中的server.propertities，**只要配置的zookeeper的地址一样，brokerid不同就属于同一个kafka集群**
各个机器启动就可以，kafka-server-start.sh 指定使用的propertities
里面的log.dir不仅仅存日志，还存数据，所有的topic的分区都是在这里。
#### 对于某个topic的指定offset的位置数据的读取过程：
例如一个first-0，一个分区的落盘的内容：/etc/software/kafka/logs/first-1
    -rw-r--r--. 1 root root 10485760 5月  30 10:05 00000000000000000014.index 索引
    -rw-r--r--. 1 root root      153 5月  30 10:38 00000000000000000014.log   数据
    -rw-r--r--. 1 root root       10 5月  30 10:05 00000000000000000014.snapshot
    -rw-r--r--. 1 root root 10485756 5月  30 10:05 00000000000000000014.timeindex
    -rw-r--r--. 1 root root       10 5月  30 10:38 leader-epoch-checkpoint
 通过分片和索引来实现快速查找，指定了offset的时候进行查找时候，先查找index名，在查找log对应的文件内数据，index文件存了最大的数据的offset，
offset=10 命中00000000000000000014.index 拿到176 指引去00000000000000000014.log找176位置数据。
队列只能保证区内有序但是和直接的生产的顺序是不一致的，topic是一个逻辑上的，分区是一个文件夹。
### 生产者原理
向topic的分区（物理上的文件夹中写数据），哪个分区？可靠性？分区的主是leader 其他副本是follower
生产者分区策略：
分区原则：是通过send方法指定的，生产时候指定的。数据被包装成record，kv形式，1.代码明确指明了去哪个分区自定义分区器  2.默认按照key的hash值，%当前主题的分区数 3.轮询各个分区，首次随机，三个策略按照这样的优先级生效。
可靠性保证：ack机制(生产者对于broker的应答要求级别)+同步follower(leader和follower之间的数据同步) -1--all（同步时候leader出错，数据会重复） 0--不用收到任何ack（不等任何回复，不知道写没写成了）  1--leader收到并落盘返回（但是没有和follower同步时候leader down，同时如果ISR=1） 1或者0 可能发生丢了 -1可能会重复
ISR:和leader几乎同步的follower集合（最新的版本是最新同步时间比较接近的）
ISR和leader之间的数据是如何保证同步的：HW(当前ISR中的最小的LEO) 和 LEO(LOG_END_OFFSET)
生产写入的exactly once 语义： 数据重复的基础上实现幂等性 0.11版本之后操作被放在broker中，一个数据被包装做成了<PID,PARTITION,SEQNnum>PID
为producer的id，这是不能跨越session的。幂等性是指0.11版本之后无论向broker发送了多少次同一个数据，都只在broker中存一条。写操作的幂等性结合At Least Once语义实现了单一Session内的Exactly Once语义
操作是设置：producer的属性enable.idempotence=true。
### 消费者原理
大体上是按照长轮询的方式感知队列中的数据变化，长轮询可以传入一个参数，表示没有拉取到数据的话，间隔多久再次去轮询，长轮询本身是个缺点。消费哪个分区，对应消费那个哪个问价夹中的内容？分区分配策略和offset维护市重点。
#### 分区分配策略
唯一不能：同一时刻同一个CG中的两个消费者不能同时消费同一个topic的同一个分区。
分区策略（消费者个数发生变化或者首次的时候都要触发这个分区策略）：可参考 https://blog.csdn.net/dz77dz/article/details/89384935
    range（默认） 前M个比M+1后面的多分一个的，看单个主题内。
    RoundRobin: 轮询每个partition分给一个consumer，所有的主题看成一个。
CG只有一个主题的话不解释，多个分区比如T1(3分区) T2(3分区) RoundBin的话把这两个topic最为一个整体，所有分区平铺，所有消费者组组内消费者平铺求%，分区之间最多
差一个分区。range会看单个主题内分区，排在前面的分区数普遍会分配给前面的消费者，一个topic最多差出来一个分区，所以多个主题都这样分配的话，排在前面的消费者会被要求去消费差距性质比较多的分区数。
### offset
三者确定offset的值：CG不是按照消费者+topic+partiton 来决定offset
0.9 为zk中consumer/consumer_group_N/offsets/topic_N
0.11之后 存在kafka本地，前提设置可以消费系统topic(配置consumer.properties中exclude。internal.topic=false)，然后在__consumer_offsets中可以读取
./kafka-console-consumer.sh --topic __consumer_offsets --from-beginning --bootstrap-server 192.168.3.107:9092 --formatter "kafka.coordinator.group.GroupMetadataManager\$OffsetsMessageFormatter" --consumer.config ../config/consumer.properties
（指定使用的配置和formatter）
数据的格式：三者确定
[console-consumer-45481,first,0]::[OffsetMetadata[14,NO_METADATA],CommitTime 1582891639578,ExpirationTime 1582978039578]
### 控制台消费者配置属于同一个CG
启动kafka-console-consumer.sh 时候值分别读取consumer.properties，两台机器都设置成一样的group.id
然后都起来，订阅一个topic，那么同一个时刻只有一个接收到了数据，同时增加一个消费者之后实现分区策略的分配
### kafka高效读写数据的基础
log数据文件是追加的形式写的，顺序写，速度快；零拷贝技术
### 事务
幂等性只是解决单次的session

### api操作
#### 生产者的api
有个主线程，send是一个单独的线程。ack用于重试，保证生产数据不丢失，发完发下一波，等结果一段时间。
exactlyonce通过至少一次+幂等性
入队列的所有步骤(ipad上有图)send->拦截器（k，v都是正常的）->序列化器->分区器（输入是bytes数组）->够一定大小了或者到指定了时间了（batch.size和linger.ms）->入队列
样例producer：big_data_self_learn.kafka.kafkaProducer有回调函数版本和无回调函数版本 。与图对应 几个点：batch.size
注意配置文件也可以自己用的ProducerConfig这个样例类代替写的prop的key字符串。 
同步发送的方法很少用。
#### 消费者api
主要有自动提交和手动提交（enable.auto.commit是否为true，自动提交的时间不好设置大小）这也是高阶api和低阶api的差异，存储的offset的位置也按照kafka的版本被放置在zk或者kafka本地，
同时手动提交还要区分是同步提交offset还是异步的提交offset（consumer.commitSync和consumer.commitAsync），有回调函数和无回调函数的版本（提交的第二参数callback）。
异步提交是主线程去做别事情，offset提交之后下次就从offset之后读取，先提交offset在处理会丢数据，后提交offset会出现重复。
自定义存储offset可是试下exactly once，维护在一个非易失。把处理和提价offset变成事务，这样提交和offset的维护是同时失败同时成功的。
首先获取offset：topic + CG +partition获取到offset。默认是从最大的offset开始的
从哪里开始消费：获取不到这个CG的最新的offset触发rest的（earliest或者latest）
从头消费的设置条件：换个无效CG并且设置为earliest，不然只是从能访问到的earliest或者latest两种。
消费者的rebalance
#### 分区器
可以自己定义分区器：参考big_data_self_learn.kafka.kafkaProducerWithSelfDefinedPatritioner
在这里实现是按照key和val的值决定去哪个分区。
实际的默认的分区原则：
#### 拦截器
自定义拦截器在big_data_self_learn.kafka.producer.kafkaProducerInterceptor
#### kafka对接flume
上游是flume，将数据写到kafka中。
参考flume_memory_kafka.conf
启动方式：bin/flume-ng agent -c conf/ -n a1 jobs/flume_momory_kafka.conf
#### 监控的Eagle

#### 面试题

