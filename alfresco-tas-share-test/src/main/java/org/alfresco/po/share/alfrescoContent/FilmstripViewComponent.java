package org.alfresco.po.share.alfrescoContent;

import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.utility.model.ContentModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
public class FilmstripViewComponent extends AlfrescoContentPage
{
    private final By filmstripCarousel = By.cssSelector("div[id$='filmstrip-nav-carousel']");
    private final By carouselContentsList = By.cssSelector("div[id$='filmstrip-nav-carousel'] .alf-label");
    private final By nextArrow = By.cssSelector("div[id$='filmstrip-nav-main-next']");
    private final By previousArrow = By.cssSelector("div[id$='filmstrip-nav-main-previous']");

    private final String filmstripSelectedContent  = "//div[@class='alf-filmstrip-main-content']//li[contains(@class, 'carousel-item-selected')]//div[text()='%s']";

    protected FilmstripViewComponent(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public FilmstripViewComponent assertFilmstripDocumentsCarouselIsDisplayed()
    {
        log.info("Assert Filmstrip documents carousel is displayed");
        waitUntilElementIsVisible(filmstripCarousel);
        assertTrue(isElementDisplayed(filmstripCarousel), "Documents carousel is not displayed");
        return this;
    }

    public FilmstripViewComponent assertContentsAreDisplayInFilmstripCarousel(ContentModel... contents)
    {
        List<String> expectedContentLists = Collections.synchronizedList(new ArrayList<>());
        Arrays.stream(contents).map(ContentModel::getName).forEach(expectedContentLists::add);

        log.info("Assert contents {} are displayed in Filmstrip view carousel", Arrays.asList(expectedContentLists));
        List<WebElement> items = waitUntilElementsAreVisible(carouselContentsList);
        List<String> actualContentLists = getTextFromElementList(items);
        assertTrue(actualContentLists.equals(expectedContentLists),
                String.format("Not all contents were found %s", Arrays.asList(expectedContentLists)));

        return this;
    }

    public FilmstripViewComponent assertSelectedContentInFilmstripViewIs(ContentModel contentModel)
    {
        log.info("Assert content {} is selected in filmstrip view", contentModel.getName());
        By selectedContent = By.xpath(String.format(filmstripSelectedContent, contentModel.getName()));
        waitUntilElementIsVisible(selectedContent);
        assertTrue(isElementDisplayed(selectedContent), String.format("Content %s is not selected", contentModel.getName()));

        return this;
    }

    public FilmstripViewComponent clickNextArrow()
    {
        log.info("Click Next arrow");
        clickElement(nextArrow);
        return this;
    }

    public FilmstripViewComponent clickPreviousArrow()
    {
        log.info("Click Previous arrow");
        clickElement(previousArrow);
        return this;
    }
}
