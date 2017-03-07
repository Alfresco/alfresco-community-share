package org.alfresco.po.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * Indicates that an annotated class is a "Page Object".
 * Such classes are considered as candidates for auto-detection
 * when using annotation-based configuration and classpath scanning.
 * 
 * @see org.springframework.context.annotation.ClassPathBeanDefinitionScanner
 *      
 * @author Paul.Brodner
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
@Documented
public @interface PageObject
{

    /**
     * The value may indicate a suggestion for a logical page object name,
     * to be turned into a Spring bean in case of an autodetected component.
     * 
     * @return the suggested component name, if any
     */
    String value() default "";

}
