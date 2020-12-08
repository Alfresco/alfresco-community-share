package org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders;

import java.util.ArrayList;
import java.util.List;
import org.alfresco.common.DataUtil;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class EditPropertiesPage extends SiteCommon<EditPropertiesPage>
{
    private SelectDialog selectDialog;

    private final By selectButtonForCustomSmartFolder = By.cssSelector("button[id*='yui-gen21-button']");
    private final By helpIconForRestrictableAspect = By.cssSelector("[id*='default_prop_dp_offlineExpiresAfter-help-icon']");
    private final By helpMessageForRestrictableAspect = By.cssSelector("[id='template_x002e_edit-metadata_x002e_edit-metadata_x0023_default_prop_dp_offlineExpiresAfter-help']");
    private final By offlineExpiresafterInput = By.cssSelector("[id*='default_prop_dp_offlineExpiresAfter-entry']");
    @RenderWebElement
    private final By buttonsList = By.cssSelector("button[id*='form']");
    @RenderWebElement
    private final By propertiesElements = By.cssSelector(".form-field>label");
    private final By selectorSF = By.cssSelector("select[id*='default_prop_smf_system-template-location']");

    public EditPropertiesPage(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/edit-metadata?nodeRef=workspace://SpacesStore/", getCurrentSiteName());
    }

    public boolean arePropertiesDisplayed(String... expectedPropertiesList)
    {
        List<String> propertiesList = new ArrayList<>();
        getBrowser().waitUntilElementsVisible(propertiesElements);

        for (WebElement propertyElement : getBrowser().findElements(propertiesElements))
            if (propertyElement.getText().contains(":"))
                propertiesList.add(propertyElement.getText().substring(0, propertyElement.getText().indexOf(":")));
            else
                propertiesList.add(propertyElement.getText());

        return DataUtil.areListsEquals(propertiesList, expectedPropertiesList);
    }

    public String checkPropertiesAreNotDisplayed(List<String> propertiesNotDisplayedList)
    {
        List<WebElement> elements = getBrowser().findElements(propertiesElements);
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
        getBrowser().findFirstElementWithValue(buttonsList, buttonName).click();
        return (DocumentDetailsPage) new DocumentDetailsPage(browser).renderedPage();
    }

    public void clickButtonForFolder(String buttonName)
    {
        getBrowser().selectOptionFromFilterOptionsList(buttonName, getBrowser().findElements(buttonsList));
    }

    public void clickHelpIconForRestrictableAspect()
    {
       getBrowser().findElement(helpIconForRestrictableAspect).click();
    }

    public String getHelpMessageForRestrictableAspect()
    {

        return getBrowser().findElement(helpMessageForRestrictableAspect).getText();
    }

    public void addOfflineExpiresAfterValue(String hours)
    {
        clearAndType(getBrowser().findElement(offlineExpiresafterInput), hours);
    }

    public void selectSFTemplate(int index)
    {
        Select selectByIndex = new Select(getBrowser().findElement(selectorSF));
        selectByIndex.selectByIndex(index);
    }

    public boolean isButtonDisplayed(String buttonName)
    {
        for (WebElement aButtonsList : getBrowser().findElements(buttonsList))
        {
            if (aButtonsList.getText().equals(buttonName))
                return true;
        }
        return false;

    }

    public SelectDialog clickSelectButtonForCustomSmartFolder()
    {
        getBrowser().findElement(selectButtonForCustomSmartFolder).click();
        return (SelectDialog) selectDialog.renderedPage();
    }
}
