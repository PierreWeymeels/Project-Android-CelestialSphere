package weymeelspierre.starstracker.model;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;
import java.util.TimeZone;

import weymeelspierre.starstracker.dao.Ephemeris_dao;
import weymeelspierre.starstracker.library.Math_lib;
import weymeelspierre.starstracker.library.Time_lib;
import weymeelspierre.starstracker.renderOpenGl.RenderManager;
import weymeelspierre.starstracker.renderOpenGl.SolarSystemBodyRender;

/**
 * Created by Pierre on 7/01/2015.
 */
public class Ephemeris {
  private Ephemeris_dao ephemeris_dao = null;
  private Context context = null;
  private SolarSystemBodyRender solSysBodyRender = null;

  private ArrayList<Float> solSysBody_positions = new ArrayList<Float>();
  private ArrayList<Float> solSysBody_colors = new ArrayList<Float>();


  protected Ephemeris(Context context) {
    this.context = context;
    ephemeris_dao = new Ephemeris_dao(context);
  }

  protected void initializeSolarSystemBodyRender(RenderManager renderManager) throws Exception {
    float[] positions = Math_lib.getFloatTab(solSysBody_positions);
    float[] colors = Math_lib.getFloatTab(solSysBody_colors);
    solSysBodyRender = new SolarSystemBodyRender(renderManager,positions, colors);
  }

  protected void updateSolarSystemBodyRender() throws Exception {
    float[] positions = Math_lib.getFloatTab(solSysBody_positions);
    solSysBodyRender.update(positions);
  }

  protected void clearSolSysPosition() {
    solSysBody_positions.clear();
  }

  /**
   * cf. Interpolation de Bessel
   * ( Emploi des éphémérides de position p7)
   */
  protected double[] besselInterpolation(String solarSystemBody,Calendar now) {
    try {
      //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS");
      //verif calendar if current after !!!
      //sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
      //String now_isoString = sdf.format(now.getTime());
      //int hTEST = now.get(Calendar.HOUR_OF_DAY);
      Calendar firstBesselDate = ephemeris_dao.giveFirstBesselDate(now);
      HashMap<Integer, double[]> besselList =
              ephemeris_dao.getDataForBesselInterpolation(firstBesselDate, solarSystemBody);
      int sizeOfList = besselList.size();
      if (sizeOfList == 4) {
        double[] besselRA_xFactor = new double[besselList.size()];
        double[] besselDE_xFactor = new double[besselList.size()];
        Set<Integer> keySet = besselList.keySet();
        int i = 0;
        for (Integer elem : keySet) {
          double[] RA_DE = besselList.get(elem);
          besselRA_xFactor[i] = RA_DE[0];
          besselDE_xFactor[i] = RA_DE[1];
          ++i;
        }
        double bessel_nFactor =
                besselInterpolationFactor(now, firstBesselDate);
        double resultRA = getBesselResult(bessel_nFactor, besselRA_xFactor, "RA");
        double resultDE = getBesselResult(bessel_nFactor, besselDE_xFactor, "DE");
        addPosition(resultRA, resultDE);
        return new double[]{resultRA, resultDE};
      } else
        throw new Exception(" besselList != 4 !!!");
    } catch (Exception e) {
      String err = e.getMessage();
      String end;
      return null;
    }
  }


  /**
   * @param t         the current Calendar
   * @param tMinusOne the older Calendar
   *                  tOne  the younger Calendar
   * @return double bessel interpolation factor between [0,1]
   */
  private double besselInterpolationFactor(Calendar t, Calendar tMinusOne) throws Exception {
    Calendar tZero = Time_lib.calendarCopy(tMinusOne);
    tZero.add(Calendar.HOUR_OF_DAY, ephemeris_dao.getHOUR_PAS());
    Calendar tOne = Time_lib.calendarCopy(tZero);
    tOne.add(Calendar.HOUR_OF_DAY, ephemeris_dao.getHOUR_PAS());
    double factor1 = besselInterpolationSubFactor(t, tZero);
    double factor2 = besselInterpolationSubFactor(tOne, tZero);
    return factor1 / factor2;
  }

  private double besselInterpolationSubFactor(Calendar youngerCal, Calendar olderCal)
          throws Exception {
    int yearDiff = youngerCal.get(Calendar.YEAR) - olderCal.get(Calendar.YEAR);
    int dayDiff = youngerCal.get(Calendar.DAY_OF_YEAR) - olderCal.get(Calendar.DAY_OF_YEAR);
    double hourDiff = Math_lib.getHoursDecimal(youngerCal) - Math_lib.getHoursDecimal(olderCal);
    return (365.0 * yearDiff + dayDiff) * 24.0 + hourDiff;
  }

  private double getBesselResult(double n, double[] bessel_xFactor,
                                 String type) throws Exception {
    if (bessel_xFactor.length == 4) {
      double xZero = bessel_xFactor[1];
      double a = bessel_xFactor[1] - bessel_xFactor[0];
      double b = bessel_xFactor[2] - bessel_xFactor[1];
      double c = bessel_xFactor[3] - bessel_xFactor[2];

      if (type.compareTo("RA") == 0) {
        double min = Math_lib.getMin(a, b, c);
        a = setIfBesselRA_anomaly(a, min, 10.0);
        b = setIfBesselRA_anomaly(b, min, 10.0);
        c = setIfBesselRA_anomaly(c, min, 10.0);
      }

      double d = b - a;
      double e = c - b;

      return xZero + n * b - (n * (1 - n) / 4.0) * (d + e);
    } else
      throw new Exception(" bessel_xFactor.length != 4 !!!");
  }

  /**
   * TO DO Use maximale fluctuation for ephemeris PAS of the celeste body
   * instead of min+deltafluctuation (min+10) is ok for PAS = 12h
   *
   * @param value
   * @param min
   * @param deltaFluctuation
   */
  private double setIfBesselRA_anomaly(double value, double min, double deltaFluctuation) {
    if (Math.abs(value) > (min + deltaFluctuation)) {
      if (value > 0) {
        value = value - 360;
      } else {//NEVER 0 FOR ephemeris PAS <> 0 !
        value = value + 360;
      }
    }
    return value;
  }

  protected void addColor(float[] color) {
    for (int j = 0; j < color.length; ++j) {
      solSysBody_colors.add(color[j]);
    }
  }

  private void addPosition(double RA, double DE) {
    double[] xyzPosition = Math_lib.degreeRaDe_to_xyzSphereUnity(RA, DE);
    for (int j = 0; j < 3; ++j) {
      solSysBody_positions.add((float) xyzPosition[j]);
    }
  }

}
