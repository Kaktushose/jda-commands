import com.github.kaktushose.jda.commands.annotations.interactions.Button;
import com.github.kaktushose.jda.commands.annotations.interactions.Command;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent;
import internal.TestScenario;
import internal.invocation.SlashCommandInvocation;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
class MockTest {

    private static final String COMMAND_NAME = "test";
    private static final String BUTTON_NAME = "Press me";
    private TestScenario scenario;

    @BeforeEach
    void init() {
        scenario = TestScenario.create(TestController.class);
    }

    @Test
    void test() {
        SlashCommandInvocation invocation = scenario.slash(COMMAND_NAME);
        CompletableFuture<MessageCreateData> reply = invocation.invoke();
        MessageCreateData message = reply.join();

        System.out.println(message.toData().toPrettyString());

        reply = scenario.button(message.getComponents().get(0).getActionComponents().get(0).getId()).invoke();

        System.out.println(reply.join().toData().toPrettyString());
    }

    @Interaction
    public static class TestController {

        @Command(COMMAND_NAME)
        public void onCommand(CommandEvent event) {
            event.with().components("onButton").reply("Hello World!");
        }

        @Button(BUTTON_NAME)
        public void onButton(ComponentEvent event) {
            event.with().reply("You pressed me!");
        }
    }
}
