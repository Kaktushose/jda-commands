package io.github.kaktushose.jdac.testing;

import com.github.kaktushose.jda.commands.annotations.interactions.AutoComplete;
import com.github.kaktushose.jda.commands.annotations.interactions.Command;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.AutoCompleteEvent;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AutoCompleteTest {

    private static TestScenario scenario;

    @BeforeAll
    static void init() {
        scenario = TestScenario.create(TestController.class);
    }

    @Test
    void autoCompleteTest() {
        List<Choice> choices = scenario.autoComplete("command", "complete").invoke();

        assertEquals("value", choices.getFirst().getAsString());
    }

    @Interaction
    public static class TestController {

        @AutoComplete("command")
        public void onAutoComplete(AutoCompleteEvent event) {
            event.replyChoice("name", "value");
        }

        @Command("command")
        public void onCommand(CommandEvent event, String complete) {
        }
    }
}
