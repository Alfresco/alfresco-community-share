package org.alfresco.po.share.dashlet;

import java.util.List;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.HtmlElement;
import ru.yandex.qatools.htmlelements.element.Select;

/**
 * Created by Mirela Tifui on 12/11/2017.
 */
@PageObject
public class SiteWebQuickStartDashlet extends Dashlet<SiteWebQuickStartDashlet>
{
    @RenderWebElement
    @FindBy (css = "div[class='dashlet']")
    protected HtmlElement dashletContainer;

    @FindBy (css = "select[id$='_default-load-data-options']")
    private Select selectData;

    @FindBy (css = "button[id$='_default-load-data-link']")
    private Button importButton;

    @Override
    protected String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
    }

    public void selectTypeOfData(String data)
    {
        selectData.selectByValue(data);
    }

    public boolean isOptionAvailable(String option)
    {
        List<WebElement> options = selectData.getOptions();
        return options.contains(option);
    }

    public boolean isImportButtonAvailable()
    {
        return importButton.getWrappedElement().isDisplayed();
    }


}
