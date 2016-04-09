package weymeelspierre.starstracker.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import weymeelspierre.starstracker.R;
import weymeelspierre.starstracker.library.Math_lib;
import weymeelspierre.starstracker.renderOpenGl.NamesRender;
import weymeelspierre.starstracker.renderOpenGl.RenderManager;

/**
 * Created by Pierre on 13/01/2015.
 */
public class Names {
  private final double CST_HEIGHT_PER_LETTER  =  0.04;
  private final double CST_WIDTH_PER_LETTER  =  0.02;
  private final double STARS_HEIGHT_PER_LETTER  =  0.01;
  private final double STARS_WIDTH_PER_LETTER  =  0.005;


  private HashMap<Integer,float[]> constellationsNameResMap = null;
  private HashMap<Integer,float[]> starsNameResMap = null;

  private final Context context;
  private NamesRender namesRender;




  protected Names(Context context, RenderManager renderManager,
               StarsDome starsDome) throws Exception {
    this.context = context;
    initialize(starsDome);
    namesRender = new NamesRender(constellationsNameResMap,starsNameResMap,renderManager,context);
  }


  //INITIALISATION PHASE:-----------------------------------
  private void initialize(StarsDome starsDome) throws Exception {
    HashMap<String,Integer> cstNameWidth = initializeCstNameWidth();
    HashMap<String,double[]> cstNameAndXyzPositions =
            starsDome.getNameAndXyzPosition("constellation");
    constellationsNameResMap =
            initialiseResMap(cstNameWidth, cstNameAndXyzPositions,
                    CST_HEIGHT_PER_LETTER, CST_WIDTH_PER_LETTER);
    HashMap<String,double[]> starsNameAndXyzPositions =
            starsDome.getNameAndXyzPosition("stars");
    HashMap<String,Integer> starsNameWidth = initializeStarsNameWidth(starsNameAndXyzPositions.keySet());
    starsNameResMap =
            initialiseResMap(starsNameWidth, starsNameAndXyzPositions,
                    STARS_HEIGHT_PER_LETTER,STARS_WIDTH_PER_LETTER);
  }

  private HashMap<String, Integer> initializeStarsNameWidth(
          Set<String> stringKeySet) throws Exception{
    HashMap<String, Integer> reply = new HashMap<String, Integer>();
    for (String starName : stringKeySet) {
      reply.put(starName,starName.length());
    }
    return reply;
  }

  private HashMap<Integer, float[]> initialiseResMap(
          HashMap<String, Integer> namesWidth, HashMap<String, double[]> nameAndXyzPositions,
          double letterHeight, double letterWidth)
          throws Exception {
    HashMap<Integer, float[]> resMap = new HashMap<Integer, float[]>();
    Set<String> keySet = nameAndXyzPositions.keySet();

    for (String cstName : keySet) {
      int drawableResourceId = context.getResources().getIdentifier(
              cstName.toLowerCase(),"drawable", context.getPackageName());
      int textWidth = namesWidth.get(cstName);
      double[] xyzPosition = nameAndXyzPositions.get(cstName);
      float[] bitmapXyzFramePosition = Math_lib.getXyzPositionForBitmap(
              textWidth*letterWidth,letterHeight, xyzPosition);
      resMap.put(drawableResourceId,bitmapXyzFramePosition);
    }
    return resMap;
  }

  private HashMap<String,Integer> initializeCstNameWidth() {
    HashMap<String,Integer> namesWidth = new HashMap<String,Integer>();
    namesWidth.put("And",9); namesWidth.put("Ant",6); namesWidth.put("Aps",4);
    namesWidth.put("Aqr",8); namesWidth.put("Aql",6); namesWidth.put("Ara",3);
    namesWidth.put("Ari",5); namesWidth.put("Aur",6); namesWidth.put("Boo",6);
    namesWidth.put("Cam",14); namesWidth.put("Cnc",6); namesWidth.put("CVn",14);
    namesWidth.put("CMa",11); namesWidth.put("CMi",11); namesWidth.put("Cap",11);
    namesWidth.put("Car",6); namesWidth.put("Cas",10); namesWidth.put("Cen",9);
    namesWidth.put("Cep",7); namesWidth.put("Cyg",6); namesWidth.put("Leo",3);
    namesWidth.put("Ori",5); namesWidth.put("Lib",5); namesWidth.put("Lyr",4);
    namesWidth.put("Peg",7); namesWidth.put("Per",7); namesWidth.put("Psc",6);
    namesWidth.put("Sge",7); namesWidth.put("Sgr",11); namesWidth.put("Sco",8);
    namesWidth.put("UMi",10); namesWidth.put("UMa",10);
    return namesWidth;
  }
 //------------------------------------------------------------------------------

}