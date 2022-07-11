package org.alfresco.po.share.user.admin;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

/**
 * @author Laura.Capsa
 */
@PageObject
public class CreateEditReplicationJobPage extends SharePage<CreateEditReplicationJobPage>
{
    //@Autowired
    ReplicationJobsPage replicationJobsPage;

    @FindBy (css = ".form-manager")
    private WebElement headerTitle;

    @RenderWebElement
    @FindBy (css = "input[id*='name']")
    private WebElement nameInputField;

    @RenderWebElement
    @FindBy (css = "textarea[id*='description']")
    private WebElement descriptionTextarea;

    @FindBy (xpath = "(.//div[contains(@id, 'payloadContainer')]//button)[1]")
    private WebElement selectPaylodButton;

    @FindBy (css = "[id*='transferTargetContainer'] button")
    private WebElement selectTransferTargetButton;

    @FindAll (@FindBy (css = "tr.yui-dt-rec"))
    private List<WebElement> leftSideRows;

    @FindBy (css = "button[id*='payload-cntrl-ok']")
    private WebElement okButtonPayload;

    @FindBy (css = "button[id*='targetName-cntrl-ok']")
    private WebElement okButtonTransferTarget;

    @FindBy (css = ".form-container .name")
    private WebElement selectedPayload;

    @FindBy (css = "div[class*='transferTarget']")
    private WebElement selectedTransferTarget;

    @FindBy (css = "input[id*='enabled-entry']")
    private WebElement enabledCheckbox;

    @FindBy (css = "button[id*='submit']")
    private WebElement creatJobButton;

    private By itemNameSelector = By.cssSelector(".item-name a");
    private By addIcon = By.cssSelector(".addIcon");

    @Override
    public String getRelativePath()
    {
        return "page/console/replication-job?jobName=";
    }

    public String getHeaderTitle()
    {
        return headerTitle.getText();
    }

    public boolean isSelectDialogDisplayed()
    {
        return browser.isElementDisplayed(okButtonPayload);
    }

    public void typeName(String name)
    {
        nameInputField.clear();
        nameInputField.sendKeys(name);
    }

    public void typeDescriptionTextArea(String description)
    {
        descriptionTextarea.clear();
        descriptionTextarea.sendKeys(description);
    }

    public void fillInInputFields(String name, String description)
    {
        typeName(name);
        typeDescriptionTextArea(description);
    }

    public void clickSelectPayloadButton()
    {
        selectPaylodButton.click();
    }

    public void clickSelectTransferTargetButton()
    {
        selectTransferTargetButton.click();
        browser.waitUntilElementClickable(addIcon, 20);
    }

    /**
     * Click 'Add' icon for an item from left side panel, in 'Select...' dialog
     *
     * @param itemName
     */
    public void clickAddIconFromList(String itemName)
    {
        //browser.waitUntilElementsVisible(By.cssSelector("tr.yui-dt-rec"));
        browser.findFirstElementWithValue(leftSideRows, itemName).findElement(addIcon).click();
    }

    public void clickAddIcon()
    {
        browser.waitUntilElementClickable(addIcon, 20).click();
    }

    public void clickTargetOkButton()
    {
        browser.waitUntilElementClickable(okButtonTransferTarget, 30).click();
    }

    /**
     * Click 'OK' button from 'Select...' dialog
     */
    public void clickPayloadOkButton()
    {
        browser.waitUntilElementVisible(okButtonPayload).click();
    }

    /**
     * @param itemName to be clicked from dialog's left side panel
     */
    public void clickLeftSidePanelDialogItem(String itemName)
    {
        browser.waitUntilElementsVisible(itemNameSelector);
        browser.findFirstElementWithValue(itemNameSelector, itemName).click();
    }

    /**
     * Click on each item from listOfLeftSideItems. Click Add icon
     *
     * @param listOfLeftSideItems to be clicked on from dialog's left side panel
     */
    public void selectItemFromDialog(ArrayList<String> listOfLeftSideItems)
    {
        for (int i = 0; i < listOfLeftSideItems.size() - 1; i++)
        {
            clickLeftSidePanelDialogItem(listOfLeftSideItems.get(i));
        }
        clickAddIconFromList(listOfLeftSideItems.get(listOfLeftSideItems.size() - 1));
    }

    public String getSelectedPayload()
    {
        return selectedPayload.getText();
    }

    public String getSelectedTransferTarget()
    {
        return selectedTransferTarget.getText();
    }

    public void clickEnabledCheckbox()
    {
        enabledCheckbox.click();
    }

    public boolean isEnabledChecked()
    {
        return enabledCheckbox.isSelected();
    }

    public void clickCreateJobButton()
    {
        creatJobButton.click();
    }
}