package org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders;

import org.alfresco.common.WebElementInteraction;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CommentsPage extends WebElementInteraction
{
    private By bold_ButtonEnabled = By.xpath("//div[@aria-label='Bold']//button/..");
    private By italic_ButtonEnabled = By.xpath("//div[@aria-label='Italic']//button/..");
    public CommentsPage(ThreadLocal<WebDriver> webDriver) {
        super(webDriver);
}
    public WebElement boldButtonEnabled (){
        return findElement(bold_ButtonEnabled);
    }
    public WebElement italicButtonEnabled (){
        return findElement(italic_ButtonEnabled);
    }

    public boolean isTextEditorButtonEnabled(WebElement element)
    {
        boolean state = false;
        String value = element.getAttribute("aria-pressed");
        if (value.equals("true"))
        {
            state = true;
        } else
        {
            state = false;
        }

        return state;
    }
}
