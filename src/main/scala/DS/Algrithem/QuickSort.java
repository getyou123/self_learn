package DS.Algrithem;

public class QuickSort {
    public static int parttion(int[] arr,int left,int right){
        int mid=(left+right)/2;
        int i=left;
        int j=right;
        int pivot=arr[mid];
        while(true){
            while (i<=j&&arr[i]<=pivot)i++;
            while (j>=i&&arr[j]>=pivot)j--;
            if(i>=j)break;
            int tmp=arr[i];
            arr[i]=arr[j];
            arr[j]=tmp;
        }
        arr[mid]=arr[j];
        arr[j]=pivot;
        return j;
    }

    public static int[] quickSort(int[] arr,int left,int right){
        if(left<right){
            int j= parttion(arr,left,right);//选出中轴，两侧有序
            //两侧分别排序
            arr=quickSort(arr,left,j-1);
            arr=quickSort(arr,j+1,right);
        }
        return arr;
    }
    public static void main(String[] args) {
        int[] arr=new int[]{1,4,3,2,6,7,9,5};
        int[] res=quickSort(arr,0,arr.length-1);
        for (int i = 0; i < res.length; i++) {
            System.out.println(res[i]);
        }

    }
}
