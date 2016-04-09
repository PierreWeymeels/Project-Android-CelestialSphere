package weymeelspierre.starstracker.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import weymeelspierre.starstracker.library.Math_lib;
import weymeelspierre.starstracker.renderOpenGl.GeoElementsRender;
import weymeelspierre.starstracker.renderOpenGl.RenderManager;

/**
 * Created by Pierre on 24/12/2014.
 */

/**
 * Selon le ystème de coordonnées azimutales (a= azimuth, h = hauteur)
 * le plan du méridien est le plan xy, l'axe y étant l'origine
 * de l'angle (positif vers l'axe x (ouest)) azimutal !
 * x = sin(a)*cos(h)
 * y = cos(a)*cos(h)
 * z = sin(h)
 */
public class GeoElements {
  GeoElementsRender geoRender = null;

  private float[] geoColor = new float[]{0.0f,0.5f,0.0f,1.0f};//green
  private double latitude;
  private double longitude;

  private final ArrayList<Float> meridian;
  private final ArrayList<Float> horizon;

  private final float[] sud = new float[]{0.0f,1.0f,0.0f};
  private final float[] nord = new float[]{0.0f,-1.0f,0.0f};
  private final float[] est = new float[]{-1.0f,0.0f,0.0f};
  private final float[] ouest = new float[]{1.0f,0.0f,0.0f};
  private final float[] zenith = new float[]{0.0f,0.0f,1.0f};
  private final float[] nadir = new float[]{0.0f,0.0f,-1.0f};

  protected GeoElements(RenderManager renderManager,double latitude,
                     double longitude) throws Exception {
    this.latitude = latitude;
    this.longitude = longitude;
    meridian = calculMeridian();
    horizon = calculHorizon();
    initializeRender(renderManager);
  }



  private void initializeRender(RenderManager renderManager) throws Exception {
    ArrayList<Float> geoPositionList = buildGeoPositionList();
    float[] geoPositionsTab = Math_lib.getArrayFromNoPrimitiveArrayList(geoPositionList);
    geoRender = new GeoElementsRender(renderManager,geoPositionsTab,geoColor);
  }

  private ArrayList<Float> buildGeoPositionList() {
    ArrayList<Float> geoPositionList  = new ArrayList<Float>();
    geoPositionList.addAll(horizon);
    geoPositionList.addAll(meridian);
    return geoPositionList;
  }

  private ArrayList<Float> calculHorizon() throws Exception {
    ArrayList<Float> horizon = new ArrayList<Float>();
    for (int a = 0; a < 360; ++a) {
      double rad = Math.toRadians(a);
      horizon.add(Math_lib.convertFromDouble(Math.sin(rad)));
      horizon.add(Math_lib.convertFromDouble(Math.cos(rad)));
      horizon.add(0.0f);
    }
    return horizon;
  }

  private ArrayList<Float> calculMeridian() throws Exception {
    ArrayList<Float> meridian = new ArrayList<Float>();
    for (int h = 0; h <=90; ++h) {
      double rad = Math.toRadians(h);
      //Sur l'axe y
      meridian.add(0.0f);
      meridian.add(Math_lib.convertFromDouble(Math.cos(rad)));
      meridian.add(Math_lib.convertFromDouble(Math.sin(rad)));
    }

    //Du Zénith au pôle :
    //pour ne pas dépasser le pôle !
    /*
    int hApproxOfPole = (int)Math.ceil(latitude);
    for (int h = 89; h >=hApproxOfPole;--h) {
      double rad = Math.toRadians(h);
      //Sur l'axe y
      meridian.add(0.0f);
      meridian.add(-Math_lib.convertFromDouble(Math.cos(rad)));
      meridian.add(Math_lib.convertFromDouble(Math.sin(rad)));
    }*/
    return meridian;
  }

}
