package weymeelspierre.starstracker.library;

import android.opengl.GLU;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Pattern;

/**
 * Created by Pierre on 20/11/2014.
 */
public class Math_lib {

  public static float[] convertFromVector3Double(double[] vector3Double) throws Exception {
    float[] reply = new float[3];
    for (int i = 0; i < 3; ++i)
      reply[i] = convertFromDouble(vector3Double[i]);
    return reply;
  }

  public static float convertFromDouble(double d) throws Exception {
    float f;
    try {
      f = (float) d;
      if (Float.isInfinite(f) || Float.isNaN(f))
        f = convertStringOfDouble(d);
    } catch (Exception e) {
      f = convertStringOfDouble(d);
    }
    return f;
  }

  /**
   * Keep only 6 significant digits for float type (more is dangerous).
   * 30 à été arbitrairement choisi !
   *
   * @param d
   * @return
   */
  private static float convertStringOfDouble(double d) throws Exception {
    //TO DO : not consider integer part of 0.12 like a significant letter
    String strReply;
    String str = String.valueOf(d);
    String[] strDotPartition = str.split(Pattern.quote("."));
    if (strDotPartition.length != 2)
      throw new Exception("convertStringOfDouble problem !");
    else {
      String[] strDecimalExpoPartition =
              strDotPartition[1].split(Pattern.quote("E"));
      //DECIMAL PART ALGORITHM
      int integerPartSize = strDotPartition[0].length();
      String appropriateDecimalPart =
              getAppropriateDecimalPart(
                      strDecimalExpoPartition[0], integerPartSize);
      //EXPONENT PART ALGORITHM
      String expoPart;
      if (strDecimalExpoPartition.length == 2) {
        expoPart = getAppropriateExpoPart(strDecimalExpoPartition[1], 30);
        //INTEGER PART ALWAYS SIZE=1 IN SCIENTIFIC NOTATION !
        strReply =
                strDotPartition[0].concat(".").
                        concat(appropriateDecimalPart).concat(expoPart);
      } else {
        // CHECK IF INTEGER PART <=6
        if (integerPartSize <= 6) {
          strReply = strDotPartition[0].concat(".").
                  concat(appropriateDecimalPart);
        } else {
          //TO DO: round significantIntegerPart and check
          // if expo > 30 (never the case in this app)
          int expo = integerPartSize - 6;
          expoPart = "E".concat(String.valueOf(expo));
          String significantIntegerPart = strDotPartition[0].substring(0, 6);
          strReply =
                  significantIntegerPart.concat(".").
                          concat(appropriateDecimalPart).concat(expoPart);
        }
      }
      return Float.valueOf(strReply);
    }
  }

  private static String getAppropriateExpoPart(
          String expoPart, int expoLimit) throws Exception {
    int expo = Integer.valueOf(expoPart);
    if (Math.abs(expo) > expoLimit) {
      if (expo < 0) {
        expo = -expoLimit;
      } else // that must not be the case in this app !
        throw new Exception("convertStringOfDouble too big value  !");
    }
    return "E".concat(String.valueOf(expo));
  }

  private static String getAppropriateDecimalPart(
          String decimalPart, int integerPartSize) throws Exception {
    int doubleDecimalSize = decimalPart.length();
    int decimalPartSize = 6 - integerPartSize;
    if (decimalPartSize <= 0) {
      return "0";
    } else
      return decimalPart.substring(
              0, Math.min(doubleDecimalSize, decimalPartSize));
  }


  public static double[] degreeRaDe_to_xyzSphereUnity(double RA, double DE) {
    double RAradian = Math.toRadians(RA);
    double DEradian = Math.toRadians(DE);
    double[] xyzPosition = new double[]{
            Math.cos(DEradian) * Math.cos(RAradian),
            Math.cos(DEradian) * Math.sin(RAradian),
            Math.sin(DEradian)
    };
    return xyzPosition;
  }

