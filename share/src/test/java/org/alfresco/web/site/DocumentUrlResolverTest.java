package org.alfresco.web.site;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModelException;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.springframework.extensions.surf.util.I18NUtil;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mockStatic;


public class DocumentUrlResolverTest {

    private DocumentUrlResolver documentUrlResolver;

    @Test
    public void testResolvePlaceHolders_ValuesPresent() throws TemplateModelException {
        try (MockedStatic<I18NUtil> mocked = mockStatic(I18NUtil.class)) {
            mocked.when(() -> I18NUtil.getMessage("key")).thenReturn("value");
            DocumentUrlResolver resolver = new DocumentUrlResolver();
            String result = (String)resolver.exec(List.of(new SimpleScalar("${key}")));
            assertEquals("value", result);
        }
    }

    @Test
    public void testResolvePlaceHolders_ValuesAbsent() throws TemplateModelException{
        DocumentUrlResolver resolver = new DocumentUrlResolver();
        String result = (String)resolver.exec(List.of(new SimpleScalar("${missingKey}")));
        assertEquals("${missingKey}", result);
    }

    @Test
    public void testResolvePlaceHolders_ValuesPresentInSentenceNonIncludingSpecialCharacters() throws TemplateModelException{
        try (MockedStatic<I18NUtil> mocked = mockStatic(I18NUtil.class)) {
            mocked.when(() -> I18NUtil.getMessage("key")).thenReturn("value");
            DocumentUrlResolver resolver = new DocumentUrlResolver();
            String result = (String)resolver.exec(List.of(new SimpleScalar("This is a ${key} in a sentence.")));
            assertEquals("This is a value in a sentence.", result);
        }
    }

    @Test
    public void testResolvePlaceHolders_ValuesPresentInSentenceIncludingUnicodeCharacters() throws TemplateModelException{
        try (MockedStatic<I18NUtil> mocked = mockStatic(I18NUtil.class)) {
            mocked.when(() -> I18NUtil.getMessage("key")).thenReturn("valeur");
            DocumentUrlResolver resolver = new DocumentUrlResolver();
            String result = (String)resolver.exec(List.of(new SimpleScalar("Weitere Administratoren-Tools finden Sie in der Repository Administration Console. Mehr Informationen dazu finden Sie in der <a href=\\u201c${key}\\u201d target=\\u201cnew\\u201d>Alfresco Documentation</a>.")));
            assertEquals("Weitere Administratoren-Tools finden Sie in der Repository Administration Console. Mehr Informationen dazu finden Sie in der <a href=\\u201cvaleur\\u201d target=\\u201cnew\\u201d>Alfresco Documentation</a>.", result);
        }
    }

    @Test
    public void testResolvePlaceHolders_EmptyMessage() throws TemplateModelException {
        DocumentUrlResolver resolver = new DocumentUrlResolver();
        String result = (String)resolver.exec(List.of(new SimpleScalar("")));
        assertEquals("", result);
    }

    @Test
    public void testResolvePlaceHolders_WithUnicodeFormat() throws TemplateModelException {
        try (MockedStatic<I18NUtil> mocked = mockStatic(I18NUtil.class)) {
            mocked.when(() -> I18NUtil.getMessage("key")).thenReturn("value");
            DocumentUrlResolver resolver = new DocumentUrlResolver();
            String result = (String)resolver.exec(List.of(new SimpleScalar("\\u8ffd\\u52a0\\u306e\\u7ba1\\u7406\\u30c4\\u30fc\\u30eb\\u306f\\u30ea\\u30dd\\u30b8\\u30c8\\u30ea\\u7ba1\\u7406\\u30b3\\u30f3\\u30bd\\u30fc\\u30eb\\u306b\\u3042\\u308a\\u307e\\u3059\\u3002\\u8a73\\u7d30\\u306b\\u3064\\u3044\\u3066\\u306f\\u3001<a href=\"${key}\" target=\"new\">Alfresco Documentation</a> \\u3092\\u53c2\\u7167\\u3057\\u3066\\u304f\\u3060\\u3055\\u3044\\u3002: {0}")));
            assertEquals("\\u8ffd\\u52a0\\u306e\\u7ba1\\u7406\\u30c4\\u30fc\\u30eb\\u306f\\u30ea\\u30dd\\u30b8\\u30c8\\u30ea\\u7ba1\\u7406\\u30b3\\u30f3\\u30bd\\u30fc\\u30eb\\u306b\\u3042\\u308a\\u307e\\u3059\\u3002\\u8a73\\u7d30\\u306b\\u3064\\u3044\\u3066\\u306f\\u3001<a href=\"value\" target=\"new\">Alfresco Documentation</a> \\u3092\\u53c2\\u7167\\u3057\\u3066\\u304f\\u3060\\u3055\\u3044\\u3002: {0}", result);
        }
    }

    @Test
    public void testResolvePlaceHolders_WithJavaScriptMethod(){
        try (MockedStatic<I18NUtil> mocked = mockStatic(I18NUtil.class)) {
            mocked.when(() -> I18NUtil.getMessage("key")).thenReturn("value");
            DocumentUrlResolver resolver = new DocumentUrlResolver();
            String result = (String) resolver.get("This is a ${key} in a sentence.");
            assertEquals("This is a value in a sentence.", result);
        }
    }

}
