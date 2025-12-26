package io.github.kaktushose.jdac.configuration;

import io.github.kaktushose.jdac.introspection.Stage;

import java.lang.annotation.*;


/// This annotation solely serves to provide information on a [Property]'s stage, category and fallback behaviour.
///
/// It can be used by tools or read by the user in the javadocs.
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
@Documented
@interface PropertyInformation {
    Stage stage();
    Property.Category category();
    Property.FallbackBehaviour fallbackBehaviour() default Property.FallbackBehaviour.NONE;

}
