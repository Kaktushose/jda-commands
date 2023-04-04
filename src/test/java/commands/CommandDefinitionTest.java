package commands;

import com.github.kaktushose.jda.commands.dispatching.commands.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.CommandDefinition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class CommandDefinitionTest {

    private static Class<?> controller;
    private static CommandDefinitionTestController instance;
    private static ValidatorRegistry validator;
    private static TypeAdapterRegistry adapter;

    @BeforeAll
    public static void setup() {
        instance = new CommandDefinitionTestController();
        controller = instance.getClass();

        validator = new ValidatorRegistry();

        // make sure that this type is not registered before testing
        adapter = new TypeAdapterRegistry();
        adapter.unregister(UnsupportedType.class);
    }

    @Test
    public void method_withoutAnnotation_ShouldReturnEmpty() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("noAnnotation");

        assertEquals(Optional.empty(), CommandDefinition.build(method, instance, adapter, validator));
    }

    @Test
    public void method_withoutArgs_ShouldReturnEmpty() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("noArgs");

        assertEquals(Optional.empty(), CommandDefinition.build(method, instance, adapter, validator));
    }

    @Test
    public void method_withoutCommandEvent_ShouldReturnEmpty() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("noCommandEvent", int.class);

        assertEquals(Optional.empty(), CommandDefinition.build(method, instance, adapter, validator));
    }

    @Test
    public void method_withWrongCommandEvent_ShouldReturnEmpty() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("wrongCommandEvent", int.class, CommandEvent.class);

        assertEquals(Optional.empty(), CommandDefinition.build(method, instance, adapter, validator));
    }

    @Test
    public void method_withCommandEvent_ShouldWork() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("commandEvent", CommandEvent.class);
        CommandDefinition definition = CommandDefinition.build(method, instance, adapter, validator).orElse(null);

        assertNotNull(definition);

        assertEquals(definition.getMethod(), method);
        assertEquals(definition.newInstance(), instance);

        assertEquals(1, definition.getParameters().size());
        assertEquals(CommandEvent.class, definition.getParameters().get(0).getType());
    }

    @Test
    public void method_withUnsupportedType_ShouldWork() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("unsupported", CommandEvent.class, UnsupportedType.class);

        CommandDefinition definition = CommandDefinition.build(method, instance, adapter, validator).orElse(null);

        assertNotNull(definition);

        assertEquals(definition.getMethod(), method);
        assertEquals(definition.newInstance(), instance);

        assertEquals(2, definition.getParameters().size());
        assertEquals(CommandEvent.class, definition.getParameters().get(0).getType());
        assertEquals(UnsupportedType.class, definition.getParameters().get(1).getType());
    }

    @Test
    public void method_withStringArray_ShouldWork() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("arrayArgument", CommandEvent.class, String[].class);
        CommandDefinition definition = CommandDefinition.build(method, instance, adapter, validator).orElse(null);

        assertNotNull(definition);

        assertEquals(definition.getMethod(), method);
        assertEquals(definition.newInstance(), instance);

        assertEquals(2, definition.getParameters().size());
        assertEquals(CommandEvent.class, definition.getParameters().get(0).getType());
        assertEquals(String[].class, definition.getParameters().get(1).getType());
    }

    @Test
    public void method_withArgumentsAfterArray_ShouldReturnEmpty() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("argsAfterArray", CommandEvent.class, String[].class, int.class);

        assertEquals(Optional.empty(), CommandDefinition.build(method, instance, adapter, validator));
    }

    @Test
    public void method_withArgumentsAfterOptional_ShouldReturnEmpty() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("argsAfterOptional", CommandEvent.class, String.class, int.class);

        assertEquals(Optional.empty(), CommandDefinition.build(method, instance, adapter, validator));
    }

    @Test
    public void method_withOptionalAfterOptional_ShouldWork() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("optionalAfterOptional", CommandEvent.class, String.class, int.class);
        CommandDefinition definition = CommandDefinition.build(method, instance, adapter, validator).orElse(null);

        assertNotNull(definition);

        assertEquals(definition.getMethod(), method);
        assertEquals(definition.newInstance(), instance);

        assertEquals(3, definition.getParameters().size());
        assertEquals(CommandEvent.class, definition.getParameters().get(0).getType());
        assertEquals(String.class, definition.getParameters().get(1).getType());
        assertEquals(Integer.class, definition.getParameters().get(2).getType());
    }

    @Test
    public void method_withArgumentsAfterConcat_ShouldReturnEmpty() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("argsAfterConcat", CommandEvent.class, String.class, int.class);

        assertEquals(Optional.empty(), CommandDefinition.build(method, instance, adapter, validator));
    }

    @Test
    public void command_isInactive_ShouldReturnEmpty() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("inactive");

        assertEquals(Optional.empty(), CommandDefinition.build(method, instance, adapter, validator));
    }

    @Test
    public void command_isSuperAndNotDM_ShouldBeSuperAndNotDM() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("superAndDM", CommandEvent.class);
        CommandDefinition definition = CommandDefinition.build(method, instance, adapter, validator).orElse(null);

        assertNotNull(definition);

        assertTrue(definition.isSuper());
        assertFalse(definition.isDM());
    }

    @Test
    public void labels_superSubAndAlias_ShouldGenerateAll() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("label", CommandEvent.class);
        CommandDefinition definition = CommandDefinition.build(method, instance, adapter, validator).orElse(null);

        assertNotNull(definition);

        assertEquals(2, definition.getLabel().size());
        assertTrue(definition.getLabel().contains("sub"));
        assertTrue(definition.getLabel().contains("subAlias"));
    }

    @Test
    public void cooldown_zeroTimeUnits_ShouldNotBeSet() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("zeroCooldown", CommandEvent.class);
        CommandDefinition definition = CommandDefinition.build(method, instance, adapter, validator).orElse(null);

        assertNotNull(definition);

        assertFalse(definition.hasCooldown());
    }

    @Test
    public void cooldown_tenMilliseconds_ShouldWork() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("cooldown", CommandEvent.class);
        CommandDefinition definition = CommandDefinition.build(method, instance, adapter, validator).orElse(null);

        assertNotNull(definition);

        assertTrue(definition.hasCooldown());
        assertEquals(10, definition.getCooldown().getDelay());
        assertEquals(TimeUnit.MILLISECONDS, definition.getCooldown().getTimeUnit());
    }

    @Test
    public void permission_oneString_ShouldWork() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("permission", CommandEvent.class);
        CommandDefinition definition = CommandDefinition.build(method, instance, adapter, validator).orElse(null);

        assertNotNull(definition);

        assertEquals(1, definition.getPermissions().size());
        assertTrue(definition.getPermissions().contains("permission"));
    }

}
