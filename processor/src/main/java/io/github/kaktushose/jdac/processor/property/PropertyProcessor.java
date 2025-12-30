package io.github.kaktushose.jdac.processor.property;

import com.sun.source.tree.*;
import com.sun.source.util.Trees;
import io.github.kaktushose.jdac.processor.property.api.PropertyProcessed;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Set;

public abstract class PropertyProcessor extends AbstractProcessor {

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
                    if (!element.getSimpleName().contentEquals("Property")) return false;

                    VariableTree tree = (VariableTree) trees.getPath(variable).getLeaf();
                    ExpressionTree initializer = tree.getInitializer();


                    if (!(initializer instanceof NewClassTree nc)) {
                        messager.printError(
                                "Initializer of property %s is not readable! Must be initialized directly via new Enumeration/Singleton/Mapping".formatted(variable.getSimpleName()),
                                variable
                        );

                        return false;
                    }

                    try {
                        Property property = extract(nc, variable);

                        processField(property, roundEnv, messager);
                    } catch (AbortException _) {
                        // already logged
                    }
                }
            }
        }

        return false;
    }

    private Property extract(NewClassTree nc, VariableElement variableElement) {
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


        return new Property(variableElement, nc, variableElement.getSimpleName().toString(), type, category, fallbackBehaviour, stage);
    }

    private String getValue(ExpressionTree tree) {
        return switch (tree) {
            case LiteralTree lt -> lt.getValue().toString();
            case MemberSelectTree mt -> mt.getIdentifier().toString();
            case IdentifierTree it -> it.toString();
            default -> throw new IllegalArgumentException("Unsupportd tree! %s".formatted(tree));
        };
    }

    abstract void processField(Property property, RoundEnvironment roundEnvironment, Messager messager);

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(PropertyProcessed.class.getName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_25;
    }
}
