import com.github.kaktushose.jda.commands.annotations.interactions.Command;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import io.github.kaktushose.jdac.testing.TestScenario;
import io.github.kaktushose.jdac.testing.reply.MessageEventReply;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.Command.Type;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContextCommandTest {

    private static TestScenario scenario;

    @BeforeAll
    static void init() {
        scenario = TestScenario.create(TestController.class);
    }

    @Test
    void userContextCommandTest() {
        User target = mock(User.class);
        final String name = "Kaktushose";
        when(target.getName()).thenReturn(name);

        MessageEventReply reply = scenario.context("context user", target).invoke();

        assertEquals(name, reply.content());
    }

    @Test
    void messageContextCommandTest() {
        Message target = mock(Message.class);
        final String content = "Hello World!";
        when(target.getContentRaw()).thenReturn(content);

        MessageEventReply reply = scenario.context("context message", target).invoke();

        assertEquals(content, reply.content());
    }

    @Interaction
    public static class TestController {

        @Command(value = "context user", type = Type.USER)
        public void onUserContext(CommandEvent event, User user) {
            event.reply(user.getName());
        }

        @Command(value = "context message", type = Type.MESSAGE)
        public void onMessageContext(CommandEvent event, Message message) {
            event.reply(message.getContentRaw());
        }
    }
}
