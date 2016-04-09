package weymeelspierre.starstracker.renderOpenGl;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * Created by Pierre on 27/11/2014.
 */
public abstract class AbstractRender {
  private final static String TAG = "AbstractRender";

  protected boolean active = false;

  // Size per color element.
  protected final int colorDimension = 4;
  protected final int bytesPerFloat = 4;
  protected final int mPositionDataSize = 3;
  protected final int mNormalDataSize = 3;
  protected int nbDots = 0;

  protected FloatBuffer positions = null;
  protected FloatBuffer colors = null;
  protected FloatBuffer normals = null;

  protected int programHandle = -1;
  protected int vertexShaderHandle = -1;
  protected int fragmentShaderHandle = -1;

  protected static boolean azimuthalMode = false;
  //MATRIX:------------------------------------------------------------
  protected static float[] modelMatrix = new float[16];
  protected static float[] raDe_toAzModelMatrix =  new float[16];
  protected static float[] viewMatrix = new float[16];
  protected static float[] stereoProjMatrix = new float[16];

  protected float[] modelViewProjMatrix = new float[16];

  //----------------------------------------------------------

  //STATIC METHOD:------------------------------------------------
  protected static void setViewMatrix(float[] viewMatrix) {
    AbstractRender.viewMatrix = viewMatrix;
  }

  protected static void setStereoProjMatrix(float[] stereoProjMatrix) {
    AbstractRender.stereoProjMatrix = stereoProjMatrix;
  }

  protected static float[] getViewMatrix() {
    return viewMatrix;
  }

  protected static float[] getStereoProjMatrix() {
    return stereoProjMatrix;
  }

  protected static void iniAbstractRenderStaticMatrix() {
    Matrix.setIdentityM(modelMatrix, 0);
    Matrix.setIdentityM(raDe_toAzModelMatrix, 0);
    Matrix.setIdentityM(viewMatrix, 0);
    Matrix.setIdentityM(stereoProjMatrix, 0);
  }

  public static void setModelMatrix(float[] modelMatrix) {
    AbstractRender.modelMatrix = modelMatrix;
  }

  public static void setAzimuthalMode(boolean activate) {
    AbstractRender.azimuthalMode = activate;
  }

  public static void setRaDe_toAzModelMatrix(float[] raDe_toAzModelMatrix) {
    AbstractRender.raDe_toAzModelMatrix = raDe_toAzModelMatrix;
  }
  //--------------------------------------------------

  protected abstract void setActive(Boolean activate)throws Exception;

  protected boolean isActive() {
    return active;
  }
  //ABSTRACT METHOD------------------------------------------------------------------
  protected abstract void iniProgramAndShader() throws Exception;

  protected abstract void draw() throws Exception;
  //-------
  //METHOD:---------------------------------------------------------
  protected void iniProgramAndShader(String vertexShader,String fragmentShader) throws Exception {
    if (active) {
      vertexShaderHandle = loadShader(GLES20.GL_VERTEX_SHADER, vertexShader);
      fragmentShaderHandle = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);
      programHandle = iniProgram(vertexShaderHandle, fragmentShaderHandle);
    }
  }

  protected void deleteProgramAndShader() throws Exception {
    GLES20.glDeleteProgram(programHandle);//délie mais ne supprime pas les shaders !
    programHandle = -1;
    GLES20.glDeleteShader(vertexShaderHandle);
    vertexShaderHandle = -1;
    GLES20.glDeleteShader(fragmentShaderHandle);
    fragmentShaderHandle = -1;
  }

  protected FloatBuffer changeIntoFloatBuffer(float[] data) throws Exception {
    FloatBuffer reply = ByteBuffer.allocateDirect(data.length * bytesPerFloat)
            .order(ByteOrder.nativeOrder()).asFloatBuffer();
    reply.put(data).position(0);
    return reply;
  }

  protected void setModelViewProjMatrix(String RA_DErenderType) throws Exception {
    Matrix.setIdentityM(modelViewProjMatrix, 0);
    float[] appropriateModel = modelMatrix;
    if(azimuthalMode && (RA_DErenderType.compareTo("RA_DErender")==0))
      appropriateModel = raDe_toAzModelMatrix;
    Matrix.multiplyMM(modelViewProjMatrix, 0, viewMatrix, 0, appropriateModel, 0);
    Matrix.multiplyMM(modelViewProjMatrix, 0, stereoProjMatrix, 0, modelViewProjMatrix, 0);
  }

  protected int preparePositionForShader() throws Exception {
    // get handle to vertex shader's vPosition member
    int position_GlslLocation = GLES20.glGetAttribLocation(programHandle, "a_Position");
    // Enable a handle to the  vertices
    GLES20.glEnableVertexAttribArray(position_GlslLocation);
    // Prepare the  coordinate data
    GLES20.glVertexAttribPointer(position_GlslLocation, mPositionDataSize,
            GLES20.GL_FLOAT, false, 0, positions);
    return position_GlslLocation;
  }

  protected int prepareColorsForShader() throws Exception {
    // get handle to vertex shader's vPosition member
    int color_GlslLocation = GLES20.glGetAttribLocation(programHandle, "a_Color");
    // Enable a handle to the  vertices
    GLES20.glEnableVertexAttribArray(color_GlslLocation);
    // Prepare the  coordinate data
    GLES20.glVertexAttribPointer(color_GlslLocation, 4,
            GLES20.GL_FLOAT, false, 0, colors);
    return color_GlslLocation;
  }

  protected int prepareMatrixMVPForShader() throws Exception {
    int matrixVP_GlslLocation = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");
    GLES20.glUniformMatrix4fv(matrixVP_GlslLocation, 1, false, modelViewProjMatrix, 0);
    return matrixVP_GlslLocation;
  }

  protected int loadShader(int type, String shaderCode) throws Exception {
    String err = "null";
    // Load in the vertex shader.
    int shaderHandle = GLES20.glCreateShader(type);
    if (shaderHandle != 0) {
      // add the source code to the shader and compile it
      GLES20.glShaderSource(shaderHandle, shaderCode);
      GLES20.glCompileShader(shaderHandle);
      // Get the compilation status.
      final int[] compileStatus = new int[1];
      GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
      // If the compilation failed, delete the shader.
      if (compileStatus[0] == 0) {
        err = GLES20.glGetShaderInfoLog(shaderHandle);
        GLES20.glDeleteShader(shaderHandle);
        shaderHandle = 0;
      }
    }
    if (shaderHandle == 0) {
      throw new RuntimeException(TAG + " : " + err);
    }
    return shaderHandle;
  }

  protected int iniProgram(int vertexShaderHandle, int fragmentShaderHandle) throws Exception {
    // Create a program object and store the handle to it.
    int programHandle = GLES20.glCreateProgram();
    if (programHandle != 0) {
      // Bind the vertex shader to the program.
      GLES20.glAttachShader(programHandle, vertexShaderHandle);
      // Bind the fragment shader to the program.
      GLES20.glAttachShader(programHandle, fragmentShaderHandle);
      // Link the two shaders together into a program.
      GLES20.glLinkProgram(programHandle);
      // Get the link status.
      final int[] linkStatus = new int[1];
      GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);
      // If the link failed, delete the program.
      if (linkStatus[0] == 0) {
        GLES20.glDeleteProgram(programHandle);
        programHandle = 0;
      }
    }
    if (programHandle == 0) {
      throw new RuntimeException(TAG + " : " + "Error creating program.");
    }
    return programHandle;
  }
}
