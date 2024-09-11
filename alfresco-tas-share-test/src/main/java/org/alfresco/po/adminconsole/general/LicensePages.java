package org.alfresco.po.adminconsole.general;

import org.alfresco.po.adminconsole.AdminConsoleObject;
import org.alfresco.po.adminconsole.ControlObject;
import org.alfresco.po.share.SharePage2;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import static org.alfresco.utility.report.log.Step.STEP;

public class LicensePages<T> extends SharePage2<LicensePages<T>> {

    private static By pageControls = By.cssSelector("div[class~=control]");
    private By uploadLicenseButton = By.id("upload-license");
    private By dialogFrame = By.id("admin-dialog");
    private By resultLocator = By.id("ul-result");
    private By closeButtonLocator = By.className("cancel");

    public LicensePages(ThreadLocal<WebDriver> webDriver) {
        super(webDriver);
    }

    @Override
    public String getRelativePath() {
        return "alfresco/s/enterprise/admin/admin-license";
    }

    public LicensePages<T> navigate()
    {
        String baseUrl1 = String.format("%s://%s:%s@%s:%s", defaultProperties.getScheme(),
            defaultProperties.getAdminUser(), defaultProperties.getAdminPassword(),
            defaultProperties.getServer(), defaultProperties.getPort());
        STEP(String.format("Navigate to %s", baseUrl1 + "/" + getRelativePath()));
        getWebDriver().navigate().to(baseUrl1 + "/" + getRelativePath());
        return null;
    }

    public List<ControlObject> getPageFields()
    {
        List<ControlObject> pageControlObjects = new ArrayList<ControlObject>();
        for (WebElement control : findElements(pageControls))
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
            description = isElementDisplayed(control, By.className("description")) ? control.findElement(By.className("description")).getText() : "";

            pageControlObjects.add(new AdminConsoleObject(label, input, description, type));
        }
        return pageControlObjects;
    }

    public void clickUploadLicense()
    {
        findElement(uploadLicenseButton).click();
    }

    public boolean isDialogDisplayed()
    {
        return isElementDisplayed(dialogFrame);
    }

    private static File newFile(String fileName, String contents)
    {
        File file = new File(fileName);
        try
        {
            if (!file.exists())
            {
                if (!contents.isEmpty())
                {
                    OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file),
                        Charset.forName("UTF-8").newEncoder());
                    writer.write(contents);
                    writer.close();
                } else
                {
                    file.createNewFile();
                }
            } else
            {
                // TODO to be written
            }
        } catch (java.io.IOException e)
        {
            e.printStackTrace();
        }
        return file;
    }

    public void uploadContent(String filePath, String contentsOfFile)
    {
        waitUntilNotificationMessageDisappears();

        File fileToUpload = newFile(filePath, contentsOfFile);
        try
        {
            waitInSeconds(3);
            findElement(dialogFrame).sendKeys(fileToUpload.getAbsolutePath());
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        String[] strPaths = filePath.split("\\\\");
        String fileName = strPaths[strPaths.length - 1];
        By selector = By.xpath("//a[contains(., '" + fileName + "')]");
        try
        {
            waitUntilElementIsDisplayedWithRetry(selector);
        }
        catch (TimeoutException exception)
        {
            exception.printStackTrace();
        }
    }

    public String getResult()
    {
        findElement(resultLocator).getText();
        return null;
    }

    public void clickClose()
    {
        STEP("Click Close button");
        switchTo().frame(findElement(dialogFrame));
        waitUntilElementClickable(closeButtonLocator).click();
        switchTo().defaultContent();
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
}
