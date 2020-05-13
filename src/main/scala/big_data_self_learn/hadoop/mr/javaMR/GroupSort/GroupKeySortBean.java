package big_data_self_learn.hadoop.mr.javaMR.GroupSort;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

//数据的输入格式：
//order_id\torder_count\torder_aomou
public class GroupKeySortBean implements WritableComparable<GroupKeySortBean> {


    private Long order_id;
    private Long order_count;
    private Long order_amou;


    public GroupKeySortBean(){}
    public GroupKeySortBean(Long id,Long count,Long amou){
        this.order_id=id;
        this.order_count=count;
        this.order_amou=amou;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeLong(order_id);
        dataOutput.writeLong(order_amou);
        dataOutput.writeLong(order_count);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.order_id=dataInput.readLong();
        this.order_amou=dataInput.readLong();
        this.order_count=dataInput.readLong();
    }

    @Override
    public int compareTo(GroupKeySortBean o) {//首先按照orderid升序排列，再按照价格降序排列
        if(this.order_id<o.order_id)return -1;
        else if(this.order_id==o.order_id && this.order_amou<order_amou) return -1;
        else return 1;
    }

    @Override
    public String toString() {
        return this.order_id+"\t"+this.order_count+"\t"+this.order_amou;
    }

    public Long getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Long order_id) {
        this.order_id = order_id;
    }

    public Long getOrder_count() {
        return order_count;
    }

    public void setOrder_count(Long order_count) {
        this.order_count = order_count;
    }

    public Long getOrder_amou() {
        return order_amou;
    }

    public void setOrder_amou(Long order_amou) {
        this.order_amou = order_amou;
    }
}
