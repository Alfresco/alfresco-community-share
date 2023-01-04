package org.alfresco.share;

import org.alfresco.utility.model.GroupModel;

/**
 * This class contains common data that is used in tests
 */
public class TestUtils
{
    public static final GroupModel ALFRESCO_ADMIN_GROUP = new GroupModel("ALFRESCO_ADMINISTRATORS");
    public static final GroupModel ALFRESCO_SITE_ADMINISTRATORS = new GroupModel("SITE_ADMINISTRATORS");
    public static final GroupModel ALFRESCO_SEARCH_ADMINISTRATORS = new GroupModel("ALFRESCO_SEARCH_ADMINISTRATORS");
    public static String FILE_CONTENT = "Share file content";
    public static String FILE_CONTENT1 = "file_content";
    public static final String PASSWORD = "password";
}
