只学习的最新的框架过程：
#kafka基础
消息队列好处，异步，解耦，削峰，可恢复性，缓冲一般来说是生产大于消费的，速度不一致
消息队列的两种方式：一对一（双方都在线，消费完成就没了），一对多（接着分成两种，基于队列的推，基于消费者的拉kafka后者，消息还是在队列中的）也是发布订阅模式，kafka由消费者来决定消费的速度，消费者维护一个长轮询，而不是队列主动推。
kafka基础架构：producer（写到topic中的哪个分区由分区器来决定） consumer（按照cG的形式来进行消费） consumer——group（一个组去消费） broker（集群就是一个个的broker进程，最多维护这各个topic中的一个partition） topic（生产者和消费这都是面向一个topic） partition（topic中的一块信息，一个topic里面有多个topic）
leader（分区的副本的leader） follower(分区副本的follower) leader和follow是不在同一个broker上的。
生产者和消费者必须要是找leader的进行数据的交互的。
kafka中存的一段时间内的数据，如果过了这个设置时间的话就会把数据清除，有时效性。
topic划分了数据，topic分区提高并发的，负载均衡。
某个分区在同一个时刻只能由同一个消费者租内的一个消费者消费，实际是同一个时刻，同一个消费者组中只能有一个消费者
访问这个文件夹。消费者组用于提高消费能力，但是组内的数据是无序的，虽然叫队列。一般来说，消费者组中消费者数量和分区数相同，
这样每个分区被消费者组中的一个消费者消费，效率理论上是最高的。
重要的基础架构图：ipad上生产者追加发给topic上的多个分区，相邻数据不一定发送到了同一个分区；消费者组中的多个消费者，每个最多拉取topic中的一个
分区，进行消费，消费的程度offset放在0.9之前是zk，0.9之后kafka本地topic中的__consumer_offset中存着。
### toipic的基本命令：
####topic的增删改查：
可以使用kafka-topic.sh --help查询用法
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
### kafka中的底层存储机制
主要的文件在配置中的server.propertities，只要配置的zookeeper的地址一样，brokerid不同就属于同一个kafka集群，
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
生产者分区策略：分区原则：是通过send方法指定的，生产时候指定的。数据被包装成record，kv形式，1.代码明确指明了去哪个分区  2.默认按照key的hash值，%当前主题的分区数 3.轮询各个分区，首次随机三个策略优先级生效。
可靠性保证：ack机制+同步follower -1--all（同步时候leader出错，数据会重复） 0--不用收到任何ack（不等任何回复，不知道写没写成了）  1--leader收到并落盘返回（但是没有和follower同步时候leader down，同时如果ISR=1） 1或者0 可能发生丢了 -1可能会重复
ISR:和leader几乎同步的follower集合（最新的版本是最新同步时间比较接近的）
ISR和leader之间的数据是如何保证同步的：HW 和 LEO
exactly once 语义： 数据重复的基础上实现幂等性 0.11版本之后操作被放在broker中，一个数据被包装做成了<PID,PARTITION,SEQNnum>PID
为producer的id，这是不能跨越session的。幂等性是指0.11版本之后无论broker发送了多少次同一个数据，都只在broker中存一条
操作是设置