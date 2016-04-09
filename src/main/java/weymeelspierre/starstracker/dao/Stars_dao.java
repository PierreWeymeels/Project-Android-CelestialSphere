package weymeelspierre.starstracker.dao;

import android.content.Context;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Pierre on 20/11/2014.
 */
public class Stars_dao extends Dao {

  private static final String TAG = "Stars_dao";
  private final String sqlTables = "stars";

  public Stars_dao(Context context) {
    super(context);
  }

  //STARS-----------------------------------------------------------------
  public ArrayList<double[]> getStarsPosition(double magnLimit) throws Exception {
    ArrayList<double[]> reply = new ArrayList<double[]>();
    String[] sqlSelect = {"RAJ2000", "DEJ2000"};
    String where = "magnitude <= ?";
    Cursor c = this.getDataCursor(sqlTables,sqlSelect,where,
            new String[]{String.valueOf(magnLimit)}, null, null, null);
    if (c != null) {
      if (c.moveToFirst()) {
        do {
          reply.add(new double[]{c.getDouble(0), c.getDouble(1)});
        } while (c.moveToNext());
      }
      c.close();
    }
    return reply;
  }

  //CONSTELLATIONS-----------------------------------------------------------------
  public HashMap<String, double[]> greekLetterPosition(String cst) throws Exception {
    HashMap<String, double[]> greekLetterPosition = new HashMap<String, double[]>();
    String[] sqlSelect = {"greekletter", "RAJ2000", "DEJ2000"};
    String where = "constellation = ? and greekletter != ?";
    Cursor c = this.getDataCursor(sqlTables,sqlSelect,where,
            new String[]{cst,""}, null, null, null);
    if (c != null) {
      if (c.moveToFirst()) {
        do {
          greekLetterPosition.put(c.getString(0), new double[]{c.getDouble(1), c.getDouble(2)});
        } while (c.moveToNext());
      }
      c.close();
    }
    return greekLetterPosition;
  }

  // STARS POSITION AND NAMES----------------------------------------------------------
  public HashMap<String, double[]> getStarsNameAndPosition() throws Exception {
    HashMap<String, double[]> reply = new HashMap<String, double[]>();
    String[] sqlSelect = {"name", "RAJ2000", "DEJ2000"};
    String where = "name != ? and constellation in (?,?,?,?,?,?,?,?,?,?,?,?," +
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";//"name != ?";*/
    String[] selectionArgs =  new String[]{"","And", "Ant", "Aps","Aqr", "Aql","Ara", "Ari", "Aur",
            "Boo", "Cam", "Cnc", "Cvn", "CMa", "CMi", "Cap", "Car", "Cas","Cen", "Cep", "Cyg",
            "Leo", "Ori", "Lib","Lyr", "Peg", "Per", "Psc", "Sge", "Sgr", "Sco", "UMi", "UMa"
    };
    try {
      Cursor c = this.getDataCursor(sqlTables,sqlSelect,where,
              selectionArgs, null, null, null);
      if (c != null) {
        if (c.moveToFirst()) {
          do {
            double[] RA_DE = new double[]{c.getDouble(1), c.getDouble(2)};
            reply.put(c.getString(0), RA_DE);
          } while (c.moveToNext());
        }
        c.close();
      }
      return reply;
    } catch (Exception e) {
      throw new Exception(TAG + " : " + e.getMessage());
    }
  }

}
