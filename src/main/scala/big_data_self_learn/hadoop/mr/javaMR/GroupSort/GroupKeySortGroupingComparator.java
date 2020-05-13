package big_data_self_learn.hadoop.mr.javaMR.GroupSort;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class GroupKeySortGroupingComparator extends WritableComparator {

    protected GroupKeySortGroupingComparator(){//这个无参函数必须要实现
        super(GroupKeySortBean.class, true);
    }
    @Override
    public int compare(WritableComparable a, WritableComparable b) {
        GroupKeySortBean b1=(GroupKeySortBean)a;
        GroupKeySortBean b2=(GroupKeySortBean)b;

        if(b1.getOrder_id()<b2.getOrder_id()){
            return -1;
        }else if(b1.getOrder_id()>b2.getOrder_id()){
            return 1;
        }else return 0;//如何认为组内的相同呢
    }
}
