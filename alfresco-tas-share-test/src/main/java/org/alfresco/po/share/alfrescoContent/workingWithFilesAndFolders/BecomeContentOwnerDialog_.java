package org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.ShareDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * @author Laura.Capsa
 */

@Slf4j
public class BecomeContentOwnerDialog_ extends SiteCommon<BecomeContentOwnerDialog_>
{
    //@Autowired
    DocumentDetailsPage documentDetailsPage;
    private final By title = By.id("prompt_h");
    private final By message = By.cssSelector("#prompt .bd");
    private final By cancelButton = By.xpath("//span[@class='button-group']//button[text()='Cancel']");

//    @FindBy (id = "prompt_h")
//    private WebElement title;
//
//    @FindBy (css = "#prompt .bd")
//    private WebElement message;
//
//    @FindBy (css = ".button-group button")
//    private List<WebElement> buttonList;

    public BecomeContentOwnerDialog_(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    private WebElement getButton(String buttonName)
    {
        return findElement(By.xpath(String.format("//button[contains(@id, 'button')][text()='%s']", buttonName)));
    }

    public boolean isDialogDisplayed()
    {
        return isElementDisplayed(message);
    }

    public String getMessage()
    {
        return findElement(message).getText();
    }

    public String getHeader()
    {
        return findElement(title).getText();
    }

    /**
     * Click on any button from the dialog
     *
     * @param buttonName to be clicked
     */
    public void clickButton(String buttonName)
    {
        getButton(buttonName).click();
        waitInSeconds(2);
    }

    public void clickCancelButton()
    {
        findElement(cancelButton).click();
    }

    @Override public String getRelativePath()
    {
        return String.format("share/page/site/%s/documentlibrary", getCurrentSiteName());
    }

    public BecomeContentOwnerDialog_ assertIsBecomeContentOwnerDialogDisplayed()
    {
        log.info("Verify the Become content owner dialog displayed");
        assertTrue(isDialogDisplayed(), "'Become Owner' dialog is not displayed.");
        return this;
    }

    public BecomeContentOwnerDialog_ assertIsBecomeContentOwnerDialogNotDisplayed()
    {
        log.info("Verify the Become content owner dialog not displayed");
        assertFalse(isDialogDisplayed(), "'Become Owner' dialog is displayed.");
        return this;
    }

    public BecomeContentOwnerDialog_ assertVerifyDialogHeader(String header)
    {
        log.info("Verify the Become content owner dialog header {}", header);
        assertEquals(getHeader(), header,  String.format("Dialog header not matched with %s ", header));
        return this;
    }

    public BecomeContentOwnerDialog_ assertVerifyDialogMessage(String contentName)
    {
        log.info("Verify the Become content owner dialog message");
        assertEquals(getMessage(), "You'll become the owner of '" + contentName + "' and the previous owner may have their permissions restricted.", "Message not matched");
        return this;
    }
}
