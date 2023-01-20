package controller;

import com.github.kaktushose.jda.commands.dependency.DependencyInjector;
import com.github.kaktushose.jda.commands.dispatching.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.reflect.ControllerDefinition;
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
    private static TypeAdapterRegistry adapters;
    private static DependencyInjector dependencyInjector;

    @BeforeAll
    public static void setup() {
        instance = new ControllerDefinitionTestController();
        controller = instance.getClass();

        validators = new ValidatorRegistry();

        // make sure that this type is not registered before testing
        adapters = new TypeAdapterRegistry();
        adapters.unregister(UnsupportedType.class);

        dependencyInjector = new DependencyInjector();
    }

    @Test
    public void command_NoValues_ShouldAdoptControllerValues() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("adopt", CommandEvent.class);

        ControllerDefinition controllerDefinition = ControllerDefinition.build(controller, adapters, validators, dependencyInjector).orElse(null);
        assertNotNull(controllerDefinition);
        SlashCommandDefinition definition = controllerDefinition.getSubCommands().stream().filter(c -> c.getMethod().equals(method)).findFirst().orElse(null);
        assertNotNull(definition);


        assertEquals(2, definition.getLabel().size());
        assertTrue(definition.getLabel().contains("super"));
        assertTrue(definition.getLabel().contains("superAlias"));

        assertTrue(definition.hasCooldown());
        assertEquals(10, definition.getCooldown().getDelay());
        assertEquals(TimeUnit.MILLISECONDS, definition.getCooldown().getTimeUnit());

        assertEquals(1, definition.getPermissions().size());
        assertTrue(definition.getPermissions().contains("superPermission"));
    }

    @Test
    public void command_OwnValues_ShouldCombineOrOverride() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("combine", CommandEvent.class);

        ControllerDefinition controllerDefinition = ControllerDefinition.build(controller, adapters, validators, dependencyInjector).orElse(null);
        assertNotNull(controllerDefinition);
        SlashCommandDefinition definition = controllerDefinition.getSubCommands().stream().filter(c -> c.getMethod().equals(method)).findFirst().orElse(null);
        assertNotNull(definition);

        assertEquals(4, definition.getLabel().size());
        assertTrue(definition.getLabel().contains("super sub"));
        assertTrue(definition.getLabel().contains("superAlias sub"));
        assertTrue(definition.getLabel().contains("super subAlias"));
        assertTrue(definition.getLabel().contains("superAlias subAlias"));

        assertTrue(definition.hasCooldown());
        assertEquals(5, definition.getCooldown().getDelay());
        assertEquals(TimeUnit.DAYS, definition.getCooldown().getTimeUnit());

        assertEquals(2, definition.getPermissions().size());
        assertTrue(definition.getPermissions().contains("superPermission"));
        assertTrue(definition.getPermissions().contains("subPermission"));
    }

    @Test
    public void commands_Overloading_ShouldOnlyRegisterOne() {
        ControllerDefinition controllerDefinition = ControllerDefinition.build(controller, adapters, validators, dependencyInjector).orElse(null);

        assertNotNull(controllerDefinition);
        assertEquals(3, controllerDefinition.getSubCommands().size());
        assertEquals(1, controllerDefinition.getSuperCommands().size());

    }

    @Test
    public void command_isSuper_ShouldWork() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("superCommand", CommandEvent.class);
        ControllerDefinition controllerDefinition = ControllerDefinition.build(controller, adapters, validators, dependencyInjector).orElse(null);

        assertNotNull(controllerDefinition);
        assertTrue(controllerDefinition.hasSuperCommands());
        assertEquals(1, controllerDefinition.getSuperCommands().size());
        assertEquals(method, controllerDefinition.getSuperCommands().get(0).getMethod());
    }
}
