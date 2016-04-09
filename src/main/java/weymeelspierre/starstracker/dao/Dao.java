package weymeelspierre.starstracker.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by Pierre on 5/02/2015.
 */
public class Dao extends SQLiteAssetHelper {

  private static final String TAG = "Dao";
  private static final String DATABASE_NAME = "db0102_starsTracker.db";
  private static final int DATABASE_VERSION = 1;


  protected Dao(Context context){
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  protected Cursor getDataCursor(
          String sqlTables,String [] projectionIn,String selection,String[] selectionArgs,
          String groupBy, String having, String sortOrder)throws Exception{
    SQLiteDatabase db = getReadableDatabase();
    SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
    qb.setTables(sqlTables);
    Cursor c = qb.query(db,projectionIn,selection,selectionArgs,groupBy, having, sortOrder);
    return c;
  }
}
