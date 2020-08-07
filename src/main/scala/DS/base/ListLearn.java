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


import java.util.*;


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

    public static String[] union(String[] arr1, String[] arr2) {
        Set<String> set = new HashSet<String>();

        for (String str : arr1) {
            set.add(str);
        }

        for (String str : arr2) {
            set.add(str);
        }

        String[] result = {  };

        return set.toArray(result);
    }
    public static void main(String[] args) {

        //List接口有两个实现，ArrayList和LinkedList
        //底层实现不同，一个是变长数组，一个是双链表
        {
            //ArrayListLearn

            // 默认构造函数
            ArrayList<Integer> Arr = new ArrayList<Integer>();
            // capacity是ArrayList的默认容量大小。当由于增加数据导致容量不足时，容量会添加上一次容量大小的一半。
            ArrayList arrayList1 = new ArrayList<Integer>(12);

            //增 默认是是在末尾增加
            Arr.add(1);
            Arr.add(1,4);//[1,4]
            //打印
            System.out.println("first");
            for (int i = 0; i < Arr.size(); i++) {
                System.out.print(Arr.get(i)+" ");
            }

            System.out.println("");
            //删 [1]注意下标从0开始
            System.out.println(Arr.remove(0));//[1]注意下标从0开始，返回值是index指向的值
            System.out.println("after deletion");
            for (int i = 0; i < Arr.size(); i++) {
                System.out.println(Arr.get(i)+" ");
            }
//            for(Integer I: Arr){//使用这个语法前提是声明时候就指明了Integer
//                System.out.println(I);
//            }

            //改 [3]
            Arr.set(0,3);
            System.out.println("first change to 3");
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

            //iterator进行遍历,前提是实现了Iterator接口
            Iterator<Integer> itr=Arr.iterator();
            while (itr.hasNext()){
                Integer N=itr.next();
                System.out.println(N);
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

        //数据的差集
        {
            ArrayList<Integer> integers = new ArrayList<Integer>();
            integers.add(2);
            integers.add(3);
            ArrayList<Integer> integers1 = new ArrayList<Integer>();
            integers1.add(3);
            integers1.add(4);

            boolean b = integers.removeAll(integers1);//此时第一个数组就是差集
        }
        //数据的交集
        {
            ArrayList<Integer> integers = new ArrayList<Integer>();
            integers.add(2);
            integers.add(3);
            ArrayList<Integer> integers1 = new ArrayList<Integer>();
            integers1.add(3);
            integers1.add(4);
            integers.retainAll(integers1);//此时第一个就是交集
        }
        //数据的并集
        {
            String[] arr1 = { "1", "2", "3" };
            String[] arr2 = { "4", "5", "6" };
            String[] result_union = union(arr1, arr2);
            System.out.println("并集的结果如下：");

            for (String str : result_union) {
                System.out.println(str);
            }
        }

        {
            //LinkedList_Learn

            //声明和构造
            LinkedList<Integer> linkedList = new LinkedList<Integer>();

            System.out.println("linked list learn");
            //增加
            linkedList.add(1);
            linkedList.add(0,4);
            linkedList.addFirst(1);//头插
            linkedList.addLast(3);//尾插
            for (int i = 0; i <linkedList.size() ; i++) {
                System.out.println(linkedList.get(i));
            }

            //删除
            linkedList.removeFirst();//头删除
            linkedList.removeLast();//尾删除
            linkedList.remove(0);//指定index

            //改
            linkedList.set(0,23);

            //查找
            System.out.println(linkedList.getLast());
            System.out.println(linkedList.getFirst());
            System.out.println(linkedList.get(0));

            //作为队列使用
            System.out.println("work as queue");
            linkedList.clear();
            linkedList.addFirst(123);
            linkedList.addFirst(234);
            linkedList.addFirst(456);
            while (!linkedList.isEmpty()){
                System.out.println(linkedList.getLast());
                linkedList.removeLast();
            }

            //作为栈使用
            System.out.println("work as stack");
            linkedList.clear();
            linkedList.addFirst(123);
            linkedList.addFirst(234);
            linkedList.addFirst(456);
            while (!linkedList.isEmpty()){
                System.out.println(linkedList.getFirst());
                linkedList.removeFirst();
            }



        }

    }
}
