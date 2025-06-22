import com.github.kaktushose.jda.commands.annotations.interactions.*;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent;
import framework.EventReply;
import framework.TestScenario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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
        EventReply reply = scenario.slash(COMMAND_NAME).invoke();
        System.out.println(reply.asCreateData().toData().toPrettyString());

        reply = reply.button("onButton").invoke();
        System.out.println(reply.asCreateData().toData().toPrettyString());

        reply = reply.stringSelect("onMenu").values("pizza").invoke();
        System.out.println(reply.asCreateData().toData().toPrettyString());
    }

    @Interaction
    public static class TestController {

        @Command(COMMAND_NAME)
        public void onCommand(CommandEvent event) {
            event.with().components("onButton").reply("Hello World!");
        }

        @Button(BUTTON_NAME)
        public void onButton(ComponentEvent event) {
            event.with().components("onMenu").reply("You pressed me!");
        }

        @MenuOption(label = "Pizza", value = "pizza")
        @MenuOption(label = "Hamburger", value = "hamburger")
        @MenuOption(label = "Sushi", value = "Sushi")
        @StringSelectMenu("What's your favourite food?")
        public void onMenu(ComponentEvent event, List<String> choices) {
            event.reply(choices.get(0));
        }
    }
}
