package io.github.kaktushose.jdac.processor.property;

import java.util.function.Function;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.VariableElement;

public class ValidationProcessor extends PropertyProcessor {

    @Override
    public void processField(Property field, RoundEnvironment roundEnv, Messager messager) {
        Property annotation = getInfo(field.element());


        if (!equalsContent(field, annotation)) {
            String text = "@PropertyInformation not matching property declaration!";
            text += addMismatch(field, annotation, "Category", Property::source);
            text += addMismatch(field, annotation, "Stage", Property::scope);
            text += addMismatch(field, annotation, "fallback", Property::fallback);

            messager.printError(text, field.element());
        }

        if (!field.scope().equals("CONFIGURATION") && !field.source().equals("PROVIDED")) {
            messager.printError("Properties with scope CONFIGURATION must have source set to PROVIDED", field.element());
        }
    }

    private boolean equalsContent(Property one, Property other) {
        return one.source().equals(other.source())
                && one.scope().equals(other.scope())
                && (one.fallback().equals(other.fallback()) || one.type().equals("JDACSingletonProperty"));
    }

    private String addMismatch(Property field, Property annotation, String name, Function<Property, String> getter) {
        String fieldVal = getter.apply(field);
        String annotationVal = getter.apply(annotation);

        if (!fieldVal.equals(annotationVal)) {
            return "\n  %s: %s (field) vs. %s (annotation)".formatted(name, fieldVal, annotationVal);
        }

        return "";
    }

    private Property getInfo(VariableElement element) {
        AnnotationCopy annotation = getPropertyInfoAnn(element);
        return new Property(
                null,
                null,
                null,
                null,
                annotation.getValue("source"),
                annotation.getValue("fallbackBehaviour"),
                annotation.getValue("scope")
        );
    }

    private AnnotationCopy getPropertyInfoAnn(VariableElement variable) {
        return variable.getAnnotationMirrors()
                .stream()
                .map(AnnotationCopy::new)
                .filter(ann -> ann.typeName().equals("PropertyInformation"))
                .findFirst()
                .orElseGet(() -> {
                    processingEnv.getMessager().printError("@PropertyInformation missing on property field!", variable);
                    throw new AbortException();
                });
    }
}
