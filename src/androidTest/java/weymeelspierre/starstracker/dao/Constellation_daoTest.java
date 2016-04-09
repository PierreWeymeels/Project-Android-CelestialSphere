package weymeelspierre.starstracker.dao;


import android.content.Context;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.HashMap;

import weymeelspierre.starstracker.dao.Constellation_dao;

/**
 * Created by Pierre on 14/02/2015.
 */
public class Constellation_daoTest extends AndroidTestCase {

  Constellation_dao cst_dao = null;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    Context context = this.mContext;
    cst_dao = new Constellation_dao(context);

  }

  @MediumTest
  public void testGetConstellationsData() {
    try {
     ArrayList<String[]> result = cst_dao.getConstellationsData();
     int dataSize = result.size();
     Assert.assertEquals(32, dataSize);
    } catch (Exception e) {
      Assert.assertTrue(false);
    }
  }

  @MediumTest
  public void testGetConstellationsNameAndPosition() {
    try {
      HashMap<String, double[]> result = cst_dao.getConstellationsNameAndPosition();
      int dataSize = result.size();
      Assert.assertEquals(32, dataSize);
    } catch (Exception e) {
      Assert.assertTrue(false);
    }
  }


}
