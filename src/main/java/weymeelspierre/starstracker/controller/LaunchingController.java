package weymeelspierre.starstracker.controller;

import android.os.AsyncTask;
import android.util.Log;

import weymeelspierre.starstracker.hac.LocationHac;
import weymeelspierre.starstracker.model.HeavenCanopy;
import weymeelspierre.starstracker.activity.LaunchingActivity;

/**
 * Created by Pierre on 3/11/2014.
 */
public class LaunchingController extends AsyncTask<Void, Integer, Void> {

  private final static String TAG = "LaunchingController";
  private LaunchingActivity launchActi = null;
  private boolean withLocation = false;
  private boolean manualLocationIni = false;

  private LocationHac locationHac = null;
  private double latitude = 0.0;
  private double longitude = 0.0;

  public LaunchingController(LaunchingActivity launchActi, boolean withLocation) throws Exception {
    this.launchActi = launchActi;
    this.withLocation = withLocation;
    manualLocationIni = false;
    if (withLocation) {
      locationHac = new LocationHac(launchActi);
      locationHac.start();
      }
  }

  public LaunchingController(LaunchingActivity launchActi, double latitude, double longitude) {
    this.launchActi = launchActi;
    this.withLocation = true;
    manualLocationIni = true;
    this.latitude = latitude;
    this.longitude = longitude;
  }

  //boolean On UI thread
  @Override
  protected void onPreExecute() {
    if (withLocation && !manualLocationIni)
      launchActi.setText("Initialisation GPS...");
    else
      launchActi.setText("Initialisation en cours...");
    launchActi.iniProgressBar();
  }

  // On background thread
  @Override
  protected Void doInBackground(Void... voids) {
    int[] progress = new int[]{10, 100};
    try {
      if (withLocation && !manualLocationIni) {
        waitLocationUpdate();
        latitude = locationHac.getLatitude();
        longitude = locationHac.getLongitude();
      }else if(manualLocationIni)
        locationHac = new LocationHac(latitude,longitude);
      publishProgress((Integer) progress[0]);
      HeavenCanopy hCanopy = new HeavenCanopy(launchActi, this,latitude,longitude, withLocation);
      publishProgress((Integer) progress[1]);
      new HeavenCanopyController(hCanopy, hCanopy.getRenderManager(),locationHac);
    } catch (Exception e) {
      Log.e(TAG, e.getMessage());
      msgToLaunchingActivity("ALERT", "Une erreur est survenue !");
      cancel(true);//will call onCancelled instead of onPostExecute.
    }
    return null;
  }

  private void waitLocationUpdate() throws InterruptedException {
    int i = 0;
    while(!locationHac.isGpsUpdated() && (i<10)){
      synchronized (this) {
        wait(1000);
      }
      ++i;
      publishProgress((Integer) (i%2)*100);
    }
    publishProgress((Integer) 0);
    if(!locationHac.isGpsUpdated() && (i==10)){
      msgToLaunchingActivity("GPS_ALERT", "Le gps tarde à fournir la position !\n"+
          "Le mode manuel est une option à envisager.");
      locationHac.stopLoop();
      cancel(true);
    }else{
      launchActi.runOnUiThread(new Runnable() {
        public void run() {
          launchActi.setText("Initialisation en cours...");
        }
      });
    }
  }


  @Override
  protected void onCancelled() {
    super.onCancelled();
  }

  // On UI thread
  @Override
  public void onProgressUpdate(Integer... values) {
    super.onProgressUpdate(values);
    launchActi.updateProgressBar(values[0]);
  }

  // On UI thread
  @Override
  protected void onPostExecute(Void aVoid) {
    super.onPostExecute(aVoid);
    launchActi.startHeavenCanopyActivity();
  }

  private void msgToLaunchingActivity(final String type,final String msg){
    launchActi.runOnUiThread(new Runnable() {
      public void run() {
        launchActi.backMessage(type, msg);
      }
    });
  }

}
