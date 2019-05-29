package org.alfresco.po.share.site;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

@PageObject
public class SelectDocumentPopupPage extends SelectPopUpPage
{
    @FindBy (css = "[id*=default-content-docPicker-cntrl-picker-head]")
    private WebElement header;

    @RenderWebElement
    @FindBy (css = ".container-close")
    private WebElement containerClose;

    @FindBy (css = "cntrl-picker-folderUp-button")
    private WebElement folderUpButton;

    @FindBy (css = "[id$=cntrl-picker-navigator-button]")
    private WebElement documentLibraryButton;

    @FindAll (@FindBy (css = "[id$=cntrl-picker-selectedItems] [class*=dt-data] tr"))
    private List<WebElement> selectedDocumentsList;

    public List<String> getSelectedDocumentTitlesList()
    {
        List<String> selectedDocssTitleList = new ArrayList<>();
        for (WebElement docTitle : selectedDocumentsList)
        {
            selectedDocssTitleList.add(docTitle.getText().trim());
        }
        return selectedDocssTitleList;
    }

}
