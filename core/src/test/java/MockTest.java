import com.github.kaktushose.jda.commands.annotations.interactions.Command;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.annotations.interactions.Param;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import internal.TestScenario;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class MockTest {

    private static final String COMMAND_NAME = "test";
    private static final String REPLY = "Hello World";
    private TestScenario scenario;

    @BeforeEach
    void init() {
        scenario = TestScenario.create(TestController.class);
    }

    @Test
    void test() {
        OptionData expected = new OptionData(
                OptionType.STRING,
                "message",
                "empty description",
                false
        );
        OptionData actual = scenario.command(COMMAND_NAME).get().getOptions().get(0);

        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.isRequired(), actual.isRequired());

        CompletableFuture<MessageCreateData> reply = scenario.slash(COMMAND_NAME).invoke();
        assertEquals("%s %s".formatted(REPLY, null), reply.join().getContent());
    }

    @Interaction
    public static class TestController {

        @Command(COMMAND_NAME)
        public void onCommand(CommandEvent event, @Param(optional = true) String message) {
            event.reply("%s %s".formatted(REPLY, message));
        }
    }
}
