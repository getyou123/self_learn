package java_learn;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

//没多继承通过这个方式来实现。一个父类多个接口
//有一个抽象方法必然为抽象类，但是抽象类不一定有抽象方法
//接口中所有方法都是抽象的，不用直接说明，接口的关键字也是抽象的，所有的都是蓝图，不实现
public class Class_1 extends java_learn.HelloWorld implements Writable, Serializable {

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public int getJ() {
        return j;
    }

    public void setJ(int j) {
        this.j = j;
    }

    private int i;//声明为私有,同一个包不能访问
    public int j;
    private final int ave;//一经产生不在变化
    public static int count=0;//属于整个类的

    public Class_1(int i, int j) {
        this.i = i;
        this.j = j;
        this.ave = i + 1;
        Class_1.count++;
    }

    public Class_1() {
        this.i = 0;
        this.j = 0;
        this.ave = i + 1;
        Class_1.count++;
    }

    @Override
    public String toString() {
//        this.ave++;//不能改变的
        return this.i+" "+this.j+" "+this.ave+" "+ Class_1.count;
    }

    public int add1(int i) {//只是值传递，但是由于是引用
        return i + 1;
    }

    public static void main(String[] args) {//静态的所有的对象共享，在main之前就生成

        Class_1 obj1 = new Class_1(1, 4);
        System.out.println(obj1);
        Class_1 obj_2=new Class_1(3,2);
        System.out.println(obj_2);
        System.out.println(obj_2.i);

        //同一个包中的这个语法错误
//        Class_1 class_1 = new Class_1(1, 2);
//        System.out.println(class_1.i);



        //匿名对象的使用：
        System.out.println(new Class_1(1, 2).add1(1));
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {

    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {

    }

}
