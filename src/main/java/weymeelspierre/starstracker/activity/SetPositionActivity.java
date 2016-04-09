package weymeelspierre.starstracker.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import weymeelspierre.starstracker.R;

public class SetPositionActivity extends Activity {
  private final static String TAG = "SetPositionActivity";
  private Switch switchBxl = null;
  private TextView textLatitude = null;
  private TextView textLongitude = null;
  private Button iniButton = null;
  private boolean isLatitudeTxt = false;
  private boolean isLongitudeTxt = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.setposition_layout);
    iniButton = (Button) findViewById(R.id.butIni);
    switchBxl = (Switch) findViewById(R.id.switchBxl);
    textLatitude = (TextView) findViewById(R.id.latitude);
    textLongitude = (TextView) findViewById(R.id.longitude);
    onClickListener();
    switchBxlListener();
    latitudeTxtListener();
    longitudeTxtListener();
  }

  private void onClickListener() {
    final Button button = (Button) findViewById(R.id.butIni);
    button.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        initializePosition();
      }
    });
  }


  public void initializePosition(){
    if(isLatitudeTxt && isLongitudeTxt){
      String latitudeStr = String.valueOf(textLatitude.getText());
      String longitudeStr = String.valueOf(textLongitude.getText());
      RadioButton estRb = (RadioButton)findViewById(R.id.est);
      RadioButton sudRb = (RadioButton)findViewById(R.id.sud);
      if(estRb.isChecked())
        longitudeStr = "-".concat(longitudeStr);
      if(sudRb.isChecked())
        latitudeStr = "-".concat(latitudeStr);
      sendToLaunchActivity(Double.parseDouble(latitudeStr),Double.parseDouble(longitudeStr));
    }else{
      showAlert();
    }

  }

  private void sendToLaunchActivity(double latitude,double longitude) {
    Intent resultIntent = new Intent();
    resultIntent.putExtra("latitude",latitude);
    resultIntent.putExtra("longitude",longitude);
    setResult(RESULT_OK, resultIntent);
    finish();
  }

  //LISTENERS:------------------------------------------------------------------
  private void switchBxlListener() {
    switchBxl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          sendToLaunchActivity(50.85033960,-4.3517103);
        }
      }
    });
  }

  private void latitudeTxtListener() {
    textLatitude.addTextChangedListener(new TextWatcher() {
      @Override
      public void afterTextChanged(Editable s) {
        int a=0;
        if(s.length() !=0){
          int number = Integer.parseInt(s.toString());
          if(isInRange(0,90,number)) {
            isLatitudeTxt = true;
          }else {
            s.delete(0, s.length());
            backMsg("La valeur doit être comprise entre 0 et 90 degrées !");
            isLatitudeTxt = false;
          }
        }else
          isLatitudeTxt = false;
      }


      @Override
      public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
      }
    });
  }

  private void longitudeTxtListener() {
    textLongitude.addTextChangedListener(new TextWatcher() {
      @Override
      public void afterTextChanged(Editable s) {
        int a=0;
        if(s.length() !=0){
          int number = Integer.parseInt(s.toString());
          if(isInRange(0,180,number)) {
            isLongitudeTxt = true;
          }else {
            s.delete(0, s.length());
            backMsg("La valeur doit être comprise entre 0 et 180 degrées !");
            isLongitudeTxt = false;
          }
        }else
          isLongitudeTxt = false;
      }

      @Override
      public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
      }
    });
  }
//------------------------------------------------------------------------------------------

  @Override
  public void onBackPressed() {
    Intent mIntent = new Intent();
    setResult(Activity.RESULT_CANCELED, mIntent);
    super.onBackPressed();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.set_position, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    if (id == R.id.action_settings) {
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

//-----------------------------------------------------------------------

  private boolean isInRange(int min, int max,int value) {
    return  (value >= min) && (value<=max);
  }

  private void backMsg(String msg) {
    Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
  }

  private void showAlert() {
    String err = "La latitude doit être comprise entre 0 et 90 degrées !"+ "\n"+
            "La longitude doit être comprise entre 0 et 180 degrées !";
    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this)
            .setTitle("ALERT !")
            .setMessage(err)
            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
              }
            });
    alertDialog.show();
  }
}
