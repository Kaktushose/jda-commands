package definitions;

import io.github.kaktushose.jdac.annotations.interactions.AutoComplete;
import io.github.kaktushose.jdac.annotations.interactions.Interaction;
import io.github.kaktushose.jdac.annotations.interactions.Permissions;
import io.github.kaktushose.jdac.definitions.description.AnnotationDescription;
import io.github.kaktushose.jdac.definitions.description.ClassDescription;
import io.github.kaktushose.jdac.definitions.description.Descriptor;
import io.github.kaktushose.jdac.definitions.description.MethodDescription;
import io.github.kaktushose.jdac.definitions.description.reflective.ReflectiveDescriptor;
import io.github.kaktushose.jdac.definitions.interactions.AutoCompleteDefinition;
import io.github.kaktushose.jdac.definitions.interactions.MethodBuildContext;
import io.github.kaktushose.jdac.definitions.interactions.command.CommandDefinition;
import io.github.kaktushose.jdac.definitions.interactions.command.SlashCommandDefinition;
import io.github.kaktushose.jdac.dispatching.validation.internal.Validators;
import io.github.kaktushose.jdac.message.i18n.FluavaLocalizer;
import io.github.kaktushose.jdac.message.i18n.I18n;
import dev.goldmensch.fluava.Fluava;
import net.dv8tion.jda.api.interactions.commands.localization.ResourceBundleLocalizationFunction;

import java.util.*;
import java.util.stream.Collectors;

public class TestHelpers {

    public static final I18n I18N = new I18n(Descriptor.REFLECTIVE, new FluavaLocalizer(Fluava.create(Locale.ENGLISH)));

    public static final Validators validators = new Validators(Map.of());

    public static MethodBuildContext getBuildContext(Class<?> controller, String method) {
        return getBuildContextOptionalAutoComplete(controller, method, false);
    }

    public static MethodBuildContext getBuildContextOptionalAutoComplete(Class<?> controller, String method, boolean autoComplete) {
        var clazz = new ReflectiveDescriptor().describe(controller);

        return new MethodBuildContext(
                validators,
                ResourceBundleLocalizationFunction.empty().build(),
                I18N,
                controller.getAnnotation(Interaction.class),
                permissions(clazz),
                SlashCommandDefinition.CooldownDefinition.build(null),
                clazz,
                methodDescription(controller, method),
                autoComplete ? autoCompleteDefinitions(clazz) : Set.of(),
                new CommandDefinition.CommandConfig()
        );
    }

    private static Collection<AutoCompleteDefinition> autoCompleteDefinitions(ClassDescription clazz) {
        return clazz.methods().stream()
                .filter(it -> it.annotation(AutoComplete.class).isPresent())
                .map(method -> AutoCompleteDefinition.build(clazz, method))
                .toList();
    }

    private static Set<String> permissions(ClassDescription clazz) {
        return clazz.annotations()
                .stream()
                .filter(ann -> ann.type() == Permissions.class)
                .map(AnnotationDescription::value)
                .map(Permissions.class::cast)
                .map(Permissions::value)
                .flatMap(Arrays::stream)
                .collect(Collectors.toSet());
    }

    private static MethodDescription methodDescription(Class<?> controller, String method) {
        ClassDescription clazz = Descriptor.REFLECTIVE.describe(controller);
        return clazz.methods().stream().filter(it -> it.name().equals(method)).findFirst().orElseThrow();
    }

}
