package org.alfresco.po.adminconsole.consoles;

import org.alfresco.utility.web.annotation.PageObject;

/**
 * Created by Mirela Tifui on 11/1/2017.
 */
@PageObject
public class WebScriptsPage extends Console<WebScriptsPage>
{
    @Override
    protected String relativePathToURL()
    {
        return "alfresco/service/index";
    }
}
