/*
 值过滤器，作用于所有的cf的所有的列上，不常用
 */
package big_data_self_learn.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.SubstringComparator;
import org.apache.hadoop.hbase.filter.ValueFilter;
import org.apache.hadoop.hbase.ipc.ConnectionId;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.net.URISyntaxException;

//rowkey | mycf:name
//row1   | bilyWangpaul
//row2   | sara
//row3   | chris
//row4   | HELEN
//row5   | angdyWang
//row6   | kateWang

public class HbaseValueFilterLearn {
    public static void main(String[] args) throws URISyntaxException {
        Configuration config = HBaseConfiguration.create();
        //添加必要的hive-site.xml和core-site.xml
        config.addResource(new Path(ClassLoader.getSystemResource("hbase-site.xml").toURI()));
        config.addResource(new Path(ClassLoader.getSystemResource("core-site.xml").toURI()));

        try {
            Connection connection = ConnectionFactory.createConnection(config);
            Table table = connection.getTable(TableName.valueOf("test"));//表的名字
            Scan scan = new Scan();

            //过滤出的rowkey的所有的列族中的列中有Wang的row，注意获取到的是行，因为是从row进行访问的
            ValueFilter filter = new ValueFilter(CompareFilter.CompareOp.EQUAL, new SubstringComparator("wang"));//其实实现的对于有wang的
            scan.setFilter(filter);
            //过滤row之后的取出的指定的列的结果为
//            bilyWangpaul
//            angdyWang
//            kateWang

            ResultScanner rs = table.getScanner(scan);//这里我们取出来的是所有的row
            //下面对于所有的row取出来相应的行的数据，取出的只是nam这一列数据
            for (Result r : rs) {
                String name = Bytes.toString(r.getValue(Bytes.toBytes("mycf"), Bytes.toBytes("name")));//cf名称和col名称
                System.out.println(name);
            }
            rs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
