* 对于三种的日志监控的不同点的对比：

spoolingDir的方式是监控一个文件夹下的文件，对于文件是按照一下放进去，而不是对文件夹中的文件的内容的修改。实际上是按照对于文件夹中的文件按照他的后缀名来确定这个
  文件是不是已经被访问了。
tailDIr的方式是可以实现对于多个文件夹内的多个文件的内容实现监控，可以通过设置多个filefamily的方式。
exec 方式存在断点无法续传的。tail -f 默认从最后第十行的开始执行监控

* 启动的时候bin/flume-ng agent -c conf/ -n a2 -f *.conf文件或者非简写 bin/flume-ng agent --conf conf/ --name a2 --conf-file *.conf

* 事务性：三个组件（source-channel-sink） 2个事务（put-take） 事务性：失败的重试直到成功不会产生影响

* source->chanel（channel选择器：一个source对应多个chanel）

* chanel->sink （sink组：多个sink对应一个chanel）

* 拓扑的结构的：通过avro的来实现，当然多个源对应一个sink或者一个源给多个

* conf文件的注释必须是另外起一行的，否则无效报错，执行的时候可以加上-Dflume.root.logger=INFO,console用于debug

* 注意跨机器通信avro，后者的source是服务端，所以是直接发送到另外一台机器的端口。因为那个端口提供服务。

* 拦截器实现对于同一个路径下的日志文件给到不同的部分，浏览日志，悬停日志等分别送到不同的sink（简单的）

* 自定义source监控mysql的数据，注意使用developer的文档