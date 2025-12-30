package io.github.kaktushose.jdac.processor.property;

import com.palantir.javapoet.*;

import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class ListCreatorProcessor extends PropertyProcessor {

    private static final String PROPERTY_PACKAGE = "io.github.kaktushose.jdac.configuration";

    private final List<String> loadable = new ArrayList<>();
    private final List<String> userSettable = new ArrayList<>();
    private final List<String> provided = new ArrayList<>();

    private boolean alreadyRun = false;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (alreadyRun) return false;

        super.process(annotations, roundEnv);
        alreadyRun = true;

        JavaFile file = createListFile();
        try {
            file.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            processingEnv.getMessager().printError("Error while trying to create file: " + e.getMessage());
        }

        return false;
    }

    @Override
    void processField(Property property, RoundEnvironment roundEnvironment, Messager messager) {
        Collection<String> list = switch (property.category()) {
            case "LOADABLE" -> loadable;
            case "USER_SETTABLE" -> userSettable;
            case "PROVIDED" -> provided;
            default -> throw new IllegalArgumentException("Unknown category: %s".formatted(property.category()));
        };

        list.add(property.name());
    }

    private JavaFile createListFile() {
        TypeSpec typeSpec = TypeSpec.classBuilder("PropertyListAccessor")
                .addMethod(createGetterMethod("Loadable", loadable))
                .addMethod(createGetterMethod("Provided", provided))
                .addMethod(createGetterMethod("Settable", userSettable))
                .build();

        return JavaFile.builder(PROPERTY_PACKAGE, typeSpec)
                .build();
    }

    private MethodSpec createGetterMethod(String name, List<String> fields) {
        ClassName propertyClassName = ClassName.get(PROPERTY_PACKAGE, "Property");
        TypeName propertyTypeName = ParameterizedTypeName.get(propertyClassName, WildcardTypeName.subtypeOf(Object.class));

        MethodSpec.Builder spec = MethodSpec.methodBuilder("get" + name)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ParameterizedTypeName.get(ClassName.get(Collection.class), propertyTypeName))
                .addCode("return $T.of(", List.class);

        for (String field : fields) {
            spec.addCode("$T.$L", propertyClassName, field);
            if (!fields.getLast().equals(field)) spec.addCode(",");
        }

        return spec.addStatement(")")
                .build();

    }
}
