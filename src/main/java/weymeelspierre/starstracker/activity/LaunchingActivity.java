package weymeelspierre.starstracker.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import weymeelspierre.starstracker.R;
import weymeelspierre.starstracker.controller.HeavenCanopyController;
import weymeelspierre.starstracker.controller.LaunchingController;

/**
 * Created by Pierre on 3/11/2014.
 */
public class LaunchingActivity extends Activity {

  private final static String TAG = "LaunchingActivity";
  private final static int SET_POSITION_RETURN = 1;
  private ProgressBar bar = null;
  private TextView text = null;
  private LaunchingController launchCtrl = null;
  private boolean returnOfGpsSetting = false;
  private boolean firstOnResume = true;
  private boolean returnOfHeavenCanopyActivity = false;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.launching_layout);
    bar = (ProgressBar) findViewById(R.id.launchingBar);
    text = (TextView) findViewById(R.id.textView);
    text.setVisibility(View.VISIBLE);
  }

  @Override
  protected void onRestart() {
    super.onRestart();
    if (returnOfHeavenCanopyActivity){
      returnOfHeavenCanopyActivity = false;
      firstOnResume = true;//finish();//onDestroy();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (returnOfGpsSetting) {
      returnOfGpsSetting = false;
      launchingIniStep(true, false, 0.0, 0.0);
    } else if (firstOnResume) {
      firstOnResume = false;
      askGpsInitialisation();
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }

  public void launchingIniStep(boolean withLocation, boolean manualIni,
                               double latitude, double longitude) {
    try {
      if (manualIni)
        launchCtrl = new LaunchingController(this, latitude, longitude);
      else
        launchCtrl = new LaunchingController(this, withLocation);
      launchCtrl.execute();
    } catch (Exception ex) {
      String errMsg = ex.getMessage();
      if(errMsg.compareTo("GPS_DISABLED")==0)
        backMessage("TOAST",errMsg);
      else
        backMessage("ALERT",errMsg);
    }
  }

  public void askGpsInitialisation() {
    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

    alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
      @Override
      public void onCancel(DialogInterface dialog) {
        finish();
      }
    });
    //text.setText("Appuyer sur la touche retour pour fermer l'application !.");

    alertDialog.setTitle("Initialisation");
    alertDialog.setMessage("Voulez-vous initialiser votre position ?");
    alertDialog.setPositiveButton("Avec GPS", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        checkIfGpsEnable();
      }
    });
    alertDialog.setNeutralButton("Manuellement", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        launchSetPositionActivity();
      }
    });
    alertDialog.setNegativeButton("Non", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        launchingIniStep(false, false, 0.0, 0.0);
      }
    });
    alertDialog.show();
  }

  private void checkIfGpsEnable() {
    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    try {
      boolean gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
      if (gps_enabled) {
        launchingIniStep(true, false, 0.0, 0.0);
      } else {
        returnOfGpsSetting = true;
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
      }
    } catch (Exception ex) {
      String t = "t";
    }
  }

  private void launchSetPositionActivity() {
    Intent i = new Intent(this, SetPositionActivity.class);
    startActivityForResult(i, SET_POSITION_RETURN);

  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      case (SET_POSITION_RETURN): {
        if (resultCode == Activity.RESULT_OK) {
          double latitude = data.getDoubleExtra("latitude", 0.0);
          double longitude = data.getDoubleExtra("longitude", 0.0);
          launchingIniStep(true, true, latitude, longitude);
        }else if(resultCode == Activity.RESULT_CANCELED){
          firstOnResume = true;
        }
        break;
      }
    }
  }

  public void iniProgressBar() {
    bar.setVisibility(ProgressBar.VISIBLE);
  }

  public void updateProgressBar(int value) {
    bar.setProgress(value);
  }

  public void startHeavenCanopyActivity() {
    text.setText("");
    bar.setVisibility(ProgressBar.INVISIBLE);
    returnOfHeavenCanopyActivity = true;
    Intent intent = new Intent(this, HeavenCanopyActivity.class);
    startActivity(intent);
  }

  public void backMessage(String msgType, String msg) {
    if (msgType.compareTo("TOAST") == 0) {
      Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
      if(msg.compareTo("GPS_DISABLED")==0)
        askGpsInitialisation();
    } else if (msgType.compareTo("ALERT") == 0) {
      showAlert(msg, true);
    } else if (msgType.compareTo("GPS_ALERT") == 0) {
      showAlert(msg, false);
    }
  }

  private void showAlert(String err, final boolean fatalError) {
    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this)
            .setTitle("ALERT !")
            .setMessage(err)
            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                if (fatalError)
                  finish();
                else
                  askGpsInitialisation();
              }
            });
    alertDialog.show();
  }

  public void setText(String msg) {
    text.setText(msg);
  }
}
