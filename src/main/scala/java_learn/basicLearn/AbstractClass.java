package java_learn.basicLearn;

public abstract class AbstractClass {// abstract class 符合模板方法的设计模式，画好蓝图子类实现
    //可以有具体的变量
    protected int age;
    protected String name;

    //定义抽象方法
    public abstract void sound();
}
