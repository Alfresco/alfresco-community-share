package org.alfresco.po.share.user.profile;

public class AboutUserDetails
{
    private String firstName;
    private String lastName;
    private String jobTitle;
    private String location;
    private String summary;

    public AboutUserDetails(String firstName, String lastName, String jobTitle, String location, String summary)
    {
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setJobTitle(jobTitle);
        this.setLocation(location);
        this.setSummary(summary);
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getJobTitle()
    {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle)
    {
        this.jobTitle = jobTitle;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getSummary()
    {
        return summary;
    }

    public void setSummary(String summary)
    {
        this.summary = summary;
    }
}
