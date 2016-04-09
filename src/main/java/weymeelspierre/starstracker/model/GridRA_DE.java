package weymeelspierre.starstracker.model;

import java.util.ArrayList;


import weymeelspierre.starstracker.library.Math_lib;
import weymeelspierre.starstracker.renderOpenGl.GridRA_DE_render;
import weymeelspierre.starstracker.renderOpenGl.RenderManager;

/**
 * Created by Pierre on 27/11/2014.
 */
public class GridRA_DE {
  private GridRA_DE_render gridRA_DE_render = null;
  private final float[] gridColor = new float[]{0.5f,0.5f,0.5f, 1.0f};//Grey
  // {0.0f,0.0f,1.0f,1.0f};//false blue color for report
  private float[] positionData = new float[]{
          //center
          // 0.0f, 0.0f, 0.0f,
          // axe x (négatif)
          -1.0f, 0.0f, 0.0f,
          // axe x (positif)
          1.0f, 0.0f, 0.0f,
          // axe y (négatif)
          0.0f, -1.0f, 0.0f,
          // axe y (positif)
          0.0f, 1.0f, 0.0f,
          // axe z (négatif)
          0.0f, 0.0f, -1.0f,
          // axe z (positif)
          0.0f, 0.0f, 1.0f,
  };

  protected GridRA_DE(RenderManager renderManager) throws Exception {
    initializeGridRender(renderManager);
  }

  private void initializeGridRender(RenderManager renderManager) throws Exception {
    float[] positions = gridPosition(15, 10, 1, 1);
    gridRA_DE_render = new GridRA_DE_render(renderManager, positions, gridColor);
  }

  private float[] gridPosition(int perRALongitude, int perDELatitude, int accuracyForLongInDegrees,
                               int accuracyForLatInDegrees) throws Exception {
    ArrayList<Float> gridPosition = new ArrayList<Float>();
    for (int RALong = 0; RALong < 360; RALong = RALong + perRALongitude) {
      gridPosition.addAll(RALongitude(RALong, accuracyForLongInDegrees));
    }
    for (int DELat = -90 + perDELatitude; DELat < 90; DELat = DELat + perDELatitude) {
      gridPosition.addAll(DELatitude(DELat, accuracyForLatInDegrees));
    }
    for (int i = 0; i < positionData.length; ++i) {
      gridPosition.add(positionData[i]);
    }
    return Math_lib.getArrayFromNoPrimitiveArrayList(gridPosition);
  }


  //numberOfDots pair
  private ArrayList<Float> DELatitude(double DE, int accuracyInDegrees) {
    ArrayList<Float> dotsPosition = new ArrayList<Float>();
    double DEradian = Math.toRadians(DE);
    double RADivisionRadian = Math.toRadians(accuracyInDegrees);
    double z = Math.sin(DEradian);
    for (int i = 0; i < 360; i = i + accuracyInDegrees) {
      dotsPosition.add((float) (Math.cos(DEradian) * Math.cos(Math.toRadians(i))));
      dotsPosition.add((float) (Math.cos(DEradian) * Math.sin(Math.toRadians(i))));
      dotsPosition.add((float) z);
    }
    return dotsPosition;
  }

  //numberOfDots impair
  private ArrayList<Float> RALongitude(double RA, int accuracyInDegrees) {
    ArrayList<Float> dotsPosition = new ArrayList<Float>();
    double RAradian = Math.toRadians(RA);
    double DEDivisionRadian = Math.toRadians(accuracyInDegrees);

    for (int i = -90 + accuracyInDegrees; i < 90; i = i + accuracyInDegrees) {
      dotsPosition.add((float) (Math.cos(Math.toRadians(i)) * Math.cos(RAradian)));
      dotsPosition.add((float) (Math.cos(Math.toRadians(i)) * Math.sin(RAradian)));
      dotsPosition.add((float) Math.sin(Math.toRadians(i)));
    }

    return dotsPosition;
  }

}
