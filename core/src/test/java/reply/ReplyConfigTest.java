package reply;

import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.definitions.description.reflective.ReflectiveDescriptor;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition.ReplyConfig;
import com.github.kaktushose.jda.commands.definitions.interactions.MethodBuildContext;
import com.github.kaktushose.jda.commands.definitions.interactions.command.CommandDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.ButtonDefinition;
import com.github.kaktushose.jda.commands.dispatching.validation.internal.Validators;
import com.github.kaktushose.jda.commands.internal.Helpers;
import net.dv8tion.jda.api.interactions.commands.localization.ResourceBundleLocalizationFunction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ReplyConfigTest {

    private static Class<?> defaultController;
    private static Class<?> customController;

    @BeforeAll
    public static void setup() {
        defaultController = DefaultController.class;
        customController = CustomController.class;
    }

    private ReplyConfig getReplyConfig(Class<?> clazz, String method, ReplyConfig replyConfig) {
        var description = new ReflectiveDescriptor().describe(clazz);
        var definition = ButtonDefinition.build(
                new MethodBuildContext(
                        new Validators(Map.of()),
                        ResourceBundleLocalizationFunction.empty().build(),
                        clazz.getAnnotation(Interaction.class),
                        Set.of(),
                        SlashCommandDefinition.CooldownDefinition.build(null),
                        description,
                        description.methods().stream().filter(it -> it.name().equals(method)).findFirst().get(),
                        Set.of(),
                        new CommandDefinition.CommandConfig()
                )
        ).orElseThrow();
        return Helpers.replyConfig(definition, replyConfig);
    }

    @Test
    public void defaultController_withDefaultMethod_ShouldUseGlobalValues() throws NoSuchMethodException {
        var fallback = new ReplyConfig();
        var config = getReplyConfig(defaultController, "defaultValues", fallback);


        assertEquals(config.ephemeral(), fallback.ephemeral());
        assertEquals(config.editReply(), fallback.editReply());
        assertEquals(config.keepComponents(), fallback.keepComponents());
    }

    @Test
    public void defaultController_withCustomMethod_ShouldUseMethodValues() throws NoSuchMethodException {
        var config = getReplyConfig(defaultController, "customValues", new ReplyConfig());

        assertTrue(config.ephemeral());
        assertFalse(config.editReply());
        assertFalse(config.keepComponents());
    }

    @Test
    public void customController_withDefaultMethod_ShouldUseControllerValues() throws NoSuchMethodException {
        var config = getReplyConfig(customController, "defaultValues", new ReplyConfig());
        assertTrue(config.ephemeral());
        assertFalse(config.editReply());
        assertFalse(config.keepComponents());
    }

    @Test
    public void customController_withSameCustomMethod_ShouldBeEquals() throws NoSuchMethodException {
        var first = getReplyConfig(customController, "defaultValues", new ReplyConfig());
        var second = getReplyConfig(customController, "sameValues", new ReplyConfig());

        assertEquals(first.ephemeral(), second.ephemeral());
        assertEquals(first.editReply(), second.editReply());
        assertEquals(first.keepComponents(), second.keepComponents());
    }

    @Test
    public void customController_withDifferentCustomMethod_ShouldUseMethodValues() throws NoSuchMethodException {
        var config = getReplyConfig(customController, "customValues", new ReplyConfig());
        assertFalse(config.ephemeral());
        assertTrue(config.editReply());
        assertTrue(config.keepComponents());
    }

}
