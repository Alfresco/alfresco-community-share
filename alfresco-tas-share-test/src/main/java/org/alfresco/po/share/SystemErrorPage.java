package org.alfresco.po.share;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.annotation.PageObject;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * @author Laura.Capsa
 */
@PageObject
public class SystemErrorPage extends HtmlPage
{
    @FindBy(css = ".alf-error-header")
    private WebElement errorHeader;

    public String getErrorHeader()
    {
        return errorHeader.getText();
    }
}
