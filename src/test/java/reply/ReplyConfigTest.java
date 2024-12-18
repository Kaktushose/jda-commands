package reply;

import com.github.kaktushose.jda.commands.Helpers;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent;
import com.github.kaktushose.jda.commands.dispatching.reply.GlobalReplyConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ReplyConfigTest {

    private static Class<?> defaultController;
    private static Class<?> customController;

    @BeforeAll
    public static void setup() {
        defaultController = DefaultController.class;
        customController = CustomController.class;
    }

    @Test
    public void defaultController_withDefaultMethod_ShouldUseGlobalValues() throws NoSuchMethodException {
        var config = Helpers.replyConfig(defaultController.getDeclaredMethod("defaultValues", ComponentEvent.class));

        assertEquals(config.ephemeral(), GlobalReplyConfig.ephemeral);
        assertEquals(config.editReply(), GlobalReplyConfig.editReply);
        assertEquals(config.keepComponents(), GlobalReplyConfig.keepComponents);
    }

    @Test
    public void defaultController_withCustomMethod_ShouldUseMethodValues() throws NoSuchMethodException {
        var config = Helpers.replyConfig(defaultController.getDeclaredMethod("customValues", ComponentEvent.class));

        assertTrue(config.ephemeral());
        assertFalse(config.editReply());
        assertFalse(config.keepComponents());
    }

    @Test
    public void customController_withDefaultMethod_ShouldUseControllerValues() throws NoSuchMethodException {
        var config = Helpers.replyConfig(customController.getDeclaredMethod("defaultValues", ComponentEvent.class));

        assertTrue(config.ephemeral());
        assertFalse(config.editReply());
        assertFalse(config.keepComponents());
    }

    @Test
    public void customController_withSameCustomMethod_ShouldBeEquals() throws NoSuchMethodException {
        var first = Helpers.replyConfig(customController.getDeclaredMethod("defaultValues", ComponentEvent.class));
        var second = Helpers.replyConfig(customController.getDeclaredMethod("sameValues", ComponentEvent.class));

        assertEquals(first.ephemeral(), second.ephemeral());
        assertEquals(first.editReply(), second.editReply());
        assertEquals(first.keepComponents(), second.keepComponents());
    }

    @Test
    public void customController_withDifferentCustomMethod_ShouldUseMethodValues() throws NoSuchMethodException {
        var config = Helpers.replyConfig(customController.getDeclaredMethod("customValues", ComponentEvent.class));

        assertFalse(config.ephemeral());
        assertTrue(config.editReply());
        assertTrue(config.keepComponents());
    }

}
