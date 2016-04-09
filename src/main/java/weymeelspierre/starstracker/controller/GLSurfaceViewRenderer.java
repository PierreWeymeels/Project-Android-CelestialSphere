package weymeelspierre.starstracker.controller;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import weymeelspierre.starstracker.renderOpenGl.RenderCommand;
import weymeelspierre.starstracker.renderOpenGl.RenderManager;
import weymeelspierre.starstracker.activity.HeavenCanopyActivity;

/**
 * Created by Pierre on 9/11/2014.
 */
public class GLSurfaceViewRenderer implements GLSurfaceView.Renderer{

  private static final String TAG = "HeavenCanopyRenderer";
  private HeavenCanopyActivity hcActivity = null;

  private int withGlScreen;
  private int heightGlScreen;

  private RenderManager renderManager;

  public GLSurfaceViewRenderer(HeavenCanopyActivity hcActivity,
                               RenderManager renderManager) {
    this.hcActivity = hcActivity;
    this.renderManager = renderManager;

    GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    GLES20.glDepthMask(true);
    GLES20.glDepthFunc(GLES20.GL_LEQUAL);//GLES20.GL_ALWAYS);
  }

  @Override
  public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
    // Set the background clear color to night blue.
    GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);//black
    // 0.98f, 0.98f, 0.98f, 1.0f);//fair grey false color for report
    try{
      renderManager.iniRender();
    } catch (Exception e) {
      Log.e("onSurfaceCreated", e.getMessage());
      msgToHeavenCanopyActivity("ALERT", "Une erreur est survenue !");
    }
  }

  @Override
  public void onSurfaceChanged(GL10 glUnused, int width, int height) {
    // Set the OpenGL viewport to the same size as the surface.
    withGlScreen = width;
    heightGlScreen = height;
    //GLES20.glDepthRangef(0.0f,1.0f);
    GLES20.glViewport(0, 0, width, height);
    try{
      renderManager.alterStereoProjMatrix(width, height);
    } catch (Exception e) {
      Log.e("onSurfaceCreated", e.getMessage());
      msgToHeavenCanopyActivity("ALERT", "Une erreur est survenue !");
    }
  }

  @Override
  public void onDrawFrame(GL10 glUnused) {
    // Nettoyage de l'écran
    GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
    try{
      renderManager.draw(withGlScreen,heightGlScreen);
    } catch (Exception e) {
      Log.e("onDrawFrame", e.getMessage());
      msgToHeavenCanopyActivity("ALERT", "Une erreur est survenue !");
    }
  }

  private void msgToHeavenCanopyActivity(final String type,final String msg){
    hcActivity.runOnUiThread(new Runnable() {
      public void run() {
        hcActivity.backMessage(type, msg);
      }
    });
  }

  /*
  public void checkGLError() {
    int error;
    while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
      Log.e("MyApp", TAG + ": glError " + error);
    }
  }*/

}

