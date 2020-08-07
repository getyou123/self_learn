### hive
用于处理结构化的日志数据统计
本质是将HQL转化成MR执行
优点缺点：大量 非实时，数据挖掘和迭代不擅长
架构原理：https://blog.csdn.net/wwwzydcom/article/details/84038048
架构这里的几个组件：MetaDataStore Client（包含 CLI JDBC Driver ）
Driver(包括SQL parser，physical plan，Query Optimizer和Execution)
两种存储MetaData的数据库的比较：derby，Mysql
和传统的数据库做对比：数据存储位置HDFS，数据规模，数据更新（少写多读），可以扩展
#### hive的基本操作启停
/bin/hive 启动的客户端

show database;
use default;
show tables;
create table test(id int,name string);
desc test;
insert into test values(100,"nn"");
select * from test;
quit;
#### mysql存储hive的元数据MetaData
1.配置mysql密码。多个地方可以登录等
2./hive/conf下新建hive-site.xml根据官方文档添加链接JDBC的信息
3.启动mysql，启动多个窗口进行测试
#### 执行hql的最基本方式
/bin/hive 进入客户端
hive -e "select * from student;"
hive -f test.hql > res.txt
进入hive客户端之后查看hdfs和本地的文件系统：
dfs -ls /user/hgw 查看hdfs的文件
！ls /opt/data

还有一些点：
hive日志路径配置
默认的配置文件是hive-default.xml
用户定义的文件是hive-site.xml 会覆盖掉默认的配置文件
hive启动是进行参数配置 /bin/hive -hiveconf param=value(仅对本次hive的客户端有效)
可以在Hive的客户端查看当前的所有的配置的信息 set;查看某个的话set param
也可以在进入之后set param=values
####DDl 数据库定义语言 CREATE,DROP,SHOW,ALTER
####DML 数据管理语言 UPDATE，DELETE，INSERT
####DCL 数据库控制语言 GRANT，REVOKE
####DQL  数据库查询语言 SELECT
#### 数据导入到hive表中
##### hive中的数据类型
类型	长度	备注
TINYINT	1字节	有符号整型
SMALLINT	2字节	有符号整型
INT	4字节	有符号整型
BIGINT	8字节	有符号整型
FLOAT	4字节	有符号单精度浮点数
DOUBLE	8字节	有符号双精度浮点数
DECIMAL	--	可带小数的精确数字字符串
TIMESTAMP	--	时间戳，内容格式：yyyy-mm-dd hh:mm:ss[.f...]
DATE	--	日期，内容格式：YYYY­MM­DD
INTERVAL	--	--
STRING	--	字符串
VARCHAR	字符数范围1 - 65535	长度不定字符串
CHAR	最大的字符数：255	长度固定字符串
ARRAY	--	包含同类型元素的数组，索引从0开始 ARRAY<data_type>
MAP	--	字典 MAP<primitive_type, data_type>
STRUCT	--	结构体 STRUCT<col_name : data_type [COMMENT col_comment], ...>
UNIONTYPE	--	联合体 UNIONTYPE<data_type, data_type, ...>
##### 本地数据导入hive表
已经存在本地，导入到已经存在的hive中，增加表中的数据量(建表语句和load local data)：
create student(id int, name string) ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
load data local inpath '/data/student.txt' into table student;
select * from student limit 4;
##### hdfs数据导入到hive表中
