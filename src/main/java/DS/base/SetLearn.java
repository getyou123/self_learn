package DS.base;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

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

//set 接口 两种实现一种那是hashset 一种是treeset
public class SetLearn {
    public static void main(String[] args) {

        HashSet<Integer> hashSet = new HashSet<Integer>();
        //增 【1】
        hashSet.add(1);

        //删
        if (hashSet.contains(1)) {
            hashSet.remove(1);
        }else{
            System.out.println("no contains");
        }

        //查
        if(hashSet.contains(1)){
            System.out.println("T");
        }else{
            System.out.println("F");
        }

        //清空
        hashSet.clear();

        hashSet.add(1);
        hashSet.add(10);
        hashSet.add(11);
        //遍历
        for (Object o:hashSet) {
            int i=(Integer)o;
            System.out.println(i);
        }

        Iterator<Integer> iterator = hashSet.iterator();
        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }
        //大小 3
        System.out.println(hashSet.size());

        TreeSet<Character> treeSet = new TreeSet<Character>();//自然排序或者自定义排序
        //同样是增删改查 遍历2种
        //自定义时候是按照类的CompareTo方法，返回-1或者1 类似实现按照 ListLearn中的sturct1的类的实现
        //是通过Comparable的接口来实现的

        HashSet<Character> characters = new HashSet<Character>();
        String s=new String("fasdf");
        int i=0;
        int j=0;
        while(i<s.length()||j<s.length()){

        }
    }
}
