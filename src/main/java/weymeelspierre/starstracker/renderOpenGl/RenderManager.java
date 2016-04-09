package weymeelspierre.starstracker.renderOpenGl;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.util.HashMap;
import java.util.Set;

import weymeelspierre.starstracker.library.Math_lib;
import weymeelspierre.starstracker.library.Time_lib;

/**
 * Created by Pierre on 10/01/2015.
 */
public class RenderManager implements RenderCommand {

  private static final String TAG = "RenderManager";



  private HashMap<String, AbstractRender> renderHMap = new HashMap<String, AbstractRender>();

  private float shereRadius = 1.0f;
  //Initial view:
  private double currentViewRaDirection = 81.2825;
  private double currentViewDeDirection = 0.0;
  private double[] eyeXyzPosition;
  private double[] viewXyzDirection;
  private double[] upXyzDirection;
  private float zoom = 1;


  public RenderManager() {
    alterViewData(currentViewRaDirection, currentViewDeDirection);
    AbstractRender.iniAbstractRenderStaticMatrix();
    /*
    try {
      putInRenderHMap("TestRender", new TestRender());
    } catch (Exception e) {
      e.printStackTrace();
    }*/
  }



  @Override
  public synchronized void iniRender() throws Exception {
    Set<String> keySet = renderHMap.keySet();
    for (String renderName : keySet) {
      AbstractRender render = renderHMap.get(renderName);
      render.iniProgramAndShader();
    }
  }

  @Override
  public synchronized void alterStereoProjMatrix(int width, int height) {
    float[] stereoProjMat = new float[16];
    final float value = 1.0f / zoom;
    final float ratio ;
    if(width < height) //PORTRAIT
      ratio = (float) width / height;
    else //LANDSCAPE
      ratio = (float) height / width;
    //Axe X (eye)
    final float left = -value * ratio;
    final float right = value * ratio;
    //Axe Y (eye)
    final float bottom = -value;
    final float top = value;
    //distance of the near plane and far plane
    final float near = 1.0f;
    final float far = 4.0f;//2.0f;
    if(width < height) //PORTRAIT
      Matrix.frustumM(stereoProjMat, 0, left, right, bottom, top, near, far);
    else  //LANDSCAPE
      Matrix.frustumM(stereoProjMat, 0, bottom, top, left, right, near, far);
    AbstractRender.setStereoProjMatrix(stereoProjMat);
  }

  @Override
  public synchronized void draw(int withGlScreen, int heightGlScreen) throws Exception {
    iniViewMatrix();
    alterStereoProjMatrix(withGlScreen, heightGlScreen);
    drawRender();
  }

  @Override
  public synchronized void setZoom(float amount) {
    zoom = Math.max(Math.min(zoom + amount / 100, 10.0f), 1.0f);
  }

  @Override
   public synchronized void setCenterOfView(double dx, double dy) {
    double win_dx = dx;
    double win_dy = -dy;
    double[] newViewXyzDirection;
    try {
      float[] winXyzCenter =
              Math_lib.obtainWinXyz(viewXyzDirection, getModelView(),
                      AbstractRender.getStereoProjMatrix(), getViewport());
      newViewXyzDirection = getWorldXyzAimPoint(
              winXyzCenter[0] + win_dx, winXyzCenter[1] + win_dy);
      //Area authorized for ViewXyzDirection[2] = [-0.98,0.98]:
      newViewXyzDirection[2] =
              Math.max(Math.min(newViewXyzDirection[2],0.98),-0.98);

    } catch (Exception e) {
      Log.e(TAG + " setCenterOfView ", e.getMessage());
      newViewXyzDirection = null;
    }
    //Area not authorized for radius of polar circle < 0.0396:
    if ((newViewXyzDirection != null) &&
            !isInsidePolarCircle(0.0396,newViewXyzDirection)) {
      alterViewData(newViewXyzDirection);
    }
  }

  private boolean isInsidePolarCircle(
          double radiusOfPolarCircle,double[] xyzVector){
    double radiusOfCircle = Math.sqrt(
            Math.pow(xyzVector[0], 2.0) + Math.pow(xyzVector[1], 2.0));
    if(radiusOfCircle<radiusOfPolarCircle)
      return true;
    return false;
  }

  @Override
  public synchronized double[] getWorldXyzAimPoint(double winX, double winY) throws Exception {
    float[] modelView = getModelView();
    float[] projectionMatrix = AbstractRender.getStereoProjMatrix();
    int[] viewport = getViewport();
    double[] newWorldXyz_of_newWinXy0 =
            Math_lib.obtainWorldXyz(winX, winY, 0.0,
                    modelView, projectionMatrix, viewport);
    double[] newWorldXyz_of_newWinXy1 =
            Math_lib.obtainWorldXyz(winX, winY, 1.0,
                    modelView, projectionMatrix, viewport);
    return Math_lib.obtainWorldXyzPerInterpolation(newWorldXyz_of_newWinXy0,
            newWorldXyz_of_newWinXy1);
  }

