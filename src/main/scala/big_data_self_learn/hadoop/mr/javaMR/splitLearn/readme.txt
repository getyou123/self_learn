        // 关于切片规则设置：三个切片规则 1.FileInputFormat(默认) 2.CombineFileInputFormat(小文件的合并) 3.自定义InputFormat
        // 重点的 note: 设置切片规则 job.setInputFormatClass(CombineFileInputFormat.class); 这个进行了小文件的合并
        //job.setInputFormatClass(CombineFileInputFormat.class);
        //CombineFileInputFormat.setMaxInputSplitSize(job,4194304);设置为4M

        // 默认是 job.setInputFormatClass(FileInputFormat.class);;这个是按照文件的大小进行切片 默认是 集群128M 本地32M但是这种对于
        // 小文件的不好支持，会产生很多maptask

        // FileInputFormat有很多的实现类：
        1.TextInputFormat(这个是默认的实现的类，每个文件中的内容按照行进一行一行行处理)在达到一定的大小切片，
        注意这个是对于单个文件进行的切片，这个大小设置同FileInputFormat。
        job.setInputFormatClass(FileInputFormat.class);
        2.KeyValueTextInputFormat可以看成是在TextFileInputFormat的基础上进行的增强版本，这个会对文件中的内容也是按照一行一行来处理，
        但是还同时会对文件的内容按照指定的分隔符进行划分，如果不包含分割符的话，那么就是整个的一行作为key，value为空。
        // 设置切割符
        conf.set(KeyValueLineRecordReader.KEY_VALUE_SEPERATOR, " ");
        // 设置输入格式
        job.setInputFormatClass(KeyValueTextInputFormat.class);
        3.NLineInputFormat 顾名思义就是按照指定的行数来进行切片，到达指定的行数数就会进行切片：
         //这里改了一下，把TextInputFormat改成了NLineInputFormat
        NLineInputFormat.setNumLinesPerSplit(job, Integer.parseInt("2"));
        //NLineInputFormat.setNumLinesPerSplit(job, Integer.parseInt(args[0]));
        job.setInputFormatClass(NLineInputFormat.class);
