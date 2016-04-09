package weymeelspierre.starstracker.model;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Date;
import java.util.HashMap;

import weymeelspierre.starstracker.R;
import weymeelspierre.starstracker.controller.LaunchingController;
import weymeelspierre.starstracker.renderOpenGl.NamesRender;
import weymeelspierre.starstracker.renderOpenGl.RenderManager;

/**
 * Created by Pierre on 27/11/2014.
 */
public class HeavenCanopy {
  private final static String TAG = "HeavenCanopy";
  private final Context context;
  private final boolean withLocation;

  private GridRA_DE gridRA_DE = null;
  private StarsDome starsDome = null;
  private Ephemeris ephemeris = null;
  private Moon moon = null;
  private Sun sun = null;
  private Names names = null;
  private GeoElements geoElements = null;
  private RenderManager renderManager;


  public HeavenCanopy(Context c, LaunchingController lCtrl,
                      double latitude, double longitude,boolean withLocation) throws Exception {
    this.context = c;
    this.withLocation = withLocation;
    renderManager = new RenderManager();
    long t0 = new Date(System.currentTimeMillis()).getTime();
    initialize(lCtrl,latitude,longitude);
    long t1 = new Date(System.currentTimeMillis()).getTime();
    long t = t1 - t0;
    String test = "ok";
  }

  private void initialize(LaunchingController lCtrl,double latitude,
                          double longitude) throws Exception {
    try {
      int[] progress = new int[]{20,30,40,50,60,70,80,90};
      if(withLocation)
        geoElements = new GeoElements(renderManager,latitude,longitude);
      lCtrl.onProgressUpdate((Integer) progress[0]);
      starsDome = new StarsDome(context, renderManager);
      lCtrl.onProgressUpdate((Integer) progress[1]);
      gridRA_DE = new GridRA_DE(renderManager);
      lCtrl.onProgressUpdate((Integer) progress[2]);
      names = new Names(context,renderManager,starsDome);
      lCtrl.onProgressUpdate((Integer) progress[7]);
      ephemeris = new Ephemeris(context);
      lCtrl.onProgressUpdate((Integer) progress[3]);
      moon = new Moon(ephemeris);
      lCtrl.onProgressUpdate((Integer) progress[4]);
      sun = new Sun(ephemeris);
      lCtrl.onProgressUpdate((Integer) progress[5]);
      ephemeris.initializeSolarSystemBodyRender(renderManager);
      lCtrl.onProgressUpdate((Integer) progress[6]);
    } catch (Exception e) {
      throw new Exception(TAG + " : " + e.getMessage());
    }
  }

  public RenderManager getRenderManager() {
    return renderManager;
  }

  public void updateSolarSystem() throws Exception {
    try {
      ephemeris.clearSolSysPosition();
      moon.updatePosition();
      sun.updatePosition();
      ephemeris.updateSolarSystemBodyRender();
    } catch (Exception e) {
      throw new Exception(TAG + " : " + e.getMessage());
    }
  }

}
