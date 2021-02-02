package org.alfresco.po.enums;

public enum DataListTypes
{
    CONTACT_LIST("Contact List", "Contacts list including first name, last name, full name, email, job title, phone (office), phone (mobile)."),
    EVENT_AGENDA("Event Agenda", "Manage event agenda items including session names, presenters, start and end times."),
    EVENT_LIST("Event List", "Events list including title, description, location, start and end date/time."),
    ISSUE_LIST("Issue List", "Issues list including ID, status, priority, description, due data, comments, assign to, related issues."),
    LOCATION_LIST("Location List", "Locations/Addresses list"),
    MEETING_AGENDA("Meeting Agenda", "Manage meeting agenda items including description, owner, allocated time."),
    TASK_LIST_ADVANCED("Task List (Advanced)", "Advanced tasks list including title, description, start and end dates, priority, status, comments, assignees and attachments."),
    TASK_LIST_SIMPLE("Task List (Simple)", "Simple tasks list including title, description, due date, priority, status, comments."),
    TO_DO_LIST("To Do List", "A simple to do list with optional assignee.");

    public final String title;
    public final String description;

    DataListTypes(String title, String description)
    {
        this.title = title;
        this.description = description;
    }

    @Override
    public String toString()
    {
        return this.title;
    }
}
