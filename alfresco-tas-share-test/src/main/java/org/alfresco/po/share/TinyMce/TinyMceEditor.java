package org.alfresco.po.share.TinyMce;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.enums.FormatType;
import org.alfresco.po.share.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class TinyMceEditor extends BasePage
{
    private final By CSS_STR_FORE_COLOUR = By.cssSelector("div[aria-label^='Text'] button[class$='mce-open']");
    private final By iFrame = By.cssSelector("div[class^='mce-tinymce'] iframe");
    private final By CSS_STR_BOLD = By.cssSelector("i[class$='mce-i-bold']");
    private final By CSS_STR_ITALIC = By.cssSelector("i[class$='mce-i-italic']");
    private final By CSS_STR_UNDER_LINED = By.cssSelector("i[class$='mce-i-underline']");
    private final By CSS_STR_BULLETS = By.cssSelector("i[class$='mce-i-bullist']");
    private final By CSS_STR_NUMBERS = By.cssSelector("i[class$='mce-i-numlist']");
    private final By CSS_EDIT = By.cssSelector("button[id$='mce_43-open']");
    private final By CSS_FORMAT = By.cssSelector("button[id$='mce_46-open']");
    private final By CSS_UNDO = By.cssSelector("i[class$='mce-i-undo']");
    private final By CSS_REDO = By.cssSelector("i[class$='mce-i-redo']");
    private final By CSS_BOLD_EDIT = By.cssSelector("DIV[class='comments-list']>DIV[class='comment-form'] i[class$='mce-i-bold']");
    private final By CSS_STR_BACK_GROUND_COLOUR = By.cssSelector("div[aria-label*='Background'] button[class='mce-open']");

    private String TINYMCE_CONTENT = "body[id$='tinymce']";
    private String TINY_MCE_SELECT_ALL_COMMAND = "tinyMCE.activeEditor.selection.select(tinyMCE.activeEditor.getBody(),true);";
    private String frameId = null;
    private FormatType formatType;
    private final By boldText = By.xpath("//*[@id=\"tinymce\"]/p/strong");
    private final By italicText = By.xpath("//*[@id=\"tinymce\"]/p/em");

    public TinyMceEditor(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public String getFrameId()
    {
        return frameId;
    }

    private void setFrameId(String frameId)
    {

        this.frameId = frameId;
    }

    public void setFormatType(FormatType formatType)
    {
        this.formatType = formatType;
    }

    public By textElements()
    {
        switch (formatType)
        {
            case BOLD:
                return CSS_STR_BOLD;
            case ITALIC:
                return CSS_STR_ITALIC;
            case UNDERLINED:
                return CSS_STR_UNDER_LINED;
            case BULLET:
                return CSS_STR_BULLETS;
            case NUMBER:
                return CSS_STR_NUMBERS;
            case COLOR:
                return CSS_STR_FORE_COLOUR;
            case FORMAT:
                return CSS_FORMAT;
            case EDIT:
                return CSS_EDIT;
            case UNDO:
                return CSS_UNDO;
            case REDO:
                return CSS_REDO;
            // temporary solution
            case BOLD_EDIT:
                return CSS_BOLD_EDIT;
            case BACK_GROUND_COLOR:
                return CSS_STR_BACK_GROUND_COLOUR;
            default:
                //should changeeeeee
                return null;

        }
    }

    public void setTinyMce(String frameId)
    {
        setFrameId(frameId);
    }

    public TinyMceEditor addContent(String txt)
    {
        try
        {
            String setCommentJs = String.format("tinyMCE.activeEditor.setContent('%s');", txt);
            executeJavaScript(setCommentJs);
        } catch (NoSuchElementException noSuchElementExp)
        {
            log.error("Element : " + txt + " is not present", noSuchElementExp);
        }
        return this;
    }

    public void clickTextFormatter(FormatType formatType)
    {
        setFormatType(formatType);
        selectTextFromEditor();
        clickElementOnRichTextFormatter(findElement(textElements()));
    }

    /**
     * @author Michael Suzuki Changed to use tinymce directly as its faster to edit with tinymce object instead of using the ui.
     * The script below will select every thing inside the editing pane.
     */
    public void selectTextFromEditor()
    {
        executeJavaScript(TINY_MCE_SELECT_ALL_COMMAND);
    }

    protected void clickElementOnRichTextFormatter(WebElement element)
    {
        try
        {
            switchToDefaultContent();
            clickElement(element);

        } catch (NoSuchElementException noSuchElementExp)
        {
            log.error("Element :" + element + " does not exist", noSuchElementExp);
        }
    }

    public String getText()
    {
        try
        {
            switchToFrame(getFrameId());
            String text = findElement(By.cssSelector(TINYMCE_CONTENT)).getText();
            switchToDefaultContent();
            return text;
        }
        catch (NoSuchElementException noSuchElementExp)
        {
            log.error("Element : does not exist", noSuchElementExp);
            return "";
        }
    }

    public void setText(String text)
    {
        clickJS(findElement(iFrame));

        if (text == null)
        {
            throw new IllegalArgumentException("Text is required");
        }

        String setCommentJs = String.format("tinyMCE.activeEditor.setContent('%s');", "");
        executeJavaScript(setCommentJs);
        setCommentJs = String.format("tinyMCE.activeEditor.setContent('%s');", text);
        executeJavaScript(setCommentJs);
    }
    public boolean verifyBoldText ()
    {
        String boldFontWeightValue = "700";
        switchToFrame("template_x002e_comments_x002e_document-details_x0023_default-add-content_ifr");
        String fontWeight = findElement(boldText).getCssValue("font-weight");
        boolean isBold = boldFontWeightValue.equals(fontWeight);
        return isBold;
    }
    public boolean verifyItalicText ()
    {
        String boldFontStyle = "italic";
        switchToFrame("template_x002e_comments_x002e_document-details_x0023_default-add-content_ifr");
        String fontStyle = findElement(italicText).getCssValue("font-style");
        boolean italicText = boldFontStyle.equals(fontStyle);
        return italicText;
    }
}
