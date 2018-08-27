package org.alfresco.po.share.TinyMce;

import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Created by Alex Argint
 */
@PageObject
public class TinyMceEditor extends HtmlPage {

    @FindBy(css = "iframe[id$='-configDialog-text_ifr']")
    private WebElement iFrame;

    private String TINY_MCE_SELECT_ALL_COMMAND = "tinyMCE.activeEditor.selection.select(tinyMCE.activeEditor.getBody(),true);";

    // private static final String XPATH_COLOUR_FONT = "//font";

    @FindBy(css = "div[aria-label*='Background'] button span")
    private WebElement CSS_COLOUR_FONT2;

    @FindBy(css = "i.mce-i-removeformat")
    private WebElement CSS_REMOVE_FORMAT;

    @FindBy(css = "rich.txt.editor.color.code")
    private WebElement CSS_COLOR_ATT;

    @FindBy(css = "i[class$='mce-i-bold']")
    private WebElement CSS_STR_BOLD;

    public String FRAME_ID = "";

    public String TINYMCE_CONTENT = "body[id$='tinymce']";

    @FindBy(css = "i[class$='mce-i-italic']")
    private WebElement CSS_STR_ITALIC;

    @FindBy(css = "i[class$='mce-i-underline']")
    private WebElement CSS_STR_UNDER_LINED;

    @FindBy(css = "i[class$='mce-i-bullist']")
    private WebElement CSS_STR_BULLETS;

    @FindBy(css = "i[class$='mce-i-numlist']")
    private WebElement CSS_STR_NUMBERS;

    @FindBy(css = "#tinymce>p>b")
    private WebElement CSS_STR_BOLD_FMT_TXT;

    @FindBy(css = "#tinymce>p>i")
    private WebElement CSS_STR_ITALIC_FMT_TXT;

    @FindBy(css = "#tinymce>p>u")
    private WebElement CSS_STR_UNDER_LINED_FMT_TXT;

    @FindBy(css = "#tinymce>ul>li")
    private WebElement CSS_STR_BULLET_FMT_TXT;

    @FindBy(css = "#tinymce>ol>li")
    private WebElement CSS_STR_NUMBER_FMT_TXT;

    @FindBy(css = "#tinymce>p")
    private WebElement CSS_STR_TEXT_TAG;

    @FindBy(css = "div[aria-label^='Text'] button[class$='mce-open']")
    public WebElement CSS_STR_FORE_COLOUR;

    @FindBy(css = "#tinymce>p>font")
    private WebElement CSS_COLOR_FONT;

    @FindBy(css = "button[id$='mce_43-open']")
    private WebElement CSS_EDIT;

    @FindBy(css = "button[id$='mce_46-open']")
    private WebElement CSS_FORMAT;

    @FindBy(css = "i[class$='mce-i-undo']")
    private WebElement CSS_UNDO;

    //    private static final String CSS_UNDO  = ".//form[contains(@id, 'default-add-form')]//i[contains(@class, 'mce-i-undo')]";

    @FindBy(css = "i[class$='mce-i-redo']")
    private WebElement CSS_REDO;

    @FindBy(css = "#tinymce>ul>li")
    private WebElement CSS_BULLET_TEXT;

    @FindBy(css = "DIV[class='comments-list']>DIV[class='comment-form'] i[class$='mce-i-bold']")
    private WebElement CSS_BOLD_EDIT;

    @FindBy(css = "div[aria-label*='Background'] button[class='mce-open']")
    private WebElement CSS_STR_BACK_GROUND_COLOUR;
    private String frameId = null;
    private FormatType formatType;

    public enum FormatType {
        BOLD,
        ITALIC,
        UNDERLINED,
        NUMBER,
        BULLET,
        BOLD_FMT_TXT,
        ITALIC_FMT_TXT,
        UNDER_LINED_FMT_TXT,
        BULLET_FMT_TXT,
        NUMBER_FMT_TXT,
        COLOR,
        FORMAT,
        EDIT,
        UNDO,
        REDO,
        DEFAULT,
        COLOR_FONT,
        BULLET_TEXT,
        BOLD_EDIT,
        BACK_GROUND_COLOR
    }


    public String getFrameId() {
        return frameId;
    }

    private void setFrameId(String frameId) {

        this.frameId = frameId;
    }

    public void setFormatType(FormatType formatType) {
        this.formatType = formatType;
    }

