package DS.Algrithem;

public class BinarySearch {
    public static int binarySearch(int[] arr,int num){
        int low=0;
        int high=arr.length-1;
        while(low<=high){
            int mid = (low+high) / 2 ;
            if (num > arr[mid]){
                low=mid+1;
            }else if(num<arr[mid]){
                high=mid-1;
            }else return mid;
        }
        return -1;
    }
    public static void main(String[] args) {
        int[] arr=new int[]{1,2,3,4,6,7,8,9};
        System.out.println(binarySearch(arr, 5));
    }
}
