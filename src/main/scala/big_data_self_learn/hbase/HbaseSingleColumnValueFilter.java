/*
单列值过滤器，作用于单个列上，但是危险性在于
如果这个row中没有要比较的cf下的这个col的话
也被留下来作为filter之后的数据集合，所以使用单列值比较器的话，必须要保证所有的row都有这个cf:col
 */
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

public class HbaseSingleColumnValueFilter {
    public static void main(String[] args) throws URISyntaxException {
        Configuration config = HBaseConfiguration.create();
        //添加必要的hive-site.xml和core-site.xml
        config.addResource(new Path(ClassLoader.getSystemResource("hbase-site.xml").toURI()));
        config.addResource(new Path(ClassLoader.getSystemResource("core-site.xml").toURI()));

        try {
            Connection connection = ConnectionFactory.createConnection(config);
            Table table = connection.getTable(TableName.valueOf("test"));//表的名字
            Scan scan = new Scan();

            //过滤出来的是行注意获取到的是行，因为是从row进行访问的
            Filter filter=new SingleColumnValueFilter(Bytes.toBytes("mycf"),
                    Bytes.toBytes("name"),
                    CompareFilter.CompareOp.EQUAL,
                    new SubstringComparator("Wang"));//子串中有Wang

            scan.setFilter(filter);
            //过滤得到的row之后，取出的指定的列的结果为
//            bilyWangpaul
//            angdyWang
//            kateWang

            ResultScanner rs = table.getScanner(scan);//这里我们取出来的是所有的row
            //下面对于所有的row取出来相应的行的数据，取出的只是nam这一列数据
            for (Result r : rs) {
                String name = Bytes.toString(r.getValue(Bytes.toBytes("mycf"), Bytes.toBytes("name")));//cf名称和col名称
                System.out.println(name);
                System.out.println(r.getRow());
            }
            rs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