  /**
   * Soit PI = Math.PI; , pour Math
   * .sin(0)      //= 0
   * .cos(0)      //= 1
   * .sin(PI/2)   //= 1
   * .cos(PI/2)   //= 6.123031769111886e-17 => ko
   * .sin(PI)     //= 1.2246063538223773e-16 => ko
   * .cos(PI)     //= -1
   * .sin(PI*3/2) //= -1
   * .cos(PI*3/2) //=-1.836909530733566e-16 => ko
   * .sin(PI*2)   //= -2.4492127076447545e-16 => ko
   * .cos(PI*2)   //=1
   *
   * @param RA [0°,360°[
   * @param DE [-90°,+90°]
   * @return double[3] xyz position inside a unity sphere (1= sqrt(x*x+y*y+z*z)).
   */
  public static double[] degreeRaDe_to_unityDirectionVectorXyz(double RA, double DE) {
    double[] val = new double[]{RA, DE};
    double[] cosVal = new double[]{Math.cos(Math.toRadians(RA)), Math.cos(Math.toRadians(DE))};
    double[] sinVal = new double[]{Math.sin(Math.toRadians(RA)), Math.sin(Math.toRadians(DE))};
    //to evit imprecise value:
    for (int i = 0; i < 2; ++i) {
      if (val[i] == 90.0) {
        cosVal[i] = 0.0;
        sinVal[i] = 1.0;
      } else if (val[i] == 180.0) {
        cosVal[i] = -1.0;
        sinVal[i] = 0.0;
      } else if ((val[i] == 270.0) || (val[i] == -90.0)) {
        cosVal[i] = 0.0;
        sinVal[i] = -1.0;
      }
    }
    double[] xyzPosition = new double[]{
            cosVal[1] * cosVal[0], cosVal[1] * sinVal[0], sinVal[1]
    };
    return xyzPosition;
  }

  /**
   * @param newViewDirection
   * @param sphereRadius
   * @return vector unity xyz upDirectionTowardOfNorth
   */
  public static double[] viewDirXyz_to_towardOfNorthUpDirXyz(
          double[] newViewDirection, double sphereRadius)
          throws Exception {
    //double[] tangentPlane = {viewDirection[0],viewDirection[1],viewDirection[2],Math.pow(sphereRadius,2.0)};
    if (newViewDirection[2] != 0.0) {
      double[] ptIntersecAxeZ_tangentPlane = {0.0, 0.0, sphereRadius / newViewDirection[2]};
      double[] upDirectionTowardOfPole = {-newViewDirection[0], -newViewDirection[1],
              ptIntersecAxeZ_tangentPlane[2] - newViewDirection[2]};
      double[] unityUpDirection = new double[]{upDirectionTowardOfPole[0],
              upDirectionTowardOfPole[1], upDirectionTowardOfPole[2]};
      //Prevent equator DE and -45° DE view axe inversion !
      if (newViewDirection[2] < 0.0) {
        unityUpDirection[2] = -unityUpDirection[2];

        if (newViewDirection[2] < -Math.cos(Math.toRadians(45.0))) {
          unityUpDirection[0] = -unityUpDirection[0];
          unityUpDirection[1] = -unityUpDirection[1];
        }
      }
      return unityUpDirection;
    } else {
      return new double[]{0.0, 0.0, 1.0};
    }
  }


  //ENSEMBLE DE METHODES POUR OBTENIR LA POSITION DU CADRE CONTENANT LE BITMAP A AFFICHER:-----

