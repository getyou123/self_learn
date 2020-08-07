package interview.xioadaka;

public  class Version {
        int mainVersion;
        int subVersion;
        int stageVersion;

        public Version(String versionStr){
            String[] strings = versionStr.split(".");
            this.mainVersion=Integer.parseInt(strings[0].substring(1,-1));
            this.subVersion=Integer.parseInt(strings[1]);
            if(strings.length>=3)this.stageVersion=Integer.parseInt(strings[2]);
            else this.stageVersion=0;
        }

    /**
     * 比较两个版本的大小关系
     * @param other
     * @return
     */
        boolean largeThan(Version other){
            if (this.mainVersion>other.mainVersion)return true;
            else if (this.mainVersion==other.mainVersion&&this.subVersion>other.subVersion) return true;
            else if(this.mainVersion==other.mainVersion&&this.subVersion==other.subVersion&&this.stageVersion>other.stageVersion) return true;
            else return false;
        }

    /**
     * 获取最大的版本号
     * @param versionArr
     * @return
     */
    public static String findLargestVersion(String[] versionArr){
        if (versionArr.length<=0)return null;
        int maxIndex=0;
        for (int i = 1; i < versionArr.length; i++) {
            if(new Version(versionArr[i]).largeThan(new Version(versionArr[maxIndex]))){
                maxIndex=i;
            }
        }
        return versionArr[maxIndex];
    }

    /**
     * 按照快排序进行排序
     * @param versionArr
     * @param left
     * @param right
     * @return
     */
    public static int getIndex(String[] versionArr,int left,int right) {
        int i=left;
        int j=right;
        int mid=(left+right)/2;
        Version midVersion=new Version(versionArr[mid]);
        String midVersionStr=versionArr[mid];
        while(true){
            while (i<=mid&&midVersion.largeThan(new Version(versionArr[i])))i++;
            while (j>=mid&&new Version(versionArr[j]).largeThan(midVersion))j--;
            if (j>=i)break;
            String tmp=versionArr[i];
            versionArr[i]=versionArr[j];
            versionArr[j]=tmp;
        }
        versionArr[mid]=versionArr[j];
        versionArr[j]=midVersionStr;
        return j;
    }

    /**
     * 服务于快排
     * @param versionArr
     * @param i
     * @param j
     */
    public static void quickSortVersionArr(String[] versionArr, int i, int j){
            if(i<j){
                int povet=getIndex(versionArr,i,j);
                quickSortVersionArr(versionArr,i,povet-1);
                quickSortVersionArr(versionArr,povet+1,j);
            }
    }

    /**
     * 基于排序结果进行对于所有的版本的标号
     * @param versionArr
     */
    public static void getAllVersionSeq(String[] versionArr){
        quickSortVersionArr(versionArr,0,versionArr.length-1);
        int seq=0;
        for (int i = 0; i < versionArr.length; i++) {
            if(i==0) System.out.println(versionArr[i]+","+seq);
            else {
                if(!versionArr[i].equals(versionArr[i-1])){
                    System.out.println(versionArr+","+seq);
                    seq++;
                }
            }
        }
    }
}
