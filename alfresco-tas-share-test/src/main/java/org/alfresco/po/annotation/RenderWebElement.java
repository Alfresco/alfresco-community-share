package org.alfresco.po.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.alfresco.po.renderer.ElementState;

/**
 * The Locator By annotated with RenderWebElement can be rendered while calling the render on Page.
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface RenderWebElement
{
    /**
     * Render the element based on the {@link ElementState}
     * The default value of {@link ElementState} is Visible,
     * if it have to render for other other {@link ElementState} user have to set attribute.
     * 
     * @return {@link ElementState} element state
     */
    ElementState state() default ElementState.VISIBLE;

    String text() default "";
}