package org.alfresco.po.enums;

public enum TaskStatus
{
    NOT_STARTED("Not Yet Started"),
    IN_PROGRESS("In Progress"),
    ON_HOLD("On Hold"),
    CANCELLED("Cancelled"),
    COMPLETED("Completed");

    private final String status;

    TaskStatus(String status)
    {
        this.status = status;
    }

    public CharSequence getStatus()
    {
        return this.status;
    }
}
