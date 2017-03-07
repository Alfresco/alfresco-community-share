package org.alfresco.po.renderer;

import org.alfresco.browser.WebBrowser;
import org.alfresco.common.EnvProperties;
import org.alfresco.po.annotation.RenderWebElement;
import org.openqa.selenium.support.FindBy;

/**
 * @author Paul.Brodner
 *
 */
public interface Renderer
{
    public void render(RenderWebElement renderAnnotation, FindBy findByAnnotation, WebBrowser browser, EnvProperties properties);
}
