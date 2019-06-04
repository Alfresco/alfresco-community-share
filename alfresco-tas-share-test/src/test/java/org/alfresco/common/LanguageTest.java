package org.alfresco.common;

import org.alfresco.share.ContextAwareWebTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LanguageTest extends ContextAwareWebTest
{
    @Autowired
    Language language;

    @Test
    public void testSimpleTranslate()
    {
        Assert.assertEquals("Your authentication details have not been recognized or Alfresco may not be available at this time.",
                language.translate("login.authError"));
    }
}
