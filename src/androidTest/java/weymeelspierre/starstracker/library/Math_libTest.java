package weymeelspierre.starstracker.library;

import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.TestCase;

import weymeelspierre.starstracker.library.Math_lib;

import static weymeelspierre.starstracker.library.Math_lib.convertFromDouble;


/**
 * Created by Pierre on 25/01/2015.
 */
public class Math_libTest extends TestCase{


 @SmallTest
 public void testConvert() throws Exception{
 /** float f = convertFromDouble(466565);
   float f1 = convertFromDouble(466565.46564564);
   float f2 = convertFromDouble(0.4844454448544444444444);
   float f3 = convertFromDouble(46656464446546465.05);
   float f4 = convertFromDouble(45.0E129);
   float f5 = convertFromDouble(466464.454446E-44);*/
   float f6 = convertFromDouble(12.0000000000000000000000045);
   //float f7 = convertFromDouble(1200000000000000000000000000000000.0);
   float f8 = convertFromDouble(0.000000000000000000000000000000000000001);
   //float f5 = Math_lib.convertStringOfDouble(466464.454446E-44);
   //assertEquals(466565.0,0.0,10.0);
 }

  @Override
  public void tearDown() throws Exception {
    super.tearDown();
  }

  public Math_libTest() {
    super();
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();
  }
}
