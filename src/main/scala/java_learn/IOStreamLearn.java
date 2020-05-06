package java_learn;

import org.apache.hadoop.io.SecureIOUtils;

import java.io.*;


public class IOStreamLearn {
    public static void main(String[] args) throws IOException {
        //文件流
        FileInputStream fileInputStream = new FileInputStream("in/1.log");

        File file = new File("out/2.txt");

        FileOutputStream fileOutputStream = new FileOutputStream(file);

        fileOutputStream.write(22);//二进制写法

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF-8");

    }
}
