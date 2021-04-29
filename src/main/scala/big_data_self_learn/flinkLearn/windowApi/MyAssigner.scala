package big_data_self_learn.flinkLearn.windowApi

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.parser.Feature
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.windowing.time.Time

/**
 * @ProjectName: spark_learn 
 * @Package: big_data_self_learn.flinkLearn.windowApi
 * @ClassName: MyAssigner
 * @Author: guowanghao
 * @Description: ${description}  
 * @Date:    2021/3/30 7:57 下午
 * @Version:    1.0
*/
class MyAssigner extends BoundedOutOfOrdernessTimestampExtractor[String](Time.seconds(5)){
  override def extractTimestamp(t: String): Long = JSON.parseObject(t, Feature.OrderedField).get("ts").asInstanceOf[java.lang.Long]
}
