package definitions;

import com.github.kaktushose.jda.commands.annotations.interactions.*;
import com.github.kaktushose.jda.commands.definitions.description.AnnotationDescription;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.definitions.description.ParameterDescription;
import com.github.kaktushose.jda.commands.definitions.description.reflective.ReflectiveDescriptor;
import com.github.kaktushose.jda.commands.definitions.interactions.MethodBuildContext;
import com.github.kaktushose.jda.commands.definitions.interactions.command.CommandDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.validation.internal.Validators;
import net.dv8tion.jda.api.interactions.commands.localization.ResourceBundleLocalizationFunction;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class SlashCommandDefinitionTest {

    private static Class<?> controller;
    private static Validators validator;

    @BeforeAll
    static void setup() {
        controller = CommandDefinitionTestController.class;

        validator = new Validators(Map.of());
    }

    private static MethodBuildContext getBuildContext(Method method) {
        var clazz = new ReflectiveDescriptor().describe(method.getClass());

        return new MethodBuildContext(
                validator,
                ResourceBundleLocalizationFunction.empty().build(),
                controller.getAnnotation(Interaction.class),
                Set.of(),
                SlashCommandDefinition.CooldownDefinition.build(null),
                clazz,
                method(method),
                Set.of(),
                new CommandDefinition.CommandConfig()
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
                annotationList(method.getAnnotations()),
                (instance, arguments) -> method.invoke(instance, arguments.toArray())
        );
    }

    private static ParameterDescription parameter(@NotNull Parameter parameter) {
        Class<?>[] arguments = {};
        if (parameter.getParameterizedType() instanceof ParameterizedType type) {
            arguments = Arrays.stream(type.getActualTypeArguments())
                    .map(it -> it instanceof ParameterizedType pT ? pT.getRawType() : it)
                    .map(Class.class::cast)
                    .toArray(Class[]::new);
        }

        return new ParameterDescription(
                parameter.getType(),
                arguments,
                parameter.getName(),
                annotationList(parameter.getAnnotations())
        );
    }

    private static List<AnnotationDescription<?>> annotationList(Annotation[] array) {
        return Arrays.stream(array)
                .map(SlashCommandDefinitionTest::annotation)
                .collect(Collectors.toUnmodifiableList());
    }

    // only add annotations one level deep
    private static AnnotationDescription<?> annotation(@NotNull Annotation annotation) {
        return new AnnotationDescription<>(annotation, Arrays.stream(annotation.annotationType().getAnnotations())
                .map(ann -> new AnnotationDescription<>(ann, List.of()))
                .collect(Collectors.toUnmodifiableList()));
    }

    private static <T> Collection<T> toList(T[] array) {
        return Arrays.stream(array).toList();
    }

    @Test
    void method_withoutAnnotation_ShouldThrowNoSuchElementException() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("noAnnotation");

        assertThrows(NoSuchElementException.class, () -> SlashCommandDefinition.build(getBuildContext(method)));
    }

    @Test
    void method_withoutArgs_ShouldReturnEmptyOptional() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("noArgs");

        assertEquals(Optional.empty(), SlashCommandDefinition.build(getBuildContext(method)));
    }

    @Test
    void method_withoutCommandEvent_ShouldReturnEmptyOptional() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("noCommandEvent", int.class);

        assertEquals(Optional.empty(), SlashCommandDefinition.build(getBuildContext(method)));
    }

    @Test
    void method_withCommandEventNotAtIndex0_ShouldReturnEmpty() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("commandEventWrongIndex", int.class, CommandEvent.class);

        assertEquals(Optional.empty(), SlashCommandDefinition.build(getBuildContext(method)));
    }

    @Test
    void method_withCommandEvent_ShouldWork() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("commandEvent", CommandEvent.class);

        SlashCommandDefinition definition = SlashCommandDefinition.build(getBuildContext(method)).orElse(null);

        assertTrue(definition.commandOptions().isEmpty());
    }

    @Interaction
    static class CommandDefinitionTestController {

        public void noAnnotation() {
        }

        @Command("a")
        public void noArgs() {
        }

        @Command("b")
        public void noCommandEvent(int i) {
        }

        @Command("c")
        public void commandEventWrongIndex(int i, CommandEvent event) {
        }

        @Command("d")
        public void commandEvent(CommandEvent event) {
        }
    }

}
