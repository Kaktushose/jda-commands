package parameters;

import com.github.kaktushose.jda.commands.definitions.description.AnnotationDescription;
import com.github.kaktushose.jda.commands.definitions.description.ParameterDescription;
import com.github.kaktushose.jda.commands.definitions.interactions.command.OptionDataDefinition;
import com.github.kaktushose.jda.commands.dispatching.validation.internal.Validators;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OptionDataDefinitionTest {

    private static Class<?> controller;
    private static Validators validatorRegistry;

    @BeforeAll
    public static void setup() {
        controller = ParameterTestController.class;
        validatorRegistry = new Validators(Map.of());
    }

    private static ParameterDescription parameter(Parameter parameter) {
        return new ParameterDescription(
                parameter.getType(),
                parameter.getName(),
                annotationList(parameter.getAnnotations())
        );
    }

    private static List<AnnotationDescription<?>> annotationList(Annotation[] array) {
        return Arrays.stream(array)
                .map(OptionDataDefinitionTest::annotation)
                .collect(Collectors.toUnmodifiableList());
    }

    // only add annotations one level deep
    private static AnnotationDescription<?> annotation(@NotNull Annotation annotation) {
        return new AnnotationDescription<>(annotation, Arrays.stream(annotation.annotationType().getAnnotations())
                .map(ann -> new AnnotationDescription<>(ann, List.of()))
                .collect(Collectors.toUnmodifiableList()));
    }

    @Test
    public void optional_withoutDefault_ShouldBeNull() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("optional", Object.class);
        OptionDataDefinition parameter = OptionDataDefinition.build(parameter(method.getParameters()[0]), null, validatorRegistry);

        assertTrue(parameter.optional());
    }

    @Test
    public void optional_withDefault_ShouldWork() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("optionalWithDefault", Object.class);
        OptionDataDefinition parameter = OptionDataDefinition.build(parameter(method.getParameters()[0]), null, validatorRegistry);

        assertTrue(parameter.optional());
    }
}
