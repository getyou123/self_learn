package DS.base;

import java.util.Stack;

public class StackLearn {
    public static void main(String[] args) {
        Stack<Integer> S=new Stack<Integer>();
        for(int i=0;i<10;i++){
            S.push(i);
        }
        while (!S.empty()){
            int top=S.peek();
            System.out.println(top);
            S.pop();
        }
    }
}
