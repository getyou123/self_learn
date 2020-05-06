package big_data_self_learn.flume.selfDefinedSource

// 打包后放到flume的lib下

import org.apache.flume.PollableSource.Status
import org.apache.flume.{Context, Event, PollableSource}
import org.apache.flume.conf.Configurable
import org.apache.flume.event.SimpleEvent
import org.apache.flume.source.AbstractSource

class mySource extends AbstractSource with PollableSource with Configurable {

  var prefix:String="pre"
  var subfix:String=""

  // 后续版本会增加start stop等方法

  //产生event
  def getSomeData():Event = {
    var e:Event =new SimpleEvent()
    e.setBody((this.prefix+"----bushishuo----"+this.subfix).getBytes())
    e
  }

  // 1.获取数据源，2.封装成event,3.发送出去
  override def process(): PollableSource.Status = {
    //获取数据源

    var status:Status = null
    try {
      // 自定义处理

      // 接收新数据
      var e:Event = getSomeData()

      // 将 Event  存储到此 Source 相关Channel(s) 中
      getChannelProcessor().processEvent(e)
      status = Status.READY;

    } catch {// 异常记录, 处理

      //出现异常则事务失败从新来
      case t:Throwable=>
        {
          status = Status.BACKOFF
          // 异常抛出// 异常抛出
          if (t.isInstanceOf[Error]) throw t.asInstanceOf[Error]
        }
    } finally {
      //
    }
    return status
  }

  // 事务发送失败的增加时间，暂时不用
  override def getBackOffSleepIncrement: Long = 0L

  // 事务发送失败的时间管理，暂时不用
  override def getMaxBackOffSleepInterval: Long = 0L

  //读取conf文件的配置，可以向程序中添加变量的来源
  override def configure(context: Context): Unit = {

    //定义conf文件中可以使用的key,最好见名知意
    val prefixStringKey="preFix"
    val subFixStingKey="subFix"

    this.prefix=context.getString(prefixStringKey,"prefix")//尝试从conf文件中获取，如果没有的话使用prefix
    this.subfix=context.getString(subFixStingKey,"subfix")//尝试从conf中获取
  }
}