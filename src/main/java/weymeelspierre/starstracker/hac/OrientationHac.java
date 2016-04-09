package weymeelspierre.starstracker.hac;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.List;

import weymeelspierre.starstracker.activity.HeavenCanopyActivity;

/**
 * Created by Pierre on 20/12/2014.
 */
public class OrientationHac  implements SensorEventListener{

    private static final String LOG_TAG = "OrientationHac";
    private HeavenCanopyActivity hcActivity;
    private SensorManager sensorManager;
    private Sensor rotationSensor = null;
    public boolean exist = false;
    private float maxRange;
    /**
     * Rotation vector values:
     * xRot: x*sin(θ/2)
     * yRot: y*sin(θ/2)
     * zRot: z*sin(θ/2)
     * cosRot: cos(θ/2)
     * angle θ which the device has rotated around the axis <x, y, z>.
     */
    float xRot, yRot, zRot, cosRot;
    private float[] mRotationMatrix = new float[16];

    /* FOR THE S3 DEVICE owner.compareTo("Google Inc.")==0) && (version == 3) ok POUR
        TYPE_GRAVITY et TYPE_LINEAR_ACCELERATION
     */



    /*
    *http://developer.android.com/guide/topics/sensors/sensors_motion.html
    * Android Open Source Project Sensors : a gravity sensor, a linear acceleration sensor,
     * and a rotation vector sensor,
     * in Android 4.0 and now use a device's gyroscope , identify them by using the getVendor()
      * method and the getVersion() : vendor is Google Inc. , the version number is 3
     */
    public OrientationHac(HeavenCanopyActivity hcActivity) {
        this.hcActivity = hcActivity;
        Context context = hcActivity;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        //rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ROTATION_VECTOR);
        for (Sensor sensor : sensors) {
            String owner = sensor.getVendor();
            int version = sensor.getVersion();
            if((owner.compareTo("Google Inc.")==0) && (version == 3)){
                rotationSensor = sensor;
                exist = true;
                maxRange = rotationSensor.getMaximumRange();
            }
        }
        String test = "ok";
    }

    public void register_listener(){
        if(exist) {//SENSOR_DELAY_UI => delay = 66667 *10 -6sec
            sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void unregister_listener(){
        if(exist) {
            sensorManager.unregisterListener(this, rotationSensor);
        }
    }

    /*
     *  http://developer.android.com/reference/android/hardware/
     *  SensorManager.html#getRotationMatrixFromVector(float[], float[])
     *  http://developer.android.com/reference/android/hardware/SensorEvent.html#values
     *  http://developer.android.com/guide/topics/sensors/sensors_motion.html
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        final SensorEvent event  = sensorEvent;
        String test = "ok";
        new Thread(new Runnable(){
            @Override
            public void run() {
                if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){
                    String owner = event.sensor.getVendor();
                    int version = event.sensor.getVersion();
                    if((owner.compareTo("Google Inc.")==0) && (version == 3)) {
                        // La valeur angulaire pour chaque axe
                        xRot = event.values[0];
                        yRot = event.values[1];
                        zRot = event.values[2];
                        //values[4] is a new value that has been added in SDK Level 18.
                        if (event.values.length == 4) {
                            cosRot = event.values[3];
                        }
                        hcActivity.runOnUiThread(new Runnable() {
                            public void run() {
                                hcActivity.backMessage("INFO","" + xRot + " , "+ yRot + "," + zRot + "," + cosRot +"");
                                String test3 = "ok";
                            }
                        });
                        //Cette matrice est interprétée par OpenGl comme étant l'inverse
                        //du vecteur de rotation.Vérifier mRotationMatrix.length ==9 || ==16
                        // SensorManager.getRotationMatrixFromVector(mRotationMatrix, event.values);
                    }
                }
            }
        }).start();
        String test2 = "ok";
    }

    /*
	 * http://developer.android.com/reference/android/hardware/SensorEventListener.html
	 */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        String test = "ok";
        /*if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            String accuracyStr;
            if (SensorManager.SENSOR_STATUS_ACCURACY_HIGH == i) {
                accuracyStr = "SENSOR_STATUS_ACCURACY_HIGH";
            } else if (SensorManager.SENSOR_STATUS_ACCURACY_LOW == i) {
                accuracyStr = "SENSOR_STATUS_ACCURACY_LOW";
            } else if (SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM == i) {
                accuracyStr = "SENSOR_STATUS_ACCURACY_MEDIUM";
            } else {
                accuracyStr = "SENSOR_STATUS_UNRELIABLE";
            }
            Log.d(LOG_TAG, "Values (" + xRot + "," + yRot + "," + zRot + ") ,accuracy : "
                    + accuracyStr);
        }*/
    }


    @Override
    public String toString(){
        return "Values ( " + xRot + "," + yRot + "," + zRot + "," + cosRot +" )";
    }
}
