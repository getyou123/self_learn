package big_data_self_learn.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.net.URISyntaxException;

public class HbaseFilterList {
    public static void main(String[] args) throws URISyntaxException {
        Configuration config = HBaseConfiguration.create();
        //添加必要的hive-site.xml和core-site.xml
        config.addResource(new Path(ClassLoader.getSystemResource("hbase-site.xml").toURI()));
        config.addResource(new Path(ClassLoader.getSystemResource("core-site.xml").toURI()));

        try {
            Connection connection = ConnectionFactory.createConnection(config);
            Table table = connection.getTable(TableName.valueOf("test"));//表的名字
            Scan scan = new Scan();

            //创建过滤器列表
            FilterList filterlist=new FilterList(FilterList.Operator.MUST_PASS_ALL);

            //只有列族mycf存在的记录才放入到结果row中
            FamilyFilter familyFilter = new FamilyFilter(CompareFilter.CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes("mycf")));
            filterlist.addFilter(familyFilter);

            //只有含有列teacher的才会被放入结果row中
            QualifierFilter qualifierFilter = new QualifierFilter(CompareFilter.CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes("teacher")));
            filterlist.addFilter(qualifierFilter);

            //只有值中包含Wang的记录才会被放入结果集row，这里没有使用单列过滤器
            ValueFilter valueFilter = new ValueFilter(CompareFilter.CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes("Wang")));
            filterlist.addFilter(valueFilter);

            //构造完成所有的过滤列表之后，添加到scan操作中
            scan.setFilter(filterlist);

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
