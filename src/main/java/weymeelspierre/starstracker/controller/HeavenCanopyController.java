package weymeelspierre.starstracker.controller;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import weymeelspierre.starstracker.activity.HeavenCanopyActivity;
import weymeelspierre.starstracker.hac.LocationHac;
import weymeelspierre.starstracker.model.HeavenCanopy;
import weymeelspierre.starstracker.renderOpenGl.Render;
import weymeelspierre.starstracker.renderOpenGl.RenderManager;

/**
 * Created by Pierre on 4/11/2014.
 */
public class HeavenCanopyController {
  private final static String TAG = "HeavenCanopyController";
  private static HeavenCanopyController _instance = null;
  private boolean modeAzimuthal = false;

  private HeavenCanopy hCanopy = null;
  private RenderManager renderManager = null;
  private HeavenCanopyActivity hcActivity = null;
  private LocationHac locationHac = null;

  private Timer solarBodiesTimer = null;
  private Timer raDeToAzModelTimer = null;


  public HeavenCanopyController(HeavenCanopy hCanopy,RenderManager renderManager,
                                LocationHac locationHac) {
    this.hCanopy = hCanopy;
    this.renderManager = renderManager;
    if(locationHac != null) {
      locationHac.setNullLaunchingParam();
      this.locationHac = locationHac;
    }
    _instance = this;
  }

  public static HeavenCanopyController InstanceOfHeavCanoCtrl() {//HeavenCanopyActivity hcActivity
    if (_instance == null)
      return null;//ERREUR INITIALISATION !
    //_instance.hcActivity = hcActivity;
    return _instance;
  }

  public RenderManager getRenderManager() {
    return renderManager;
  }

  public void userChoice(final char choice) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        try{
          switch (choice) {
            case 'a':
              setAzimuthalMode(true) ;
              break;
            case 'e':
              setAzimuthalMode(false);
              break;
            case 'i':
              getInfoForUser();
              break;
            case 'g':
              renderActivation(Render.GridRA_DE_render);
              break;
          }
        } catch (Exception e) {
          Log.e(TAG, e.getMessage());
          msgToHeavenCanopyActivity("TOAST", " Le choix n'a pu aboutir !");
        }
      }
    }).start();
  }

  private synchronized void getInfoForUser() {
    final String msg;
    if(isWithLocation())
      msg = locationHac.toString();
    else
      msg = "Pas de position initialisée !";
    msgToHeavenCanopyActivity("TOAST",msg);
  }

  private void renderActivation(Render render) throws Exception {
    if(render != null) {
      if (renderManager.isActiveRender(render))
        renderManager.activateRender(render, false);
      else
        renderManager.activateRender(render, true);
      glSurfaceRenderingRequest();
    }
  }

  public boolean isWithLocation(){
  if(locationHac != null)
    return true;
  else
   return false;
}

  public synchronized void setAzimuthalMode(boolean activate) throws Exception {
    if((modeAzimuthal == !activate) && (locationHac != null)){
      this.modeAzimuthal = activate;
      if(activate)
        renderManager.alterRaDe_toAzModel(locationHac.getLatitude(),locationHac.getLongitude());
      renderManager.setAzimutalMode(activate);
      glSurfaceRenderingRequest();
      raDeToAzModelUpdate(activate);
    }
  }

  //UPDATE TIMER METHODS:-----------------------------------------------------------

  /**
   * update après 20 sec, et toutes les 20sec
   * Utile même si pas de maj de la latitude et de la longitude
   * car le temps sidéral est calculé en aval.
   * @param start
   */
  public void raDeToAzModelUpdate(boolean start) {
    if(start){
      raDeToAzModelTimer = new Timer();
      raDeToAzModelTimer.schedule(new TimerTask() {
        public void run() {
          try {
            renderManager.alterRaDe_toAzModel(
                    locationHac.getLatitude(),locationHac.getLongitude());
            glSurfaceRenderingRequest();
          } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            msgToHeavenCanopyActivity("TOAST","Une erreur est survenue !");
          }
        }
      }, 20000, 20000);
    }else if(raDeToAzModelTimer != null){
      raDeToAzModelTimer.cancel();
      raDeToAzModelTimer = null;
    }
  }

  //update après 20 sec, et toutes les 20sec
  public void solarBodiesUpdate(boolean start) {
    if(start){
      solarBodiesTimer = new Timer();
      solarBodiesTimer.schedule(new TimerTask() {
        public void run() {
          try {
            hCanopy.updateSolarSystem();
            glSurfaceRenderingRequest();
          } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            msgToHeavenCanopyActivity("TOAST","Une erreur est survenue !");
          }
}
}, 20000, 20000);
        }else if(solarBodiesTimer != null){
        solarBodiesTimer.cancel();
        solarBodiesTimer = null;
        }
        }
//--------------------------------------------------------------------------

//MAIN AND OpenGl THREADS METHODS:-----------------------------------------
private void msgToHeavenCanopyActivity(final String type,final String msg){
        hcActivity.runOnUiThread(new Runnable() {
public void run() {
        hcActivity.backMessage(type, msg);
        }
        });
        }

private void glSurfaceRenderingRequest(){
final GLSurfaceView glView = hcActivity.getGlView();
        glView.queueEvent(new Runnable() {
public void run() {
        glView.requestRender();
        }
        });
        }

public boolean isModeAzimuthal() {
        return modeAzimuthal;
        }

  public void saveInstance() {
    _instance = this;
  }

  public void setHcActivity(HeavenCanopyActivity hcActivity) {
    this.hcActivity = hcActivity;
  }
  //--------------------------------------------------------------------------
  public static boolean instanceExist(){
    if (_instance == null)
      return false;
    return true;
  }

  /*public void setToInitialState(LocationHac locationHac){

  }*/

}