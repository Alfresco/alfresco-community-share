package org.alfresco.web.site.servlet.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class AIMSEnabled implements Condition {
    public static final String AIMS_ENABLED = "aims.enabled";

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return context.getEnvironment()
            .getProperty(AIMS_ENABLED, Boolean.class, false);
    }
}
