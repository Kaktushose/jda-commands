package controller;

import com.github.kaktushose.jda.commands.dependency.DependencyInjector;
import com.github.kaktushose.jda.commands.dispatching.commands.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import com.github.kaktushose.jda.commands.reflect.interactions.CommandDefinition;
import com.github.kaktushose.jda.commands.reflect.ControllerDefinition;
import commands.UnsupportedType;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import net.dv8tion.jda.api.interactions.commands.localization.ResourceBundleLocalizationFunction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class ControllerDefinitionTest {

    private static final LocalizationFunction LOCALIZATION_FUNCTION = ResourceBundleLocalizationFunction.empty().build();
    private static Class<?> controller;
    private static ValidatorRegistry validators;
    private static DependencyInjector dependencyInjector;

    @BeforeAll
    public static void setup() {
        ControllerDefinitionTestController instance = new ControllerDefinitionTestController();
        controller = instance.getClass();

        validators = new ValidatorRegistry();

        // make sure that this type is not registered before testing
        TypeAdapterRegistry adapters = new TypeAdapterRegistry();
        adapters.unregister(UnsupportedType.class);

        dependencyInjector = new DependencyInjector();
    }

    @Test
    public void command_NoValues_ShouldAdoptControllerValues() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("adopt", CommandEvent.class);

        ControllerDefinition controllerDefinition = ControllerDefinition.build(controller, validators, dependencyInjector, LOCALIZATION_FUNCTION).orElse(null);
        assertNotNull(controllerDefinition);
        CommandDefinition definition = controllerDefinition.getCommands().stream().filter(c -> c.getMethod().equals(method)).findFirst().orElse(null);
        assertNotNull(definition);


        assertTrue(definition.getName().contains("super"));

        assertTrue(definition.hasCooldown());
        assertEquals(10, definition.getCooldown().getDelay());
        assertEquals(TimeUnit.MILLISECONDS, definition.getCooldown().getTimeUnit());

        assertEquals(1, definition.getPermissions().size());
        assertTrue(definition.getPermissions().contains("superPermission"));
    }

    @Test
    public void command_OwnValues_ShouldCombineOrOverride() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("combine", CommandEvent.class);

        ControllerDefinition controllerDefinition = ControllerDefinition.build(controller, validators, dependencyInjector, LOCALIZATION_FUNCTION).orElse(null);
        assertNotNull(controllerDefinition);
        CommandDefinition definition = controllerDefinition.getCommands().stream().filter(c -> c.getMethod().equals(method)).findFirst().orElse(null);
        assertNotNull(definition);

        assertEquals("super sub", definition.getName());

        assertTrue(definition.hasCooldown());
        assertEquals(5, definition.getCooldown().getDelay());
        assertEquals(TimeUnit.DAYS, definition.getCooldown().getTimeUnit());

        assertEquals(2, definition.getPermissions().size());
        assertTrue(definition.getPermissions().contains("superPermission"));
        assertTrue(definition.getPermissions().contains("subPermission"));
    }

}
