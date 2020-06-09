//给定一个含有 n 个正整数的数组和一个正整数 s ，找出该数组中满足其和 ≥ s 的长度最小的连续子数组，并返回其长度。如果不存在符合条件的连续子数组，返回
// 0。 
//
// 示例: 
//
// 输入: s = 7, nums = [2,3,1,2,4,3]
//输出: 2
//解释: 子数组 [4,3] 是该条件下的长度最小的连续子数组。
// 
//
// 进阶: 
//
// 如果你已经完成了O(n) 时间复杂度的解法, 请尝试 O(n log n) 时间复杂度的解法。 
// Related Topics 数组 双指针 二分查找


//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public int minSubArrayLen(int s, int[] nums) {
        //状态初始化
        //移动右节点 更新状态
        //移动左节点 更新状态

        //初始化
        int R=-1;
        int L=0;
        int minLength=nums.length+1;
        int sum=0;
        while(R<nums.length) {
            //更新右侧状态，需要注意的是右侧的窗口应该总是能做到向右更新一下，不然会出现死循环
            while (R < nums.length) {
                R+=1;
                if (R == nums.length) break;//移动后的可能是超过
                sum += nums[R];//更新状态
                if(sum>=s){
                    minLength=(R-L+1<minLength)?R-L+1:minLength;
//                    System.out.println(L+" "+R+" "+sum);
                    break;
                }
            }
            if (R == nums.length) break;//移动后的可能是超过
            while (L < R){
                sum-=nums[L];//总要进行移动的
                L+=1;
                if (sum >= s){
                    minLength=(R-L+1<minLength)?R-L+1:minLength;
//                     System.out.println(L+" "+R+" "+sum);
                }
                else break;
            }
        }

        return minLength==nums.length+1?0:minLength;

    }
}
//leetcode submit region end(Prohibit modification and deletion)
