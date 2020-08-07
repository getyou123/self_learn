## hive中的行数据的和声明表时候的创建语句
本地test.txt文件内容：
liuxing,xiaoyu_xiaoxue,xiadonghai:49 liumei:49,zhangjiakou_china
yagguang,beiji_fuhelei,mazu；90,shanxi_china

创建的表的语句：
create table tb1(
name string,
friends array<String>,
parent map<string,int>,
address struct< city:string,countary:string>
)
row format delimited by fields terminated by ','
collection items terminated by '_'
map keys terminated by ':'
lines terminated by '\n';

分别描述列分隔符，集合（Array，Map，struct元素之间的分隔符）分隔符，map中的key的分隔符，行分隔符。

导入文本
load data local inpath '/data/test.txt' into table tb1;
各种数据类型的访问方式：
select friends[0],parent[ 'xiadonghang' ],address.city from tb1 where name='liuxing'

可以看出来的是map访问用M1["key"],struct访问用S.yu,array访问用arr[0]。
强制类型转化：CAST(“1” AS INT)失败的话返回的是NULL。