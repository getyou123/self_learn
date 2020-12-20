package big_data_self_learn.flinkLearn;

import org.apache.flink.api.common.functions.Partitioner;

/**
 * 自定义分区规则按照数据的奇数或者偶数进行数据分区
 */
public class MyPartition implements Partitioner<Long> {


    /**
     *
     * @param aLong 数据
     * @param i 总分区数
     * @return
     */
    @Override
    public int partition(Long aLong, int i) {
        return 0;
    }
}
