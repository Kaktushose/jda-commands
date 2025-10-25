package definitions;

import com.github.kaktushose.jda.commands.annotations.interactions.AutoComplete;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.annotations.interactions.Permissions;
import com.github.kaktushose.jda.commands.definitions.description.AnnotationDescription;
import com.github.kaktushose.jda.commands.definitions.description.ClassDescription;
import com.github.kaktushose.jda.commands.definitions.description.Descriptor;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.definitions.description.reflective.ReflectiveDescriptor;
import com.github.kaktushose.jda.commands.definitions.interactions.AutoCompleteDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.MethodBuildContext;
import com.github.kaktushose.jda.commands.definitions.interactions.command.CommandDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.dispatching.validation.internal.Validators;
import net.dv8tion.jda.api.interactions.commands.localization.ResourceBundleLocalizationFunction;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TestHelpers {

    public static final Validators validators = new Validators(Map.of());

    public static MethodBuildContext getBuildContext(Class<?> controller, String method) {
        return getBuildContextOptionalAutoComplete(controller, method, false);
    }

    public static MethodBuildContext getBuildContextOptionalAutoComplete(Class<?> controller, String method, boolean autoComplete) {
        var clazz = new ReflectiveDescriptor().describe(controller);

        return new MethodBuildContext(
                validators,
                ResourceBundleLocalizationFunction.empty().build(),
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
