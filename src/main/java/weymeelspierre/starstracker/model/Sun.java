package weymeelspierre.starstracker.model;

import weymeelspierre.starstracker.library.Time_lib;

/**
 * Created by Pierre on 7/01/2015.
 */
public class Sun {
  private Ephemeris ephemeris = null;
  private double[] lastRA_DE = null;
  private float[] color = new float[]{
          //jaune
          1.0f, 1.0f, 0.0f, 1.0f
  };

  protected Sun(Ephemeris ephemeris) {
    this.ephemeris = ephemeris;
    ephemeris.addColor(color);
    lastRA_DE = ephemeris.besselInterpolation("SUN",Time_lib.getCurrentCalendarUTC());
  }

  protected void updatePosition() {
    lastRA_DE = ephemeris.besselInterpolation("SUN", Time_lib.getCurrentCalendarUTC());
  }
}
