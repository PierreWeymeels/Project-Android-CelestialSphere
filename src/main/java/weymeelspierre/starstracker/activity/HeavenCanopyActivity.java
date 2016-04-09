package weymeelspierre.starstracker.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import weymeelspierre.starstracker.R;
import weymeelspierre.starstracker.controller.HeavenCanopyController;
import weymeelspierre.starstracker.controller.GLSurfaceViewRenderer;
import weymeelspierre.starstracker.controller.GLSurfaceViewListener;
import weymeelspierre.starstracker.hac.OrientationHac;

/**
 * Created by Pierre on 4/11/2014.
 */
public class HeavenCanopyActivity extends Activity {
  private final static String TAG = "HeavenCanopyActivity";
  private GLSurfaceView glView = null;
  private HeavenCanopyController hcCtrl = null;
  private Menu action_bar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if( savedInstanceState != null ) {
      ;/*if(savedInstanceState.get...){
      }*/
    }
    setContentView(R.layout.heavencanopy_layout);//Without HeavenCanopyView class!

    hcCtrl = HeavenCanopyController.InstanceOfHeavCanoCtrl();
    if (hcCtrl == null){
      Log.e(TAG, "initialisation fatal error => InstanceOfHeavCanoCtrl==null !");
      return;
    }else {
      hcCtrl.setHcActivity(this);
      // Check if the system supports OpenGL ES 2.0.
      final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
      final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
      final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
      if (supportsEs2) {
        glView = (GLSurfaceView) findViewById(R.id.heavenCanopy_view);
        // Request an OpenGL ES 2.0 compatible context.
        glView.setEGLContextClientVersion(2);
        // Set the renderer to our demo renderer, defined below.
        GLSurfaceViewRenderer hcRenderer = new GLSurfaceViewRenderer(this,
                hcCtrl.getRenderManager());
        glView.setRenderer(hcRenderer);
        glView.setOnTouchListener(new GLSurfaceViewListener(this,glView,hcCtrl.getRenderManager()));
      } else {
        Log.e(TAG, "initialisation fatal error => supportsEs2 == false !");
        return;
      }
      // Render the view only when there is a change in the drawing data
      // and so when I call manually onDrawFrame ! Without that, it's automatic !
      glView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.heavencanopy_action_menu, menu);
    this.action_bar = menu;
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    if(hcCtrl.isWithLocation()){
      final MenuItem actionBarMenu = this.action_bar.findItem(R.id.menu_action);
      actionBarMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }
    if(hcCtrl.isModeAzimuthal()){
      final MenuItem item = this.action_bar.findItem(R.id.azimuth_mode);
      item.setChecked(true);
    }
    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_grid:
        hcCtrl.userChoice('g');
        return true;
      case R.id.action_info:
        hcCtrl.userChoice('i');
        return true;
      case R.id.equatorial_mode:
        if(!item.isChecked()) { //&& hcCtrl.getMode.compareTo("azimuthal"
          item.setChecked(true);
          hcCtrl.userChoice('e');
        }
      case R.id.azimuth_mode:
        if(!item.isChecked()) {
          item.setChecked(true);
          hcCtrl.userChoice('a');
        }
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  protected void onResume() {
    //orientationSensor.register_listener();
    super.onResume();
    glView.onResume();
    hcCtrl.solarBodiesUpdate(true);
    if(hcCtrl.isModeAzimuthal()){
      hcCtrl.raDeToAzModelUpdate(true);
    }
  }

  @Override
  protected void onPause() {
    //orientationSensor.unregister_listener();
    hcCtrl.solarBodiesUpdate(false);
    hcCtrl.raDeToAzModelUpdate(false);
    glView.onPause();
    super.onPause();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    //outState.put...;
    hcCtrl.saveInstance();
  }



  public void backMessage(String msgType, String msg) {
    if (msgType.compareTo("TOAST") == 0) {
      Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    } else if (msgType.compareTo("ALERT") == 0) {
      showAlert(msg);
    }
  }

  private void showAlert(String err) {
    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this)
            .setTitle("ALERT !")
            .setMessage(err)
            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                finish();
              }
            });
    alertDialog.show();
  }

  public GLSurfaceView getGlView() {
    return glView;
  }


}
