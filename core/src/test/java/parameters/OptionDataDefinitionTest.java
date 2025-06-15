package parameters;

import com.github.kaktushose.jda.commands.definitions.description.ParameterDescription;
import com.github.kaktushose.jda.commands.definitions.interactions.command.OptionDataDefinition;
import com.github.kaktushose.jda.commands.dispatching.validation.internal.Validators;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class OptionDataDefinitionTest {

    private static Class<?> controller;
    private static Validators validatorRegistry;

    @BeforeAll
    public static void setup() {
        controller = ParameterTestController.class;
        validatorRegistry = new Validators(Map.of());
    }

    private static ParameterDescription parameter(@NotNull Parameter parameter) {
        Class<?>[] arguments = {};
        if (parameter.getParameterizedType() instanceof ParameterizedType type) {
            arguments = Arrays.stream(type.getActualTypeArguments())
                    .map(it -> it instanceof ParameterizedType pT ? pT.getRawType() : it)
                    .map(Class.class::cast)
                    .toArray(Class[]::new);
        }

        return new ParameterDescription(
                parameter.getType(),
                arguments,
                parameter.getName(),
                toList(parameter.getAnnotations())
        );
    }

    private static <T> Collection<T> toList(T[] array) {
        return Arrays.stream(array).toList();
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

    @Test
    public void constraint_withMessage_ShouldWork() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("constraintWithMessage", Member.class);
        OptionDataDefinition parameter = OptionDataDefinition.build(parameter(method.getParameters()[0]), null, validatorRegistry);
        var constraints = List.copyOf(parameter.constraints());

        assertEquals("error message", constraints.getFirst().message());
    }
}
