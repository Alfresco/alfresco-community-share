package org.alfresco.share;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

@Slf4j
public class RetryListener implements IAnnotationTransformer
{
    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor,
        Method testMethod)
    {
      annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }
}
