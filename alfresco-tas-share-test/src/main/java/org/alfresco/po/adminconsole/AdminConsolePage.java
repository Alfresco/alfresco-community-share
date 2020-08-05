package org.alfresco.po.adminconsole;

import static org.alfresco.utility.report.log.Step.STEP;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.utility.web.HtmlPage;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

/**
 * As for SharePage, we have one for AdminConsole page
 */
public abstract class AdminConsolePage<T> extends HtmlPage implements AdminConsole
{
    @Autowired
    private AdminNavigator navigator;

    @FindBy (tagName = "h1")
    private WebElement header;

    @FindBy (className = "message")
    WebElement message;

    @FindAll (@FindBy (css = "div[class~=control]"))
    List<WebElement> pageControls;

    @FindBy (css = ".submission.buttons>input[type='submit']")
    WebElement saveButton;

    @FindBy (css = ".submission.buttons>input.cancel")
    WebElement cancelButton;

    protected abstract String relativePathToURL();

    @SuppressWarnings ("unchecked")
    public T navigate()
    {
        String baseUrl = String.format("%s://%s:%s@%s:%s", properties.getScheme(),
            properties.getAdminUser(),
            properties.getAdminPassword(),
            properties.getServer(), properties.getPort());

        STEP(String.format("Navigate to %s", baseUrl + "/" + relativePathToURL()));

        browser.navigate().to(baseUrl + "/" + relativePathToURL());
        navigator.setBrowser(browser);
        navigator.renderedPage();
        return (T) renderedPage();
    }

    @Override
    public AdminNavigator getNavigator()
    {
        return navigator;
    }

    @Override
    public String getPageHeader()
    {
        return header.getText();
    }

    @Override
    public String getMessage()
    {
        return message.getText().trim();
    }

    @Override
    public List<ControlObject> getPageFields()
    {
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
        throw new Exception(String.format("Could not find admin console field with label %s", fieldLabel));
    }

    public String getFieldValue(String fieldLabel) throws Exception
    {
        WebElement input = getPageField(fieldLabel).getInput();
        switch (getPageField(fieldLabel).getType())
        {
            case "field":
            case "status":
                return input.getText();
            case "checkbox":
                return input.isSelected() ? "checked" : "unchecked";
            case "text":
                return input.getAttribute("value");
            case "options":
                return input.findElement(By.cssSelector("option[selected='selected']")).getText();
            default:
                return input.getText();
        }
    }

    public void checkField(String fieldLabel) throws Exception
    {
        try
        {
            getPageField(fieldLabel).getInput().click();
        } catch (NoSuchElementException noSuchElementExp)
        {
            LOG.error(String.format("Could not find admin console field with label %s", fieldLabel), noSuchElementExp);
        }
    }

    public void typeValueInField(String value, String fieldLabel) throws Exception
    {
        try
        {
            WebElement input = getPageField(fieldLabel).getInput();
            input.clear();
            input.sendKeys(value);
        } catch (NoSuchElementException noSuchElementExp)
        {
            LOG.error(String.format("Could not find admin console field with label [%s]", fieldLabel), noSuchElementExp);
        }
    }

    public void selectOptionFromField(String option, String fieldLabel) throws Exception
    {
        List<WebElement> optionsList = getPageField(fieldLabel).getInput().findElements(By.cssSelector("option"));
        browser.selectOptionFromFilterOptionsList(option, optionsList);
    }

    public void printPageFields()
    {
        for (ControlObject field : getPageFields())
        {
            System.out.println(field.toString());
        }
    }

    public T clickSaveButton()
    {
        browser.waitUntilElementClickable(saveButton).click();
        return (T) renderedPage();
    }

    public T clickCancelButton()
    {
        browser.waitUntilElementClickable(cancelButton).click();
        return (T) renderedPage();
    }

    public T assertPageTitleIs(String expectedPageTitle)
    {
        Assert.assertTrue(getPageTitle().contains(expectedPageTitle), "Page title is correct");
        return (T) renderedPage();
    }
}
