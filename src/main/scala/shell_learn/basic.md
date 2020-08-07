#### 这里定义一些常用的shell脚本
#### ssh_init 实现部署bn到wn的ssh免密登录，原理是私钥在服务器端

### rsync限制带宽是按照kB/s 类似于scp
src写到文件，dest写到目录即可。或者可以加上dest/
rsync -avr --bwlimit=50000 --progress src dest 
-a 表示归档模式，保持所有文件的属性
-v 详细输出模式
-r 子目录进行递归
--bwlimt 用于限制同步的带宽
em:
mercury中将所有的searchnode的原始日志收集回来
rsync -avr --bwlimit=50000 --progress $SEARCH_NODE:$LOCAL_RAW_SRC_DIR/ ${LOCAL_RAW_DEST_DIR}/`echo $SEARCH_NODE | cut -d '@' -f 2`
### 上传hdfs
hdfs dfs -put src dest 也是指明了吧src，要么是文件要么是路径（都算作是文件了，做好写dest/）
同样的src最好写

### 初步进行rsplog的数据提取工作python 默认程序
HADOOP_CMD="hadoop jar \
            $HADOOP_STREAMING_JAR \
            -D stream.non.zero.exit.is.failure=false \
            -D mapred.job.name=\'rsplog_query_parse\' \
            -D mapred.job.priority=HIGH \
            -file $CUR_DIR/rsplog_query_parse.py \
            -mapper \"python rsplog_query_parse.py mapper\"
            -combiner \"python rsplog_query_parse.py reducer\"            
            -reducer \"python rsplog_query_parse.py reducer\"
            -input $HDFS_VIEW_DIR/* \
            -output $HDFS_QORI_DIR"
HADOOP_STREAMING_JAR=$HADOOP_HOME/share/hadoop/tools/lib/hadoop-streaming-2.6.0-cdh5.8.0.jar
实际完成的是按照python中的正则表达式进行提取并进行输出。可以在IDE中进行调试test。
数据格式说明：

###