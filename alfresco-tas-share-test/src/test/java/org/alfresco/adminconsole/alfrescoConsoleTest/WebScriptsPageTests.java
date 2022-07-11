package org.alfresco.adminconsole.alfrescoConsoleTest;

import org.alfresco.po.adminconsole.consoles.WebScriptsPage;
import org.alfresco.adminconsole.ContextAwareWebAdminConsoleTest;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

/**
 * Created by Mirela Tifui on 11/1/2017.
 */
public class WebScriptsPageTests extends ContextAwareWebAdminConsoleTest
{
    @Autowired
    private WebScriptsPage webScriptsPage;

    private String userName = "webScriptsUser" + RandomData.getRandomAlphanumeric();

    @Test (groups = { TestGroup.SHARE, "AlfrescoConsoles", "Acceptance" })
    public void checkWebScriptsPage()
    {
        webScriptsPage.navigate()
            .assertPageTitleIs("Web Scripts Home");
    }
}

