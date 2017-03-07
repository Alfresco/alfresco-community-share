package org.alfresco.po.share.user.admin;

import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Created by Claudia Agache on 7/1/2016.
 */
public class ManagedSiteRow
{
    private WebElement siteName;
    private String siteDescription;
    private String siteVisibilityText;
    private WebElement siteVisibilityButton;
    private List<WebElement> siteVisibilityOptions;
    private WebElement successIndicator;
    private String siteManager;
    private WebElement siteActionsButton;
    private List<WebElement> siteActions;

    public WebElement getSiteName()
    {
        return siteName;
    }

    public void setSiteName(WebElement siteName)
    {
        this.siteName = siteName;
    }

    String getSiteDescription()
    {
        return siteDescription;
    }

    void setSiteDescription(String siteDescription)
    {
        this.siteDescription = siteDescription;
    }

    String getSiteVisibilityText()
    {
        return siteVisibilityText;
    }

    void setSiteVisibilityText(String siteVisibilityText)
    {
        this.siteVisibilityText = siteVisibilityText;
    }

    WebElement getSiteVisibilityButton()
    {
        return siteVisibilityButton;
    }

    void setSiteVisibilityButton(WebElement siteVisibilityButton)
    {
        this.siteVisibilityButton = siteVisibilityButton;
    }

    List<WebElement> getSiteVisibilityOptions()
    {
        return siteVisibilityOptions;
    }

    void setSiteVisibilityOptions(List<WebElement> siteVisibilityOptions)
    {
        this.siteVisibilityOptions = siteVisibilityOptions;
    }

    void setUpdateVisibilitySuccessIndicator(WebElement successIndicator)
    {
        this.successIndicator = successIndicator;
    }

    WebElement getUpdateVisibilitySuccessIndicator()
    {
        return successIndicator;
    }

    String getSiteManager()
    {
        return siteManager;
    }

    void setSiteManager(String siteManager)
    {
        this.siteManager = siteManager;
    }

    WebElement getSiteActionsButton()
    {
        return siteActionsButton;
    }

    void setSiteActionsButton(WebElement siteActionsButton)
    {
        this.siteActionsButton = siteActionsButton;
    }

    List<WebElement> getSiteActions()
    {
        return siteActions;
    }

    void setSiteActions(List<WebElement> siteActions)
    {
        this.siteActions = siteActions;
    }
}