package io.github.kaktushose.jdac.property;

import dev.goldmensch.propane.property.Property;

import java.lang.annotation.*;


/// This annotation solely serves to provide information on a [Property]'s stage, category and fallback behaviour.
///
/// It can be used by tools or read by the user in the javadocs.
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
@Documented
@interface PropertyInformation {

    JDACScope scope();

    Property.Source source();

    Property.FallbackBehaviour fallbackBehaviour() default Property.FallbackBehaviour.ACCUMULATE;

}
