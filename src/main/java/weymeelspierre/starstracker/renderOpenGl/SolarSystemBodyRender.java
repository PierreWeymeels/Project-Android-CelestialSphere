package weymeelspierre.starstracker.renderOpenGl;

import android.opengl.GLES20;

import java.util.ArrayList;

/**
 * Created by Pierre on 7/01/2015.
 */
public class SolarSystemBodyRender extends AbstractRender {
  private static final String TAG = "SolarSystemBodyRender";

  private final String vertexShader =
          "attribute vec3 a_Position;" +
                  "attribute vec4 a_Color;" +
                  "uniform mat4 u_MVPMatrix;" +

                  "varying vec4 color;" +

                  "void main() {" +
                  " gl_Position = u_MVPMatrix * vec4(a_Position,1.0);" +
                  " gl_PointSize = 20.0;" +
                  " color = a_Color;" +
                  "}";


  private final String fragmentShader =
          "precision mediump float;" +
                  "varying vec4 color;" +

                  "void main() {" +
                  " gl_FragColor = color;" +
                  "}";


  public SolarSystemBodyRender(RenderManager renderManager,float[] positionData,float[] colorData) throws Exception {
    nbDots = positionData.length / mPositionDataSize;
    positions = changeIntoFloatBuffer(positionData);
    colors = changeIntoFloatBuffer(colorData);
    this.active = true;
    renderManager.putInRenderHMap(this.TAG, this);
  }

  @Override
  protected void iniProgramAndShader() throws Exception {
    iniProgramAndShader(vertexShader,fragmentShader);
  }

  @Override
  protected void setActive(Boolean activate) throws Exception {
    this.active = activate;
  }

  @Override
  protected void draw() throws Exception {
    setModelViewProjMatrix("RA_DErender");

    if(programHandle == -1)
      iniProgramAndShader(vertexShader,fragmentShader);
    // Add program to OpenGL ES environment
    GLES20.glUseProgram(programHandle);

    int position_GlslLocation = preparePositionForShader();
    int color_GlslLocation = prepareColorsForShader();
    int matrixMVP_GlslLocation = prepareMatrixMVPForShader();

    // Draw the points
    GLES20.glDrawArrays(GLES20.GL_POINTS, 0, nbDots);
    // Disable vertex array
    GLES20.glDisableVertexAttribArray(matrixMVP_GlslLocation);
    GLES20.glDisableVertexAttribArray(color_GlslLocation);
    GLES20.glDisableVertexAttribArray(position_GlslLocation);
    // Deactivate shader(s)
    GLES20.glUseProgram(0);
    //delete:
    deleteProgramAndShader();
  }

  public void update(float[] positionData) throws Exception {
    positions = changeIntoFloatBuffer(positionData);
  }

  protected String getVertexShader() {
    return vertexShader;
  }

  protected String getFragmentShader() {
    return fragmentShader;
  }
}

