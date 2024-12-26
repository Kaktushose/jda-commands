package parameters;

import com.github.kaktushose.jda.commands.annotations.constraints.Min;
import com.github.kaktushose.jda.commands.definitions.description.ParameterDescription;
import com.github.kaktushose.jda.commands.definitions.interactions.impl.command.ParameterDefinition;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import com.github.kaktushose.jda.commands.dispatching.validation.impl.MinimumValidator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ParameterDefinitionTest {

    private static Class<?> controller;
    private static ValidatorRegistry validatorRegistry;

    @BeforeAll
    public static void setup() {
        controller = ParameterTestController.class;
        validatorRegistry = new ValidatorRegistry();
    }

    private static ParameterDescription parameter(Parameter parameter) {
        return new ParameterDescription(
                parameter.getType(),
                parameter.getName(),
                toList(parameter.getAnnotations())
        );
    }

    private static <T> Collection<T> toList(T[] array) {
        return Arrays.stream(array).toList();
    }

    @Test
    public void method_withPrimitiveInt_ShouldWrap() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("primitives", int.class);
        ParameterDefinition parameter = ParameterDefinition.build(parameter(method.getParameters()[0]), false, validatorRegistry);

        assertEquals(Integer.class, parameter.type());
        assertTrue(parameter.primitive());
    }

    @Test
    public void optional_withoutDefault_ShouldBeNull() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("optional", Object.class);
        ParameterDefinition parameter = ParameterDefinition.build(parameter(method.getParameters()[0]), false, validatorRegistry);

        assertTrue(parameter.optional());
        assertNull(parameter.defaultValue());
    }

    @Test
    public void optional_withDefault_ShouldWork() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("optionalWithDefault", Object.class);
        ParameterDefinition parameter = ParameterDefinition.build(parameter(method.getParameters()[0]), false, validatorRegistry);

        assertTrue(parameter.optional());
        assertEquals("default", parameter.defaultValue());
    }

    @Test
    public void constraintMin_withLimit10_ShouldWork() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("constraint", int.class);
        ParameterDefinition parameter = ParameterDefinition.build(parameter(method.getParameters()[0]), false, validatorRegistry);

        var constraints = List.copyOf(parameter.constraints());
        assertEquals(1, parameter.constraints().size());
        assertEquals(10, ((Min) constraints.get(0).annotation()).value());
        assertEquals(MinimumValidator.class, constraints.get(0).validator().getClass());
        assertFalse(constraints.get(0).message().isEmpty());
    }

    @Test
    public void constraint_withMessage_ShouldWork() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("constraintWithMessage", int.class);
        ParameterDefinition parameter = ParameterDefinition.build(parameter(method.getParameters()[0]), false, validatorRegistry);
        var constraints = List.copyOf(parameter.constraints());

        assertEquals("error message", constraints.getFirst().message());
    }
}
