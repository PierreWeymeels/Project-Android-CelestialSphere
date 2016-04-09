package weymeelspierre.starstracker.renderOpenGl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.Set;

import weymeelspierre.starstracker.library.Math_lib;
import weymeelspierre.starstracker.library.Time_lib;
import weymeelspierre.starstracker.renderOpenGl.AbstractRender;

/**
 * Created by Pierre on 12/01/2015.
 */
public class GeoElementsRender extends AbstractRender {
  private float[] specificModelMatrix;

  private static final String TAG = "GeoElementsRender";
  private float[] geoColor;


  //-------------------------------

  private final String vertexShader =
          "attribute vec3 a_Position;" +
                  "uniform mat4 u_MVPMatrix;" +

                  "void main() {" +
                  " gl_Position = u_MVPMatrix * vec4(a_Position,1.0);" +
                  " gl_PointSize = 2.0;" +
                  "}";


  private final String fragmentShader =
          "precision mediump float;" +
                  "uniform vec4 color;" +

                  "void main() {" +
                  " gl_FragColor = color;" +
                  "}";
  //--------------------------------------------

  public GeoElementsRender(RenderManager renderManager, float[] positionData,
                           float[] color) throws Exception {
    nbDots = positionData.length / mPositionDataSize;
    geoColor = color;
    positions = changeIntoFloatBuffer(positionData);
    renderManager.putInRenderHMap(this.TAG, this);
  }


  private int prepareUniformColorsForShader() {
    // get handle to fragment shader's vColor member
    int color_GlslLocation = GLES20.glGetUniformLocation(programHandle, "color");
    // Set color blue for drawing
    GLES20.glUniform4f(color_GlslLocation, geoColor[0], geoColor[1],
            geoColor[2], geoColor[3]);
    return color_GlslLocation;
  }


  //---------------------------------------------------------------------------------------

  @Override
  protected void iniProgramAndShader() throws Exception {
    iniProgramAndShader(vertexShader,fragmentShader);
  }

  @Override
  protected void setActive(Boolean activate) throws Exception{
    this.active = activate;
  }

  //---------------------------------------
  // -------------------------------
  private float[] getSpecificModelViewProjMatrix() throws Exception {
    float[] model = new float[16];
    Matrix.setIdentityM(model, 0);
    float[] reply = new float[16];
    Matrix.setIdentityM(reply, 0);
    Matrix.multiplyMM(reply, 0, viewMatrix, 0, model, 0);
    Matrix.multiplyMM(reply, 0, stereoProjMatrix, 0, reply, 0);
    return reply;
  }

  protected int prepareSpecificMatrixMVPForShader(float[] matrixMVP) throws Exception {
    int matrixVP_GlslLocation = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");
    GLES20.glUniformMatrix4fv(matrixVP_GlslLocation, 1, false, matrixMVP, 0);
    return matrixVP_GlslLocation;
  }
  //--------------------------------------
  //----------------------------------

  @Override
  protected void draw() throws Exception {
    //setModelViewProjMatrix("RA_DErender");"AzimuthRender"
    specificModelMatrix = getSpecificModelViewProjMatrix();

    if(programHandle == -1)
      iniProgramAndShader(vertexShader,fragmentShader);
    // Add program to OpenGL ES environment
    GLES20.glUseProgram(programHandle);

    int position_glslLocation = preparePositionForShader();
    int color_glslLocation = prepareUniformColorsForShader();
    int matrixMVP_glslLocation = prepareSpecificMatrixMVPForShader(specificModelMatrix);//prepareMatrixMVPForShader();

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
