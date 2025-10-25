package definitions.config;

import com.github.kaktushose.jda.commands.annotations.interactions.Button;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.annotations.interactions.ReplyConfig;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.ButtonDefinition;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent;
import com.github.kaktushose.jda.commands.internal.Helpers;
import definitions.TestHelpers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReplyConfigTest {

    private static InteractionDefinition.ReplyConfig getReplyConfig(Class<?> clazz, String method) {
        return getReplyConfig(clazz, method, new InteractionDefinition.ReplyConfig());
    }

    private static InteractionDefinition.ReplyConfig getReplyConfig(Class<?> clazz, String method, InteractionDefinition.ReplyConfig fallback) {
        var definition = ButtonDefinition.build(TestHelpers.getBuildContext(clazz, method));
        return Helpers.replyConfig(definition, fallback);
    }

    @Test
    void methodLevelConfig_withDefaultMethod_ShouldUseGlobalValues() {
        var fallback = new InteractionDefinition.ReplyConfig();
        var config = getReplyConfig(MethodLevelConfig.class, "defaultValues", fallback);


        assertEquals(config.ephemeral(), fallback.ephemeral());
        assertEquals(config.editReply(), fallback.editReply());
        assertEquals(config.keepComponents(), fallback.keepComponents());
        assertEquals(config.keepSelections(), fallback.keepSelections());
    }

    @Test
    void methodLevelConfig_withCustomMethod_ShouldUseMethodValues() {
        var config = getReplyConfig(MethodLevelConfig.class, "customValues");

        assertTrue(config.ephemeral());
        assertFalse(config.editReply());
        assertFalse(config.keepComponents());
        assertFalse(config.keepSelections());
    }

    @Test
    void classLevelConfig_withDefaultMethod_ShouldUseControllerValues() {
        var config = getReplyConfig(ClassLevelConfig.class, "defaultValues");
        assertTrue(config.ephemeral());
        assertFalse(config.editReply());
        assertFalse(config.keepComponents());
        assertFalse(config.keepSelections());
    }

    @Test
    void classLevelConfig_withSameCustomMethod_ShouldBeEquals() {
        var first = getReplyConfig(ClassLevelConfig.class, "defaultValues");
        var second = getReplyConfig(ClassLevelConfig.class, "sameValues");

        assertEquals(first.ephemeral(), second.ephemeral());
        assertEquals(first.editReply(), second.editReply());
        assertEquals(first.keepComponents(), second.keepComponents());
        assertEquals(first.keepSelections(), second.keepSelections());
    }

    @Test
    void classLevelConfig_withDifferentCustomMethod_ShouldUseMethodValues() {
        var config = getReplyConfig(ClassLevelConfig.class, "customValues");
        assertFalse(config.ephemeral());
        assertTrue(config.editReply());
        assertTrue(config.keepComponents());
        assertTrue(config.keepSelections());
    }

    @Interaction
    private static class MethodLevelConfig {

        @Button
        public void defaultValues(ComponentEvent event) {
        }

        @Button
        @ReplyConfig(ephemeral = true, editReply = false, keepComponents = false, keepSelections = false)
        public void customValues(ComponentEvent event) {
        }
    }

    @Interaction
    @ReplyConfig(ephemeral = true, editReply = false, keepComponents = false, keepSelections = false)
    private static class ClassLevelConfig {

        @Button
        public void defaultValues(ComponentEvent event) {
        }

        @Button
        @ReplyConfig(ephemeral = true, editReply = false, keepComponents = false, keepSelections = false)
        public void sameValues(ComponentEvent event) {
        }

        @Button
        @ReplyConfig
        public void customValues(ComponentEvent event) {
        }
    }
}
