package dateLearn;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class dateUtil {
    /**
     * 获取某一天的变化的天数的日期
     * @param dateStr 移动日期的起点
     * @param dayStep 移动几天 exam: -1 表示前一天 20200509 -1 输出为20200508
     * @param formatType
     * @return 指定日期类型的移动后的日期
     */
    public static String addDays(String dateStr,int dayStep,String formatType){
        SimpleDateFormat stringDateFormat = new SimpleDateFormat(formatType);
        Calendar calendar = Calendar.getInstance();
        Date date=null;
        try {
            date=stringDateFormat.parse(dateStr);
        }catch (Exception e){
            e.printStackTrace();
        }
     calendar.setTime(date);
     calendar.add(Calendar.DATE,dayStep);
     date=calendar.getTime();
       return stringDateFormat.format(date);
    }
    public static void main(String[] args){
        //实际就是Date对象的操作
        //日期的格式化操作
        {
            //date->string
            Date date = new Date();//最新的时间
            SimpleDateFormat stringToDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//获取Format对象
            System.out.println(stringToDateFormat.format(date));

            //string->data
            try{
                Date date1 = stringToDateFormat.parse("2018-12-22 15:34:32");//提取然后转化成需要的Date类型
                System.out.println(date1.toString());
            }catch ( ParseException e){
                e.printStackTrace();;
            }

        }

        //日期增加或者移动
        {
            Date date=new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            String resDate=addDays(simpleDateFormat.format(date),-1,"yyyyMMdd");//昨天的日期

            System.out.println(resDate);
        }

        //获取时间戳
        {
            Long timeStamp=System.currentTimeMillis();//获取当前的时间戳
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date=new Date(Long.parseLong(String.valueOf(timeStamp)));//时间戳转化成Date
            String sd = sdf.format(date);
            System.out.println(sd);
        }


    }
}


