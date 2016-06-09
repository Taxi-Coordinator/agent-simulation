package utils;

import sun.misc.IOUtils;

import java.io.FileInputStream;
import java.sql.Time;

/**
 * Created by eduardosalazar1 on 6/6/16.
 */
public class Helper {
    public static Time currentTime(){
        Time result = new Time;
        // Get File Time
        try(FileInputStream inputStream = new FileInputStream("src/main/resources/time.txt")) {

            String everything = IOUtils.toString(inputStream);

        }

        return result;
    }
}
