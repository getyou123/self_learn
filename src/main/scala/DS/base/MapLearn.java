package DS.base;

import org.apache.commons.collections.map.HashedMap;

import java.util.*;

//map的典型实现是hashmap

public class MapLearn {
    public static void main(String[] args) {

        Map<Character,Integer> M=new HashMap<Character,Integer>();//接口和实现的对象

//        Map<char,int> M1=new HashedMap();kv不能是基础类型

        Character C=new Character('a');

        M.put(C,34);
        if(M.containsKey(C))System.out.println(M.get(C));


        //遍历
        Set<Character> characters = M.keySet();
        for (Character key:characters){
            System.out.println(M.get(key));
        }

        Set<Map.Entry<Character, Integer>> entries = M.entrySet();
        for (Map.Entry<Character, Integer> en:entries){
            System.out.println("key:"+en.getKey()+" value:"+en.getValue());
        }
    }
}
