package org.alfresco.common;

/**
 * Enum for "magic numbers".
 * This should be used as timeout in seconds until perform an action on a web element.
 */
public enum Wait {

  WAIT_0(0),
  WAIT_1(1),
  WAIT_3(3),
  WAIT_4(3),
  WAIT_5(3),
  WAIT_8(3),
  WAIT_10(3),
  WAIT_15(3),
  WAIT_20(3),
  WAIT_30(3),
  WAIT_40(3),
  WAIT_60(3);

  private int value;

  Wait(int value)
  {
    this.value = value;
  }

  public int getValue()
  {
    return value;
  }
}