  /**
   * Donne les coordonnées xyz des 4 coins du cadre qui doit contenir le bitmap
   * sachant que ces 4 points,appartiennent au plan tangent au point appelé
   * xyzUpLeftFramePosition de la sphère unité.
   *
   * @param widthOfFrame
   * @param heightOfFrame
   * @param xyzUpLeftFramePosition
   * @return
   */
  public static float[] getXyzPositionForBitmap(double widthOfFrame, double heightOfFrame,
                                                double[] xyzUpLeftFramePosition) throws Exception {
    if (isFor2DvariationBitmapPlane(xyzUpLeftFramePosition)) {
      return getXyzPosFor2D_planeBitmap(widthOfFrame, heightOfFrame, xyzUpLeftFramePosition);
    } else {
      if(xyzUpLeftFramePosition[2] >=1.0) {
        throw new Exception("getXyzPositionForBitmap problem !");
      }
      double xyPlaneRadiusOfUpZ = Math.sqrt(1.0 - Math.pow(xyzUpLeftFramePosition[2], 2.0));
      double cosDE_upZ = xyPlaneRadiusOfUpZ; // (xyPlaneRadiusOfUpZ / 1)
      double sinDE_upZ = xyzUpLeftFramePosition[2]; // (xyzUpLeftFramePosition[2] / 1)
      double sinRA_left =
              xyzUpLeftFramePosition[1] / xyPlaneRadiusOfUpZ;//= sinRA_leftUpZ = sinRA_leftLowZ
      double cosRA_left =
              xyzUpLeftFramePosition[0] / xyPlaneRadiusOfUpZ;//= cosRA_leftUpZ = cosRA_leftLowZ
      double[] xyzLowLeftFramePosition = new double[]{
              (xyzUpLeftFramePosition[0] + heightOfFrame * sinDE_upZ * cosRA_left),
              (xyzUpLeftFramePosition[1] + heightOfFrame * sinDE_upZ * sinRA_left),
              (xyzUpLeftFramePosition[2] - heightOfFrame * cosDE_upZ)
      };
      if(xyzLowLeftFramePosition[2] >=1.0){
        throw new Exception("getXyzPositionForBitmap problem !");
      }
      double xyPlaneRadiusOfLowZ = Math.sqrt(1.0 - Math.pow(xyzLowLeftFramePosition[2], 2.0));
      float zUpLeft = Math_lib.convertFromDouble(xyzUpLeftFramePosition[2]);
      float zLowLeft = Math_lib.convertFromDouble(xyzLowLeftFramePosition[2]);
      return new float[]{
              //low Left corner:
              Math_lib.convertFromDouble(xyzLowLeftFramePosition[0]),
              Math_lib.convertFromDouble(xyzLowLeftFramePosition[1]),
              zLowLeft,
              // low right corner:
              Math_lib.convertFromDouble(xyzLowLeftFramePosition[0] +
                      xyzLowLeftFramePosition[1] / xyPlaneRadiusOfLowZ * widthOfFrame),
              Math_lib.convertFromDouble(xyzLowLeftFramePosition[1] -
                      xyzLowLeftFramePosition[0] / xyPlaneRadiusOfLowZ * widthOfFrame),
              zLowLeft,
              // up right corner:
              Math_lib.convertFromDouble(xyzUpLeftFramePosition[0] +
                      xyzUpLeftFramePosition[1] / xyPlaneRadiusOfUpZ * widthOfFrame),
              Math_lib.convertFromDouble(xyzUpLeftFramePosition[1] -
                      xyzUpLeftFramePosition[0] / xyPlaneRadiusOfUpZ * widthOfFrame),
              zUpLeft,
              //up Left corner:
              Math_lib.convertFromDouble(xyzUpLeftFramePosition[0]),
              Math_lib.convertFromDouble(xyzUpLeftFramePosition[1]),
              zUpLeft,
      };
    }
  }

  private static boolean isFor2DvariationBitmapPlane(double[] xyzUpLeftFramePosition) {
    for (int i = 0; i < xyzUpLeftFramePosition.length; ++i)
      if ((Math.abs(xyzUpLeftFramePosition[i]) != 1.0) && (xyzUpLeftFramePosition[i] != 0.0))
        return false;
    return true;
  }

