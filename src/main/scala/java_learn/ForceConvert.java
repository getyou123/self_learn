package java_learn;


class base{
    int i;
    int j;
    public base(int i,int j){
        this.i=i;
        this.j=j;
    }
}

class child extends base{
    int m;
    int n;
    public child(int m,int n){
        super(m,n);
        this.m=m;
        this.n=n;
    }
}

public class ForceConvert {
    public static void main(String[] args) {
        child c= null;
        try {
            base b=new base(1,2);
            c = new child(2,3);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(c instanceof base){
            System.out.println("c 为base实例");//T
            base c2b=(base) c;//强制类型转换
        }else {
            System.out.println("c 不是base实例");
        }
        if(c instanceof child){
            System.out.println("c 为child 实例");//T
        }else {
            System.out.println("c 不是 child 实例");
        }

        System.out.println(c.toString());
    }
}
