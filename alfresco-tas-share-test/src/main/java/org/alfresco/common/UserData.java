package org.alfresco.common;

public class UserData
{

    private String userRole;
    private String userName;

    public UserData(String name, String role)
    {
        userName = name;
        userRole = role;
    }

    public String getUserName()
    {
        return userName;
    }

    public String getUserRole()
    {
        return userRole;
    }

}
