package weymeelspierre.starstracker.renderOpenGl;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Set;

import weymeelspierre.starstracker.model.Constellation;

/**
 * Created by Pierre on 29/12/2014.
 */
public class ConstellationRender extends AbstractRender {
  private static final String TAG = "ConstellationRender";

  private HashMap<String, Constellation> constellations;
  private HashMap<String, FloatBuffer> bufferPositionsMap = new HashMap<String, FloatBuffer>();

  private final String vertexShader =
          "attribute vec3 a_Position;" +
                  "uniform mat4 u_MVPMatrix;" +

                  "void main() {" +
                  " gl_Position = u_MVPMatrix * vec4(a_Position,1.0);" +
                  " gl_PointSize = 1.0;" +
                  "}";


  private final String fragmentShader =
          "precision mediump float;" +

                  "void main() { " +
                  "gl_FragColor = vec4(1.0,0.0,0.0,1.0); " +
                  "}";

//
  public ConstellationRender(RenderManager renderManager, HashMap<String, Constellation> constellations) {
    this.constellations = constellations;
    Set<String> keySet = constellations.keySet();
    for (String elem : keySet) {
      Constellation cst = constellations.get(elem);
      bufferPositionsMap.put(elem, getFloatPositionsBufferOf(cst.getPositionSequence()));
    }
    this.active = true;
    renderManager.putInRenderHMap(this.TAG, this);
  }

  private FloatBuffer getFloatPositionsBufferOf(float[] positionData) {
    FloatBuffer positions = ByteBuffer.allocateDirect(positionData.length * bytesPerFloat)
            .order(ByteOrder.nativeOrder()).asFloatBuffer();
    positions.put(positionData).position(0);
    return positions;
  }

  private int preparePositionForShader(FloatBuffer positions) {
    // get handle to vertex shader's vPosition member
    int position_GlslLocation = GLES20.glGetAttribLocation(programHandle, "a_Position");
    // Enable a handle to the  vertices
    GLES20.glEnableVertexAttribArray(position_GlslLocation);
    // Prepare the  coordinate data
    GLES20.glVertexAttribPointer(position_GlslLocation, mPositionDataSize,
            GLES20.GL_FLOAT, false, 0, positions);
    return position_GlslLocation;
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

    int matrixMVP_GlslLocation = prepareMatrixMVPForShader();

    // Draw the lines:
    GLES20.glEnable(GLES20.GL_LINE_WIDTH);
    GLES20.glLineWidth(1.0f);

    Set<String> keySet = constellations.keySet();
    for (String elem : keySet) {
      int position_GlslLocation = preparePositionForShader(bufferPositionsMap.get(elem));
      Constellation cst = constellations.get(elem);
      int[] branch = cst.getBranchIndex();
      int seqLength = cst.getConstellationSequence().length;
      if (branch.length != 1) {
        for (int i = 0; i < branch.length - 1; ++i) {
          GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, branch[i], branch[i + 1] - branch[i]);
        }
      }
      GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, branch[branch.length - 1], seqLength - branch[branch.length - 1]);
      GLES20.glDisableVertexAttribArray(position_GlslLocation);
    }

    // Disable vertex array
    GLES20.glDisableVertexAttribArray(matrixMVP_GlslLocation);

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