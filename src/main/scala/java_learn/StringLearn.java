package java_learn;

public class StringLearn {

    public static void main(String[] args) {
        String s1="for a test";
        System.out.println(s1);

        String s = new String("jiushishuo");
        System.out.println(s);

        String concat = s1.concat(s);
        System.out.println(concat);

        System.out.println(s+s1);

        System.out.println(s1.substring(0,2));

        System.out.println(s1.indexOf('o'));

        System.out.println(s1.indexOf("a"));

        System.out.println(s1.indexOf("a test"));

        System.out.printf("一个串为 %s，一个数为 %d ","hui",34);

        System.out.println(s1.length());

        for(int i=0;i<s1.length();i++){
            System.out.println(s1.charAt(i));
        }

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

        String tmp="";
        System.out.println(tmp.length());



    }
}
