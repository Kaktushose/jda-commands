package commands;

import com.github.kaktushose.jda.commands.dispatching.interactions.commands.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.CommandDefinition;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import net.dv8tion.jda.api.interactions.commands.localization.ResourceBundleLocalizationFunction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class CommandDefinitionTest {

    private static final LocalizationFunction LOCALIZATION_FUNCTION = ResourceBundleLocalizationFunction.empty().build();
    
    private static Class<?> controller;
    private static ValidatorRegistry validator;

    @BeforeAll
    public static void setup() {
        controller = CommandDefinitionTestController.class;

        validator = new ValidatorRegistry();

        // make sure that this type is not registered before testing
        TypeAdapterRegistry adapter = new TypeAdapterRegistry();
        adapter.unregister(UnsupportedType.class);
    }

    @Test
    public void method_withoutAnnotation_ShouldReturnEmpty() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("noAnnotation");

        assertEquals(Optional.empty(), CommandDefinition.build(method, validator, LOCALIZATION_FUNCTION));
    }

    @Test
    public void method_withoutArgs_ShouldReturnEmpty() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("noArgs");

        assertEquals(Optional.empty(), CommandDefinition.build(method, validator, LOCALIZATION_FUNCTION));
    }

    @Test
    public void method_withoutCommandEvent_ShouldReturnEmpty() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("noCommandEvent", int.class);

        assertEquals(Optional.empty(), CommandDefinition.build(method, validator, LOCALIZATION_FUNCTION));
    }

    @Test
    public void method_withWrongCommandEvent_ShouldReturnEmpty() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("wrongCommandEvent", int.class, CommandEvent.class);

        assertEquals(Optional.empty(), CommandDefinition.build(method, validator, LOCALIZATION_FUNCTION));
    }

    @Test
    public void method_withCommandEvent_ShouldWork() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Method method = controller.getDeclaredMethod("commandEvent", CommandEvent.class);
        CommandDefinition definition = CommandDefinition.build(method, validator, LOCALIZATION_FUNCTION).orElse(null);

        assertNotNull(definition);

        assertEquals(definition.getMethod(), method);

        assertEquals(1, definition.getParameters().size());
        assertEquals(CommandEvent.class, definition.getParameters().get(0).getType());
    }

    @Test
    public void method_withUnsupportedType_ShouldWork() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Method method = controller.getDeclaredMethod("unsupported", CommandEvent.class, UnsupportedType.class);

        CommandDefinition definition = CommandDefinition.build(method, validator, LOCALIZATION_FUNCTION).orElse(null);

        assertNotNull(definition);

        assertEquals(definition.getMethod(), method);

        assertEquals(2, definition.getParameters().size());
        assertEquals(CommandEvent.class, definition.getParameters().get(0).getType());
        assertEquals(UnsupportedType.class, definition.getParameters().get(1).getType());
    }

    @Test
    public void method_withStringArray_ShouldWork() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Method method = controller.getDeclaredMethod("arrayArgument", CommandEvent.class, String[].class);
        CommandDefinition definition = CommandDefinition.build(method, validator, LOCALIZATION_FUNCTION).orElse(null);

        assertNotNull(definition);

        assertEquals(definition.getMethod(), method);

        assertEquals(2, definition.getParameters().size());
        assertEquals(CommandEvent.class, definition.getParameters().get(0).getType());
        assertEquals(String[].class, definition.getParameters().get(1).getType());
    }

    @Test
    public void method_withArgumentsAfterArray_ShouldReturnEmpty() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("argsAfterArray", CommandEvent.class, String[].class, int.class);

        assertEquals(Optional.empty(), CommandDefinition.build(method, validator, LOCALIZATION_FUNCTION));
    }

    @Test
    public void method_withArgumentsAfterOptional_ShouldWork() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("argsAfterOptional", CommandEvent.class, String.class, int.class);

        CommandDefinition definition = CommandDefinition.build(method, validator, LOCALIZATION_FUNCTION).orElse(null);

        assertNotNull(definition);

        assertEquals(definition.getMethod(), method);

        assertEquals(3, definition.getParameters().size());
        assertEquals(CommandEvent.class, definition.getParameters().get(0).getType());
        assertTrue(definition.getParameters().get(1).isOptional());
        assertFalse(definition.getParameters().get(2).isOptional());
    }

    @Test
    public void method_withOptionalAfterOptional_ShouldWork() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Method method = controller.getDeclaredMethod("optionalAfterOptional", CommandEvent.class, String.class, int.class);
        CommandDefinition definition = CommandDefinition.build(method, validator, LOCALIZATION_FUNCTION).orElse(null);

        assertNotNull(definition);

        assertEquals(definition.getMethod(), method);

        assertEquals(3, definition.getParameters().size());
        assertEquals(CommandEvent.class, definition.getParameters().get(0).getType());
        assertEquals(String.class, definition.getParameters().get(1).getType());
        assertEquals(Integer.class, definition.getParameters().get(2).getType());
    }

    @Test
    public void command_isInactive_ShouldReturnEmpty() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("inactive");

        assertEquals(Optional.empty(), CommandDefinition.build(method, validator, LOCALIZATION_FUNCTION));
    }

    @Test
    public void cooldown_zeroTimeUnits_ShouldNotBeSet() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("zeroCooldown", CommandEvent.class);
        CommandDefinition definition = CommandDefinition.build(method, validator, LOCALIZATION_FUNCTION).orElse(null);

        assertNotNull(definition);

        assertFalse(definition.hasCooldown());
    }

    @Test
    public void cooldown_tenMilliseconds_ShouldWork() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("cooldown", CommandEvent.class);
        CommandDefinition definition = CommandDefinition.build(method, validator, LOCALIZATION_FUNCTION).orElse(null);

        assertNotNull(definition);

        assertTrue(definition.hasCooldown());
        assertEquals(10, definition.getCooldown().getDelay());
        assertEquals(TimeUnit.MILLISECONDS, definition.getCooldown().getTimeUnit());
    }

    @Test
    public void permission_oneString_ShouldWork() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("permission", CommandEvent.class);
        CommandDefinition definition = CommandDefinition.build(method, validator, LOCALIZATION_FUNCTION).orElse(null);

        assertNotNull(definition);

        assertEquals(1, definition.getPermissions().size());
        assertTrue(definition.getPermissions().contains("permission"));
    }

}
