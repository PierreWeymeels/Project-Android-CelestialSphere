package weymeelspierre.starstracker.hac;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import weymeelspierre.starstracker.activity.LaunchingActivity;

/**
 * Created by Pierre on 22/12/2014.
 */
public class LocationHac extends Thread implements LocationListener {

  private final static String TAG = "LocationHac";

  private LaunchingActivity lActivity = null;
  private LocationManager lm;
  private boolean manualLocation = false;

  private double latitude;
  private double longitude;
  private double altitude;
  private float accuracy;
  private Handler mUserLocationHandler = null;
  private final float MIN_DISTANCE_UPDATES = 0; // 0 meters
  private final long MIN_TIME_UPDATES = 0; // 1 sec
  private boolean gpsUpdated = false;


  public LocationHac(double latitude, double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.manualLocation = true;
  }

  public LocationHac(LaunchingActivity launchingActivity) throws Exception {
    this.lActivity = launchingActivity;
    lm = (LocationManager) launchingActivity.getSystemService(Context.LOCATION_SERVICE);
    if(lm == null){
      throw new Exception("LOCATION_SERVICE indisponible !");
    }
    if ( ! lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
      throw new Exception("GPS_DISABLED");
  }


  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setNullLaunchingParam() {
    this.lActivity = null;
  }

  public void run() {
    Looper.prepare();
    mUserLocationHandler = new Handler();
    try {
      if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER) && (lm != null)) {
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                MIN_TIME_UPDATES, MIN_DISTANCE_UPDATES, this);
      } else {
        Log.e(TAG, "GPS_DISABLED");
        stopLoop();
      }
    } catch (Exception e) {
      Log.e(TAG, "run error");
      stopLoop();
      this.interrupt();
      int  i = 0;
    }
    Looper.loop();
  }

  @Override
  public void onLocationChanged(Location location) {
    try {
      stopLoop();
      lm.removeUpdates(this);
      lm = null;
      latitude = location.getLatitude();
      longitude = -location.getLongitude();
      altitude = location.getAltitude();
      accuracy = location.getAccuracy();
      gpsUpdated = true;
      this.interrupt();
    } catch (Exception e) {
      Log.e(TAG, e.getMessage());
    }
  }

  @Override
  public String toString() {
    return "Latitude : " + latitude + "\n" +
            "Longitude : " + longitude + "";
  }

  @Override
  public void onStatusChanged(String s, int i, Bundle bundle) {
    String t = "t";
  }

  @Override
  public void onProviderEnabled(String s) {
    String t = "t";
  }

  @Override
  public void onProviderDisabled(String s) {
    String t = "t";
  }


  public boolean isManualLocation() {
    return manualLocation;
  }



  public void stopLoop() {
    //le thread quitte la boucle d'instruction
    if (mUserLocationHandler != null) {
      mUserLocationHandler.getLooper().quit();
    }
  }

  public boolean isGpsUpdated() {
    return gpsUpdated;
  }

//----------------------------------------------------------------------------------
  /*
  @Override
  public void run() {
    super.run();
    try {

      if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        Looper.prepare();
        mUserLocationHandler = new Handler() ;
        lm.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 0,0, this);
        Looper.loop();
        String t ="t";
      } else
        throw new Exception(TAG + " Provider GPS disabled !");

    } catch (Exception e) {
      Log.e(TAG, e.getMessage());
    }
  }*/

  /*
  public void iniGPS() throws Exception {
    try {
      if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            lm.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, MIN_TIME_UPDATES,MIN_DISTANCE_UPDATES, this);

      } else
        throw new Exception(TAG + " Provider GPS disabled !");
    } catch (Exception e) {
      Log.e(TAG, e.getMessage());
    }
  }*/
}
