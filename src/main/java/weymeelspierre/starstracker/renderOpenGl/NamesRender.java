package weymeelspierre.starstracker.renderOpenGl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.Set;

import weymeelspierre.starstracker.renderOpenGl.AbstractRender;

/**
 * Created by Pierre on 1/01/2015.
 */
public class NamesRender extends AbstractRender {
  private static final String TAG = "NamesRender";
  private final Context context;

  //--------------------------------------------------------------
  private  HashMap<Integer,FloatBuffer> cst_texturesResAndBuffer = null;
  private  HashMap<Integer,FloatBuffer> stars_texturesResAndBuffer = null;
  //-----------------------------------------------------------------
  private  HashMap<Bitmap,FloatBuffer> cst_texturesBitmapAndBuffer = null;
  private  HashMap<Bitmap,FloatBuffer> stars_texturesBitmapAndBuffer = null;
  //-----------------------------------------------------------------
  private final ShortBuffer drawListBuffer;
  private final FloatBuffer uvBuffer;

  private short[] indices = new short[]{0,1,2,2,3,0};
  /**
   FOR IMAGE computer:this correspond to xy mapping coordinates like:
   (0,0)--(1,0)
   |       |
   (0,1)--(1,1)
   FOR IMAGE openGL: create our UV coordinates.
   (0,1)--(1,1)
   |      |
   (0,0)--(1,0)
   */
  private float[] uvs = new float[] {
          0.0f, 1.0f,
          1.0f, 1.0f,
          1.0f, 0.0f,
          0.0f, 0.0f,
  };
  //-----------------------------------------------------------------
  private final String vertexShader =
          "attribute vec3 a_Position;" +
                  "uniform mat4 u_MVPMatrix;" +
                  "attribute vec2 a_texCoord;" +
                  "varying vec2 v_texCoord;" +


                  "void main() {" +
                  " gl_Position = u_MVPMatrix * vec4(a_Position,1.0);" +
                  "  v_texCoord = a_texCoord;" +
                  "}";


  private final String fragmentShader =
          "precision mediump float;" +
          "varying vec2 v_texCoord;" +
          "uniform sampler2D s_texture;" +

                  "void main() {" +
                  " gl_FragColor = texture2D( s_texture, v_texCoord );" +
                  "}";
  //----------------------------------------------------------------------------------

  public NamesRender(HashMap<Integer, float[]> constellationsNameResMap,
                     HashMap<Integer, float[]> starsNameResMap,RenderManager renderManager,
                     Context context) throws Exception {
    this.context = context;
    cst_texturesResAndBuffer = iniTexturesBufferNb(constellationsNameResMap);
    stars_texturesResAndBuffer = iniTexturesBufferNb(starsNameResMap);
    drawListBuffer = changeIntoShortBuffer(indices);
    uvBuffer = changeIntoFloatBuffer(uvs);
    renderManager.putInRenderHMap(this.TAG, this);
  }

  private HashMap<Integer,FloatBuffer> iniTexturesBufferNb(
          HashMap<Integer, float[]> nameResMap) throws Exception {
    HashMap<Integer,FloatBuffer> texturesBuffer = new HashMap<Integer,FloatBuffer>();
    Set<Integer> keySet = nameResMap.keySet();
    for (Integer res : keySet) {
      float[] bitmapPosition = nameResMap.get(res);
      texturesBuffer.put(res,getFloatPositionsBufferOf(bitmapPosition));
    }
    return texturesBuffer;
  }

  private FloatBuffer getFloatPositionsBufferOf(float[] positionData) throws Exception{
    FloatBuffer positions = ByteBuffer.allocateDirect(positionData.length * bytesPerFloat)
            .order(ByteOrder.nativeOrder()).asFloatBuffer();
    positions.put(positionData).position(0);
    return positions;
  }

  private ShortBuffer changeIntoShortBuffer(short[] data) throws Exception {
    ByteBuffer dlb = ByteBuffer.allocateDirect(data.length * 2);
    dlb.order(ByteOrder.nativeOrder()).asFloatBuffer();
    ShortBuffer reply = dlb.asShortBuffer();
    reply.put(data);
    reply.position(0);
    return reply;
  }

