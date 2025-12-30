package io.github.kaktushose.jdac.processor.property;

import com.sun.source.tree.*;
import com.sun.source.util.Trees;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@SupportedSourceVersion(SourceVersion.RELEASE_25)
public class ValidationProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Trees trees = Trees.instance(processingEnv);
        Messager messager = processingEnv.getMessager();

        for (TypeElement validated : annotations) {
            Set<? extends Element> annotatedElements
                    = roundEnv.getElementsAnnotatedWith(validated);
            for (Element annotatedElement : annotatedElements) {
                if (!(annotatedElement instanceof TypeElement typeElement)) continue;
                for (Element child : typeElement.getEnclosedElements()) {
                    if (!(child instanceof VariableElement variable && variable.getKind() == ElementKind.FIELD)) {
                        continue;
                    }

                    TypeMirror type = variable.asType();
                    Element element = processingEnv.getTypeUtils().asElement(type);
                    if (!element.getSimpleName().contentEquals("Property")) return true;
                    VariableTree tree = (VariableTree) trees.getPath(child).getLeaf();
                    ExpressionTree initializer = tree.getInitializer();

                    if (!(initializer instanceof NewClassTree nc)) {
                        messager.printError(
                                "Initializer of property %s is not readable! Must be initialized directly via new Enumeration/Singleton/Mapping".formatted(variable.getSimpleName()),
                                child
                        );
                        return true;
                    }

                    try {
                        Property field = extract(nc);
                        Property annotation = getInfo(variable);

                        if (!field.equals(annotation)) {
                            String text = "@PropertyInformation not matching property declaration!";
                            text += addMismatch(field, annotation, "Category", Property::category);
                            text += addMismatch(field, annotation, "Stage", Property::stage);
                            text += addMismatch(field, annotation, "fallbackBehaviour", Property::fallbackBehaviour);

                            messager.printError(text, variable);
                        }

                    } catch (AbortException _) {
                        // already logged
                    }
                }
            }
        }
        return true;
    }

    private String addMismatch(Property field, Property annotation, String name, Function<Property, String> getter) {
        String fieldVal = getter.apply(field);
        String annotationVal = getter.apply(annotation);

        if (!fieldVal.equals(annotationVal)) {
            return "\n  %s: %s (field) vs. %s (annotation)".formatted(name, fieldVal,  annotationVal);
        }

        return "";
    }

    private Property getInfo(VariableElement element) {
        AnnotationCopy annotation = getPropertyInfoAnn(element);
        return new Property(
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

    private Property extract(NewClassTree nc) {
        List<? extends ExpressionTree> arguments = nc.getArguments();

        String type = ((ParameterizedTypeTree) nc.getIdentifier()).getType().toString();

        String category = getValue(arguments.get(1));
        String stage = switch (type) {
            case "Enumeration" -> getValue(arguments.get(4));
            case "Singleton" -> getValue(arguments.get(3));
            case "Map" -> getValue(arguments.get(5));
            default -> throw new IllegalStateException("Unknown property type: %s".formatted(nc.getIdentifier().toString()));
        };

        String fallbackBehaviour = switch (type) {
            case "Enumeration" -> getValue(arguments.get(3));
            case "Singleton" -> "NONE";
            case "Map" -> getValue(arguments.get(4));
            default -> throw new IllegalStateException("Unknown property type: %s".formatted(nc.getIdentifier().toString()));
        };


        return new Property(category, fallbackBehaviour, stage);
    }

    private String getValue(ExpressionTree tree) {
        return switch (tree) {
            case LiteralTree lt -> lt.getValue().toString();
            case MemberSelectTree mt -> mt.getIdentifier().toString();
            case IdentifierTree it -> it.toString();
            default -> throw new IllegalArgumentException("Unsupportd tree! %s".formatted(tree));
        };
    }

    static class AbortException extends RuntimeException {}
    record Property(String category, String fallbackBehaviour, String stage) {}

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(Validated.class.getName());
    }

}
