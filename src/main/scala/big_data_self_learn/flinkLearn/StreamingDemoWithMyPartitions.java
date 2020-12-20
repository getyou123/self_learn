package big_data_self_learn.flinkLearn;

import big_data_self_learn.flinkLearn.streamApi.source.MyNoParalleSource;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import scala.Tuple1;

public class StreamingDemoWithMyPartitions {

    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);
        DataStreamSource<Long> text = env.addSource(new MyNoParalleSource());

        //数据类型的转化
        SingleOutputStreamOperator<Tuple1<Long>> tupluData = text.map(new MapFunction<Long, Tuple1<Long>>() {
            @Override
            public Tuple1 map(Long aLong) throws Exception {
                return new Tuple1<Long>(aLong);
            }
        });

        //分区之后的数据：
        DataStream<Tuple1<Long>> partitionData = tupluData.partitionCustom(new MyPartition(), 0);



    }
}