  @Override
  protected void setActive(Boolean activate) throws Exception{
    if(activate){
      cst_texturesBitmapAndBuffer = getTexturesBufferAndBitmap(cst_texturesResAndBuffer);
      stars_texturesBitmapAndBuffer = getTexturesBufferAndBitmap(stars_texturesResAndBuffer);
      this.active = true;
    }else{
      //bmp.recycle(); aussi ?
      cst_texturesBitmapAndBuffer = null;
      stars_texturesBitmapAndBuffer = null;
      this.active = false;
    }
  }

  @Override
  protected void iniProgramAndShader() throws Exception {
    iniProgramAndShader(vertexShader,fragmentShader);
  }

  private HashMap<Bitmap, FloatBuffer> getTexturesBufferAndBitmap(
          HashMap<Integer, FloatBuffer> resAndBufferMap) throws Exception{
    BitmapFactory.Options options = new BitmapFactory.Options();
    //options.inSampleSize = 2;
    options.inScaled = false;
    HashMap<Bitmap, FloatBuffer> reply = new HashMap<Bitmap, FloatBuffer>();
    Set<Integer> keySet = resAndBufferMap.keySet();
    for (Integer resource : keySet) {
      reply.put(getBitmap(resource,options),resAndBufferMap.get(resource));
    }
    return reply;
  }

  private Bitmap getBitmap(Integer resource, BitmapFactory.Options options) throws Exception{
    return BitmapFactory.decodeResource(context.getResources(),resource,options);
  }

  //---------------------------------------------------------------------



  // EN TEST POUR AMELIORATIONS FUTURES:
  @Override
  protected void draw() throws Exception {
    setModelViewProjMatrix("RA_DErender");
    //program a:
    drawConstellationsName();
    //program b:
    drawStarsName();
  }


  private void drawConstellationsName() throws Exception{
    //program a:
    if(programHandle== -1)
      iniProgramAndShader(vertexShader,fragmentShader);
    GLES20.glUseProgram(programHandle);
    GLES20.glCullFace(GLES20.GL_BACK);
    int matrixMVP_GlslLocation_a = prepareMatrixMVPForShader();
    int texture_GlslLocation_a = prepareTextureFrameForShader();
    int textureHandle_GlslLocation_a = prepareTextureHandleForShader();

    drawTextures(cst_texturesBitmapAndBuffer);
    // Disable vertex array
    GLES20.glDisableVertexAttribArray(textureHandle_GlslLocation_a);
    GLES20.glDisableVertexAttribArray(texture_GlslLocation_a);
    GLES20.glDisableVertexAttribArray(matrixMVP_GlslLocation_a);
    // Deactivate shader(s)
    GLES20.glUseProgram(0);
    //delete:
    deleteProgramAndShader();
  }

  private void drawStarsName() throws Exception{
    if(programHandle== -1)
      iniProgramAndShader(vertexShader,fragmentShader);
    GLES20.glUseProgram(programHandle);

    int matrixMVP_GlslLocation_b = prepareMatrixMVPForShader();
    catchErrorOpenGl("drawStarsName", 1,
         "matrixMVP_GlslLocation_b", matrixMVP_GlslLocation_b,false);
    int texture_GlslLocation_b = prepareTextureFrameForShader();
    catchErrorOpenGl("drawStarsName", 2,
         "texture_GlslLocation_b", texture_GlslLocation_b,false);
    int textureHandle_GlslLocation_b = prepareTextureHandleForShader();
    catchErrorOpenGl("drawStarsName", 3,
         "textureHandle_GlslLocation_b",textureHandle_GlslLocation_b,false);
    drawTextures(stars_texturesBitmapAndBuffer);
    // Disable vertex array
    GLES20.glDisableVertexAttribArray(textureHandle_GlslLocation_b);
    GLES20.glDisableVertexAttribArray(texture_GlslLocation_b);
    GLES20.glDisableVertexAttribArray(matrixMVP_GlslLocation_b);
    // Deactivate shader(s)
    GLES20.glUseProgram(0);
    //delete:
    deleteProgramAndShader();
  }

  private int prepareTextureFrameForShader() throws Exception{
    // Get handle to texture coordinates location
    int reply = GLES20.glGetAttribLocation(programHandle, "a_texCoord" );
    catchErrorOpenGl("prepareTextureFrameForShader", 1,
            "glGetAttribLocation a_texCoord", reply,false);
    // Enable generic vertex attribute array
    GLES20.glEnableVertexAttribArray ( reply );
    catchErrorOpenGl("prepareTextureFrameForShader", 2,
            "glGetAttribLocation a_texCoord", reply,false);
    // Prepare the texture coordinates
    GLES20.glVertexAttribPointer ( reply, 2, GLES20.GL_FLOAT,
            false,
            0, uvBuffer);
    catchErrorOpenGl("prepareTextureFrameForShader", 3,
            "glGetAttribLocation a_texCoord", reply,false);
    return reply;
  }

