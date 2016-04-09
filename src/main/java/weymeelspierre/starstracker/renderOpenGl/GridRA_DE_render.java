package weymeelspierre.starstracker.renderOpenGl;

import android.opengl.GLES20;

/**
 * Created by Pierre on 27/11/2014.
 */
public class GridRA_DE_render extends AbstractRender {
  private static final String TAG = "GridRA_DE_render";
  private float[] gridColor;


  private final String vertexShader =
          "attribute vec3 a_Position;" +
                  "uniform mat4 u_MVPMatrix;" +

                  "void main() {" +
                  " gl_Position = u_MVPMatrix * vec4(a_Position,1.0);" +
                  " gl_PointSize = 1.0;" +
                  "}";


  private final String fragmentShader =
          "precision mediump float;" +
                  "uniform vec4 color;" +

                  "void main() {" +
                  " gl_FragColor = color;" +
                  "}";

  public GridRA_DE_render(RenderManager renderManager, float[] positionData,
                          float[] colorData) throws Exception {
    nbDots = positionData.length / mPositionDataSize;
    gridColor = colorData;
    positions = changeIntoFloatBuffer(positionData);
    renderManager.putInRenderHMap(this.TAG, this);
  }


  private int prepareUniformColorsForShader() {
    // get handle to fragment shader's vColor member
    int color_GlslLocation = GLES20.glGetUniformLocation(programHandle, "color");
    // Set color blue for drawing
    GLES20.glUniform4f(color_GlslLocation, gridColor[0], gridColor[1],
            gridColor[2], gridColor[3]);
    return color_GlslLocation;
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

    int position_glslLocation = preparePositionForShader();
    int color_glslLocation = prepareUniformColorsForShader();
    int matrixMVP_glslLocation = prepareMatrixMVPForShader();

    // Draw the points
    GLES20.glDrawArrays(GLES20.GL_POINTS, 0, nbDots);
    // Disable vertex array
    GLES20.glDisableVertexAttribArray(matrixMVP_glslLocation);
    GLES20.glDisableVertexAttribArray(color_glslLocation);
    GLES20.glDisableVertexAttribArray(position_glslLocation);
    // Deactivate shader(s)
    GLES20.glUseProgram(0);
    //delete:
    deleteProgramAndShader();
  }

  protected String getVertexShader() {
    return vertexShader;
  }

  protected String getFragmentShader() {
    return fragmentShader;
  }
}

