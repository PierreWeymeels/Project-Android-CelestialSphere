package weymeelspierre.starstracker.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Pierre on 1/02/2015.
 */
public class Constellation_dao extends Dao {

  private static final String TAG = "Constellation_dao";
  private final String sqlTables = "constellation";


  public Constellation_dao(Context context){
    super(context);
  }

  public ArrayList<String[]> getConstellationsData() throws Exception{
    ArrayList<String[]> reply = new ArrayList<String[]>();
    String [] sqlSelect = {"name", "stars_sequence", "branch_index"};
    try{
      Cursor c = this.getDataCursor(sqlTables,sqlSelect,null,null,null,null,null);
      if(c != null){
        if(c.moveToFirst()){
          do{
            reply.add(new String[]{c.getString(0),c.getString(1),c.getString(2)});
          }while(c.moveToNext());
        }
        c.close();
      }
      return reply;
    } catch (Exception e) {
      throw new Exception(TAG + " : " + e.getMessage());
    }
  }

  public HashMap<String, double[]> getConstellationsNameAndPosition() throws Exception {
    HashMap<String, double[]> reply = new HashMap<String, double[]>();
    String [] sqlSelect = {"name", "RA_center", "DE_center"};
    try{
      Cursor c = this.getDataCursor(sqlTables,sqlSelect,null,null,null,null,null);
      if(c != null){
        if(c.moveToFirst()){
          do{
            double[] RA_DE_center = new double[]{c.getDouble(1),c.getDouble(2)};
            reply.put(c.getString(0),RA_DE_center);
          }while(c.moveToNext());
        }
        c.close();
      }
      return reply;
    } catch (Exception e) {
      throw new Exception(TAG + " : " + e.getMessage());
    }
  }

}
