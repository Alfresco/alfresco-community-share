package org.alfresco.po.share;

import org.alfresco.common.Language;
import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

/**
 * @author Laura.Capsa
 */
@PageObject
public class SystemErrorPage extends HtmlPage
{
    @Autowired
    private Language language;

    @RenderWebElement
    @FindBy (css = ".alf-error-header")
    private WebElement errorHeader;

    public String getErrorHeader()
    {
        getBrowser().waitUntilElementVisible(errorHeader, 10L);
        return errorHeader.getText();
    }

    public SystemErrorPage assertSomethingIsWrongWithThePageMessageIsDisplayed()
    {
        LOG.info("Assert Something is wrong with the page message is displayed");
        Assert.assertTrue(getErrorHeader().contains(language.translate("systemError.header")));
        return this;
    }
}
