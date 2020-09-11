package java_learn.JVMlearn;

import java.util.ArrayList;
import java.util.List;

/**
 * 在堆中模拟产生大量对象导致heap oom
 */

public class OOM {
    static class test{
     int i;
    }

    public static void main(String[] args) {
        List<test> list=new ArrayList<test>();
        while(true){
            list.add(new test());
        }
    }

}
