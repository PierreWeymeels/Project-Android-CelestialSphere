package weymeelspierre.starstracker.renderOpenGl;

/**
 * Created by Pierre on 10/01/2015.
 */
public interface RenderCommand {

  void iniRender()throws Exception;

  void alterStereoProjMatrix(int width, int height);

  void draw(int withGlScreen, int heightGlScreen) throws Exception;

  void setZoom(float amount);

  void setCenterOfView(double dx, double dy);

  double[] getWorldXyzAimPoint(double x, double v) throws Exception ;

  boolean isActiveRender(Render renderList);

  void activateRender(Render namesRender, boolean b) throws Exception;

  void alterRaDe_toAzModel(double latitude, double longitude) throws Exception;

  void setAzimutalMode(boolean activate) throws Exception;
}
