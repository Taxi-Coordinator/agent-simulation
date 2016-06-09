package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
/**
 * Created by eduardosalazar1 on 6/6/16.
 */
public class Helper {
    public static Date nextCall(Date current){
        long curTimeInMs = current.getTime();
        Double minutes = nextCallMinutes(current);
        System.out.println("Adding "+ minutes);
        int addSeconds = new Double(minutes * 60.0).intValue();
        Date afterAddingMins = new Date(curTimeInMs + (addSeconds * 1000));
        return afterAddingMins;
    }

    public static Double nextCallMinutes(Date current){
        return new Double(-Math.log(1.0 - StdRandom.uniform(0.0,1.0)) / getLambda(current));
    }

    public static double getLambda(Date current){
        return 1.0/(60.0 / getCallsPerHour(current));
    }
    public static double getCallsPerHour(Date current){
        try{
            String string1 = "07:00:00";
            Date time1 = new SimpleDateFormat("HH:mm:ss").parse(string1);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(time1);

            String string2 = "09:00:00";
            Date time2 = new SimpleDateFormat("HH:mm:ss").parse(string2);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(time2);
            //calendar2.add(Calendar.DATE, 1);

            String string3 = "17:00:00";
            Date time3 = new SimpleDateFormat("HH:mm:ss").parse(string3);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(time3);

            String string4 = "19:00:00";
            Date time4 = new SimpleDateFormat("HH:mm:ss").parse(string4);
            Calendar calendar4 = Calendar.getInstance();
            calendar4.setTime(time4);
            //calendar4.add(Calendar.DATE, 1);

            String string5 = "23:00:00";
            Date time5 = new SimpleDateFormat("HH:mm:ss").parse(string5);
            Calendar calendar5 = Calendar.getInstance();
            calendar5.setTime(time5);
            //calendar5.add(Calendar.DATE, 1);


            //Extract just the time to avoid colisions
            Calendar calendar = Calendar.getInstance();
            //calendar.add(Calendar.DATE, 1);
            calendar.setTime(current);

            Date x = calendar.getTime();
            if ((x.after(calendar1.getTime()) || x.equals(calendar1.getTime())) && x.before(calendar2.getTime())) {
                //checkes whether the current time is between 07:00:00 and 09:00:00.
                return 3;
            }else if((x.after(calendar2.getTime()) || x.equals(calendar1.getTime())) && x.before(calendar3.getTime())){
                //checkes whether the current time is between 09:00:00 and 17:00:00.
                return 2;
            }else if((x.after(calendar3.getTime())  || x.equals(calendar1.getTime())) && x.before(calendar4.getTime())){
                //checkes whether the current time is between 17:00:00 and 19:00:00.
                return 3;
            }else if((x.after(calendar4.getTime())  || x.equals(calendar1.getTime())) && x.before(calendar5.getTime())){
                //checkes whether the current time is between 19:00:00 and 23:00:00.
                return 2;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Rest of the time
        return 1;
    }
}
