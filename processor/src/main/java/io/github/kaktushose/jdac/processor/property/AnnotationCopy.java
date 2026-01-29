package io.github.kaktushose.jdac.processor.property;

import javax.lang.model.element.*;
import java.util.Optional;

public class AnnotationCopy {
    private final AnnotationMirror mirror;
    private final Element element;


    public AnnotationCopy(AnnotationMirror mirror) {
        this.mirror = mirror;
        this.element = mirror.getAnnotationType().asElement();
    }

    public String getValue(String name) {
        return getExplicitValue(name)
                .or(() -> getFallback(name))
                .orElseThrow(() -> new IllegalArgumentException("Field %s not found on %s".formatted(name,
                                                                                                     element.getSimpleName())));
    }

    public String typeName() {
        return element.getSimpleName().toString();
    }

    private Optional<String> getExplicitValue(String name) {
        return mirror.getElementValues()
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().toString().equals(memberName(name)))
                .map(entry -> entry.getValue().getValue().toString())
                .findFirst();
    }

    private Optional<String> getFallback(String name) {
        return element.getEnclosedElements()
                .stream()
                .filter(e -> e.getKind() == ElementKind.METHOD)
                .filter(e -> e.getSimpleName().contentEquals(name))
                .map(e -> ((ExecutableElement) e).getDefaultValue())
                .map(AnnotationValue::toString)
                .findFirst();
    }

    private String memberName(String name) {
        return "%s()".formatted(name);
    }
}
