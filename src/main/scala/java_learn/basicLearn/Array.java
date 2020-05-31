package java_learn.basicLearn;

import java.util.ArrayList;
import java.util.Arrays;
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


    private static void printArray(String message, int array[]) {
        System.out.println(message
                + ": [length: " + array.length + "]");
        for (int i = 0; i < array.length; i++) {
            if(i != 0){
                System.out.print(", ");
            }
            System.out.print(array[i]);
        }
        System.out.println();
    }
    public static void main(String[] args) {
        int[] arr1;//声明了数组变量
        arr1=new int[2];//指向,定长数组，通过下标来访问
        int[] arr2=new int[]{1,2,3};//内容初始化的，不用指定大小了




        //arraylist
        {
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
        }

        //实现冒泡排序
        {
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
        }

        //排序和自定义排序
        {
            int array[] = { 2, 5, -2, 6, -3, 8, 0, -7, -9, 4 };
            Arrays.sort(array);
            printArray("数组排序结果为", array);
            int index = Arrays.binarySearch(array, 2);
            System.out.println("元素 2  在第 " + index + " 个位置");

            Integer[] arrInteger =new Integer[]{-1,4,3,4,5};
            Arrays.sort(arrInteger, new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    if(o2==o1)return 0;
                    if(o2>o1)return -1;
                    else return 1;
                }
            });
            for (int i = 0; i < arrInteger.length; i++) {
                System.out.print(arrInteger[i]+" ");
            }
            System.out.println();
        }

        //二维数组
        {
            int a2[ ][ ] = new int[2][ ];
            a2[0] = new int[3];
            a2[1] = new int[5];

            int intArray[ ][ ]={{1,2},{2,3},{3,4,5}};
        }

        //数组填充
        {
            int [] arrInt=new int[100];
            Arrays.fill(arrInt,-1);
            for (int i = 0; i < arrInt.length; i++) {
                System.out.print(arrInt[i]+" ");
            }
            System.out.println();

            //指定开始的位置和结束的位置
            Arrays.fill(arrInt, 3, 6, 50);
            for (int i = 0; i < arrInt.length; i++) {
                System.out.print(arrInt[i]+" ");
            }
            System.out.println();
        }
        //数组的扩容
        {
            String[] names = new String[] { "A", "B", "C" };
            String[] extended = new String[5];
            extended[3] = "D";
            extended[4] = "E";
            System.arraycopy(names, 0, extended, 0, names.length);
            for (String str : extended){
                System.out.println(str);
            }
        }




    }
}
