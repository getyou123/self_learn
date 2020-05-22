//将两个升序链表合并为一个新的升序链表并返回。新链表是通过拼接给定的两个链表的所有节点组成的。 
//
// 示例： 
//
// 输入：1->2->4, 1->3->4
//输出：1->1->2->3->4->4
// 
// Related Topics 链表


//leetcode submit region begin(Prohibit modification and deletion)


class Solution {
    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        ListNode h1=l1;
        ListNode h2=l2;
        ListNode res=new ListNode(0);
        res.next=null;
        ListNode h3=res;
        while(h1!=null && h2!=null){
            if(h1.val>h2.val){
                ListNode tmp=new ListNode(h2.val);
                tmp.next=null;
                h3.next=tmp;
                h2=h2.next;
                h3=h3.next;
            }else if(h1.val<h2.val){
                ListNode tmp=new ListNode(h1.val);
                tmp.next=null;
                h3.next=tmp;
                h1=h1.next;
                h3=h3.next;
            }else if(h1.val==h2.val){
                ListNode tmp1=new ListNode(h1.val);
                ListNode tmp2=new ListNode(h2.val);
                tmp1.next=tmp2;
                tmp2.next=null;
                h3.next=tmp1;
                h3=h3.next.next;
                h1=h1.next;
                h2=h2.next;
            }
        }

        if(h2!=null){
            h3.next=h2;
        }else if(h1!=null){
            h3.next=h1;
        }
        return res.next;
    }
}
//leetcode submit region end(Prohibit modification and deletion)
