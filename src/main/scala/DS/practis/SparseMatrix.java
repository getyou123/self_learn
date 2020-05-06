package DS.practis;

public class SparseMatrix {

    public static int[][] zip2Origin(int[][] zipArr) {
        if (zipArr.length < 1) {
            int a[][] = {{0, 0}, {0, 0}};
            return a;
        } else {

            int rows = zipArr[0][0];
            int cols = zipArr[0][1];

            int resArr[][] = new int[rows][];
            for (int i = 0; i < resArr.length; i++) {
                resArr[i] = new int[cols];
            }

            for (int i = 1; i < zipArr.length; i++) {
                int rowTmp = zipArr[i][0];
                int colTmp = zipArr[i][1];
                int valTmp = zipArr[i][2];
                resArr[rowTmp - 1][colTmp - 1] = valTmp;
            }
            return resArr;
        }
    }

    public static int[][] origin2Zip(int[][] origin) {
        if (origin.length <= 0) {
            int zip[][] = {{0, 0, 0}};
            return zip;
        } else {
            int rows = origin.length;
            int cols = origin[0].length;
            int n0Cnt = 0;
            for (int i = 0; i < origin.length; i++) {
                for (int j = 0; j < origin[i].length; j++) {
                    if (origin[i][j] != 0) n0Cnt += 1;
                }
            }

            int zip[][] = new int[n0Cnt + 1][];
            for (int i = 0; i < zip.length; i++) {
                zip[i] = new int[3];
            }
            zip[0] = new int[]{rows, cols, n0Cnt};
            int lineCount = 1;
            for (int i = 0; i < origin.length; i++) {
                for (int j = 0; j < origin[i].length; j++) {
                    if (origin[i][j] != 0) {
                        zip[lineCount] = new int[]{i+1, j+1, origin[i][j]};
                        lineCount+=1;
                    }
                }
            }
        return zip;
    }
}


    public static void main(String[] args) {

        int zipArr[][]={{3,4,2},{1,2,3},{2,1,6}};
        int allArr[][]=zip2Origin(zipArr);
        for (int i = 0; i < allArr.length; i++) {
            for (int j = 0; j < allArr[i].length; j++) {
                System.out.print(allArr[i][j]);
            }
                System.out.println();

        }

        System.out.println("");

        int zip[][]=origin2Zip(allArr);
        for (int i = 0; i < zip.length; i++) {
            for (int j = 0; j < zip[i].length; j++) {
                System.out.print(zip[i][j]);
            }
            System.out.println();

        }
    }
}
