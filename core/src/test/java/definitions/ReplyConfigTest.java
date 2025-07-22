package definitions;

import com.github.kaktushose.jda.commands.annotations.interactions.Button;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.definitions.description.reflective.ReflectiveDescriptor;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition.ReplyConfig;
import com.github.kaktushose.jda.commands.definitions.interactions.MethodBuildContext;
import com.github.kaktushose.jda.commands.definitions.interactions.command.CommandDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.ButtonDefinition;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent;
import com.github.kaktushose.jda.commands.dispatching.validation.internal.Validators;
import com.github.kaktushose.jda.commands.internal.Helpers;
import net.dv8tion.jda.api.interactions.commands.localization.ResourceBundleLocalizationFunction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ReplyConfigTest {

    private static Class<?> defaultController;
    private static Class<?> customController;

    @BeforeAll
    static void setup() {
        defaultController = MethodLevelReplyConfig.class;
        customController = ClassLevelReplyConfig.class;
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
    void defaultController_withDefaultMethod_ShouldUseGlobalValues() {
        var fallback = new ReplyConfig();
        var config = getReplyConfig(defaultController, "defaultValues", fallback);


        assertEquals(config.ephemeral(), fallback.ephemeral());
        assertEquals(config.editReply(), fallback.editReply());
        assertEquals(config.keepComponents(), fallback.keepComponents());
    }

    @Test
    void defaultController_withCustomMethod_ShouldUseMethodValues() {
        var config = getReplyConfig(defaultController, "customValues", new ReplyConfig());

        assertTrue(config.ephemeral());
        assertFalse(config.editReply());
        assertFalse(config.keepComponents());
    }

    @Test
    void customController_withDefaultMethod_ShouldUseControllerValues() {
        var config = getReplyConfig(customController, "defaultValues", new ReplyConfig());
        assertTrue(config.ephemeral());
        assertFalse(config.editReply());
        assertFalse(config.keepComponents());
    }

    @Test
    void customController_withSameCustomMethod_ShouldBeEquals() {
        var first = getReplyConfig(customController, "defaultValues", new ReplyConfig());
        var second = getReplyConfig(customController, "sameValues", new ReplyConfig());

        assertEquals(first.ephemeral(), second.ephemeral());
        assertEquals(first.editReply(), second.editReply());
        assertEquals(first.keepComponents(), second.keepComponents());
    }

    @Test
    void customController_withDifferentCustomMethod_ShouldUseMethodValues() {
        var config = getReplyConfig(customController, "customValues", new ReplyConfig());
        assertFalse(config.ephemeral());
        assertTrue(config.editReply());
        assertTrue(config.keepComponents());
    }

    @Interaction
    static class MethodLevelReplyConfig {

        @Button
        public void defaultValues(ComponentEvent event) {

        }

        @Button
        @com.github.kaktushose.jda.commands.annotations.interactions.ReplyConfig(ephemeral = true, editReply = false, keepComponents = false)
        public void customValues(ComponentEvent event) {

        }
    }

    @Interaction
    @com.github.kaktushose.jda.commands.annotations.interactions.ReplyConfig(ephemeral = true, editReply = false, keepComponents = false)
    static class ClassLevelReplyConfig {

        @Button
        public void defaultValues(ComponentEvent event) {

        }

        @Button
        @com.github.kaktushose.jda.commands.annotations.interactions.ReplyConfig(ephemeral = true, editReply = false, keepComponents = false)
        public void sameValues(ComponentEvent event) {

        }

        @Button
        @com.github.kaktushose.jda.commands.annotations.interactions.ReplyConfig
        public void customValues(ComponentEvent event) {

        }
    }
}
