#### 总体架构

#### 常用的api


##### 获取连接：
//创建配置文件对象
Configuration conf = HBaseConfiguration.create();
//设置连接zookeeper
conf.set("hbase.zookeeper.quorum", "hadoop01:2181,hadoop02:2181,hadoop03:2181");
Connection connection = ConnectionFactory.createConnection(conf);

##### 获取表名，连接表
TableName tableName = TableName.valueOf("hbase_race");
Table table = connection.getTable(tableName);

##### 获取admin并操作表和命名空间
val connection=ConnectionFactory.createConnection(conf)//获取connection
val admin=connection.getAdmin//获取admin对象
 admin常见的操作
 ```
    boolean tableExists(TableName tableName)	判断表是否存在
    void createTable(TableName tableName)	创建表
    void enableTable(TableName tableName)	禁用表
    boolean isTableEnabled(TableName tableName)	判断表是否启用
    boolean isTableAvailable(TableName tableName)	判断表是否可用
    void addColumnFamily(TableName tableName,ColumnFamilyDescriptor columnFamily)	往指定的表中添加列族
    void deleteColumn(TableName tableName,byte[] columnFamily)	删除指定的表中的列
    void deleteColumnFamily(TableName tableName，byte[] columnFamily)	删除指定的表中的列族
    namespaceDescriptor getNamespaceDescriptor(String namespace)	获取命名空间
    void createNamespace(NamespaceDescriptor namespace)	创建命名空间
    void deleteNamespace(String namespace)	删除命名空间
```


#### DQL操作
put(代码)
delete（代码）
scan（见文档）

#### Filter 过滤器
```过滤器	含义
   FilterList	过滤器列表,用于使用多个过滤器时
   SingleColumnValueFilter	单列值过滤器,用于过滤单列的值
   PrefixFilter	rowkey前缀过滤器PrefixFilter,如果包含前缀,返回结果
   ColumnPrefixFilter	单列前缀过滤器,判断当前列是否包含某个前缀,是这返回匹配列的值
   MultipleColumnPrefixFilter	多列过滤器，但可以指定包含多个列的前缀,返回匹配值,多段行
   QualifierFilter	列过滤器，区分列值过滤器
   RowFilter	行键过滤器用于过滤rowkey
   FamilyFilter	列族过滤器
   SingleColumnValueExcludeFilter	单列值排除过滤器
   pageFilter	实现分页过滤器
   valueFilter	列值过滤器
```


#### Hbase中的运算符
```
// CompareFilter.CompareOp.LESS_OR_EQUAL
   LESS				<
   LESS_OR_EQUAL		<=
   EQUAL				=
   NOT_EQUAL			<>
   GREATER_OR_EQUAL	>=
   GREATER				>
   NO_OP               排除所有
```
#### Hbase 过滤器的比较器

```$xslt
BinaryComparator(byte[])    按字节索引顺序比较指定直接数组,采用 Bytes.commpareTo(byte[]) BinaryPrefixComparator(byte [])  跟前面相同,只比较左端的数据是否相同
NullComparator  				按位比较
RegexStringComparator       提供一个正则的比较器 仅支持 EQUAL 和 NOT_EQUAL
SubStringComparator         判断提供的字串是否出现在value中  相当于 contains() 方法
```
#### 结果集 ResultScanner
```$xslt
result.getValue(byte[] family, byte[] qualifier)	获取值
result.getRow()	获取rowkey
```

#### 比较过程中的比较运算符总结
字符串完全匹配 BinaryComparator
字符串中包含子串 SubStringComparator
数字比较 BinaryComparator