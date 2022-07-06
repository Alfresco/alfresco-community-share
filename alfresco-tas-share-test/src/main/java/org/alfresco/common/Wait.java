package org.alfresco.common;

/**
 * Enum for "magic numbers". This should be used as timeout in seconds until perform an action on a
 * web element.
 */
public enum Wait
{
    WAIT_0(0),
    WAIT_1(1),
    WAIT_2(2),
    WAIT_3(3),
    WAIT_4(4),
    WAIT_5(5),
    WAIT_8(8),
    WAIT_10(10),
    WAIT_15(15),
    WAIT_20(20),
    WAIT_30(30),
    WAIT_40(40),
    WAIT_50(50),
    WAIT_60(60),
    WAIT_70(70),
    WAIT_80(80);

    private final int value;

    Wait(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }
}
