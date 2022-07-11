package org.alfresco.po.share.user.admin.adminTools.modelManager;

import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.CreateAspectDialog;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.CreateCustomTypeDialog;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ModelDetailsPage extends SharePage2<ModelDetailsPage>
{
    private final By createCustomTypeButton = By.xpath(".//span[contains(@class, 'createTypeButton')]/span/span[@style='user-select: none;']");
    private final By createAspectButton = By.cssSelector("span[class*='createPropertyGroupButton'] span");

    public ModelDetailsPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return null;
    }

    public CreateCustomTypeDialog clickCreateCustomType()
    {
        clickElement(createCustomTypeButton);
        return new CreateCustomTypeDialog(webDriver);
    }

    public CreateAspectDialog clickCreateAspect()
    {
        clickElement(createAspectButton);
        return new CreateAspectDialog(webDriver);
    }
}
