package java_learn;

import java.util.ArrayList;
import java.util.Comparator;

public class Array {

    //数组作为参数
    public int maxOfArr(int[] arr){
        return 0;
    }

    //返回数组,只有值传递且是引用的值传递
    public int[] transformArr(int[] arr){
        for (int i=0;i<arr.length;i++){
            arr[i]++;
        }
        return arr;
    }

    public static void  swap(int a,int b ){
        int tmp =a;
        a=b;
        b=tmp;
    }

    public static void bubbleSort(int [] arr){//值传递加上引用
        for (int i = 0; i < arr.length; i++) {
            for (int j = i; j < arr.length-1; j++) {
                if(arr[j]>arr[j+1]){
                    int tmp =arr[j];
                    arr[j]=arr[j+1];
                    arr[j+1]=tmp;
                }
            }
        }
    }

    public static void main(String[] args) {
        int[] arr1;//声明了数组变量
        arr1=new int[2];//指向,定长数组，通过下标来访问
        int[] arr2=new int[]{1,2,3};//内容初始化的，不用指定大小了




        //arraylist
        ArrayList arrayList = new ArrayList();
        arrayList.add(1);//增
        arrayList.add("fsadfas");
        ArrayList arrayList1=new ArrayList();
        arrayList1.add(23);
        arrayList.add(arrayList1);//增

        for(int i=0;i<arrayList.size();i++){
            System.out.println(arrayList.get(i));
        }

        arrayList.addAll(arrayList1);

        for (int i = 0; i < arrayList.size(); i++) {
            System.out.println(arrayList.get(i
             ));
        }
        arrayList.remove(0);//减

        int a=1;
        int b =2;
        System.out.println(a);
        System.out.println(b);

        swap(a,b);//很明显的值传递

        System.out.println(a);
        System.out.println(b);

        System.out.println("冒泡排序--");
        int[] arrWithoutSort=new int[]{1,4,2,6,4,7};
        for (int i = 0; i < arrWithoutSort.length; i++) {
            System.out.println(arrWithoutSort[i]);
        }
        bubbleSort(arrWithoutSort);
        System.out.println("排序完");
        for (int i = 0; i < arrWithoutSort.length; i++) {
            System.out.println(arrWithoutSort[i]);
        }

        //二维数组
        int a2[ ][ ] = new int[2][ ];
        a2[0] = new int[3];
        a2[1] = new int[5];

        int intArray[ ][ ]={{1,2},{2,3},{3,4,5}};



    }
}
