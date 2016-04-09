package weymeelspierre.starstracker.model;

import java.util.HashMap;

/**
 * Created by Pierre on 28/12/2014.
 */
public class Constellation {

  private String name = null;
  private String[] constellationSequence = null;
  private int[] branchIndex = null;

  private HashMap<String, float[]> greekLetterAndPosition = null;
  private int positionDimension = 3;
  private float[] positionSequence = null;


  protected Constellation(String name, String[] constellationSequence, String[] branchIndex) {
    this.name = name;
    this.constellationSequence = constellationSequence;
    this.branchIndex = obtain_branchIndex(branchIndex);
  }

  private int[] obtain_branchIndex(String[] branchIndex) {
    int[] reply = new int[branchIndex.length];
    for (int i = 0; i < branchIndex.length; ++i) {
      reply[i] = Integer.decode(branchIndex[i]);// or .valueOf(...)
    }
    return reply;
  }


  protected void iniPositionOfConstellation(HashMap<String, float[]> greekLetterAndPosition) {
    this.greekLetterAndPosition = greekLetterAndPosition;
    iniPositionsSequence();
  }

  private void iniPositionsSequence() {
    int cstLength = constellationSequence.length;
    float[] positionData = new float[cstLength * positionDimension];
    for (int i = 0; i < cstLength; ++i) {
      float[] positionXyz = greekLetterAndPosition.get(constellationSequence[i]);
      for (int j = 0; j < 3; ++j) {
        positionData[(i * 3) + j] = positionXyz[j];
      }
    }
    this.positionSequence = positionData;
  }

  public int[] getBranchIndex() {
    return branchIndex;
  }

  public float[] getPositionSequence() {
    return positionSequence;
  }

  public String[] getConstellationSequence() {
    return constellationSequence;
  }
}
