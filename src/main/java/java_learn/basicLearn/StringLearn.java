package java_learn.basicLearn;

import java.util.Locale;
import java.util.StringTokenizer;

public class StringLearn {

    //删除某个位置的char
    public static String removeCharAt(String s, int pos) {
        return s.substring(0, pos) + s.substring(pos + 1);
    }

    public static void main(String[] args) {
        //创建字符串
        String s1="for a test";
        System.out.println(s1);
        String s = new String("jiushishuo");
        System.out.println(s);
        //连接
        String concat = s1.concat(s);
        System.out.println(concat);
        System.out.println(s+s1);
        //获取子串
        System.out.println("获取子串是不包含左闭右开的"+s1.substring(1,2));
        //查找位置
        System.out.println(s1.indexOf('o'));
        System.out.println(s1.lastIndexOf("a"));
        System.out.println(s1.indexOf("a test",2));//从指定的位置开始找
        System.out.printf("一个串为 %s，一个数为 %d ","hui",34);
        //长度
        System.out.println(s1.length());
        //打印
        for(int i=0;i<s1.length();i++){
            System.out.println(s1.charAt(i));
        }
        //一些关于内存的位置的
        String s2 = "a test";//字面量常量，存在堆的常量池，没有时候创建
        String s3 = "a test";//有时直接使用，==成立

        String s4 = new String(s3);
        String s5 = new String(s3);//先创建常量，又创建新的对象

        System.out.println(s2.equals(s3));//T
        System.out.println(s2==s3);//T
        System.out.println(s3==s4);//F
        System.out.println(s3.equals(s4));//T ==比较的是地址 方法比较的是内容
        System.out.println(s5 == s4);//F 不同的对象引用

        StringBuffer stringBuffer1 = new StringBuffer();
        stringBuffer1.append("1");
        System.out.println(stringBuffer1.toString());

        //多数情况使用stringbuilder线程安全使用stringbuffer

        StringBuilder stringBuilder = new StringBuilder("");
        stringBuilder.append("1");
        System.out.println(stringBuilder.toString());

        //字符的或者字符串的替换
        {
            String str = "Hello World";
            System.out.println(str.replace('H', 'W'));
            System.out.println(str.replaceFirst("He", "Wa"));
            System.out.println(str.replaceAll("He", "Ha"));
        }
        //字符串的翻转，先转化成StringBuffer
        {
            String string="runoob";
            String reverse = new StringBuffer(string).reverse().toString();
            System.out.println("字符串反转前:"+string);
            System.out.println("字符串反转后:"+reverse);

        }
        //字符串查找
        {
            String strOrig = "Google Runoob Taobao";
            int intIndex = strOrig.indexOf("Runoob");
            if(intIndex == - 1){//返回值为-1
                System.out.println("没有找到字符串 Runoob");
            }else{
                System.out.println("Runoob 字符串位置 " + intIndex);
            }
        }
        //字符串的分割1
        {
            String str = "www-runoob-com";
            String[] temp;
            String delimeter = "-";  // 指定分割字符，有些分隔符需要进行转义
            temp = str.split(delimeter); // 分割字符串
            // 普通 for 循环
            for(int i =0; i < temp.length ; i++){
                System.out.println(temp[i]);
                System.out.println("");
            }
        }
        //字符串分割2
        {
            String str = "This is String , split by StringTokenizer, created by runoob";
            StringTokenizer st = new StringTokenizer(str);

            System.out.println("----- 通过空格分隔 ------");
            while (st.hasMoreElements()) {
                System.out.println(st.nextElement());
            }

            System.out.println("----- 通过逗号分隔 ------");
            StringTokenizer st2 = new StringTokenizer(str, ",");

            while (st2.hasMoreElements()) {
                System.out.println(st2.nextElement());
            }
        }
        //字符串的格式化
        {
            double e = Math.E;
            System.out.format("%f%n", e);
            System.out.format(Locale.CHINA  , "%-10.4f%n%n", e);  //指定本地为中国（CHINA）
        }
    }
}
