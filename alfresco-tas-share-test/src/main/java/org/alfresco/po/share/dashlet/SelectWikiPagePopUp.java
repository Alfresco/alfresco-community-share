package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

@Slf4j
public class SelectWikiPagePopUp extends DashletPopUp<SelectWikiPagePopUp>
{
    private final By selectWikiPageText = By.cssSelector("div[class$='yui-u']");
    private final By selectAPageDropDown = By.cssSelector("select[name='wikipage']");

    protected SelectWikiPagePopUp(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public SelectWikiPagePopUp assertDialogBodyMessageEquals(String expectedDialogBodyMessage)
    {
        log.info("Assert dialog body message equals: {}", expectedDialogBodyMessage);
        assertEquals(getElementText(selectWikiPageText), expectedDialogBodyMessage,
            String.format("Dialog body message not equals %s ", expectedDialogBodyMessage));

        return this;
    }

    public SelectWikiPagePopUp assertDropdownOptionEquals(String expectedDropdownOption)
    {
        log.info("Assert dropdown option equals: {}", expectedDropdownOption);
        List<String> wikiNames = Collections.synchronizedList(new ArrayList<>());
        Select wikiSelect = new Select(waitUntilElementIsVisible(selectAPageDropDown));
        for(WebElement wiki : wikiSelect.getOptions())
        {
            wikiNames.add(wiki.getText());
        }
        assertTrue(wikiNames.contains(expectedDropdownOption),
            String.format("Drop down option doesn't contain %s ", expectedDropdownOption));

        return this;
    }

    public SelectWikiPagePopUp clickDialogDropdown()
    {
        log.info("Click dialog dropdown");
        clickElement(selectAPageDropDown);
        return this;
    }
}