  /**
   * Les valeurs utilisées pour les doubles peuvent être castées sans problème !
   *
   * @param widthOfFrame           3 ch significatif maximum
   * @param heightOfFrame          3 ch significatif maximum
   * @param xyzUpLeftFramePosition valeurs égales à +-1.0 ou 0.0
   * @return
   * @throws Exception
   */
  private static float[] getXyzPosFor2D_planeBitmap(double widthOfFrame, double heightOfFrame,
                                                    double[] xyzUpLeftFramePosition) throws Exception {
    float[] upLeftCorner = new float[]{(float) xyzUpLeftFramePosition[0],
            (float) xyzUpLeftFramePosition[1], (float) xyzUpLeftFramePosition[2]};
    float[] upRightCorner;
    float[] lowLeftCorner;
    float[] lowRightCorner;
    if (xyzUpLeftFramePosition[2] == 0.0) {
      float hValue = -(float) heightOfFrame;
      float wValue;
      if (xyzUpLeftFramePosition[1] == 0.0) {
        wValue = -(float) (widthOfFrame * xyzUpLeftFramePosition[0]);
        upRightCorner = new float[]{(float) xyzUpLeftFramePosition[0], wValue, 0.0f};
        lowLeftCorner = new float[]{(float) xyzUpLeftFramePosition[0], 0.0f, hValue};
        lowRightCorner = new float[]{(float) xyzUpLeftFramePosition[0], wValue, hValue};
      } else if (xyzUpLeftFramePosition[0] == 0.0) {
        wValue = (float) (widthOfFrame * xyzUpLeftFramePosition[1]);
        upRightCorner = new float[]{wValue, (float) xyzUpLeftFramePosition[1], 0.0f};
        lowLeftCorner = new float[]{0.0f, (float) xyzUpLeftFramePosition[1], hValue};
        lowRightCorner = new float[]{wValue, (float) xyzUpLeftFramePosition[1], hValue};
      } else
        throw new Exception("getXyzPosFor2D_planeBitmap problem !");
      return new float[]{
              //low Left corner:
              lowLeftCorner[0], lowLeftCorner[1], lowLeftCorner[2],
              // low right corner:
              lowRightCorner[0], lowRightCorner[1], lowRightCorner[2],
              // up right corner:
              upRightCorner[0], upRightCorner[1], upRightCorner[2],
              //up Left corner:
              upLeftCorner[0], upLeftCorner[1], upLeftCorner[2],
      };
    } else {
      return getXyzPosFor2D_xyPlaneBitmap(widthOfFrame, heightOfFrame, xyzUpLeftFramePosition[2]);
    }
  }

  private static float[] getXyzPosFor2D_xyPlaneBitmap(double widthOfFrame, double heightOfFrame,
                                                      double zPosition) throws Exception {
    float z = (float) zPosition;
    float hValue = -(float) heightOfFrame;
    float wValue = (float) (widthOfFrame * zPosition);
    return new float[]{
            //low Left corner:
            hValue, 0.0f, z,
            // low right corner:
            hValue, wValue, z,
            // up right corner:
            0.0f, wValue, z,
            //up Left corner:
            0.0f, 0.0f, z,
    };
  }
//END ENSEMBLE DE METHODES POUR OBTENIR LA POSITION DU CADRE ----------------------------------

//functions for GLSurface alteration:

  public static float[] obtainWinXyz(double[] viewXyzDirection, float[] modelView,
                                     float[] stereoProjMatrix, int[] viewport) throws Exception {
    float[] winXyz = new float[3];
    GLU.gluProject(convertFromDouble(viewXyzDirection[0]), convertFromDouble(viewXyzDirection[1]),
            convertFromDouble(viewXyzDirection[2]), modelView, 0,
            stereoProjMatrix, 0, viewport, 0, winXyz, 0);
    return winXyz;
  }

  public static double[] obtainWorldXyz(
          double winX, double winY, double winZ, float[] modelViewMatrix, float[] ProjMatrix,
          int[] viewport) throws Exception {
    float[] newWinXyz = new float[]{convertFromDouble(winX), convertFromDouble(winY),
            convertFromDouble(winZ)};
    float[] worldHomogeneousXyzw = obtainHomogeneousWorldXyz(
            newWinXyz, modelViewMatrix, ProjMatrix, viewport);
    return obtainWorldXyz(worldHomogeneousXyzw);
  }

