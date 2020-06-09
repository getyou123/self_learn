//反转一个单链表。 
//
// 示例: 
//
// 输入: 1->2->3->4->5->NULL
//输出: 5->4->3->2->1->NULL 
//
// 进阶: 
//你可以迭代或递归地反转链表。你能否用两种方法解决这道题？ 
// Related Topics 链表


//leetcode submit region begin(Prohibit modification and deletion)
/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode(int x) { val = x; }
 * }
 */
class Solution {
    public ListNode reverseList(ListNode head) {
        ListNode next = null;//指向当前节点的后驱
        ListNode pre = null;//指向当前节点的前驱
        while (head != null) {
            next = head.next;
            //当前节点的后驱指向前驱
            head.next = pre;
            pre = head;
            //处理下一个节点
            head = next;
        }
        return pre;

    }
}
//leetcode submit region end(Prohibit modification and deletion)
