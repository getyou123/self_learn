package big_data_self_learn.flume.Interceptor_channelSelector

import java.nio.charset.StandardCharsets
import java.util

import org.apache.flume.{Context, Event}
import org.apache.flume.interceptor.Interceptor

/**
  * 实现flume的拦截器的功能
  */

class flume_interceptor extends Interceptor with Interceptor.Builder{

  private var events = new util.ArrayList[Event]()
  //初始化
  override def initialize(): Unit = {

  }

  // 单个事件拦截，event就是header+body
  override def intercept(event: Event): Event = {

    //获取event添加header
    //var headers =event.getHeaders()

    //获取body中的内容，注意转化成string类型
    val bodyStr=new String(event.getBody, StandardCharsets.UTF_8)

    //根据body中的内容添加headers中的信息
    //用于对于事件的分类分别去向不同的sink端,这里决定了conf文件里面怎么写
    if (bodyStr.contains("click")) {
      event.getHeaders().put("type","click")
    }

    if(bodyStr.contains("view")){
      event.getHeaders().put("type","view")
    }
    //最终返回这个event
    event
  }

  // 批量的事件拦截
  override def intercept(list: util.List[Event]): util.List[Event] = {
    //清空返回事件组
    events.clear()
    //对于每一个调用定义好的单个的evecnt的事件处理过程
    for(i<- 0 to list.size()-1){
      val event: Event = list.get(i)
      events.add(intercept(event))
    }
    events
  }

  //关闭
  override def close(): Unit = {

  }

  override def build(): Interceptor = new flume_interceptor()

  override def configure(context: Context): Unit = {}
}

