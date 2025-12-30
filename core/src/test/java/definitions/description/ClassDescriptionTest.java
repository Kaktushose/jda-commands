package definitions.description;


import io.github.kaktushose.jdac.annotations.i18n.Bundle;
import io.github.kaktushose.jdac.annotations.interactions.Interaction;
import io.github.kaktushose.jdac.annotations.interactions.Param;
import io.github.kaktushose.jdac.definitions.description.*;
import definitions.description.environment.BaseClass;
import definitions.description.environment.nested.NestedClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ClassDescriptionTest {

    private static ClassDescription classDescription;

    @BeforeAll
    static void init() {
        classDescription = Descriptor.REFLECTIVE.describe(BaseClass.class);
    }

    @Test
    void testAnnotation() {
        AnnotationDescription<?> description = classDescription.annotations().stream().findFirst().orElseThrow();
        assertEquals(Interaction.class, description.type());

        Interaction interaction = classDescription.annotation(Interaction.class);
        assertEquals("base", interaction.value());
    }

    @Test
    void testPackageDescription() {
        PackageDescription description = classDescription.packageDescription();
        assertEquals("definitions.description.environment", description.name());
        assertEquals(Bundle.class, description.annotation(Bundle.class).annotationType());

        assertTrue(Descriptor.REFLECTIVE.describe(NestedClass.class).packageDescription().annotations().isEmpty());
    }

    @Test
    void testClassDescription() {
        ClassDescription description = classDescription;

        assertEquals(BaseClass.class, description.clazz());
        assertEquals("definitions.description.environment.BaseClass", description.name());
    }

    @Test
    void testMethodDescription() {
        MethodDescription description = method("publicMethod").orElseThrow();
        assertEquals(BaseClass.class, description.declaringClass());
        assertEquals(Void.TYPE, description.returnType());
        assertTrue(description.parameters().isEmpty());

        assertTrue(method("privateMethod").isEmpty());

        description = method("returnType").orElseThrow();
        assertEquals(BaseClass.class, description.declaringClass());
        assertEquals(BaseClass.BaserInnerClass.class, description.returnType());
    }

    @Test
    void testInvoker() throws InvocationTargetException, IllegalAccessException {
        MethodDescription description = method("invoke").orElseThrow();
        BaseClass baseClass = new BaseClass();
        final String input = "Test";

        assertEquals(baseClass.invoke(input), description.invoker().invoke(baseClass, List.of(input)));
    }

    @Test
    void testParameterDescription() {
        List<ParameterDescription> parameters = List.copyOf(method("parameters").orElseThrow().parameters());

        ParameterDescription first = parameters.getFirst();
        assertEquals(String.class, first.type());
        assertEquals("first", first.name());
        assertTrue(first.annotations().isEmpty());

        ParameterDescription second = parameters.get(1);
        assertEquals(int.class, second.type());

        ParameterDescription third = parameters.get(2);
        assertEquals(Param.class, third.annotation(Param.class).annotationType());

        ParameterDescription fourth = parameters.get(3);
        assertEquals(String.class, fourth.typeArguments()[0]);
    }

    private Optional<MethodDescription> method(String name) {
        return classDescription.methods().stream().filter(method -> method.name().equals(name)).findFirst();
    }
}