  public static float[] obtainHomogeneousWorldXyz(
          float[] winXyz, float[] modelView, float[] stereoProjMatrix,
          int[] viewport) throws Exception {
    float[] homogeneousWorldXyz = new float[4];
    GLU.gluUnProject(winXyz[0], winXyz[1], winXyz[2], modelView, 0,
            stereoProjMatrix, 0, viewport, 0, homogeneousWorldXyz, 0);
    return homogeneousWorldXyz;
  }

  public static double[] obtainWorldXyz(float[] worldHomogeneousXyzw) throws Exception {
    double[] worldXyz = new double[3];
    worldXyz[0] = worldHomogeneousXyzw[0] / worldHomogeneousXyzw[3];
    worldXyz[1] = worldHomogeneousXyzw[1] / worldHomogeneousXyzw[3];
    worldXyz[2] = worldHomogeneousXyzw[2] / worldHomogeneousXyzw[3];
    return worldXyz;
  }

  public static double[] obtainWorldXyzPerInterpolation(
          double[] nearWorldXyz, double[] fearWorldXyz) throws Exception {
    double[] worldXyz;
    double[] deltaFearNearWorldXyz =
            new double[]{fearWorldXyz[0] - nearWorldXyz[0],
                    fearWorldXyz[1] - nearWorldXyz[1], fearWorldXyz[2] - nearWorldXyz[2]};
    double aForEquation = Math.pow(deltaFearNearWorldXyz[0], 2) +
            Math.pow(deltaFearNearWorldXyz[1], 2) + Math.pow(deltaFearNearWorldXyz[2], 2);
    double bForEquation = 2.0 * (deltaFearNearWorldXyz[0] * nearWorldXyz[0] +
            deltaFearNearWorldXyz[1] * nearWorldXyz[1] + deltaFearNearWorldXyz[2] * nearWorldXyz[2]);
    double cForEquation = Math.pow(nearWorldXyz[0], 2) +
            Math.pow(nearWorldXyz[1], 2) + Math.pow(nearWorldXyz[2], 2) - 1.0;
    double deltaForEquation = Math.pow(bForEquation, 2) - 4.0 * (aForEquation * cForEquation);
    double[] resolution =
            obtainResolutionOfSecondDegEquation(aForEquation, bForEquation, cForEquation);
    if (resolution == null) {
      throw new Exception(" deltaForEquation < 0 !!!");
    } else {
      if ((resolution[0] <= 1) && (resolution[0] >= 0)) {
        worldXyz = obtainVectorD3_withParametricLineVectorD3(
                resolution[0], deltaFearNearWorldXyz, nearWorldXyz);
      } else if ((resolution[1] <= 1) && (resolution[1] >= 0)) {
        worldXyz = obtainVectorD3_withParametricLineVectorD3(
                resolution[1], deltaFearNearWorldXyz, nearWorldXyz);
      } else {
        throw new Exception(" Aucune solution valable pour l'équation !!!");
      }
    }
    return worldXyz;
  }

  private static double[] obtainResolutionOfSecondDegEquation(
          double aForEquation, double bForEquation, double cForEquation) throws Exception {
    double[] resolution = new double[2];
    double deltaForEquation = Math.pow(bForEquation, 2) - 4.0 * (aForEquation * cForEquation);
    if (deltaForEquation >= 0.0) {
      for (int i = 0; i < 2; ++i) {
        resolution[i] = getResolutionOfSecondDegEquation(
                aForEquation, bForEquation, deltaForEquation, i);
      }
      return resolution;
    } else if (deltaForEquation == 0.0) {
      resolution[0] = getResolutionOfSecondDegEquation(
              aForEquation, bForEquation, deltaForEquation, 0);
      resolution[1] = -444.0;
      return resolution;
    }
    return null;
  }

  private static double getResolutionOfSecondDegEquation(
          double aForEquation, double bForEquation, double deltaForEquation, int i) throws Exception {
    double scrtDelta = Math.sqrt(deltaForEquation);
    if (i == 1) {
      scrtDelta = -scrtDelta;
    }
    return (-bForEquation + scrtDelta) / (2.0 * aForEquation);
  }

