package big_data_self_learn.flinkLearn.windowApi

import java.util.Properties

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.parser.Feature
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011
import org.apache.flink.util.Collector

import scala.collection.mutable

/**
 * @ProjectName: spark_learn 
 * @Package: big_data_self_learn.flinkLearn.windowApi
 * @ClassName: OrderBinLog
 * @Author: guowanghao
 * @Description: ${description}
 * @Date: 2021/3/30 7:26 下午
 * @Version: 1.0
 */

object OrderBinLog {

  def main(args: Array[String]): Unit = {

    // 构造env
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    // 设置事件时间
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    // 设置并行度
    env.setParallelism(1)
    // 设置checkpoint
    env.getCheckpointConfig.setCheckpointInterval(1000L)
    // 设置WM
    env.getConfig.setAutoWatermarkInterval(500L)

    //设置source
    val prop = new Properties()
    prop.setProperty("bootstrap.servers", "172.24.114.134:9092,172.24.114.135:9092,172.24.114.136:9092")
    prop.setProperty("group.id", "console-consumer-64893")
    prop.setProperty("auto.offset.reset", "latest")
    val company_parser = env.addSource(new FlinkKafkaConsumer011[String]("gsxt.company_parser", new SimpleStringSchema(), prop))

    "{\"priority\":0.051831311329,\"eventId\":\"80261514_1618313113217\",\"isNew\":0,\"crawlerType\":1,\"crawlerCode\":7,\"parserCode\":1,\"companyId\":80261514,\"instance\":\"gj\",\"selector\":{\"id\":65050130,\"update\":true,\"triggerReason\":\"SN_DATA\",\"tryId\":0,\"redisKey\":\"searchCompany_gj_queue_updateOffline\",\"logId\":0,\"selectorSendTs\":1618313113290,\"base\":\"js\",\"table\":\"gsxt_logout_dispatch\",\"triggerType\":\"immediate\",\"dispatchId\":62640694},\"crawler\":{\"regNum\":\"320100001072204\",\"companyName\":\"南京濮鑫一九四五号文化艺术品投资合伙企业（有限合伙）\",\"creditCode\":\"91320100MA1MN4P39D\",\"keyWord\":\"91320100MA1MN4P39D\",\"searchResult\":null,\"crawlerReceiveTs\":1618313113345,\"crawlerSendTs\":1618313156681,\"crawlerSendQueue\":\"parseCompany_gj_queue\",\"dbExist\":null,\"flag\":\"proxy_tunnel\",\"detailPos\":-2,\"taskName\":null,\"base\":null,\"crawlerInfo\":{\"detailUrl\":null,\"info\":null,\"aFew\":null,\"meta\":{\"crawlerList\":[\"all\"]},\"afew\":null},\"domain\":\"http://www.gsxt.gov.cn\",\"errorCode\":7,\"searchResultTotal\":null,\"captchaInfo\":\"30\",\"crawlerHistory\":null},\"parser\":{\"parserReceiveTs\":1618313156678,\"parserSendTs\":1618313156678,\"abInfo\":{},\"companyId\":null,\"companyGid\":null,\"delay\":false,\"extArgs\":null,\"reRegCompany\":false,\"parserSendInfo\":{\"punishmentPdf\":null,\"investorCommitmentImg\":null,\"updateCompanyApproveEvent\":null,\"updateCompanyReportApproveEvent\":null,\"changeIds\":null,\"mcLegal\":null,\"mcStaff\":[],\"mcInvestor\":[],\"mcBusinessScope\":null,\"mcState\":null,\"mcLocation\":null,\"mcInstitute\":null,\"mcCapital\":null,\"mcOrgType\":null}},\"requestHistory\":{\"requests\":[{\"url\":\"http://www.gsxt.gov.cn/index.html\",\"startTime\":1618313113347,\"endTime\":1618313115430,\"statusCode\":200,\"proxy\":\"10.39.221.178:8023\"},{\"url\":\"http://www.gsxt.gov.cn/corp-query-custom-geetest-image.gif?v=40\",\"startTime\":1618313115860,\"endTime\":1618313117362,\"statusCode\":200,\"proxy\":\"10.39.221.178:8023\"},{\"url\":\"http://www.gsxt.gov.cn/corp-query-geetest-validate-input.html?token=4209658063\",\"startTime\":1618313117362,\"endTime\":1618313119437,\"statusCode\":200,\"proxy\":\"10.39.221.178:8023\"},{\"url\":\"http://www.gsxt.gov.cn/SearchItemCaptcha?t=1618313119687\",\"startTime\":1618313119687,\"endTime\":1618313122011,\"statusCode\":200,\"proxy\":\"10.39.221.178:8023\"},{\"url\":\"end\",\"startTime\":1618313156680,\"endTime\":1618313156680,\"statusCode\":403,\"proxy\":\"10.39.221.178:8023\"}]}}"

    company_parser.map(x=>{
      val nObject = JSON.parseObject(x, Feature.OrderedField)
      (nObject.get("parserCode"),nObject)
    }).print()

//    val value = binlogStream.map(x => JSON.parseObject(x, Feature.OrderedField))
//      .map(x => (x.getJSONArray("data"), x))
//      .flatMap(x => {
//        for (i <- 0 until x._1.size()) yield (x._1.getJSONObject(i), x._2)
//      })
//      .map(x => ((x._1.get("id").toString, x._2.get("table").toString), 1))
//      .keyBy(_._1)
//      .timeWindow(Time.seconds(100), Time.seconds(5))
//      .process(new ProcessWindowFunction[((String, String), Int),
//        ((String, String), Int),
//        (String, String),
//        TimeWindow] {
//        val MMap = mutable.Map[Tuple2[String, String], Int]()
//
//        /**
//         *
//         * @param key      每一个输入的key
//         * @param context  上下文
//         * @param elements 输入相同的key的元素的集合
//         * @param out
//         */
//        override def process(key: (String, String),
//                             context: Context,
//                             elements: Iterable[((String, String), Int)],
//                             out: Collector[((String, String), Int)]): Unit = {
//          for (element <- elements) {
//            val count = MMap.getOrElse(element._1, 0)
//            MMap(element._1) = count + 1
//          }
//          val sortedMap = MMap.toSeq.sortWith(_._2 > _._2)
//
//          // 所有的处理结束了之后进行数据的发送
//          for (ele <- sortedMap) {
//            out.collect(ele)
//          }
//        }
//      })

//    value.print()
    env.execute("Order Binlog")
  }
}
