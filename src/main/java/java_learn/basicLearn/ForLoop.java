package java_learn.basicLearn;

public class ForLoop {

    public static void main(String[] args) {

        int[] arr1=new int[]{1,23,4};

        for(int i=0;i<arr1.length;i++){
            System.out.println(arr1[i]);
        }

        for(int i: arr1){
            System.out.println(i);
        }

    }

}
