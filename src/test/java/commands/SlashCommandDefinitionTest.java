package commands;

import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.refactor.events.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import com.github.kaktushose.jda.commands.reflect.CooldownDefinition;
import com.github.kaktushose.jda.commands.reflect.MethodBuildContext;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.SlashCommandDefinition;
import net.dv8tion.jda.api.interactions.commands.localization.ResourceBundleLocalizationFunction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class SlashCommandDefinitionTest {

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

    public static MethodBuildContext getBuildContext(Method method) {
        return new MethodBuildContext(
                validator,
                ResourceBundleLocalizationFunction.empty().build(),
                controller.getAnnotation(Interaction.class),
                Set.of(),
                CooldownDefinition.build(null),
                method,
                Set.of()
        );
    }

    @Test
    public void method_withoutAnnotation_ShouldReturnEmpty() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("noAnnotation");

        assertEquals(Optional.empty(), SlashCommandDefinition.build(getBuildContext(method)));
    }

    @Test
    public void method_withoutArgs_ShouldReturnEmpty() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("noArgs");

        assertEquals(Optional.empty(), SlashCommandDefinition.build(getBuildContext(method)));
    }

    @Test
    public void method_withoutCommandEvent_ShouldReturnEmpty() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("noCommandEvent", int.class);

        assertEquals(Optional.empty(), SlashCommandDefinition.build(getBuildContext(method)));
    }

    @Test
    public void method_withWrongCommandEvent_ShouldReturnEmpty() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("wrongCommandEvent", int.class, CommandEvent.class);

        assertEquals(Optional.empty(), SlashCommandDefinition.build(getBuildContext(method)));
    }

    @Test
    public void method_withCommandEvent_ShouldWork() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Method method = controller.getDeclaredMethod("commandEvent", CommandEvent.class);
        SlashCommandDefinition definition = SlashCommandDefinition.build(getBuildContext(method)).orElse(null);

        assertNotNull(definition);

        assertEquals(definition.getMethod(), method);

        assertEquals(1, definition.getParameters().size());
        assertEquals(CommandEvent.class, definition.getParameters().get(0).type());
    }

    @Test
    public void method_withUnsupportedType_ShouldWork() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Method method = controller.getDeclaredMethod("unsupported", CommandEvent.class, UnsupportedType.class);

        SlashCommandDefinition definition = SlashCommandDefinition.build(getBuildContext(method)).orElse(null);

        assertNotNull(definition);

        assertEquals(definition.getMethod(), method);

        assertEquals(2, definition.getParameters().size());
        assertEquals(CommandEvent.class, definition.getParameters().get(0).type());
        assertEquals(UnsupportedType.class, definition.getParameters().get(1).type());
    }

    @Test
    public void method_withStringArray_ShouldWork() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Method method = controller.getDeclaredMethod("arrayArgument", CommandEvent.class, String[].class);
        SlashCommandDefinition definition = SlashCommandDefinition.build(getBuildContext(method)).orElse(null);

        assertNotNull(definition);

        assertEquals(definition.getMethod(), method);

        assertEquals(2, definition.getParameters().size());
        assertEquals(CommandEvent.class, definition.getParameters().get(0).type());
        assertEquals(String[].class, definition.getParameters().get(1).type());
    }

    @Test
    public void method_withArgumentsAfterArray_ShouldReturnEmpty() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("argsAfterArray", CommandEvent.class, String[].class, int.class);

        assertEquals(Optional.empty(), SlashCommandDefinition.build(getBuildContext(method)));
    }

    @Test
    public void method_withArgumentsAfterOptional_ShouldWork() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("argsAfterOptional", CommandEvent.class, String.class, int.class);

        SlashCommandDefinition definition = SlashCommandDefinition.build(getBuildContext(method)).orElse(null);

        assertNotNull(definition);

        assertEquals(definition.getMethod(), method);

        assertEquals(3, definition.getParameters().size());
        assertEquals(CommandEvent.class, definition.getParameters().get(0).type());
        assertTrue(definition.getParameters().get(1).isOptional());
        assertFalse(definition.getParameters().get(2).isOptional());
    }

    @Test
    public void method_withOptionalAfterOptional_ShouldWork() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Method method = controller.getDeclaredMethod("optionalAfterOptional", CommandEvent.class, String.class, int.class);
        SlashCommandDefinition definition = SlashCommandDefinition.build(getBuildContext(method)).orElse(null);

        assertNotNull(definition);

        assertEquals(definition.getMethod(), method);

        assertEquals(3, definition.getParameters().size());
        assertEquals(CommandEvent.class, definition.getParameters().get(0).type());
        assertEquals(String.class, definition.getParameters().get(1).type());
        assertEquals(Integer.class, definition.getParameters().get(2).type());
    }

    @Test
    public void cooldown_zeroTimeUnits_ShouldNotBeSet() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("zeroCooldown", CommandEvent.class);
        SlashCommandDefinition definition = SlashCommandDefinition.build(getBuildContext(method)).orElse(null);

        assertNotNull(definition);

        assertFalse(definition.hasCooldown());
    }

    @Test
    public void cooldown_tenMilliseconds_ShouldWork() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("cooldown", CommandEvent.class);
        SlashCommandDefinition definition = SlashCommandDefinition.build(getBuildContext(method)).orElse(null);

        assertNotNull(definition);

        assertTrue(definition.hasCooldown());
        assertEquals(10, definition.getCooldown().delay());
        assertEquals(TimeUnit.MILLISECONDS, definition.getCooldown().timeUnit());
    }

    @Test
    public void permission_oneString_ShouldWork() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("permission", CommandEvent.class);
        SlashCommandDefinition definition = SlashCommandDefinition.build(getBuildContext(method)).orElse(null);

        assertNotNull(definition);

        assertEquals(1, definition.getPermissions().size());
        assertTrue(definition.getPermissions().contains("permission"));
    }

}
