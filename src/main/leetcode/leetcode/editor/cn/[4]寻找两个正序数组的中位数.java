//给定两个大小为 m 和 n 的正序（从小到大）数组 nums1 和 nums2。 
//
// 请你找出这两个正序数组的中位数，并且要求算法的时间复杂度为 O(log(m + n))。 
//
// 你可以假设 nums1 和 nums2 不会同时为空。 
//
// 
//
// 示例 1: 
//
// nums1 = [1, 3]
//nums2 = [2]
//
//则中位数是 2.0
// 
//
// 示例 2: 
//
// nums1 = [1, 2]
//nums2 = [3, 4]
//
//则中位数是 (2 + 3)/2 = 2.5
// 
// Related Topics 数组 二分查找 分治算法


//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public double findMedianSortedArrays(int[] A, int[] B) {
        int m = A.length, n = B.length;
// 不论总数是奇数还是偶数，以l和r为下标的两数的均值都是medium
        int l = (m + n + 1) / 2;
        int r = (m + n + 2) / 2;

        return (getkth(A, 0, B, 0, l) + getkth(A, 0, B, 0, r)) / 2.0;
    }

    private int getkth(int[] A, int aStart, int[] B, int bStart, int k) {
        if (aStart >= A.length)
            return B[bStart + k - 1];
        if (bStart >= B.length)
            return A[aStart + k - 1];
        if (k == 1)
            return Math.min(A[aStart], B[bStart]);
        int aMin = Integer.MAX_VALUE, bMin = Integer.MAX_VALUE;
        if (aStart + k / 2 - 1 < A.length)
            aMin = A[aStart + k / 2 - 1];
        if (bStart + k / 2 - 1 < B.length)
            bMin = B[bStart + k / 2 - 1];

        if (aMin < bMin)
            return getkth(A, aStart + k / 2, B, bStart, k - k / 2);
        else
            return getkth(A, aStart, B, bStart + k / 2, k - k / 2);
    }

}
//leetcode submit region end(Prohibit modification and deletion)