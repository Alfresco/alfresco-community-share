package org.alfresco.po.share;

import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * @author Laura.Capsa
 */
@PageObject
public class SystemErrorPage extends HtmlPage
{
    @FindBy (css = ".alf-error-header")
    private WebElement errorHeader;

    public String getErrorHeader()
    {
        getBrowser().waitUntilElementVisible(errorHeader, 10L);
        return errorHeader.getText();
    }
}
