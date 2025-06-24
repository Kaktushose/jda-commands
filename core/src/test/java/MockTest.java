import com.github.kaktushose.jda.commands.annotations.interactions.*;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ModalEvent;
import io.github.kaktushose.jdac.testing.TestScenario;
import io.github.kaktushose.jdac.testing.reply.MessageEventReply;
import io.github.kaktushose.jdac.testing.reply.ModalEventReply;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class MockTest {

    private TestScenario scenario;

    @BeforeEach
    void init() {
        scenario = TestScenario.create(TestController.class);
    }

    @Test
    void test() {
        ModalEventReply modalReply = scenario.slash("modal test").invokeModal();

        MessageEventReply messageReply = modalReply.submit().input("a", "b", "c").complete();

        assertEquals("abc", messageReply.content());
    }

    @Interaction
    public static class TestController {

        @Command("modal test")
        public void onCommand(CommandEvent event) {
            event.replyModal("onModal");
        }

        @Modal("Modal Name")
        public void onModal(ModalEvent event, @TextInput("first") String first, @TextInput("second") String second, @TextInput("third") String third) {
            event.reply(first + second + third);
        }
    }
}
