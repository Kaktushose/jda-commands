package parameters;

import com.github.kaktushose.jda.commands.annotations.constraints.Min;
import com.github.kaktushose.jda.commands.reflect.ParameterDefinition;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import com.github.kaktushose.jda.commands.dispatching.validation.impl.MinimumValidator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class ParameterDefinitionTest {

    private static Class<?> controller;
    private static ValidatorRegistry validatorRegistry;

    @BeforeAll
    public static void setup() {
        controller = ParameterTestController.class;
        validatorRegistry = new ValidatorRegistry();
    }

    @Test
    public void method_withVarArgs_ShouldThrow() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("varArgs", Object[].class);

        assertThrows(IllegalArgumentException.class, () -> ParameterDefinition.build(method.getParameters()[0], validatorRegistry));
    }

    @Test
    public void method_withPrimitiveInt_ShouldWrap() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("primitives", int.class);
        ParameterDefinition parameter = ParameterDefinition.build(method.getParameters()[0], validatorRegistry);

        assertEquals(Integer.class, parameter.getType());
        assertTrue(parameter.isPrimitive());
    }

    @Test
    public void optional_withoutDefault_ShouldBeNull() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("optional", Object.class);
        ParameterDefinition parameter = ParameterDefinition.build(method.getParameters()[0], validatorRegistry);

        assertTrue(parameter.isOptional());
        assertNull(parameter.getDefaultValue());
    }

    @Test
    public void optional_withDefault_ShouldWork() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("optionalWithDefault", Object.class);
        ParameterDefinition parameter = ParameterDefinition.build(method.getParameters()[0], validatorRegistry);

        assertTrue(parameter.isOptional());
        assertEquals("default", parameter.getDefaultValue());
    }

    @Test
    public void concat_withString_ShouldWork() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("concat", String.class);
        ParameterDefinition parameter = ParameterDefinition.build(method.getParameters()[0], validatorRegistry);

        assertTrue(parameter.isConcat());
    }

    @Test
    public void concat_withNonString_ShouldThrow() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("concat", Object.class);

        assertThrows(IllegalArgumentException.class, () -> ParameterDefinition.build(method.getParameters()[0], validatorRegistry));
    }

    @Test
    public void constraintMin_withLimit10_ShouldWork() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("constraint", int.class);
        ParameterDefinition parameter = ParameterDefinition.build(method.getParameters()[0], validatorRegistry);

        assertEquals(1, parameter.getConstraints().size());
        assertEquals(10, ((Min) parameter.getConstraints().get(0).getAnnotation()).value());
        assertEquals(MinimumValidator.class, parameter.getConstraints().get(0).getValidator().getClass());
        assertEquals("Parameter validation failed", parameter.getConstraints().get(0).getMessage());
    }

    @Test
    public void constraint_withMessage_ShouldWork() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("constraintWithMessage", int.class);
        ParameterDefinition parameter = ParameterDefinition.build(method.getParameters()[0], validatorRegistry);

        assertEquals("error message", parameter.getConstraints().get(0).getMessage());
    }
}
