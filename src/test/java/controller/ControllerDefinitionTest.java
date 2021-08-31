package controller;

import com.github.kaktushose.jda.commands.entities.CommandEvent;
import com.github.kaktushose.jda.commands.rewrite.dispatching.adapter.ParameterAdapterRegistry;
import com.github.kaktushose.jda.commands.rewrite.reflect.CommandDefinition;
import com.github.kaktushose.jda.commands.rewrite.reflect.ControllerDefinition;
import com.github.kaktushose.jda.commands.rewrite.dispatching.validation.ValidatorRegistry;
import commands.UnsupportedType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class ControllerDefinitionTest {


    private static Class<?> controller;
    private static ControllerDefinitionTestController instance;
    private static ValidatorRegistry validators;
    private static ParameterAdapterRegistry adapters;

    @BeforeAll
    public static void setup() {
        instance = new ControllerDefinitionTestController();
        controller = instance.getClass();

        validators = new ValidatorRegistry();

        // make sure that this type is not registered before testing
        adapters = new ParameterAdapterRegistry();
        adapters.unregister(UnsupportedType.class);
    }

    @Test
    public void command_NoValues_ShouldAdoptControllerValues() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("adopt", CommandEvent.class);

        ControllerDefinition controllerDefinition = ControllerDefinition.build(controller, adapters, validators).orElse(null);
        assertNotNull(controllerDefinition);
        CommandDefinition definition = controllerDefinition.getSubCommands().stream().filter(c -> c.getMethod().equals(method)).findFirst().orElse(null);
        assertNotNull(definition);


        assertEquals(2, definition.getLabels().size());
        assertTrue(definition.getLabels().contains("super"));
        assertTrue(definition.getLabels().contains("superAlias"));

        assertTrue(definition.hasCooldown());
        assertEquals(10, definition.getCooldown().getDelay());
        assertEquals(TimeUnit.MILLISECONDS, definition.getCooldown().getTimeUnit());

        assertEquals(1, definition.getPermissions().size());
        assertTrue(definition.getPermissions().contains("superPermission"));
    }

    @Test
    public void command_OwnValues_ShouldCombineOrOverride() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("combine", CommandEvent.class);

        ControllerDefinition controllerDefinition = ControllerDefinition.build(controller, adapters, validators).orElse(null);
        assertNotNull(controllerDefinition);
        CommandDefinition definition = controllerDefinition.getSubCommands().stream().filter(c -> c.getMethod().equals(method)).findFirst().orElse(null);
        assertNotNull(definition);

        assertEquals(4, definition.getLabels().size());
        assertTrue(definition.getLabels().contains("super sub"));
        assertTrue(definition.getLabels().contains("superAlias sub"));
        assertTrue(definition.getLabels().contains("super subAlias"));
        assertTrue(definition.getLabels().contains("superAlias subAlias"));

        assertTrue(definition.hasCooldown());
        assertEquals(5, definition.getCooldown().getDelay());
        assertEquals(TimeUnit.DAYS, definition.getCooldown().getTimeUnit());

        assertEquals(2, definition.getPermissions().size());
        assertTrue(definition.getPermissions().contains("superPermission"));
        assertTrue(definition.getPermissions().contains("subPermission"));
    }

    @Test
    public void commands_Overloading_ShouldOnlyRegisterFirst() throws NoSuchMethodException {
        ControllerDefinition controllerDefinition = ControllerDefinition.build(controller, adapters, validators).orElse(null);

        Method adopt = controller.getDeclaredMethod("adopt", CommandEvent.class);
        Method overloading = controller.getDeclaredMethod("overloading", CommandEvent.class);

        assertNotNull(controllerDefinition);
        assertTrue(controllerDefinition.getSubCommands().stream().anyMatch(c -> c.getMethod().equals(adopt)));
        assertFalse(controllerDefinition.getSubCommands().stream().anyMatch(c -> c.getMethod().equals(overloading)));
    }

    @Test
    public void command_isSuper_ShouldWork() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("superCommand", CommandEvent.class);
        ControllerDefinition controllerDefinition = ControllerDefinition.build(controller, adapters, validators).orElse(null);

        assertNotNull(controllerDefinition);
        assertTrue(controllerDefinition.hasSuperCommand());
        assertEquals(2, controllerDefinition.getSubCommands().size());
        assertEquals(method, controllerDefinition.getSuperCommand().getMethod());
    }
}
