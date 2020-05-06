package DS.base;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class QueueLearn {
    class Solution {
        public double findMedianSortedArrays(int[] nums1, int[] nums2) {
            double res=0;

            int i=0;
            int j=0;
            boolean iF=false;
            int leftCnt=(nums1.length+nums2.length)/2;
            if((nums1.length+nums2.length)%2==1){//奇数个

            }

            while(leftCnt>=0){
                if (nums1[(nums1.length-1-i)/2]<nums2[(nums2.length-1-j)/2]){
                    i=(nums1.length-1-i)/2;
                }else{
                    j=(nums2.length-1-j)/2;
                }
                leftCnt=leftCnt-i-j;
            }

            return res;

        }
    }
    public static void main(String[] args) {
//        Queue<Integer> integers = new Queue<Integer>();

        String s="";
        int i=0;
        int j=0;
        Set<Character> set=new HashSet<Character>();
        int maxL=0;
        while (i<s.length()){
            if(set.contains(new Character(s.charAt(i)))){
                set.remove(new Character(s.charAt(j)));
                j++;
            }
            else {
                set.add(new Character(s.charAt(i)));
                if(maxL<set.size()){
                    maxL=set.size();
                }
            }
            i++;
        }
    }
}
