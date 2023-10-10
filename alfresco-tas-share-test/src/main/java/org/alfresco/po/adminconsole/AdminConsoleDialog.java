package org.alfresco.po.adminconsole;

import static org.alfresco.utility.report.log.Step.STEP;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Created by Claudia Agache on 5/9/2017.
 */
public abstract class AdminConsoleDialog extends HtmlPage
{
    @RenderWebElement
    @FindBy (id = "admin-dialog")
    protected WebElement dialogFrame;

    private By pageControlLocator = By.cssSelector("div[class~=control]");
    protected By closeButtonLocator = By.className("cancel");
    private By titleLocator = By.className("title");
    private By introLocator = By.className("intro");

    public HtmlPage clickClose(HtmlPage t)
    {
        STEP("Click Close button");
        browser.switchTo().frame(dialogFrame);
        browser.waitUntilElementClickable(closeButtonLocator, properties.getExplicitWait()).click();
        browser.switchTo().defaultContent();
        return t.renderedPage();
    }

    public boolean isCloseButtonDisplayed()
    {
        browser.switchTo().frame(dialogFrame);
        boolean isDisplayed = browser.isElementDisplayed(closeButtonLocator);
        browser.switchTo().defaultContent();
        return isDisplayed;
    }

    public String getIntro()
    {
        browser.switchTo().frame(dialogFrame);
        String intro = browser.waitUntilElementVisible(introLocator).getText().trim();
        browser.switchTo().defaultContent();
        return intro;
    }

    public String getTitle()
    {
        browser.switchTo().frame(dialogFrame);
        String title = browser.waitUntilElementVisible(titleLocator).getText().trim();
        browser.switchTo().defaultContent();
        return title;
    }

    public boolean isDialogDisplayed()
    {
        return browser.isElementDisplayed(dialogFrame);
    }

    public List<ControlObject> getPageFields()
    {
        List<WebElement> pageControls = browser.findDisplayedElementsFromLocator(pageControlLocator);
        List<ControlObject> pageControlObjects = new ArrayList<ControlObject>();
        for (WebElement control : pageControls)
        {
            String label = control.findElement(By.className("label")).getText();
            WebElement input = null;
            String description;
            String type = control.getAttribute("class").split(" ")[1];

            switch (type)
            {
                case "field":
                    input = control.findElement(By.className("value"));
                    break;
                case "status":
                    input = control.findElement(By.cssSelector(".value span"));
                    break;
                case "checkbox":
                case "text":
                    input = control.findElement(By.cssSelector(".value input"));
                    break;
                case "options":
                    input = control.findElement(By.cssSelector(".value select"));
                    break;
                default:
                    input = control.findElement(By.className("value"));
                    break;
            }
            description = browser.isElementDisplayed(control, By.className("description")) ? control.findElement(By.className("description")).getText() : "";

            pageControlObjects.add(new AdminConsoleObject(label, input, description, type));
        }
        return pageControlObjects;
    }

    public ControlObject getPageField(String fieldLabel) throws Exception
    {
        for (ControlObject field : getPageFields())
        {
            if (field.getLabel().equals(fieldLabel))
                return field;
        }
        throw new Exception(String.format("Could not find dialog field with label %s", fieldLabel));
    }

    public String getFieldValue(String fieldLabel) throws Exception
    {
        browser.switchTo().frame(dialogFrame);
        WebElement input = getPageField(fieldLabel).getInput();
        String result;
        switch (getPageField(fieldLabel).getType())
        {
            case "field":
            case "status":
                result = input.getText();
                break;
            case "checkbox":
                result = input.isSelected() ? "checked" : "unchecked";
                break;
            case "text":
                result = input.getAttribute("value");
                break;
            case "options":
                result = input.findElement(By.cssSelector("option[selected='selected']")).getText();
                break;
            default:
                result = input.getText();
                break;
        }
        browser.switchTo().defaultContent();
        return result;
    }

    public void checkField(String fieldLabel) throws Exception
    {
        STEP(String.format("Check %s field ", fieldLabel));
        try
        {
            browser.switchTo().frame(dialogFrame);

            if (!getPageField(fieldLabel).getInput().isSelected())
                getPageField(fieldLabel).getInput().click();
            browser.switchTo().defaultContent();
        } catch (NoSuchElementException noSuchElementExp)
        {
            LOG.error(String.format("Could not find admin console field with label %s", fieldLabel), noSuchElementExp);
        }
    }

    public void unCheckField(String fieldLabel) throws Exception
    {
        STEP(String.format("Uncheck %s field ", fieldLabel));
        try
        {
            browser.switchTo().frame(dialogFrame);

            if (getPageField(fieldLabel).getInput().isSelected())
                getPageField(fieldLabel).getInput().click();
            browser.switchTo().defaultContent();
        } catch (NoSuchElementException noSuchElementExp)
        {
            LOG.error(String.format("Could not find admin console field with label %s", fieldLabel), noSuchElementExp);
        }
    }

    public void typeValueInField(String value, String fieldLabel) throws Exception
    {
        STEP(String.format("Type %s in %s field ", value, fieldLabel));
        try
        {
            browser.switchTo().frame(dialogFrame);
            WebElement input = getPageField(fieldLabel).getInput();
            input.clear();
            input.sendKeys(value);
            browser.switchTo().defaultContent();
        } catch (NoSuchElementException noSuchElementExp)
        {
            LOG.error(String.format("Could not find admin console field with label [%s]", fieldLabel), noSuchElementExp);
        }
    }

    public void selectOptionFromField(String option, String fieldLabel) throws Exception
    {
        STEP(String.format("Select option %s from %s field ", option, fieldLabel));
        browser.switchTo().frame(dialogFrame);
        List<WebElement> optionsList = getPageField(fieldLabel).getInput().findElements(By.cssSelector("option"));
        browser.selectOptionFromFilterOptionsList(option, optionsList);
        browser.switchTo().defaultContent();
    }

}