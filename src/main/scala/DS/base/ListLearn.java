package DS.base;


//继承collection的接口

//collection主要方法:
//
//        boolean add(Object o)添加对象到集合
//        boolean remove(Object o)删除指定的对象
//        int size()返回当前集合中元素的数量
//        boolean contains(Object o)查找集合中是否有指定的对象
//        boolean isEmpty()判断集合是否为空
//        Iterator iterator()返回一个迭代器
//        boolean containsAll(Collection c)查找集合中是否有集合c中的元素
//        boolean addAll(Collection c)将集合c中所有的元素添加给该集合
//        void clear()删除集合中所有元素
//        void removeAll(Collection c)从集合中删除c集合中也有的元素
//        void retainAll(Collection c)从集合中删除集合c中不包含的元素 差集


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


class struct1 implements Comparable{
    int i;
    int j;

    public struct1(int i1,int j1){
        this.i=i1;
        this.j=j1;
    }

    @Override
    public int compareTo(Object o) {//返回值是固定的1或者-1等于时候返回0
        struct1 other = (struct1)o;
        if(this.i>other.i) return 1;
        else return -1;
    }

    @Override
    public String toString() {
        return ""+this.i+this.j;
    }
}

//在实现上有linklist ArrlisyList vector
public class ListLearn {
    public static void main(String[] args) {
        //1.ArrayList
        ArrayList<Integer> Arr=new ArrayList<Integer>();

        //增 [1,4]
        Arr.add(1);
        Arr.add(1,4);
        for (int i = 0; i < Arr.size(); i++) {
            System.out.print(Arr.get(i));
        }
        System.out.println("");

        //删 [1]注意下标从0开始
        Arr.remove(1);//按照索引删除
        for(int I: Arr){
            System.out.println(I);
        }

        //改 [3]
        Arr.set(0,3);
        for(int I: Arr){
            System.out.println(I);
        }

        //查找
        Arr.add(23);
        Arr.add(27);
        Arr.add(25);
        if (Arr.contains(25)) {
            System.out.println(Arr.indexOf(25));
        }else{
            System.out.println("no such object");
        }

        //排序 [3,23,27,25]
        //自定义Comparator对象，自定义排序

        //进行排序
        Collections.sort(Arr, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                if((int)o1==(int)o2) return 0;
                return (int)o1>(int)o2?1:-1;
            }
        });
        Arr.set(0,3);
        for(int I: Arr){
            System.out.print(I+" ");
        }

        System.out.println();
        ArrayList<struct1> ArrStruct=new ArrayList<struct1>();
        ArrStruct.add(new struct1(1,2));
        ArrStruct.add(new struct1(3,4));
        ArrStruct.add(new struct1(2,5));
        ArrStruct.sort(new Comparator<struct1>() {
            @Override
            public int compare(struct1 o1, struct1 o2) {
                return o1.i >o2.i ?1:-1;
            }
        });
        for(struct1 I: ArrStruct){
            System.out.print(I+"   ");
        }





    }
}
