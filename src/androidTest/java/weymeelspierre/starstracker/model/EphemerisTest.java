package weymeelspierre.starstracker.model;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

import junit.framework.Assert;

import java.util.Calendar;


public class EphemerisTest extends AndroidTestCase {

  private Ephemeris  ephemeris;


  @Override
  public void setUp() throws Exception {
    super.setUp();
    Context context = this.mContext;
    ephemeris = new Ephemeris(context);
  }


  // Source : L'Institut de Mécanique céleste et de calcul des éphémérides (IMCCE)
  // Les valeurs attendues en ascension-droite et en déclinaison
  // pour la position de la Lune et du Soleil le 2/3/2015 à 15:30 :
  @MediumTest
  public void testBesselInterpolation() {
    try {
      double deltaRA = 0.001;
      double deltaDE = 0.001;
      double moonExpectedRA = 128.44173;//(en degrés)<=> 8h 33' 46.01504" (IMCCE)
      double moonExpectedDE = 13.96370;//(en degrés)<=> +13° 57' 49.3048" (IMCCE)
      double sunExpectedRA = 342.91169;//(en degrés)<=> 22 51' 38.80576"  (IMCCE)
      double sunExpectedDE = -7.25992;//(en degrés)<=> -07° 15' 35.7221"  (IMCCE)

      Calendar timeForTest = Calendar.getInstance();
      //UTC TIME: (15:30 local = 14:30 UTC for the date below:
      timeForTest.set(2015,Calendar.MARCH,2,14,30,0);
      double[] moonRA_DE = ephemeris.besselInterpolation("MOON",timeForTest);
      double[] sunRA_DE = ephemeris.besselInterpolation("SUN",timeForTest);

      Assert.assertEquals(moonExpectedRA,moonRA_DE[0],deltaRA);
      Assert.assertEquals(moonExpectedDE,moonRA_DE[1],deltaDE);
      Assert.assertEquals(sunExpectedRA,sunRA_DE[0],deltaRA);
      Assert.assertEquals(sunExpectedDE,sunRA_DE[1],deltaDE);
    } catch (Exception e) {
      Assert.assertTrue(false);
    }
  }
}



  //FOR TEST-------------------------------------------------------------
/*
  private static String[] calendarValues(Calendar cal) {
    return new String[]{
            "day: " + cal.get(Calendar.DAY_OF_MONTH),
            "month: " + cal.get(Calendar.MONTH),
            "year: " + cal.get(Calendar.YEAR),
            "hour: " + cal.get(Calendar.HOUR_OF_DAY),
            "min: " + cal.get(Calendar.MINUTE),
            "sec: " + cal.get(Calendar.SECOND),
    };
  }*/