  private int prepareTextureHandleForShader() throws Exception{
    // Get handle to textures locations
    int reply = GLES20.glGetUniformLocation (programHandle, "s_texture" );
    // Set the sampler texture unit to 0, where we have saved the texture.
    GLES20.glUniform1i ( reply, 0);
    catchErrorOpenGl("prepareTextureHandleForShader", 1,
            "glGetUniformLocation s_texture", reply,false);
    return reply;
  }

  //--------------------------------------------------------------------------------
  //Si on veut créer (et se débarrasser en temps réel les bitmap :--------------
  //Pas très efficace, plante plus rarement, mais bien plus lent !!!---------------

 /* private void drawTexturesResAndBuffer(HashMap<Integer, FloatBuffer> texturesResAndBuffer) {
    Set<Integer> keySet = texturesBufferNoBitmap.keySet();
    BitmapFactory.Options options = new BitmapFactory.Options();
    //options.inSampleSize = 2;
    options.inScaled = false;
    Bitmap bmp;
    for (Integer res : keySet) {
      int position_GlslLocation = preparePositionForShader(texturesBufferNoBitmap.get(res));
      bmp = BitmapFactory.decodeResource(context.getResources(), res, options);
      setupImage(bmp);
      bmp.recycle();
      GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length,
              GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
      // Bind to default texture
      GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
      // Disable vertex array
      GLES20.glDisableVertexAttribArray(position_GlslLocation);
    }
  }
  */
  //-----------------------------------------------------------------------

  private void drawTextures(HashMap<Bitmap, FloatBuffer> texturesBuffer) throws Exception{
    Set<Bitmap> keySet = texturesBuffer.keySet();
    int i=0;
    int textureNb = keySet.size();
    int[] texturenames = new int[textureNb];
    GLES20.glGenTextures(textureNb, texturenames, 0);
    for (Bitmap bmp : keySet) {
      int position_GlslLocation = preparePositionForShaderTxt(texturesBuffer.get(bmp));
      // Bind texture to texturename
      GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[i]);
      // Set filtering
      GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
      GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
      // Load the bitmap into the bound texture.
      GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
      GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length,
              GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
      catchErrorOpenGl("drawTextures", 2,
              "position_GlslLocation", position_GlslLocation,false);
      // Bind to default texture
      GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
      // Disable vertex array
      GLES20.glDisableVertexAttribArray(position_GlslLocation);
      GLES20.glDeleteTextures(1,texturenames,i);
      ++i;
    }
  }

  private int preparePositionForShaderTxt(FloatBuffer positions) throws Exception {
    // get handle to vertex shader's vPosition member
    int position_GlslLocation = GLES20.glGetAttribLocation(programHandle, "a_Position");
    // Enable a handle to the  vertices
    GLES20.glEnableVertexAttribArray(position_GlslLocation);
    // Prepare the  coordinate data
    GLES20.glVertexAttribPointer(position_GlslLocation, mPositionDataSize,
            GLES20.GL_FLOAT, false, 0, positions);
    catchErrorOpenGl("preparePositionForShaderTxt", 1,
            "position_GlslLocation",position_GlslLocation,false);
    return position_GlslLocation;
  }

  private void setupImage(Bitmap bmp,int glTexture_i)throws Exception {
    // Generate Textures, if more needed, alter these numbers.

    // Bind texture to texturename
    GLES20.glActiveTexture(glTexture_i);//(GLES20.GL_TEXTURE0);


    //bmp.recycle();
  }

  protected String getVertexShader() {
    return vertexShader;
  }

  protected String getFragmentShader() {
    return fragmentShader;
  }

  //---------------------------------------------------------------------------
  public void catchErrorOpenGl(String method,int methodLocationNb, String idName,
                               int idValue,boolean launchExeption) throws Exception {
    int error = GLES20.glGetError();
    if (error  != GLES20.GL_NO_ERROR) {
      Log.e(TAG, method +" "+methodLocationNb+ " | ErrorOpenGl " +
             error + " | idName : "+idName+" | idValue : "+idValue);
      if(launchExeption)
        throw new Exception(TAG +" : "+method + ": glError " + error);
    }
  }

}
