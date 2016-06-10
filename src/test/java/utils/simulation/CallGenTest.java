package utils.simulation;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Math.abs;
import static org.junit.Assert.*;

/**
 * Created by jherez on 6/11/16.
 */
public class CallGenTest {

    private Date currentTime;

    @Before
    public void setUp() throws Exception {
        currentTime = new Date();
    }

    @After
    public void tearDown() throws Exception {
        currentTime = null;
    }

    @Test
    public void nextCall() throws Exception {
        Date nextCall = CallGen.nextCall(currentTime);
        assertTrue(nextCall.after(currentTime));
        System.out.println("@Test - nextCall");
    }

    @Test
    public void nextCallMinutes() throws Exception {
        double minutes = CallGen.nextCallMinutes(currentTime);
        assertTrue(minutes > 0);
        System.out.println("@Test - nextCallMinutes");
    }

    @Test
    public void getLambda() throws Exception {
        String str1 = "00:00:00";
        Date testDate;
        int i;

        try {
            currentTime = new SimpleDateFormat("HH:mm:ss").parse(str1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (i = 0; i < 7; i++) {
            testDate = DateUtils.addHours(currentTime, i);
            assertTrue(CallGen.getLambda(testDate) == 0.016666666666666666);
        }

        for (i = 7; i < 9; i++) {
            testDate = DateUtils.addHours(currentTime, i);
            assertTrue(CallGen.getLambda(testDate) == 0.05);
        }

        for (i = 9; i < 17; i++) {
            testDate = DateUtils.addHours(currentTime, i);
            assertTrue(CallGen.getLambda(testDate) == 0.03333333333333333);
        }

        for (i = 17; i < 19; i++) {
            testDate = DateUtils.addHours(currentTime, i);
            assertTrue(CallGen.getLambda(testDate) == 0.05);
        }

        for (i = 19; i < 23; i++) {
            testDate = DateUtils.addHours(currentTime, i);
            assertTrue(CallGen.getLambda(testDate) == 0.03333333333333333);
        }
        System.out.println("@Test - nextCallMinutes");
    }

    @Test
    public void getCallsPerHour() throws Exception {
        String str1 = "00:00:00";
        Date testDate;
        int i;

        try {
            currentTime = new SimpleDateFormat("HH:mm:ss").parse(str1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for (i = 0; i < 7; i++) {
            testDate = DateUtils.addHours(currentTime, i);
            assertTrue(CallGen.getCallsPerHour(testDate) == 1.0);
        }

        for (i = 7; i < 9; i++) {
            testDate = DateUtils.addHours(currentTime, i);
            assertTrue(CallGen.getCallsPerHour(testDate) == 3.0);
        }

        for (i = 9; i < 17; i++) {
            testDate = DateUtils.addHours(currentTime, i);
            assertTrue(CallGen.getCallsPerHour(testDate) == 2.0);
        }

        for (i = 17; i < 19; i++) {
            testDate = DateUtils.addHours(currentTime, i);
            assertTrue(CallGen.getCallsPerHour(testDate) == 3.0);
        }

        for (i = 19; i < 23; i++) {
            testDate = DateUtils.addHours(currentTime, i);
            assertTrue(CallGen.getCallsPerHour(testDate) == 2.0);
        }
        System.out.println("@Test - getCallsPerHour");
    }

}