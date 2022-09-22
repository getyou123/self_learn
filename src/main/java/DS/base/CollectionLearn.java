package DS.base;

//这些类均在java.util包中：
//        Collection
//        ├List
//        │├LinkedList
//        │├ArrayList
//        │└Vector
//        │　└Stack
//        └Set
//        Map
//        ├Hashtable
//        ├HashMap
//        └WeakHashMap


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Stack;


class struct2 implements Comparator<struct2>{
    int age;
    String name;


    public  struct2(){}//需要实现无参数的构造函数

    public struct2(int age,String name){
        this.age=age;
        this.name=name;
    }

    @Override
    public int compare(struct2 o1, struct2 o2) {
        if(o1.age>o2.age)return -1;//-1为主序
        else if(o2.age>o1.age) return 1;
        else return 0;
    }

    @Override
    public String toString() {
        return this.age+" "+this.name ;
    }
}

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
public class CollectionLearn {//Collection是最基本的集合接口，一个Collection代表一组Object
    public static void main(String[] args) {
        ArrayList<Integer> integers = new ArrayList<Integer>();
        integers.add(1);
        integers.add(3424);
        integers.add(34);

        System.out.println(integers);

        //反序
        Collections.reverse(integers);
        System.out.println(integers);

        //随机排序
        Collections.shuffle(integers);
        System.out.println(integers);

        //排序
        Collections.sort(integers);
        System.out.println(integers);
        Collections.sort(integers, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                if(o1.equals(02))return 0;
                return (int)o1<(int)o2?1:-1;
            }
        });
        System.out.println("paixu");
        System.out.println(integers);

        ArrayList<struct2> struct2s = new ArrayList<struct2>();
        struct2s.add(new struct2(1,"kexin"));
        struct2s.add(new struct2(3,"aini"));
        struct2s.add(new struct2(2,"hgw"));
        Collections.sort(struct2s,new struct2());
        for (int i = 0; i < struct2s.size(); i++) {
            System.out.println(struct2s.get(i));
        }

        //替换所有
        Collections.replaceAll(integers,2,3);

        //元素的次数
        System.out.println(Collections.frequency(integers, 3));

        //最大最小值
        System.out.println(Collections.max(integers));
        System.out.println(Collections.min(integers));

    }
}
