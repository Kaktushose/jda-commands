package definitions;

import com.github.kaktushose.jda.commands.annotations.interactions.AutoComplete;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class TestHelpers {

    public static final Validators validators = new Validators(Map.of());

    public static MethodBuildContext getBuildContext(Class<?> controller, String method) {
        var clazz = new ReflectiveDescriptor().describe(controller);

        return new MethodBuildContext(
                validators,
                ResourceBundleLocalizationFunction.empty().build(),
                controller.getAnnotation(Interaction.class),
                Set.of(),
                SlashCommandDefinition.CooldownDefinition.build(null),
                clazz,
                methodDescription(controller, method),
                autoCompleteDefinitions(clazz),
                new CommandDefinition.CommandConfig()
        );
    }

    private static Collection<AutoCompleteDefinition> autoCompleteDefinitions(ClassDescription clazz) {
        return clazz.methods().stream()
                .filter(it -> it.annotation(AutoComplete.class).isPresent())
                .map(method -> AutoCompleteDefinition.build(clazz, method))
                .toList();
    }

    private static MethodDescription methodDescription(Class<?> controller, String method) {
        ClassDescription clazz = Descriptor.REFLECTIVE.describe(controller);
        return clazz.methods().stream().filter(it -> it.name().equals(method)).findFirst().orElseThrow();
    }

}
