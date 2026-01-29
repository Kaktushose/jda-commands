package io.github.kaktushose.jdac.processor.property;

import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.VariableElement;
import java.util.function.Function;

public class ValidationProcessor extends PropertyProcessor {

    @Override
    public void processField(Property field, RoundEnvironment roundEnv, Messager messager) {
        Property annotation = getInfo(field.element());

        if (!equalsContent(field, annotation)) {
            String text = "@PropertyInformation not matching property declaration!";
            text += addMismatch(field, annotation, "Category", Property::category);
            text += addMismatch(field, annotation, "Stage", Property::stage);
            text += addMismatch(field, annotation, "fallbackBehaviour", Property::fallbackBehaviour);

            messager.printError(text, field.element());
        }

        if (!field.stage().equals("CONFIGURATION") && !field.category().equals("PROVIDED")) {
            messager.printError("Properties with stage CONFIGURATION must have category set to PROVIDED",
                                field.element());
        }
    }

    private boolean equalsContent(Property one, Property other) {
        return one.category().equals(other.category())
               && one.stage().equals(other.stage())
               && one.fallbackBehaviour().equals(other.fallbackBehaviour());
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
                annotation.getValue("category"),
                annotation.getValue("fallbackBehaviour"),
                annotation.getValue("stage")
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
