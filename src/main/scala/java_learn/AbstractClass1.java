package java_learn;

public class AbstractClass1 extends AbstractClass {
    @Override
    public void sound() {//必须要实现抽象方法
        System.out.println("ao ao ");
    }

    public AbstractClass1(){
        this.name="1";
        this.age=3;
    }

    public static void main(String[] args) {
        new AbstractClass1().sound();
    }
}
