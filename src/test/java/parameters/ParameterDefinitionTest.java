package parameters;

import com.github.kaktushose.jda.commands.annotations.constraints.Min;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import com.github.kaktushose.jda.commands.dispatching.validation.impl.MinimumValidator;
import com.github.kaktushose.jda.commands.reflect.ParameterDefinition;
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

        assertEquals(Integer.class, parameter.type());
        assertTrue(parameter.isPrimitive());
    }

    @Test
    public void optional_withoutDefault_ShouldBeNull() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("optional", Object.class);
        ParameterDefinition parameter = ParameterDefinition.build(method.getParameters()[0], validatorRegistry);

        assertTrue(parameter.isOptional());
        assertNull(parameter.defaultValue());
    }

    @Test
    public void optional_withDefault_ShouldWork() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("optionalWithDefault", Object.class);
        ParameterDefinition parameter = ParameterDefinition.build(method.getParameters()[0], validatorRegistry);

        assertTrue(parameter.isOptional());
        assertEquals("default", parameter.defaultValue());
    }

    @Test
    public void constraintMin_withLimit10_ShouldWork() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("constraint", int.class);
        ParameterDefinition parameter = ParameterDefinition.build(method.getParameters()[0], validatorRegistry);

        assertEquals(1, parameter.constraints().size());
        assertEquals(10, ((Min) parameter.constraints().get(0).annotation()).value());
        assertEquals(MinimumValidator.class, parameter.constraints().get(0).validator().getClass());
        assertFalse(parameter.constraints().get(0).message().isEmpty());
    }

    @Test
    public void constraint_withMessage_ShouldWork() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("constraintWithMessage", int.class);
        ParameterDefinition parameter = ParameterDefinition.build(method.getParameters()[0], validatorRegistry);

        assertEquals("error message", parameter.constraints().get(0).message());
    }
}
