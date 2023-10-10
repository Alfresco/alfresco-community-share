package org.alfresco.po.adminconsole;

import java.util.List;

public interface AdminConsole
{
    public Navigator getNavigator();

    /**
     * @return the page header value
     */
    public String getPageHeader();

    /**
     * @return the information of the page - this is commonly added in the "info" tag
     */
    public String getInfoPage();

    /**
     * @return the information of the page - this is commonly added in the "intro" tag
     */
    public String getIntroPage();

    public List<ControlObject> getPageFields();

    /**
     * @return
     */
    public String getMessage();

}
