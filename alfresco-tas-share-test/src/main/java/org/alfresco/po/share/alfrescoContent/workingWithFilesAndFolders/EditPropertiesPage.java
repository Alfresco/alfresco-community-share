package org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders;

import java.util.ArrayList;
import java.util.List;
import org.alfresco.common.DataUtil;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class EditPropertiesPage extends SiteCommon<EditPropertiesPage>
{
    private final By selectButtonForCustomSmartFolder = By.cssSelector("button[id*='yui-gen21-button']");
    private final By helpIconForRestrictableAspect = By.cssSelector("[id*='default_prop_dp_offlineExpiresAfter-help-icon']");
    private final By helpMessageForRestrictableAspect = By.cssSelector("[id='template_x002e_edit-metadata_x002e_edit-metadata_x0023_default_prop_dp_offlineExpiresAfter-help']");
    private final By offlineExpiresafterInput = By.cssSelector("[id*='default_prop_dp_offlineExpiresAfter-entry']");
    private final By buttonsList = By.cssSelector("button[id*='form']");
    private final By propertiesElements = By.cssSelector(".form-field>label");
    private final By selectorSF = By.cssSelector("select[id*='default_prop_smf_system-template-location']");

    public EditPropertiesPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/edit-metadata?nodeRef=workspace://SpacesStore/", getCurrentSiteName());
    }

    public boolean arePropertiesDisplayed(String... expectedPropertiesList)
    {
        List<String> propertiesList = new ArrayList<>();
        webElementInteraction.waitUntilElementsAreVisible(propertiesElements);

        for (WebElement propertyElement : webElementInteraction.findElements(propertiesElements))
            if (propertyElement.getText().contains(":"))
                propertiesList.add(propertyElement.getText().substring(0, propertyElement.getText().indexOf(":")));
            else
                propertiesList.add(propertyElement.getText());

        return DataUtil.areListsEquals(propertiesList, expectedPropertiesList);
    }

    public String checkPropertiesAreNotDisplayed(List<String> propertiesNotDisplayedList)
    {
        List<WebElement> elements = webElementInteraction.findElements(propertiesElements);
        for (int i = 0; i < elements.size(); i++)
        {
            String property = elements.get(i).getText();
            for (String aPropertiesNotDisplayedList : propertiesNotDisplayedList)
            {
                if (property.equals(aPropertiesNotDisplayedList))
                    return propertiesNotDisplayedList.get(i);
            }
        }
        return "Given list isn't displayed";
    }

    public DocumentDetailsPage clickButton(String buttonName)
    {
        webElementInteraction.findFirstElementWithValue(buttonsList, buttonName).click();
        return new DocumentDetailsPage(webDriver);
    }

    public void clickButtonForFolder(String buttonName)
    {
        webElementInteraction.selectOptionFromFilterOptionsList(buttonName, webElementInteraction.findElements(buttonsList));
    }

    public void clickHelpIconForRestrictableAspect()
    {
       webElementInteraction.findElement(helpIconForRestrictableAspect).click();
    }

    public String getHelpMessageForRestrictableAspect()
    {

        return webElementInteraction.findElement(helpMessageForRestrictableAspect).getText();
    }

    public void addOfflineExpiresAfterValue(String hours)
    {
        webElementInteraction.clearAndType(webElementInteraction.findElement(offlineExpiresafterInput), hours);
    }

    public void selectSFTemplate(int index)
    {
        Select selectByIndex = new Select(webElementInteraction.findElement(selectorSF));
        selectByIndex.selectByIndex(index);
    }

    public boolean isButtonDisplayed(String buttonName)
    {
        for (WebElement aButtonsList : webElementInteraction.findElements(buttonsList))
        {
            if (aButtonsList.getText().equals(buttonName))
                return true;
        }
        return false;
    }

    public SelectDialog clickSelectButtonForCustomSmartFolder()
    {
        webElementInteraction.findElement(selectButtonForCustomSmartFolder).click();
        return new SelectDialog(webDriver);
    }
}