  public static double[] obtainVectorD3_withParametricLineVectorD3(
          double alpha, double[] deltaOfLine, double[] initialOfLine) throws Exception {
    double[] vector = new double[3];
    for (int i = 0; i < 3; i++) {
      vector[i] = alpha * deltaOfLine[i] + initialOfLine[i];
    }
    return vector;
  }

  public static double getHoursDecimal(Calendar now) throws Exception {
    return now.get(Calendar.HOUR_OF_DAY) + now.get(Calendar.MINUTE) / 60.0
            + now.get(Calendar.SECOND) / 3600.0;
  }

  /**
   * give decimalDegrees of type HMS (hour, min,sec)
   * or type HMS (deg,min,sec)
   *
   * @param type      String
   * @param paraOne   int (hour/degree)
   * @param paraTwo   int (min)
   * @param paraThree double (sec)
   * @return double (decimalDegrees)
   */
  public static double getDecimalDegrees_fromAngleType(
          String type, int paraOne, int paraTwo, double paraThree) throws Exception {
    if (type.compareTo("HMS") == 0) {
      double decimalHour = paraOne + paraTwo / 60.0 + paraThree / 3600.0;
      return decimalHour * 15.0;//Because 360/24 = 15°/H
    } else if (type.compareTo("DMS") == 0) {
      if (paraOne < 0)
        return -((-paraOne) + paraTwo / 60.0 + paraThree / 3600.0);
      else
        return paraOne + paraTwo / 60.0 + paraThree / 3600.0;
    }
    throw new Exception("Problem with getDecimalDegrees_fromAngleType type !");
  }

  public static double getMin(double a, double b, double c) {
    double firstMin = Math.min(Math.abs(a), Math.abs(b));
    return Math.min(firstMin, Math.abs(c));
  }

  public static float[] getFloatTab(ArrayList<Float> floatArray) throws Exception {
    Float[] noPrimitiveArray = floatArray.toArray(new Float[floatArray.size()]);
    float[] primitiveArray = new float[noPrimitiveArray.length];
    for (int i = 0; i < noPrimitiveArray.length; ++i) {
      primitiveArray[i] = noPrimitiveArray[i];
    }
    return primitiveArray;
  }


//----------------------------------------------------------------------------------------------
  //NOT USED------------------------------------------------------------------------------------

  public static boolean egalityVector3(double[] v1, double[] v2) {
    for (int i = 0; i < 3; ++i) {
      if (v1[i] != v2[i]) {
        return false;
      }
    }
    return true;
  }

  private static Boolean isInsideAccuracyRange(float f, double d, int accuracy) throws Exception {
    String flt = String.valueOf(f);
    String dbl = String.valueOf(d);
    String[] fltSequence = flt.split(".");
    String[] dblSequence = flt.split(".");
    if ((fltSequence.length == 2) && (dblSequence.length == 2)) {
      if (fltSequence[0].compareTo(dblSequence[0]) == 0) {//<=> valeurs non décimales identiques !
        if (fltSequence[1].compareTo(dblSequence[1]) == 0) {//<=> valeurs décimales identiques !
          return true;
        } else {
          Boolean test = false;//check if difference is in accuracy range !
          return test;
        }
      }
    }
    return false;
  }

