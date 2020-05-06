package java_learn;

public class HelloWorld {

    public int count=1;// 实例变量
    public int n; // 类变量
    public static int out=100;// 类变量，整个类只有这个一个实例
    public static void main(String[] args) {
        int i = 2;//局部变量
        System.out.println("hello world");
    }
}
/*
java 特点没有运算符重载，没有多继承，不用指针，使用引用。内存自动回收。
java的类的命名要求首字母全都大写，单词的首字母也是，方法名是首字母小写，每个单词首字母大写
java源程序.java文件到运行过程，编译成.class字节码，然后解释器运行。
引用的行为类似C++指针
 */