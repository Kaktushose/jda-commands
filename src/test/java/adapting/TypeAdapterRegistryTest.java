package adapting;

import adapting.mock.SlashCommandInteractionEventMock;
import adapting.mock.TypeAdapterRegistryTestController;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.dependency.DefaultDependencyInjector;
import com.github.kaktushose.jda.commands.dispatching.adapter.TypeAdapterRegistry;
import com.github.kaktushose.jda.commands.dispatching.adapter.impl.IntegerAdapter;
import com.github.kaktushose.jda.commands.dispatching.refactor.events.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.interactions.commands.SlashCommandContext;
import com.github.kaktushose.jda.commands.dispatching.middleware.MiddlewareRegistry;
import com.github.kaktushose.jda.commands.dispatching.validation.ValidatorRegistry;
import com.github.kaktushose.jda.commands.reflect.CooldownDefinition;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import com.github.kaktushose.jda.commands.reflect.InteractionRegistry;
import com.github.kaktushose.jda.commands.reflect.MethodBuildContext;
import com.github.kaktushose.jda.commands.reflect.interactions.commands.SlashCommandDefinition;
import net.dv8tion.jda.api.interactions.commands.localization.ResourceBundleLocalizationFunction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TypeAdapterRegistryTest {

    private static Class<?> controller;
    private static ValidatorRegistry validator;
    private static TypeAdapterRegistry adapter;
    private TypeAdapterRegistry registry;

    @BeforeAll
    public static void setup() {
        TypeAdapterRegistryTestController instance = new TypeAdapterRegistryTestController();
        controller = instance.getClass();
        validator = new ValidatorRegistry();
        adapter = new TypeAdapterRegistry();
    }

    @BeforeEach
    public void cleanup() {
        registry = new TypeAdapterRegistry();
    }

    @Test
    public void register_withNewTypeAndNewAdapter_ShouldAdd() {
        registry = new TypeAdapterRegistry();
        registry.register(CustomType.class, new CustomTypeAdapter());

        assertTrue(registry.exists(CustomType.class));
    }

    @Test
    public void register_withExistingTypeAndNewAdapter_ShouldOverride() {
        registry = new TypeAdapterRegistry();
        CustomTypeAdapter adapter = new CustomTypeAdapter();

        assertEquals(IntegerAdapter.class, registry.get(Integer.class).get().getClass());
        registry.register(Integer.class, adapter);

        assertEquals(adapter, registry.get(Integer.class).orElse(null));
    }

    @Test
    public void unregister_withExistingType_ShouldRemove() {
        registry = new TypeAdapterRegistry();

        assertTrue(registry.exists(Integer.class));
        registry.unregister(Integer.class);

        assertFalse(registry.exists(Integer.class));
    }

    @Test
    public void adapt_withStringArray_ShouldNotAdapt() throws NoSuchMethodException {
        SlashCommandContext context = buildContext(buildCommand("stringArray", CommandEvent.class, String[].class), "a", "b", "c");

        registry.adapt(context);

        assertArrayEquals(new String[]{"a", "b", "c"}, (String[]) context.getArguments().getFirst());
    }

    @Test
    public void adapt_withLessInputThanParameters_ShouldThrow() throws NoSuchMethodException {
        SlashCommandContext context = buildContext(buildCommand("inputLength", CommandEvent.class, int.class));

        assertThrows(IllegalStateException.class, () -> registry.adapt(context));
    }

    @Test
    public void adapt_withMoreInputThanParameters_ShouldNotCancel() throws NoSuchMethodException {
        SlashCommandContext context = buildContext(buildCommand("inputLength", CommandEvent.class, int.class), "1", "2");

        registry.adapt(context);

        assertFalse(context.isCancelled());
    }

    @Test
    public void adapt_withOptionalWithDefaultNull_ShouldAddNull() throws NoSuchMethodException {
        SlashCommandContext context = buildContext(buildCommand("optionalNull", CommandEvent.class, int.class));

        registry.adapt(context);

        assertNull(context.getArguments().getFirst());
    }

    @Test
    public void adapt_withOptionalWithDefault_ShouldAddDefault() throws NoSuchMethodException {
        SlashCommandContext context = buildContext(buildCommand("optionalDefault", CommandEvent.class, String.class));

        registry.adapt(context);

        assertEquals(TypeAdapterRegistryTestController.OPTIONAL_DEFAULT, context.getArguments().getFirst());
    }

    @Test
    public void adapt_withMissingTypeAdapter_ShouldThrowIllegalArgumentException() throws NoSuchMethodException {
        adapter.register(CustomType.class, new CustomTypeAdapter());
        SlashCommandContext context = buildContext(buildCommand("noAdapter", CommandEvent.class, CustomType.class), "string");
        adapter.unregister(CustomType.class);

        assertThrows(IllegalArgumentException.class, () -> registry.adapt(context));
    }

    @Test
    public void adapt_withWrongArgument_ShouldCancel() throws NoSuchMethodException {
        SlashCommandContext context = buildContext(buildCommand("wrongArgument", CommandEvent.class, int.class), "string");

        registry.adapt(context);
        assertTrue(context.isCancelled());
    }

    private SlashCommandDefinition buildCommand(String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        Method method = controller.getMethod(name, parameterTypes);
        SlashCommandDefinition command = SlashCommandDefinition.build(new MethodBuildContext(
                validator,
                ResourceBundleLocalizationFunction.empty().build(),
                controller.getAnnotation(Interaction.class),
                Set.of(),
                CooldownDefinition.build(null),
                method,
                Set.of()
        )).orElse(null);
        assertNotNull(command);
        return command;
    }

    public static SlashCommandContext buildContext(SlashCommandDefinition command, String... input) {
        SlashCommandContext context = new SlashCommandContext(
                new SlashCommandInteractionEventMock(),
                new InteractionRegistry(
                        new ValidatorRegistry(),
                        new DefaultDependencyInjector(),
                        ResourceBundleLocalizationFunction.empty().build()
                ),
                new ImplementationRegistry(
                        new DefaultDependencyInjector(),
                        new MiddlewareRegistry(),
                        new TypeAdapterRegistry(),
                        new ValidatorRegistry()
                )
        );
        context.setInput(input);
        context.setCommand(command);
        return context;
    }

    private <T> T giveNull() {
        return null;
    }

}