  public static double[] screenMvtInterpreterDeltaXY_to_RaDeDegree(
          double deltaX, double deltaY, int glSurfHeightPix, int glSurfWidthPix,
          float screenYAngularField, double oldRA, double oldDE) {
    double[] newRaDe = new double[]{0.0, 0.0};
    double degreesPerPix = screenYAngularField / glSurfHeightPix;
    //ATTENTION pour que cela fct, il faut que la direction screenY=heigth
    // soit tj tang au méridien vers  le pôle North ou south le long du méridien !
    //tester le mode...
    //if tablet landscape mode:
    double deltaDE = deltaY * degreesPerPix;
    if (oldDE >= 0.0) {
      newRaDe[1] = oldDE + deltaDE;
      if (newRaDe[1] > 90.0)
        newRaDe[1] = newRaDe[1] - 90.0;
    }
    return newRaDe;
  }

/*
    public static void testTrigonometry(){
        double sinZero = Math.sin(0)    ;         //Expected 0, got 0
        double sinPIDivDeux = Math.sin(Math.PI/2)   ;  //Expected 1, got 1
        double sinPI =  Math.sin(Math.PI) ;     //Expected 0, got 1.2246063538223773e-16
        double sinTroisDemiPI = Math.sin(Math.PI*3/2) ;  //Expected -1, got -1
        double sinDeuxPI = Math.sin(Math.PI*2) ;    //Expected 0, got -2.4492127076447545e-16
        double cosZero = Math.cos(0);             //Expected 1, got 1
        double cosPIDivDeux = Math.cos(Math.PI/2);     //Expected 0, got 6.123031769111886e-17
        double cosPI =  Math.cos(Math.PI);      //Expected -1, got -1
        double cosTroisDemiPI = Math.cos(Math.PI*3/2);   //Expected 0, got -1.836909530733566e-16
        double cosDeuxPI = Math.cos(Math.PI*2);     //Expected 1, got 1

        double[] a = degreeRaDe_to_unityDirectionVectorXyz(0.0,90.0);
        double[] b = degreeRaDe_to_unityDirectionVectorXyz(270.0,-90.0);
        double[] c = degreeRaDe_to_unityDirectionVectorXyz(180.0,0.0);
        double[] d = degreeRaDe_to_unityDirectionVectorXyz(90.0,90.0);
        double[] e = degreeRaDe_to_unityDirectionVectorXyz(0.0,0.0);
        String test = "ok";
    }*/


  /**
   * @param x
   * @param y
   * @param z
   * @param sphereRadius
   * @return double[2] (AD,DEC) position inside the sphere.
   */
  public static double[] xyzSphereRadius_to_AdDec(
          double x, double y, double z, double sphereRadius) {
    double[] reply = new double[]{
            Math.asin(x / sphereRadius), Math.asin(z / sphereRadius)
    };
    return reply;
  }

  /**
   * http://claude.million.pagesperso-orange.fr/stereo.pdf <>???????
   * Modèle mathémathique:  http://mathworld.wolfram.com/StereographicProjection.html
   *
   * @param tanLong
   * @param tanLat
   * @param dotLong
   * @param dotLat
   * @param sphereRadius
   * @return double[2] (x,y) position inside the stereographic plan.
   */
  public static double[] stereoProAnyTangent_forDotXyzSphere_toXyDotPlan(
          double tanLong, double tanLat, double dotLong, double dotLat, double sphereRadius) {
    double[] reply = new double[2];
    double k = 2.0 * sphereRadius / (1.0 + Math.sin(tanLat) * Math.sin(dotLat)
            + Math.cos(tanLat) * Math.cos(dotLat) * Math.cos(dotLong - tanLong));
    reply[0] = k * Math.cos(dotLat) * Math.cos(dotLong - tanLong);
    reply[1] = k * (Math.cos(tanLat) * Math.sin(dotLat) -
            Math.sin(tanLat) * Math.cos(dotLat) * Math.cos(dotLong - tanLong));
    return reply;
  }

  /**
   * Modèle mathémathique:  http://mathworld.wolfram.com/StereographicProjection.html
   *
   * @param tanLong
   * @param tanLat
   * @param xp
   * @param yp
   * @param sphereRadius
   * @return double[2] RA DEC position inside the sphere.
   */
  public static double[] stereoProAnyTangentReverse_forXyDotPlan_toRaDecSphere(
          double tanLong, double tanLat, double xp, double yp, double sphereRadius) {
    double[] reply = new double[2];
    double p = Math.sqrt(xp * xp + yp * yp);
    double c = 2.0 * Math.atan(p / (2.0 * sphereRadius));
    reply[0] = tanLong + Math.atan(xp * Math.sin(c) /
            (p * Math.cos(tanLat) * Math.cos(c) - yp * Math.sin(tanLat) * Math.sin(c)));
    //ATTENTION: le calcul est valable pour des longitudes [-180,180] <> AD [0,360]
    //(Le pt d'origine est ici l'axe x pour les deux ! )
    if (reply[0] < 0) {
      reply[1] = Math.PI - reply[1];
    }
    reply[1] = Math.asin(Math.cos(c) * Math.sin(tanLat) + yp * Math.sin(c) * Math.cos(tanLat) / p);
    return reply;
  }

