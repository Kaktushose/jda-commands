package io.github.kaktushose.jdac.property;

import dev.goldmensch.propane.property.Property;

import java.lang.annotation.*;


/// This annotation solely serves to provide information on a [JDACProperty]'s scope, source and fallback strategy.
///
/// It can be used by tools or read by the user in the Javadocs.
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
@Documented
@interface PropertyInformation {

    JDACScope scope();

    Property.Source source();

    Property.FallbackStrategy fallbackBehaviour() default Property.FallbackStrategy.COMBINE;

}
