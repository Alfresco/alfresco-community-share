package org.alfresco.po.share;

import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.browser.WebBrowser;

public abstract class CommonLoginPage extends BasePage
{
    public CommonLoginPage(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    public abstract CommonLoginPage assertLoginPageIsOpened();

    public abstract void login(String username, String password);

    public abstract CommonLoginPage navigate();

    public abstract CommonLoginPage assertAuthenticationErrorIsDisplayed();

    public abstract CommonLoginPage assertAuthenticationErrorMessageIsCorrect();

    public abstract void autoCompleteUsername(String username);

    public abstract void typePassword(String password);

    public abstract void clickLogin();

    public abstract boolean isAuthenticationErrorDisplayed();

    public abstract void login(UserModel specialUser);
    
    public abstract CommonLoginPage assertLoginPageTitleIsCorrect();

}
