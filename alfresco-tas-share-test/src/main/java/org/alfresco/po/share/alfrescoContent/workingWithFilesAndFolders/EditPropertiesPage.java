package org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Laura.Capsa
 */
@PageObject
public class EditPropertiesPage extends SiteCommon<EditPropertiesPage>
{
    @Autowired
    DocumentDetailsPage documentDetailsPage;

    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    SelectDialog selectDialog;

    @RenderWebElement
    @FindAll(@FindBy(css = "button[id*='form']"))
    private List<WebElement> buttonsList;

    @RenderWebElement
    @FindAll(@FindBy(css = ".form-field>label"))
    private List<WebElement> propertiesElements;

    @FindBy(css = "button[id*='yui-gen21-button']")
    protected WebElement selectButtonForCustomSmartFolder;

    @FindBy(css = "[id*='default_prop_dp_offlineExpiresAfter-help-icon']")
    protected WebElement helpIconForRestrictableAspect;

    @FindBy(css = "[id='template_x002e_edit-metadata_x002e_edit-metadata_x0023_default_prop_dp_offlineExpiresAfter-help']")
    protected WebElement helpMessageForRestrictableAspect;

    @FindBy(css = "[id*='default_prop_dp_offlineExpiresAfter-entry']")
    protected WebElement offlineExpiresafterInput;

    private By selectorSF = By.cssSelector("select[id*='default_prop_smf_system-template-location']");

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/edit-metadata?nodeRef=workspace://SpacesStore/", getCurrentSiteName());
    }

    /**
     * Verify displayed elements from 'Edit Properties' page form
     *
     * @param expectedPropertiesList list of expected properties to be checked
     * @return displayed properties
     */
    public boolean arePropertiesDisplayed(String... expectedPropertiesList)
    {
        List<String> propertiesList = new ArrayList<>();
        getBrowser().waitUntilElementsVisible(propertiesElements);

        for(WebElement propertyElement : propertiesElements)
            if(propertyElement.getText().contains(":"))
            propertiesList.add(propertyElement.getText().substring(0, propertyElement.getText().indexOf(":")));
            else
                propertiesList.add(propertyElement.getText());

        return DataUtil.areListsEquals(propertiesList, expectedPropertiesList);
    }

    /**
     * Verify a list of elements isn't displayed in 'Edit Properties' page form
     *
     * @param propertiesNotDisplayedList list of properties to be checked that aren't displayed
     * @return first property from given list that is displayed
     */
    public String checkPropertiesAreNotDisplayed(ArrayList<String> propertiesNotDisplayedList)
    {
        for (int i = 0; i < propertiesElements.size(); i++)
        {
            String property = propertiesElements.get(i).getText();
            for (String aPropertiesNotDisplayedList : propertiesNotDisplayedList)
            {
                if (property.equals(aPropertiesNotDisplayedList))
                    return propertiesNotDisplayedList.get(i);
            }
        }
        return "Given list isn't displayed";
    }

    /**
     * Click on a submit form button
     *
     * @param buttonName to be clicked: Save, Cancel
     */
    public DocumentDetailsPage clickButton(String buttonName)
    {
        browser.findFirstElementWithExactValue(buttonsList, buttonName).click();
        return (DocumentDetailsPage) documentDetailsPage.renderedPage();
    }

    public DocumentLibraryPage clickButtonForFolder(String buttonName)
    {
        browser.selectOptionFromFilterOptionsList(buttonName, buttonsList);
        return (DocumentLibraryPage) documentLibraryPage.renderedPage();
    }

    public void clickHelpIconForRestrictableAspect()

    {
        helpIconForRestrictableAspect.click();
    }

    public String getHelpMessageForRestrictableAspect()
    {

        return helpMessageForRestrictableAspect.getText();
    }

    public void addOfflineExpiresAfterValue(String hours)

    {

        offlineExpiresafterInput.sendKeys(hours);
    }

    public void selectSFTemplate(int index)

    {
        Select selectByIndex = new Select(browser.findElement(selectorSF));
        selectByIndex.selectByIndex(index);
    }

    public boolean isButtonDisplayed(String buttonName)

    {
        for (WebElement aButtonsList : buttonsList)
        {
            if (aButtonsList.getText().equals(buttonName))
                return true;
        }
        return false;

    }

    public SelectDialog clickSelectButtonForCustomSmartFolder()
    {
        selectButtonForCustomSmartFolder.click();
        return (SelectDialog) selectDialog.renderedPage();
    }
}
