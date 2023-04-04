package org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.common.DataUtil;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Slf4j
public class EditPropertiesPage extends SiteCommon<EditPropertiesPage>
{
    private final By selectButtonForCustomSmartFolder = By.cssSelector("button[id*='yui-gen21-button']");
    private final By helpIconForRestrictableAspect = By.cssSelector("[id*='default_prop_dp_offlineExpiresAfter-help-icon']");
    private final By helpMessageForRestrictableAspect = By.cssSelector("[id='template_x002e_edit-metadata_x002e_edit-metadata_x0023_default_prop_dp_offlineExpiresAfter-help']");
    private final By offlineExpiresafterInput = By.cssSelector("[id*='default_prop_dp_offlineExpiresAfter-entry']");
    private final By buttonsList = By.cssSelector("button[id*='form']");
    private final By selectButton = By.xpath("//button[text()='Select']");
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
        waitUntilElementsAreVisible(propertiesElements);

        for (WebElement propertyElement : findElements(propertiesElements))
            if (propertyElement.getText().contains(":"))
                propertiesList.add(propertyElement.getText().substring(0, propertyElement.getText().indexOf(":")));
            else
                propertiesList.add(propertyElement.getText());

        return DataUtil.areListsEquals(propertiesList, expectedPropertiesList);
    }

    public boolean arePropertiesDisplayed_(ArrayList<String> expectedPropertiesList)
    {
        List<String> propertiesList = new ArrayList<>();
        waitUntilElementsAreVisible(propertiesElements);

        for (WebElement propertyElement : findElements(propertiesElements))
            if (propertyElement.getText().contains(":"))
                propertiesList.add(propertyElement.getText().substring(0, propertyElement.getText().indexOf(":")));
            else
                propertiesList.add(propertyElement.getText());

        return propertiesList.containsAll(expectedPropertiesList);
    }

    public String checkPropertiesAreNotDisplayed(List<String> propertiesNotDisplayedList)
    {
        List<WebElement> elements = findElements(propertiesElements);
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
        findFirstElementWithValue(buttonsList, buttonName).click();
        refresh();
        return new DocumentDetailsPage(webDriver);
    }
    public SelectDialog clickSelectButton()
    {
        findElement(selectButton).click();
        return new SelectDialog(webDriver);
    }

    public void clickButtonForFolder(String buttonName)
    {
        selectOptionFromFilterOptionsList(buttonName, findElements(buttonsList));
    }

    public void clickHelpIconForRestrictableAspect()
    {
       findElement(helpIconForRestrictableAspect).click();
    }

    public String getHelpMessageForRestrictableAspect()
    {

        return findElement(helpMessageForRestrictableAspect).getText();
    }

    public void addOfflineExpiresAfterValue(String hours)
    {
        clearAndType(findElement(offlineExpiresafterInput), hours);
    }

    public void selectSFTemplate(int index)
    {
        Select selectByIndex = new Select(findElement(selectorSF));
        selectByIndex.selectByIndex(index);
    }

    public boolean isButtonDisplayed(String buttonName)
    {
        for (WebElement aButtonsList : findElements(buttonsList))
        {
            if (aButtonsList.getText().equals(buttonName))
                return true;
        }
        return false;
    }

    public SelectDialog clickSelectButtonForCustomSmartFolder()
    {
        findElement(selectButtonForCustomSmartFolder).click();
        return new SelectDialog(webDriver);
    }

    public EditPropertiesPage assertPropertiesAreDisplayed(ArrayList<String> properties)
    {
        log.info("Assert properties are displayed {}", Arrays.asList(properties));
        assertTrue(arePropertiesDisplayed_(properties), "Not all properties are displayed");
        return this;
    }

    public EditPropertiesPage assertPropertiesAreNotDisplayed(String... properties)
    {
        log.info("Assert properties are displayed {}", Arrays.asList(properties));
        assertFalse(arePropertiesDisplayed(properties), "All properties are displayed");
        return this;
    }

    public EditPropertiesPage assertHelpMessageForRestrictableAspectIsEquals(String helpMsg)
    {
        log.info("Verify the help message of Restrictable Aspect is {}", helpMsg);
        assertEquals(getHelpMessageForRestrictableAspect(), helpMsg, String.format("Help message for Restrictable Aspect not matched with %s ", helpMsg));
        return this;
    }
}
