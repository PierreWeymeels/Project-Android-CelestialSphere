package weymeelspierre.starstracker.library;

import android.test.suitebuilder.annotation.MediumTest;

import junit.framework.Assert;
import junit.framework.TestCase;
import java.util.Calendar;



/**
 * Created by Pierre on 5/02/2015.
 */
public class Time_libTest extends TestCase {

  public Time_libTest() {
    super();
  }

// 3/3/2015=> (h,m,s) 10 41 49.29728 = 10,69703 heures
  //double sideralTimeExpected_for3March = 10.69703 ;

  // Source : L'Institut de Mécanique céleste et de calcul des éphémérides (IMCCE)
  // EPHEMERIDES ASTRONOMIQUES, CONNAISSANCE DES TEMPS 2015 (IMCCE).
  // Le temps sidéral à minuit (temps universel) pour le 2 mars 2015 :
  // 2/3/2015 => (h,m,s) 10 37 52.74462 = 10,63132 heures (angle horaire)
  @MediumTest
  public void testGetMeanGreenwichSideralTimeInDegrees(){
    double deltaSideralTime = 0.001;
    double sideralTimeExpected_for2March = 10.63132 ;
    Calendar timeForTest = Calendar.getInstance();
    //UTC TIME: (00:00:00 le 2/3/2015)
    timeForTest.set(2015,Calendar.MARCH,2,0,0,0);
    try {
      double JJ_atMidnight = Time_lib.getJulianDay(timeForTest, true);
      double jcu_stForMidnightJD =
              Time_lib.sideralTimeInJulianCenturyUnity(JJ_atMidnight);
      double sideralTime = Time_lib.getMeanGreenwichSideralTimeInDegrees(
              jcu_stForMidnightJD, Time_lib.getSecondOfDay(timeForTest));
      Assert.assertEquals(
              sideralTimeExpected_for2March,sideralTime,deltaSideralTime);
    } catch (Exception e) {
      Assert.assertTrue(false);
    }
  }















/*
  private String[] calendarValues(Calendar cal){
    return new String[]{
            "day: "+cal.get(Calendar.DAY_OF_MONTH),
            "month: "+cal.get(Calendar.MONTH),
            "year: "+cal.get(Calendar.YEAR),
            "hour: "+cal.get(Calendar.HOUR_OF_DAY),
            "min: "+cal.get(Calendar.MINUTE),
            "sec: "+cal.get(Calendar.SECOND),
    };
  }*/

  /*
  @SmallTest
  public void testJulianDayForEx3a() throws Exception {
    Calendar calUTC = getCurrentCalendarUTC();
    setDDMMYYYY_calendar(calUTC,4,10,1957);
    setHHmmssSSS_calendar(calUTC,19,26,24,0);
    double JJ = getJulianDay(calUTC,false);

    String[] calendarValues = calendarValues(calUTC);
    String endTest = "ok";
    //assertEquals();
  }

  @SmallTest
  public void testCurrentJulianDay() throws Exception {
    Calendar calUTC = getCurrentCalendarUTC();
    double JJ = getJulianDay(calUTC,false);
    String[] calendarValues = calendarValues(calUTC);
    String endTest = "ok";
  }

  @SmallTest
  public void testCurrentSideralTime() throws Exception {
    double Gmt_sidTime = getLocaleMeanSideralTime(0.0);
    Calendar calUTC = getCurrentCalendarUTC();
    double Bxl_sidTime = getLocaleMeanSideralTime(-4.37);
    String[] calendarValues = calendarValues(calUTC);
    String endTest = "ok";
  }

  @SmallTest
  public void testCurrentTime() throws Exception {
    Calendar calUTC = getCurrentCalendarUTC();
    String[] calendarValues = calendarValues(calUTC);
    String endTest = "ok";
  }*/
/*
  public static Calendar getCurrentCalendarUTC() {
    SimpleDateFormat df = new SimpleDateFormat();
    TimeZone timeZone = TimeZone.getTimeZone("UTC");
    df.setTimeZone(timeZone);
    String timeId = timeZone.getID();
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    return cal;
  }*/

  @Override
  public void tearDown() throws Exception {
    super.tearDown();
  }


  @Override
  public void setUp() throws Exception {
    super.setUp();
  }

}