  //-------------------------------------------------------------------------
  @Override
  public synchronized boolean isActiveRender(Render renderList) {
    AbstractRender render = renderHMap.get(renderList.name());
    return render.isActive();
  }

  @Override
  public synchronized void activateRender(Render renderList, boolean activate) throws Exception{
    AbstractRender render = renderHMap.get(renderList.name());
    render.setActive(activate);
  }

  @Override
  public synchronized void alterRaDe_toAzModel(
          double latitude, double longitude) throws Exception {
    float[] model = new float[16];
    Matrix.setIdentityM(model, 0);
    //ORIGINE OF VERNAL DOT = axe x of equatorial system !
    // put axe x of equatorial (vernal dot) at sideral time of Hours system
    Matrix.multiplyMM(model, 0,
            Math_lib.get4RotMatrixEquatorialToHoursCoordinates(
                    Time_lib.getLocaleMeanSideralTime(longitude)*15),0, model, 0);
    //Change x to y axe for Hours system
    Matrix.multiplyMM(model, 0,
            Math_lib.rot4OfRepereXYCounterclockMatrix(90.0),0, model, 0);
    //put axe z (polar) on polar axe of azimuth system
    Matrix.multiplyMM(model, 0,
            Math_lib.get4RotMatrixHoursToAzimuthCoordinates(90.0-latitude),0, model, 0);
    AbstractRender.setRaDe_toAzModelMatrix(model);
  }

  @Override
  public synchronized void setAzimutalMode(boolean activate) throws Exception {
    AbstractRender.setAzimuthalMode(activate);
    AbstractRender geoRender = renderHMap.get("GeoElementsRender");
    if(geoRender != null)
      geoRender.setActive(activate);
  }

  //protected and private methods:------------------------------------------------------
  protected void putInRenderHMap(String renderTag, AbstractRender render) {
    renderHMap.put(renderTag, render);
  }

  private void iniViewMatrix() throws Exception {
    float[] viewMat = new float[16];
    // Set the view matrix. This matrix can be said to represent the camera position.
    // eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
    float[] f_eyeXyzPosition = Math_lib.convertFromVector3Double(eyeXyzPosition);
    float[] f_viewXyzDirection = Math_lib.convertFromVector3Double(viewXyzDirection);
    float[] f_upXyzDirection = Math_lib.convertFromVector3Double(upXyzDirection);
    Matrix.setLookAtM(viewMat, 0,
            f_eyeXyzPosition[0],f_eyeXyzPosition[1],f_eyeXyzPosition[2],
            f_viewXyzDirection[0], f_viewXyzDirection[1], f_viewXyzDirection[2],
            f_upXyzDirection[0], f_upXyzDirection[1], f_upXyzDirection[2]);
    //Matrix.setLookAtM(viewMatrix, 0,-1.0f,-1.0f,-1.0f,0.0f,0.0f,0.0f,0.0f,0.0f,1.0f);
    AbstractRender.setViewMatrix(viewMat);
  }

  private void drawRender() throws Exception {
    for (Render r : Render.values()) {
      AbstractRender render = renderHMap.get(r.name());
      if(render != null) {
        if (render.isActive())
          render.draw();
      }
    }
  }

  private float[] getModelView() {
    float[] modelView = new float[16];
    float[] modelId = new float[16];
    Matrix.setIdentityM(modelId, 0);
    Matrix.multiplyMM(modelView, 0, AbstractRender.getViewMatrix(),
            0,modelId, 0);

    /*
    Matrix.setIdentityM(AbstractRender.modelMatrix, 0);
    Matrix.multiplyMM(modelView, 0, AbstractRender.getViewMatrix(),
            0, AbstractRender.modelMatrix, 0);*/
    return modelView;
  }

  private int[] getViewport() {
    int[] viewport = new int[4];
    GLES20.glGetIntegerv(GLES20.GL_VIEWPORT, viewport, 0);
    return viewport;
  }

  private void alterViewData(double RA, double DE) {
    try {
      eyeXyzPosition = Math_lib.degreeRaDe_to_unityDirectionVectorXyz(RA + 180.0, -DE);
      double[] newViewXyzDirection = Math_lib.degreeRaDe_to_unityDirectionVectorXyz(RA, DE);
      upXyzDirection = Math_lib.viewDirXyz_to_towardOfNorthUpDirXyz(
              newViewXyzDirection, shereRadius);
      this.viewXyzDirection = newViewXyzDirection;
    } catch (Exception e) {
      Log.e(TAG + " alterViewDataRA_DE ", e.getMessage());
    }
  }

  private void alterViewData(double[] newViewXyzDirection) {
    try {
      for (int i = 0; i < 3; ++i) {
        eyeXyzPosition[i] = -newViewXyzDirection[i];
      }
      upXyzDirection = Math_lib.viewDirXyz_to_towardOfNorthUpDirXyz(
              newViewXyzDirection, shereRadius);
      this.viewXyzDirection = newViewXyzDirection;
    } catch (Exception e) {
      Log.e(TAG + " alterViewDataXYZ ", e.getMessage());
    }
  }

  //----------------------------------------------------


}
