package org.alfresco.common;

public enum RetryTime
{
    RETRY_TIME_0(0),
    RETRY_TIME_1(1),
    RETRY_TIME_2(2),
    RETRY_TIME_3(3),
    RETRY_TIME_4(4),
    RETRY_TIME_5(5),
    RETRY_TIME_8(8),
    RETRY_TIME_10(10),
    RETRY_TIME_15(15),
    RETRY_TIME_20(20),
    RETRY_TIME_30(30),
    RETRY_TIME_40(40),
    RETRY_TIME_50(50),
    RETRY_TIME_60(60),
    RETRY_TIME_70(70),
    RETRY_TIME_80(80),
    RETRY_TIME_100(100);

    private final int value;

    RetryTime(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }
}
