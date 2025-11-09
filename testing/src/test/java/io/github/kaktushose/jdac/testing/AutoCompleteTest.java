package io.github.kaktushose.jdac.testing;

import io.github.kaktushose.jdac.annotations.interactions.AutoComplete;
import io.github.kaktushose.jdac.annotations.interactions.Command;
import io.github.kaktushose.jdac.annotations.interactions.Interaction;
import io.github.kaktushose.jdac.dispatching.events.interactions.AutoCompleteEvent;
import io.github.kaktushose.jdac.dispatching.events.interactions.CommandEvent;
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
