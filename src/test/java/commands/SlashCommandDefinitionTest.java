package commands;

import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.definitions.description.ParameterDescription;
import com.github.kaktushose.jda.commands.definitions.description.reflective.ReflectiveDescriptor;
import com.github.kaktushose.jda.commands.definitions.interactions.MethodBuildContext;
import com.github.kaktushose.jda.commands.definitions.interactions.command.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.validation.internal.Validators;
import net.dv8tion.jda.api.interactions.commands.localization.ResourceBundleLocalizationFunction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class SlashCommandDefinitionTest {

    private static Class<?> controller;
    private static Validators validator;

    @BeforeAll
    public static void setup() {
        controller = CommandDefinitionTestController.class;

        validator = new Validators(Map.of());
    }

    public static MethodBuildContext getBuildContext(Method method) {
        var clazz = new ReflectiveDescriptor().apply(method.getClass());

        return new MethodBuildContext(
                validator,
                ResourceBundleLocalizationFunction.empty().build(),
                controller.getAnnotation(Interaction.class),
                Set.of(),
                SlashCommandDefinition.CooldownDefinition.build(null),
                clazz,
                method(method),
                Set.of()
        );
    }

    private static MethodDescription method(Method method) {
        if (!Modifier.isPublic(method.getModifiers())) return null;
        List<ParameterDescription> parameters = Arrays.stream(method.getParameters())
                .map(SlashCommandDefinitionTest::parameter)
                .toList();


        return new MethodDescription(
                method.getDeclaringClass(),
                method.getReturnType(),
                method.getName(),
                parameters,
                toList(method.getAnnotations()),
                (instance, arguments) -> method.invoke(instance, arguments.toArray())
        );
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
    public void method_withoutAnnotation_ShouldThrow() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("noAnnotation");

        assertThrows(NoSuchElementException.class, () -> SlashCommandDefinition.build(getBuildContext(method)));
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
    public void method_withCommandEvent_ShouldWork() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("commandEvent", CommandEvent.class);
        SlashCommandDefinition definition = (SlashCommandDefinition) SlashCommandDefinition.build(getBuildContext(method)).orElse(null);

        assertNotNull(definition);


        assertTrue(definition.commandOptions().isEmpty());
    }

    @Test
    public void method_withUnsupportedType_ShouldWork() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("unsupported", CommandEvent.class, UnsupportedType.class);

        SlashCommandDefinition definition = (SlashCommandDefinition) SlashCommandDefinition.build(getBuildContext(method)).orElse(null);

        assertNotNull(definition);


        assertEquals(1, definition.commandOptions().size());
        assertEquals(UnsupportedType.class, definition.commandOptions().getFirst().type());
    }

    @Test
    public void method_withArgumentsAfterOptional_ShouldWork() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("argsAfterOptional", CommandEvent.class, String.class, int.class);

        SlashCommandDefinition definition = (SlashCommandDefinition) SlashCommandDefinition.build(getBuildContext(method)).orElse(null);

        assertNotNull(definition);


        assertEquals(2, definition.commandOptions().size());
        var parameters = List.copyOf(definition.commandOptions());
        assertTrue(parameters.get(0).optional());
        assertFalse(parameters.get(1).optional());
    }

    @Test
    public void method_withOptionalAfterOptional_ShouldWork() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("optionalAfterOptional", CommandEvent.class, String.class, int.class);
        SlashCommandDefinition definition = (SlashCommandDefinition) SlashCommandDefinition.build(getBuildContext(method)).orElse(null);

        assertNotNull(definition);


        assertEquals(2, definition.commandOptions().size());
        var parameters = List.copyOf(definition.commandOptions());
        assertEquals(String.class, parameters.get(0).type());
        assertEquals(int.class, parameters.get(1).type());
    }

    @Test
    public void cooldown_zeroTimeUnits_ShouldNotBeSet() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("zeroCooldown", CommandEvent.class);
        SlashCommandDefinition definition = (SlashCommandDefinition) SlashCommandDefinition.build(getBuildContext(method)).orElse(null);

        assertNotNull(definition);

        assertFalse(definition.cooldown().delay() > 0);
    }

    @Test
    public void cooldown_tenMilliseconds_ShouldWork() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("cooldown", CommandEvent.class);
        SlashCommandDefinition definition = (SlashCommandDefinition) SlashCommandDefinition.build(getBuildContext(method)).orElse(null);

        assertNotNull(definition);

        assertTrue(definition.cooldown().delay() > 0);
        assertEquals(10, definition.cooldown().delay());
        assertEquals(TimeUnit.MILLISECONDS, definition.cooldown().timeUnit());
    }

    @Test
    public void permission_oneString_ShouldWork() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("permission", CommandEvent.class);
        SlashCommandDefinition definition = (SlashCommandDefinition) SlashCommandDefinition.build(getBuildContext(method)).orElse(null);

        assertNotNull(definition);

        assertEquals(1, definition.permissions().size());
        assertTrue(definition.permissions().contains("permission"));
    }

}
