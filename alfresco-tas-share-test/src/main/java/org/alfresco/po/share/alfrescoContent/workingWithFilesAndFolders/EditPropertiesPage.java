package org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders;

import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
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

    @FindBy(css = "button[id*='form']")
    private List<WebElement> buttonsList;

    @FindBy(css = "button[id*='yui-gen21-button']")
    protected WebElement selectButtonForCustomSmartFolder;

    @FindBy(css = "[id*='default_prop_dp_offlineExpiresAfter-help-icon']")
    protected WebElement helpIconForRestrictableAspect;

    @FindBy(css = "[id='template_x002e_edit-metadata_x002e_edit-metadata_x0023_default_prop_dp_offlineExpiresAfter-help']")
    protected WebElement helpMessageForRestrictableAspect;

    @FindBy(css = "[id*='default_prop_dp_offlineExpiresAfter-entry']")
    protected WebElement offlineExpiresafterInput;

    private By selectorSF = By.cssSelector("select[id*='default_prop_smf_system-template-location']");

    private By propertiesSelector = By.cssSelector(".viewmode-label");

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
    public String checkDisplayedProperties(ArrayList<String> expectedPropertiesList)
    {
        List<WebElement> propertiesList = browser.findElements(propertiesSelector);
        if (propertiesList.size() == expectedPropertiesList.size())
            for (int i = 0; i < propertiesList.size(); i++)
            {
                String property = propertiesList.get(i).getText();
                if (!property.equals(expectedPropertiesList.get(i)))
                    return "'" + expectedPropertiesList.get(i) + "' isn't displayed.";
            }
        return expectedPropertiesList.toString();
    }

    /**
     * Verify a list of elements isn't displayed in 'Edit Properties' page form
     *
     * @param propertiesNotDisplayedList list of properties to be checked that aren't displayed
     * @return first property from given list that is displayed
     */
    public String checkPropertiesAreNotDisplayed(ArrayList<String> propertiesNotDisplayedList)
    {
        List<WebElement> propertiesList = browser.findDisplayedElementsFromLocator(propertiesSelector);
        for (int i = 0; i < propertiesList.size(); i++)
        {
            String property = propertiesList.get(i).getText();
            for (int j = 0; j < propertiesNotDisplayedList.size(); j++)
            {
                if (property.equals(propertiesNotDisplayedList.get(j)))
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
        for (int i = 0; i < buttonsList.size(); i++)
        {
            if (buttonsList.get(i).getText().equals(buttonName))
                buttonsList.get(i).click();
        }

        return (DocumentDetailsPage) documentDetailsPage.renderedPage();
    }

    public DocumentLibraryPage clickButtonForFolder(String buttonName)

    {
        for (int i = 0; i < buttonsList.size(); i++)
        {
            if (buttonsList.get(i).getText().equals(buttonName))
                buttonsList.get(i).click();
        }

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
        for (int i = 0; i < buttonsList.size(); i++)
        {
            if (buttonsList.get(i).getText().equals(buttonName))
                return true;
        }
        return false;

    }

    public void clickSelectButtonForCustomSmartFolder()

    {
        selectButtonForCustomSmartFolder.click();
    }
}