  /**
   * Modèle mathémathique: http://perso.uclouvain.be/vincent.legat/teaching/iCampusOpen/documents/meca1120/meca1120-1314-cours11.pdf
   *
   * @param xs
   * @param ys
   * @param zs
   * @param sphereRadius
   * @return double[2] (x,y,z) position inside the stereographic plan.
   */
  public static double[] stereoProNPolar_forDotXyzSphere_toXyzDotPlan(
          double xs, double ys, double zs, double sphereRadius) {
    double[] reply = new double[3];
    double factor = 2.0 * sphereRadius / (sphereRadius + zs);
    reply[0] = factor * xs;
    reply[1] = factor * ys;
    reply[2] = sphereRadius;
    return reply;
  }

  /**
   * @param xp
   * @param yp
   * @param sphereRadius
   * @return double[3] (x,y,z) position inside the sphere.
   */
  public static double[] stereoProNPolarReverse_forXyPlan_toXyzSphere(
          double xp, double yp, double sphereRadius) {
    double[] reply = new double[3];
    double cst = 4.0 * sphereRadius * sphereRadius;
    reply[0] = cst * xp / (cst + xp * xp);
    reply[1] = cst * yp / (cst + yp * yp);
    reply[2] = cst * 2.0 * sphereRadius / (cst + xp * xp) - sphereRadius;
    return reply;
  }

  //USEFUL FOR GEOELEMENTS:-------------------------------------------------------------

  public static int integerPart(double d) {
    double integerPart = (d <= 0) ? Math.ceil(d) : Math.floor(d);
    return (int) integerPart;
  }

  //matrice de changement de coordonnées
  public static float[] get4RotMatrixEquatorialToHoursCoordinates(double sideralTime)
          throws Exception {
    double radRotAng = Math.toRadians(sideralTime);
    double cosSidTime = Math.cos(radRotAng);
    double sinSidTime = Math.sin(radRotAng);
    return new float[]{
            convertFromDouble(cosSidTime), -convertFromDouble(sinSidTime), 0.0f, 0.0f,
            convertFromDouble(sinSidTime), convertFromDouble(cosSidTime), 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
    };
  }

  //matrice de changement de coordonnées
  public static float[] rot4OfRepereXYCounterclockMatrix(double angle)
          throws Exception {
    double radRotAng = Math.toRadians(angle);//-angleBetweenPoleAndZenith);
    double cosAng = Math.cos(radRotAng);
    double sinAng = Math.sin(radRotAng);
    return new float[]{
            convertFromDouble(cosAng), convertFromDouble(sinAng), 0.0f, 0.0f,
            -convertFromDouble(sinAng), convertFromDouble(cosAng), 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
    };
  }

  //matrice de changement de coordonnées
  public static float[] get4RotMatrixHoursToAzimuthCoordinates(double angleBetweenPoleAndZenith)
          throws Exception {
    double radRotAng = Math.toRadians(angleBetweenPoleAndZenith);
    double cosAng = Math.cos(radRotAng);
    double sinAng = Math.sin(radRotAng);
    return new float[]{
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, convertFromDouble(cosAng), convertFromDouble(sinAng), 0.0f,
            0.0f, -convertFromDouble(sinAng), convertFromDouble(cosAng), 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
    };
  }
//-------------------------------
  //------------------------------------

  public static float[] getArrayFromNoPrimitiveArrayList(ArrayList<Float> list) throws Exception {
    Float[] noPrimitiveArray = list.toArray(new Float[list.size()]);
    float[] primitiveArray = new float[noPrimitiveArray.length];
    for (int i = 0; i < noPrimitiveArray.length; ++i) {
      primitiveArray[i] = noPrimitiveArray[i];
    }
    return primitiveArray;
  }

}
