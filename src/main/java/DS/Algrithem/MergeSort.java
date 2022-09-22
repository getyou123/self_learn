package DS.Algrithem;

public class MergeSort {
    public static void mergeArr(int[] arr,int left ,int right,int mid){
        int l=left;
        int r=mid+1;
        int[] res=new int[right-left+1];
        int k=0;
        while(l<=mid&&r<=right){
            if (arr[l]<arr[r]){
                res[k++]=arr[l++];
            }else {
                res[k++]=arr[r++];
            }
        }
        while (r<=right)res[k++]=arr[r++];
        while (l<=mid)res[k++]=arr[l++];
        for (int i = 0; i < res.length; i++) {
            arr[left++]=res[i];
        }
    }

    public static int[] mergeSort(int[] arr,int left,int right){
        if (left<right){
            int mid=(left+right)/2;
            arr=mergeSort(arr,left,mid);
            arr=mergeSort(arr,mid+1,right);
            mergeArr(arr,left,right,mid);
        }
        return arr;
    }

    public static void main(String[] args) {
        int[] arr=new int[]{1,4,3,2,6,7,9,5};
        int[] res=mergeSort(arr,0,arr.length-1);
        for (int i = 0; i < res.length; i++) {
            System.out.println(res[i]);
        }
    }
}
