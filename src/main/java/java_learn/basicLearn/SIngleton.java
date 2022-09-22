package java_learn.basicLearn;
//这个类只被实例化一个，以后用都只用这一个类对象
public class SIngleton {
    private SIngleton(){
    }
//    private static SIngleton singleton=new SIngleton();//整个类就一个.hangurymode.pbject is there and you get it
//   public static SIngleton getInstance(){// must be static
//        return singleton;
//    }


    private static SIngleton singleton;// lazymode create after first call
    public static SIngleton getInstance(){// must be static
        if(singleton==null){
            singleton=new SIngleton();
        }
        return singleton;
    }
    public static void main(String[] args) {
    }
}
