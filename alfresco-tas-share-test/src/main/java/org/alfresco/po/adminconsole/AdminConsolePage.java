package org.alfresco.po.adminconsole;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.utility.web.HtmlPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * As for SharePage, we have one for AdminConsole page
 */
public abstract class AdminConsolePage<T> extends HtmlPage implements AdminConsole
{
    protected abstract String relativePathToURL();

    @SuppressWarnings("unchecked")
    public T navigate()
    {
        browser.navigate().to(properties.getFullServerUrl() + "/" + relativePathToURL());
        navigator.setBrowser(browser);
        navigator.renderedPage();
        return (T) renderedPage();
    }

    @Autowired
    protected AdminNavigator navigator;

    @FindBy(tagName = "h1")
    WebElement header;

    /*
     * save all web elements that contains "control" keyword - these are custom web elements for admin Pages
     */
    @FindAll(@FindBy(css = "div[class~=control]"))
    @CacheLookup
    List<WebElement> pageControls;

    /*
     * this will save in memory the ControlObject identified
     */
    protected List<ControlObject> pageControlObjects = new ArrayList<ControlObject>();

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
    public List<ControlObject> getPageFields()
    {
        for (WebElement control : pageControls)
        {
            String label = control.findElement(By.className("label")).getText();
            WebElement input = null;

            String description;
            try
            {
                input = control.findElement(By.xpath("span[@class='value']/input"));
                description = control.findElement(By.className("description")).getText();
            }
            catch (Exception e)
            {
                description = "";
            }
            pageControlObjects.add(new AdminConsoleObject(label, input, description));
        }
        return pageControlObjects;
    }

    public void printPageFields()
    {
        for (ControlObject field : getPageFields())
        {
            System.out.println(field.toString());
        }
    }

}
