package big_data_self_learn.hbase

import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{HBaseConfiguration, HColumnDescriptor, HTableDescriptor, TableName}

import org.apache.hadoop.hbase.client.{Connection, ConnectionFactory, Delete, Get, HBaseAdmin, Put, Result, ResultScanner, Scan, Table}
import org.apache.hadoop.hbase.protobuf.generated.HBaseProtos.ColumnFamilySchema

object hbase_learn {

  //连接zk的url和port
  val zkUrl="192.168.3.107"
  val zkPort="2181"


  //API的所有的操作包括对于表的这个层面的查存，创建，
  //表中的内容的修改：增删改查

  //需要注意admin对象只是在建表，查存，删除，触发合并操作等

  //查存 会用到admin函数
  def isExistsApi(table:String):Boolean={
    //首先是HbaseCOnfiguration的配置
    val conf=HBaseConfiguration.create()//注意这里的获取方式是通过create获取的
    conf.set("hbase.zookeeper.quorum",zkUrl)//这里写的是集群的地址
    conf.set("hbase.zookeeper.property.clientPort",zkPort)//其实不用特殊说明也是2181端口
    //得到conf对象之后获取admin对象,通过从conf中获取
    //使用旧的API
    //val admin=new HBaseAdmin(conf)旧的API
    //val res=admin.tableExists(table)//正好最后一行作为函数的返回值
    //admin.close()//关闭操作
    //res
    //新的API：
    val connection=ConnectionFactory.createConnection(conf)//获取connection
    val admin=connection.getAdmin//获取admin对象
    val res=admin.tableExists(TableName.valueOf(table))//多层的数据对象封装
    res
  }


  //创建表 一定会用到admin
  def createTable(tableName:String,columnFamily:Array[String]):Unit={
    val conf=HBaseConfiguration.create()
    conf.set("hbase.zookeeper.quorum",zkUrl)//这里写的是集群的地址
    conf.set("hbase.zookeeper.property.clientPort",zkPort)//其实不用特殊说明也是2181端口
    //得到conf对象之后获取admin对象,通过从conf中获取
    val connection=ConnectionFactory.createConnection(conf)
    val admin=connection.getAdmin
    if(isExistsApi(tableName)) {
      println("table exists")
    }
    else{
      //创建新的describe对象，然后从这个对象来创建表
      val describer=new HTableDescriptor(tableName)
      for(x<-columnFamily){//把每一个列族添加进去
        describer.addFamily(new HColumnDescriptor(x.getBytes))//注意这里使用的bytes数组
      }
      //构造完成descriptor之后使用admin创建这个表
      admin.createTable(describer)
      println("table created")
    }
  }

  //删除表 也会用到admin对象
  def deleteHTable(tablename:String):Unit={
    //本例将操作的表名
    val tableName = TableName.valueOf(tablename)
    val conf=HBaseConfiguration.create()
    conf.set("hbase.zookeeper.quorum",zkUrl)//这里写的是集群的地址
    conf.set("hbase.zookeeper.property.clientPort",zkPort)//其实不用特殊说明也是2181端口
    val connection=ConnectionFactory.createConnection(conf)
    val admin=connection.getAdmin
    //删除之前查存，disable
    if(admin.tableExists(tableName)){
      admin.disableTable(tableName)
      admin.deleteTable(tableName)
      println("delete success")
    }else{
      println("this table not exist")
    }
  }

  //对于表中的数据的内容的修改 增删查改

  //增 插入记录  其实是对其中的一个cell的操作
  //put "sk:table_1","row1"."cf:col1"."values_1"
  def insertHTable(connection:Connection,tablename:String,family:String,column:String,key:String,value:String):Unit={

      val userTable = TableName.valueOf(tablename)
      val table=connection.getTable(userTable)
      //准备key 的数据
      val p=new Put(key.getBytes)
      //不指定时候是删除一行数据
      //为put操作指定 column 和 value
      p.addColumn(family.getBytes,column.getBytes,value.getBytes())//这插入的是一个cell中的值？
      table.put(p)




    //如果是插入数据在多行的话构造一个put的ArryList数组，然后构造各个打的put对象add进去
    //最后table.put(ArrayList[put]),如果真的也要上升到对表中的整行数据的处理的话，是不是就是需要进行ArraList[put]整个操作
    //插入如果在一个rowkey中一般写成map 套map的形式来存储需要进行插入的对象
  }

  //删 删除某条记录
  //delete "sk:table_1","row_1","cf:col1"对单个的cell删除
  //deleteall
  def deleteRecord(connection:Connection,tablename:String,family:String,column:String,key:String): Unit ={
    var table:Table=null//注意这个检测逻辑
    try{
      val userTable=TableName.valueOf(tablename)
      table=connection.getTable(userTable)//获取表名称
      val d=new Delete(key.getBytes())//构造delete对象
      table.delete(d)//这个是删除整个行的操作
      //这个是删除cell
      //      d.addColumn(family.getBytes(),column.getBytes())
      //      table.delete(d)

      println("delete record done.")
    }finally{
      if(table!=null)table.close()
    }
  }


  //查 基于KEY查询某条数据
  //get "ns:table_1","row_1"
  //注意get是获取的是行数据然后在数据
  def getAResult(connection:Connection,tablename:String,family:String,column:String,key:String):Unit={
    var table:Table=null
    try{
      val userTable = TableName.valueOf(tablename)
      table=connection.getTable(userTable)//由tablename获取table对象
      val g=new Get(key.getBytes())//构造get对象
      val result=table.get(g)//获取数据，以行为单位
      val value=Bytes.toString(result.getValue(family.getBytes(),column.getBytes()))//获取cell中的数据
      println("key:"+value)
    }finally{
      if(table!=null)table.close()//检查逻辑

    }

  }

  //扫描记录
  //scan "ns:table_1"
  //scan "ns:table_1","cf:clo1"
  //scan "ns:table_1",{STARTROW=>'row3',ENDROW=>'row4'}不包括row4
  def scanRecord(connection:Connection,tablename:String,family:String,column:String,startRow:String,endRow:String): Unit ={
    var table:Table=null
    var scanner:ResultScanner=null
    try{
      val userTable=TableName.valueOf(tablename)
      table=connection.getTable(userTable)
      val s=new Scan()
      s.addColumn(family.getBytes(),column.getBytes())
      //设置开始和结束的位置
      s.setStartRow(startRow.getBytes())
      s.setStopRow(endRow.getBytes())
      scanner=table.getScanner(s)
      println("scan...for...")
      var result:Result=scanner.next()
      while(result!=null){
        println("Found row:" + result)
        println("Found value: "+Bytes.toString(result.getValue(family.getBytes(),column.getBytes())))
        result=scanner.next()
      }
    }finally{
      if(table!=null)
        table.close()
      scanner.close()

    }
  }
}
