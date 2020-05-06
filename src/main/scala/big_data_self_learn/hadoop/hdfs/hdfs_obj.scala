package big_data_self_learn.hadoop.hdfs

import java.io.{File, FileInputStream}
import java.net.URI

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FSDataOutputStream, FileSystem, Path}
import org.apache.hadoop.io.IOUtils
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

object hdfs_obj {



  //基本的过程
  def basic_pro():Unit = {
    //1.创建fs对象，常见的在spark中获取的hdfs的文件对象使用sparkCOnf中的来实例化
    // val hdfs_obj=org.apache.hadoop.fs.FileSystem.get(sc.hadoopConfiguration),然后就直接使用hdfs的文件对象了
    val configuration = new Configuration()

    val fs_obj: FileSystem = FileSystem.get(new URI("hdfs://192.168.3.107:9000"), configuration, "root")

    // 2 创建目录
    // 其实所有的核心都是调用fs对象的api
    fs_obj.mkdirs(new Path("/user/guowanghao/obj_mkdir_test"))

    // 3 关闭资源
    fs_obj.close()
  }

  //上传
  def selfCopyFromLocal(fsObj:FileSystem, srcPath:String, detPath:String)={

    fsObj.copyFromLocalFile(new Path(srcPath),new Path(detPath))
  }

  // 下载
  def selfPut(fsObj:FileSystem, srcPath:String, detPath:String)= {
    fsObj.copyToLocalFile(new Path(srcPath), new Path(detPath))
  }

  // 删除
  def selfDel(fsObj:FileSystem, srcPath:String)= {
    fsObj.delete(new Path(srcPath))
  }

  // 判断是dir还是普通的文件
  def isDir(fsObj:FileSystem, srcPath:String)= {
    fsObj.isDirectory(new Path(srcPath))
    //fsObj.isFile(new Path(srcPath))
  }


  //文件流copy
  def putFileToHdfs(fsObj:FileSystem, srcPath:String, detPath:String)={

    // 输入流
    val inputStream: FileInputStream = new FileInputStream(new File(srcPath))

    //输出流
    val outputStream: FSDataOutputStream = fsObj.create(new Path(detPath))

    //流对拷
    IOUtils.copyBytes(inputStream,outputStream,fsObj.getConf)

    //关闭资源
    IOUtils.closeStream(inputStream)
    IOUtils.closeStream(outputStream)
  }


  //常见的在写路径之前删除文件，实现覆盖写的方法
  def write_after_check_and_delete(hdfsPath: String, sc: SparkContext, lineRDD: RDD[String]) = {
    // get hdfsfile object
    val hdfs_obj = org.apache.hadoop.fs.FileSystem.get(sc.hadoopConfiguration)
    // check if source exist, if exists first delete and then write
    // if not exists write without delete
    if (hdfs_obj.exists(new org.apache.hadoop.fs.Path(hdfsPath))) {
      hdfs_obj.delete(new org.apache.hadoop.fs.Path(hdfsPath), true)
      lineRDD.saveAsTextFile(hdfsPath)
    } else {
      lineRDD.saveAsTextFile(hdfsPath)
    }
  }

  def main(args: Array[String]): Unit = {
    basic_pro()
  }

}
