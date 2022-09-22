package java_learn.basicLearn;

public class Interface2 implements Interface, Interface1 {//必须全部实现或者声明为abstract
    @Override
    public void sound() {
        System.out.println("aa");
    }

    @Override
    public void run() {
        System.out.println("run");
    }
}
