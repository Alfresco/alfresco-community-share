package org.alfresco.po.alfresco.admin;

import org.openqa.selenium.WebElement;

/**
 * <div class="control text">
 * <span class="label">Allowed Users:</span>
 * <span class="value">
 * <span class="description">By default, this field is empty and all users can log in. Enter a comma-separated list of users to allow only those users to log
 * in. If a list is entered that does not contain the current user, the current user will be added automatically.</span>
 * </div>
 */
public interface ControlObject
{
    public String getLabel();

    public WebElement getInput();

    public String getDescription();
   
}