    public WebElement textElements() {
        switch (formatType) {
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

    public WebElement getCSSOfText(FormatType formatType) {
        switch (formatType) {
            case BOLD_FMT_TXT:
                return CSS_STR_BOLD_FMT_TXT;
            case ITALIC_FMT_TXT:
                return CSS_STR_ITALIC_FMT_TXT;
            case UNDER_LINED_FMT_TXT:
                return CSS_STR_UNDER_LINED_FMT_TXT;
            case BULLET_FMT_TXT:
                return CSS_STR_BULLET_FMT_TXT;
            case NUMBER_FMT_TXT:
                return CSS_STR_NUMBER_FMT_TXT;
            case COLOR_FONT:
                return CSS_COLOR_FONT;
            case BULLET_TEXT:
                return CSS_BULLET_TEXT;
            default:
                return CSS_STR_TEXT_TAG;
        }
    }

    public void setTinyMce(String frameId) {
        setFrameId(frameId);
    }

//    /**
//     * Constructor
//     */
//    public TinyMceEditor() {
//
//        try {
//            this.FRAME_ID = iFrame.getAttribute("id");
//            setFrameId(FRAME_ID);
//        } catch (NoSuchElementException nse) {
//        }
//    }

    /**
     * @param txt
     */
    public void addContent(String txt) {
        try {
            String setCommentJs = String.format("tinyMCE.activeEditor.setContent('%s');", txt);
            browser.executeJavaScript(setCommentJs);
        } catch (NoSuchElementException noSuchElementExp) {
            LOG.error("Element : " + txt + " is not present", noSuchElementExp);
        }
    }

    /**
     * This method sets the given text into Site Content Configure text editor.
     *
     * @param text
     */

    public void setText(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text is required");
        }

        String setCommentJs = String.format("tinyMCE.activeEditor.setContent('%s');", "");

        getBrowser().waitInSeconds(5);
        browser.executeJavaScript(setCommentJs);

        setCommentJs = String.format("tinyMCE.activeEditor.setContent('%s');", text);
        browser.executeJavaScript(setCommentJs);

    }


    /**
     * Click on TinyMCE editor's format option.
     */
    public void clickTextFormatter(FormatType formatType) {
        setFormatType(formatType);
        selectTextFromEditor();
        clickElementOnRichTextFormatter(textElements());
    }

    /**
     * Click to select color code on text.
     */
    public void clickColorCode(TinyMceColourCode colourCode) {
        selectTextFromEditor();
        setFormatType(FormatType.COLOR);
        clickElementOnRichTextFormatter(textElements());
        WebElement colour = browser.findElement(By.cssSelector(colourCode.getForeColourLocator()));
        clickElementOnRichTextFormatter(colour);
    }

    /**
     * Click to select color code on text.
     */
    public void clickBackgroundColorCode(TinyMceColourCode bgColourCode) {
        selectTextFromEditor();
        setFormatType(FormatType.BACK_GROUND_COLOR);
        clickElementOnRichTextFormatter(textElements());
        WebElement bgColour = browser.findElement(By.cssSelector(bgColourCode.getBgColourLocator()));
        clickElementOnRichTextFormatter(bgColour);
    }

    /**
     * b
     * click to undo to default format.
     */
    public void clickUndo() {
        setFormatType(FormatType.UNDO);
        clickElementOnRichTextFormatter(textElements());
    }

    /**
     * click to edit button
     */
    public void clickEdit() {
        setFormatType(FormatType.EDIT);
        clickElementOnRichTextFormatter(textElements());
    }

    /**
     * click to format button
     */
    public void clickFormat() {
        setFormatType(FormatType.FORMAT);
        clickElementOnRichTextFormatter(textElements());
    }

    /**
     * Click to Redo the undo operation.
     */
    public void clickRedo() {
        setFormatType(FormatType.REDO);
        clickElementOnRichTextFormatter(textElements());
    }


    /**
     * Click to remove formatting from text.
     */
    public void removeFormatting() {
        try {
            CSS_REMOVE_FORMAT.click();
        } catch (NoSuchElementException noSuchElementExp) {
            LOG.error("Element :" + CSS_REMOVE_FORMAT + " does not exist", noSuchElementExp);
        }
    }

    public void selectTextFromEditor() {
        // This select all in the edit pane
        /**
         * @author Michael Suzuki Changed to use tinymce directly as its faster to edit with tinymce object instead of using the ui. The script below will
         *         select every thing inside the editing pane.
         */
        browser.executeJavaScript(TINY_MCE_SELECT_ALL_COMMAND);
    }

    /**
     * @param element
     */
    protected void clickElementOnRichTextFormatter(WebElement element) {
        try {
            browser.switchToDefaultContent();
            element.click();

        } catch (NoSuchElementException noSuchElementExp) {
            LOG.error("Element :" + element + " does not exist", noSuchElementExp);
        }
    }

    public String getText() {
        try {
            browser.switchToFrame(getFrameId());
            String text = browser.findElement(By.cssSelector(TINYMCE_CONTENT)).getText();
            browser.switchToDefaultContent();
            return text;
        } catch (NoSuchElementException noSuchElementExp) {
            LOG.error("Element : does not exist", noSuchElementExp);
            return "";
        }
    }


    /**
     * Click on TinyMCE editor's format option.
     *
     * @param formatType
     */
    public void clickTextFormatterWithOutSelectingText(FormatType formatType) {
        setFormatType(formatType);
        clickElementOnRichTextFormatter(textElements());
    }

    /**
     * This method does the removing of text/image/links and format from the tinymce editor.
     */
    public void clearAll() {

        String setCommentJs = String.format("tinyMCE.activeEditor.setContent('%s');", "");
        browser.executeJavaScript(setCommentJs);

    }
}
