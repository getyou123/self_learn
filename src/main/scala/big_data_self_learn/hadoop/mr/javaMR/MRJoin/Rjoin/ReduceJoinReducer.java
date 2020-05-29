package big_data_self_learn.hadoop.mr.javaMR.MRJoin.Rjoin;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class ReduceJoinReducer extends Reducer<Text, ReduceJoinTableBean, ReduceJoinTableBean, NullWritable> {

    @Override
    protected void reduce(Text key, Iterable<ReduceJoinTableBean> values, Context context)	throws IOException, InterruptedException {

        // 1准备存储订单的集合
        ArrayList<ReduceJoinTableBean> orderBeans = new ArrayList<ReduceJoinTableBean>();

// 2 准备bean对象
        ReduceJoinTableBean pdBean = new ReduceJoinTableBean();

        for (ReduceJoinTableBean bean : values) {

            if ("order".equals(bean.getFlag())) {// 订单表

                // 拷贝传递过来的每条订单数据到集合中
                ReduceJoinTableBean orderBean = new ReduceJoinTableBean();

                try {
                    BeanUtils.copyProperties(orderBean, bean);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                orderBeans.add(orderBean);
            } else {// 产品表

                try {
                    // 拷贝传递过来的产品表到内存中
                    BeanUtils.copyProperties(pdBean, bean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // 3 表的拼接
        for(ReduceJoinTableBean bean:orderBeans){

            bean.setPname (pdBean.getPname());

            // 4 数据写出去
            context.write(bean, NullWritable.get());
        }
    }
}
