package weymeelspierre.starstracker.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceView;
import android.view.View;

import weymeelspierre.starstracker.R;
import weymeelspierre.starstracker.activity.HeavenCanopyActivity;
import weymeelspierre.starstracker.renderOpenGl.Render;
import weymeelspierre.starstracker.renderOpenGl.RenderManager;

/**
 * Created by Pierre on 3/12/2014.
 */
public class GLSurfaceViewListener implements
        GLSurfaceView.OnTouchListener, ScaleGestureDetector.OnScaleGestureListener {

  private final static String TAG = "HeavenCanopyController";
  private final ScaleGestureDetector mScaleDetector;
  private final GestureDetector mGestureDetector;
  private final HeavenCanopyActivity hcActivity;

  private RenderManager renderManager;
  private GLSurfaceView glView;

  float oldX = 0.0f;
  float oldY = 0.0f;

  private float mLastSpan = 0;

  public GLSurfaceViewListener(HeavenCanopyActivity hcActivity, GLSurfaceView glView,
                               RenderManager renderManager) {
    this.glView = glView;
    this.renderManager = renderManager;
    this.hcActivity = hcActivity;
    mScaleDetector = new ScaleGestureDetector(hcActivity.getApplicationContext(), this);
    mGestureDetector = new GestureDetector(hcActivity.getApplicationContext(), new GestureListener());
  }

  private class GestureListener extends GestureDetector.SimpleOnGestureListener {

    @Override
    public void onLongPress(MotionEvent e) {
      super.onLongPress(e);
      String t = "t";
    }

    @Override
    public boolean onDown(MotionEvent e) {
      return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
      new Thread(new Runnable() {
        @Override
        public void run() {
          try{
            Render render = Render.NamesRender;
            if (renderManager.isActiveRender(render))
              renderManager.activateRender(render, false);
            else
              renderManager.activateRender(render, true);
            glView.queueEvent(new Runnable() {
                public void run() {
                  glView.requestRender();
                }
              });
          } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
            msgToHeavenCanopyActivity("TOAST", " Le choix n'a pu aboutir !");
          }
        }
      }).start();
      return true;
    }
  }

  @Override
  public boolean onTouch(View view, MotionEvent motionEvent) {
    mScaleDetector.onTouchEvent(motionEvent);
    mGestureDetector.onTouchEvent(motionEvent);
    float x = motionEvent.getX();
    float y = motionEvent.getY();

    int action = motionEvent.getAction();
    switch (action) {
      case MotionEvent.ACTION_MOVE:
        final double dx = (x - oldX);
        final double dy = (y - oldY);
        glView.queueEvent(new Runnable() {
          public void run() {
            renderManager.setCenterOfView(dx, dy);
            glView.requestRender();
          }
        });
        break;
      case MotionEvent.ACTION_DOWN:
        oldX = x;
        oldY = y;
        break;
      case MotionEvent.ACTION_UP:
        break;
    }
    oldX = x;
    oldY = y;
    return true;
  }

  @Override
  public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
    mLastSpan = scaleGestureDetector.getCurrentSpan();
    return true;
  }

  @Override
  public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
    final float amount = scaleGestureDetector.getCurrentSpan() - mLastSpan;
    glView.queueEvent(new Runnable() {
      public void run() {
        renderManager.setZoom(amount);
        glView.requestRender();
      }
    });
    mLastSpan = scaleGestureDetector.getCurrentSpan();
    return true;
  }

  @Override
  public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
  }

  private void msgToHeavenCanopyActivity(final String type,final String msg){
    hcActivity.runOnUiThread(new Runnable() {
      public void run() {
        hcActivity.backMessage(type, msg);
      }
    });
  }

  //----------------------------------------------------------------------------------------------
}
