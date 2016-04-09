package weymeelspierre.starstracker.dao;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Pierre on 14/02/2015.
 */
public class Stars_daoTest extends AndroidTestCase {

  enum CstName{
    And,Ant,Aps,Aqr,Aql,Ara,Ari,Aur,
    Boo,Cam,Cnc,Cvn,CMa,CMi, Cap, Car, Cas,Cen,Cep,Cyg,
    Leo,Ori,Lib,Lyr,Peg,Per,Psc,Sge,Sgr,Sco,UMi,UMa
  };
  Stars_dao stars_dao = null;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    Context context = this.mContext;
    stars_dao = new Stars_dao(context);

  }

  @MediumTest
  public void testGetStarsPosition() {
    try {
      ArrayList<double[]> result = stars_dao.getStarsPosition(6.0);
      int dataSize = result.size();
      Assert.assertEquals(1628, dataSize);
    } catch (Exception e) {
      Assert.assertTrue(false);
    }
  }

  @MediumTest
  public void testGreekLetterPosition() {
    try {
      for(CstName cstName: CstName.values()){
        HashMap<String, double[]> result = stars_dao.greekLetterPosition(cstName.name());
      }
      Assert.assertTrue(true);
    } catch (Exception e) {
      Assert.assertTrue(false);
    }
  }

  @MediumTest
  public void testGetStarsNameAndPosition() {
    try {
      HashMap<String, double[]> result = stars_dao.getStarsNameAndPosition();
      int dataSize = result.size();
      Assert.assertEquals(48, dataSize);
    } catch (Exception e) {
      Assert.assertTrue(false);
    }
  }

}
