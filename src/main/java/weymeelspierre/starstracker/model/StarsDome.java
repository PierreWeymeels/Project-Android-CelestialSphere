package weymeelspierre.starstracker.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


import weymeelspierre.starstracker.dao.Constellation_dao;
import weymeelspierre.starstracker.dao.Stars_dao;
import weymeelspierre.starstracker.library.Math_lib;
import weymeelspierre.starstracker.renderOpenGl.ConstellationRender;
import weymeelspierre.starstracker.renderOpenGl.RenderManager;
import weymeelspierre.starstracker.renderOpenGl.StarsRender;

/**
 * Created by Pierre on 20/11/2014.
 */
public class StarsDome {

  /**
   * Used for debug logs.
   */
  private static final String TAG = "StarsDome";
  private final float[] starsColor = new float[]{1.0f, 1.0f, 1.0f, 1.0f};// Stars are white or  {0.0f, 0.0f, 1.0f, 1.0f};// blue
  // {0.0f, 0.0f, 0.0f, 0.0f};//false black color for report

  private Stars_dao daoS = null;
  private Constellation_dao daoC = null;

  private StarsRender starsRender = null;
  private ConstellationRender cstRender = null;
  private HashMap<String, Constellation> constellations = new HashMap<String, Constellation>();
  //private HashMap<String, Double[]> starsOfCst = new HashMap<String, Double[]>();

  protected StarsDome(Context context, RenderManager renderManager) throws Exception {
    daoS = new Stars_dao(context);
    daoC = new Constellation_dao(context);

    initializeStarsRender(renderManager);
    iniConstellationRender(renderManager);
  }

  private void initializeStarsRender(RenderManager renderManager) throws Exception {
    try {
      ArrayList<double[]> positions = daoS.getStarsPosition(6.0);
      int positionXyzDimension = 3;
      int starsNb = positions.size();
      float[] positionsXyz = new float[starsNb * positionXyzDimension];
      for (int i = 0; i < starsNb; ++i) {
        double[] RA_DE = positions.get(i);
        double[] xyzPosition = Math_lib.degreeRaDe_to_xyzSphereUnity(RA_DE[0], RA_DE[1]);
        for (int j = i * 3; j < i * 3 + 3; ++j) {
          positionsXyz[j] = Math_lib.convertFromDouble(xyzPosition[j - i * 3]);
        }
      }
      starsRender = new StarsRender(renderManager, positionsXyz, starsColor);
    } catch (Exception e) {
      throw new Exception(TAG + " : " + e.getMessage());
    }
  }

  //CONSTELLATIONS-----------------------------------------------------------------

  private void iniConstellationRender(RenderManager renderManager) throws Exception {
    String[] data, starsSequence, branchIndex;
    String name;
    try {
      ArrayList<String[]> constellationData = daoC.getConstellationsData();
      int constellationNb = constellationData.size();
      for (int i = 0; i < constellationNb; ++i) {
        data = constellationData.get(i);
        name = data[0];
        starsSequence = data[1].split(",");
        branchIndex = data[2].split(",");
        Constellation cst = new Constellation(name, starsSequence, branchIndex);
        cst.iniPositionOfConstellation(iniPositionOfConstellation(name));
        constellations.put(name, cst);
      }
      cstRender = new ConstellationRender(renderManager, constellations);
    } catch (Exception e) {
      throw new Exception(TAG + " : " + e.getMessage());
    }
  }


  private HashMap<String, float[]> iniPositionOfConstellation(String constellation) throws Exception {
    HashMap<String, double[]> greekLetterAndPosition = daoS.greekLetterPosition(constellation);
    HashMap<String, float[]> greekLetterAndXyzPosition = new HashMap<String, float[]>();
    Set<String> greekLetterSet = greekLetterAndPosition.keySet();
    for (String greekLetter : greekLetterSet) {
      double[] RA_DE = greekLetterAndPosition.get(greekLetter);
      double[] xyzPosition = Math_lib.degreeRaDe_to_xyzSphereUnity(RA_DE[0], RA_DE[1]);
      float[] p = new float[3];
      for (int j = 0; j < 3; ++j) {
        p[j] = Math_lib.convertFromDouble(xyzPosition[j]);
      }
      greekLetterAndXyzPosition.put(greekLetter, p);
    }
    return greekLetterAndXyzPosition;
  }


  //FIN CONSTELLATIONS-----------------------------------------------------------------

  protected HashMap<String,double[]> getNameAndXyzPosition(String type) throws Exception{
    HashMap<String,double[]> nameAndXyzPosition = new HashMap<String,double[]>();
    HashMap<String,double[]> nameAndPosition;
    try {
      if(type.compareTo("constellation")==0)
        nameAndPosition = daoC.getConstellationsNameAndPosition();
      else if(type.compareTo("stars")==0)
        nameAndPosition = daoS.getStarsNameAndPosition();
      else
        throw new Exception(TAG + " : " + " bad type for getCstNameAndXyzPosition !");
      Set<String> keySet = nameAndPosition.keySet();
      for (String name : keySet) {
        double[] RA_DE_center = nameAndPosition.get(name);
        double[] xyzPosition =
                Math_lib.degreeRaDe_to_xyzSphereUnity(RA_DE_center[0], RA_DE_center[1]);
        nameAndXyzPosition.put(name,xyzPosition);
      }
      return nameAndXyzPosition;
    } catch (Exception e) {
      throw new Exception(TAG + " : " + e.getMessage());
    }
  }


}


