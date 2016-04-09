package weymeelspierre.starstracker.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import weymeelspierre.starstracker.library.Math_lib;
import weymeelspierre.starstracker.library.Time_lib;

/**
 * Created by Pierre on 4/01/2015.
 */
public class Ephemeris_dao extends Dao {

  private static final String TAG = "Ephemeris_dao";
  private final int HOUR_PAS = 12;
  private static final String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SS";


  public Ephemeris_dao(Context context) {
    super(context);
  }


  public HashMap<Integer, double[]> getDataForBesselInterpolation(
          Calendar firstBesselDate, String solarSystemBody) throws Exception {
    String sqlTables;
    if (solarSystemBody.compareTo("MOON") == 0) {
      sqlTables = "moonEphemeris";
    } else if (solarSystemBody.compareTo("SUN") == 0) {
      sqlTables = "sunEphemeris";
    } else
      throw new Exception("Bad solar system name !");
    return getBesselMapData(firstBesselDate, sqlTables);
  }


  /**
   * VALABLE SEULEMENT POUR DES PAS DE 12h !!!
   * ET DES CHAMPS DateUTC se terminant par 00:00:00 !
   *
   * @param now
   * @return
   */
  public Calendar giveFirstBesselDate(Calendar now) throws Exception {
    Calendar firstDate = Time_lib.calendarCopy(now);
    int hour = now.get(Calendar.HOUR_OF_DAY);
    if (hour < HOUR_PAS) {
      firstDate.add(Calendar.DATE, -1);
      Time_lib.setHHmmssSSS_calendar(firstDate, 12, 00, 00, 000);
    } else { //if(hour >= hour_pas){
      Time_lib.setHHmmssSSS_calendar(firstDate, 00, 00, 00, 000);
    }
    return firstDate;
  }

  /**
   * String dateFormatAdapted = dateFormat;
   * if(sqlTables.compareTo("sunEphemeris")==0){
   * dateFormatAdapted = " "+dateFormat;
   * }
   *
   * @param firstDate
   * @param sqlTables
   * @return
   * @throws Exception
   */
  private HashMap<Integer, double[]> getBesselMapData(Calendar firstDate, String sqlTables) throws Exception {
    HashMap<Integer, double[]> reply = new HashMap<Integer, double[]>();
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    Calendar besselDate = Time_lib.calendarCopy(firstDate);
    String besselDate_isoString = sdf.format(besselDate.getTime());
    reply.put(0, getRA_DE(besselDate_isoString, sqlTables));
    for (int i = 1; i < 4; ++i) {
      besselDate.add(Calendar.HOUR_OF_DAY, HOUR_PAS);
      besselDate_isoString = sdf.format(besselDate.getTime());
      reply.put(i, getRA_DE(besselDate_isoString, sqlTables));
    }
    return reply;
  }

  private double[] getRA_DE(String dateUTC, String sqlTables) throws Exception {
    double[] RA_DE = new double[2];
    Cursor c = this.getDataCursor(sqlTables, new String[]{"RA", "DE"}, "DateUTC = ?",
            new String[]{dateUTC}, null, null, null);
    if (c != null) {
      if (c.moveToFirst()) {
        RA_DE[0] = getDecimalDegrees_fromRA_DEString(c.getString(0), "HMS");
        RA_DE[1] = getDecimalDegrees_fromRA_DEString(c.getString(1), "DMS");
      }
    } else
      throw new Exception("Cursor c: c.moveToFirst()==false || (c == null) !!!");
    return RA_DE;
  }

  private double getDecimalDegrees_fromRA_DEString(String angleString, String type) throws Exception {
    String[] elem = angleString.split(" ");
    int elemSize = elem.length;
    ArrayList<Integer> index = new ArrayList<Integer>();
    for (int i = 0; i < elemSize; ++i) {
      if (elem[i].compareTo("") != 0)
        index.add(i);
    }
    if (index.size() == 3) {
      String str = elem[index.get(0)];
      if (str.startsWith("+"))
        str = str.substring(1);
      return Math_lib.getDecimalDegrees_fromAngleType(type, Integer.valueOf(str),
              Integer.valueOf(elem[index.get(1)]), Double.valueOf(elem[index.get(2)]));
    }
    throw new Exception("Problem with hmsString.split(\" \")  !!!");
  }

  public int getHOUR_PAS() {
    return HOUR_PAS;
  }
}
