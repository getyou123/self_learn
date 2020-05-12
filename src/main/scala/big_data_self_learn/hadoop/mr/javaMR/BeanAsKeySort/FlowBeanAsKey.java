package big_data_self_learn.hadoop.mr.javaMR.BeanAsKeySort;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

//需要自己实现这个接口
public class FlowBeanAsKey implements WritableComparable<FlowBeanAsKey> {

    private long upFlow;//上行流量
    private long downFlow;//下行流量
    private long allFlow;//总流量

    //必须要有这个无参数的构造器
    public FlowBeanAsKey(){super();}

    public FlowBeanAsKey(long i,long j){
        this.upFlow=i;
        this.downFlow=j;
        this.allFlow=this.upFlow+this.downFlow;
    }

    //实现排序时候的比较方法，这个是作为的key的方式
    @Override
    public int compareTo(FlowBeanAsKey o) {
        FlowBeanAsKey tmp=(FlowBeanAsKey)o;
        if(this.allFlow>tmp.allFlow) return -1;
        else if(this.allFlow<tmp.allFlow) return 1;
        else return 0;
    }

    //系列化的方法
    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeLong(upFlow);
        dataOutput.writeLong(downFlow);
        dataOutput.writeLong(allFlow);
    }

    //反序列化的时候需要按照序列化顺序
    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.upFlow=dataInput.readLong();
        this.downFlow=dataInput.readLong();
        this.allFlow=dataInput.readLong();
    }

    @Override
    public String toString() {
        return this.allFlow+"\t"+this.upFlow+"\t"+this.downFlow;
    }

    public long getUpFlow() {
        return upFlow;
    }

    public void setUpFlow(long upFlow) {
        this.upFlow = upFlow;
    }

    public long getDownFlow() {
        return downFlow;
    }

    public void setDownFlow(long downFlow) {
        this.downFlow = downFlow;
    }

    public long getAllFlow() {
        return allFlow;
    }

    public void setAllFlow(long allFlow) {
        this.allFlow = allFlow;
    }
}

