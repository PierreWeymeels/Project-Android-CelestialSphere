package weymeelspierre.starstracker.model;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;
import java.util.TimeZone;

import weymeelspierre.starstracker.dao.Ephemeris_dao;
import weymeelspierre.starstracker.library.Math_lib;
import weymeelspierre.starstracker.library.Time_lib;

/**
 * Created by Pierre on 4/01/2015.
 */
public class Moon {
  private Ephemeris ephemeris = null;
  private double[] lastRA_DE = null;
  private float[] color = new float[]{1.0f, 1.0f, 1.0f, 1.0f};//blanc//0.5f,0.5f,0.5f,1.0f};//gris
  //0.5f,0.5f,0.0f,1.0f};//false kaki color for report

  protected Moon(Ephemeris ephemeris) {
    this.ephemeris = ephemeris;
    ephemeris.addColor(color);
    lastRA_DE = ephemeris.besselInterpolation("MOON",Time_lib.getCurrentCalendarUTC());
  }


  protected void updatePosition() {
    lastRA_DE = ephemeris.besselInterpolation("MOON",Time_lib.getCurrentCalendarUTC());
  }
}
