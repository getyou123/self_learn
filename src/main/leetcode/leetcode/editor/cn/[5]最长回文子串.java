//给定一个字符串 s，找到 s 中最长的回文子串。你可以假设 s 的最大长度为 1000。 
//
// 示例 1： 
//
// 输入: "babad"
//输出: "bab"
//注意: "aba" 也是一个有效答案。
// 
//
// 示例 2： 
//
// 输入: "cbbd"
//输出: "bb"
// 
// Related Topics 字符串 动态规划


//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public String longestPalindrome(String s) {
        int sLength=s.length();
        if(sLength==1)return s;
        //总觉得是按照空间换时间的方法，把以前的记录下来
        boolean a2[ ][ ] = new boolean[sLength][sLength];
        //根据如果本身是回文的话，其左右两边加上相同的也是回文
        //所以这个数二维数组的填充的方向是
        String res=new String("");
        for(int i=sLength-1;i>=0;i--){
            for(int j=i;j<sLength;j++){
                if(i==j){
                    a2[i][j] = true;
                    if(j-i+1>res.length())res=s.substring(i,j+1);
                }else {
                    if(j-i<=2){//长度为2时候一定是回文,必须要相等才是
                        if(s.charAt(i)==s.charAt(j)) {
                            a2[i][j]=true;
                            if(j-i+1>res.length())res=s.substring(i,j+1);
                        }
                        else a2[i][j]=false;
                    }else{//长度大于2
                        if(s.charAt(i)==s.charAt(j) && a2[i+1][j-1]==true){
                            a2[i][j]=true;
                            if(j-i+1>res.length())res=s.substring(i,j+1);
                        }
                        else a2[i][j]=false;
                    }
                }
            }
        }
        return res;
    }
}
//leetcode submit region end(Prohibit modification and deletion)
