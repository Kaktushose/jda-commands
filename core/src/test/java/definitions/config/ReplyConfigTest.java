package definitions.config;

import io.github.kaktushose.jdac.annotations.interactions.Button;
import io.github.kaktushose.jdac.annotations.interactions.Interaction;
import io.github.kaktushose.jdac.annotations.interactions.ReplyConfig;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import io.github.kaktushose.jdac.definitions.interactions.component.ButtonDefinition;
import io.github.kaktushose.jdac.dispatching.events.interactions.ComponentEvent;
import io.github.kaktushose.jdac.internal.Helpers;
import definitions.TestHelpers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReplyConfigTest {

    private static InteractionDefinition.ReplyConfig getReplyConfig(Class<?> clazz, String method) {
        return getReplyConfig(clazz, method, new InteractionDefinition.ReplyConfig());
    }

    private static InteractionDefinition.ReplyConfig getReplyConfig(
            Class<?> clazz,
            String method,
            InteractionDefinition.ReplyConfig fallback
    ) {
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
        assertEquals(config.silent(), fallback.silent());
        assertEquals(config.allowedMentions(), fallback.allowedMentions());
    }

    @Test
    void methodLevelConfig_withCustomMethod_ShouldUseMethodValues() {
        var config = getReplyConfig(MethodLevelConfig.class, "customValues");

        assertTrue(config.ephemeral());
        assertFalse(config.editReply());
        assertFalse(config.keepComponents());
        assertFalse(config.keepSelections());
        assertTrue(config.silent());
        assertTrue(config.allowedMentions().isEmpty());
    }

    @Test
    void classLevelConfig_withDefaultMethod_ShouldUseControllerValues() {
        var config = getReplyConfig(ClassLevelConfig.class, "defaultValues");
        assertTrue(config.ephemeral());
        assertFalse(config.editReply());
        assertFalse(config.keepComponents());
        assertFalse(config.keepSelections());
        assertTrue(config.silent());
        assertTrue(config.allowedMentions().isEmpty());
    }

    @Test
    void classLevelConfig_withSameCustomMethod_ShouldBeEquals() {
        var first = getReplyConfig(ClassLevelConfig.class, "defaultValues");
        var second = getReplyConfig(ClassLevelConfig.class, "sameValues");

        assertEquals(first.ephemeral(), second.ephemeral());
        assertEquals(first.editReply(), second.editReply());
        assertEquals(first.keepComponents(), second.keepComponents());
        assertEquals(first.keepSelections(), second.keepSelections());
        assertEquals(first.silent(), second.silent());
        assertEquals(first.allowedMentions(), second.allowedMentions());
    }

    @Test
    void classLevelConfig_withDifferentCustomMethod_ShouldUseMethodValues() {
        var config = getReplyConfig(ClassLevelConfig.class, "customValues");
        assertFalse(config.ephemeral());
        assertTrue(config.editReply());
        assertTrue(config.keepComponents());
        assertTrue(config.keepSelections());
        assertFalse(config.silent());
        assertFalse(config.allowedMentions().isEmpty());
    }

    @Interaction
    private static class MethodLevelConfig {

        @Button
        public void defaultValues(ComponentEvent event) {
        }

        @Button
        @ReplyConfig(
                ephemeral = true,
                editReply = false,
                keepComponents = false,
                keepSelections = false,
                silent = true,
                allowedMentions = { }
        )
        public void customValues(ComponentEvent event) {
        }
    }

    @Interaction
    @ReplyConfig(
            ephemeral = true,
            editReply = false,
            keepComponents = false,
            keepSelections = false,
            silent = true,
            allowedMentions = { }
    )
    private static class ClassLevelConfig {

        @Button
        public void defaultValues(ComponentEvent event) {
        }

        @Button
        @ReplyConfig(
                ephemeral = true,
                editReply = false,
                keepComponents = false,
                keepSelections = false,
                silent = true,
                allowedMentions = { }
        )
        public void sameValues(ComponentEvent event) {
        }

        @Button
        @ReplyConfig
        public void customValues(ComponentEvent event) {
        }
    }
}
