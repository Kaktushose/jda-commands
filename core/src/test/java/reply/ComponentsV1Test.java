package reply;

import io.github.kaktushose.jdac.annotations.interactions.*;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition.ReplyConfig;
import io.github.kaktushose.jdac.dispatching.events.interactions.CommandEvent;
import io.github.kaktushose.jdac.dispatching.events.interactions.ComponentEvent;
import io.github.kaktushose.jdac.testing.TestScenario;
import io.github.kaktushose.jdac.testing.reply.MessageEventReply;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu.SelectTarget;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComponentsV1Test {

    private static TestScenario scenario;

    @BeforeAll
    static void init() {
        scenario = TestScenario.with(TestController.class)
                .replyConfig(new ReplyConfig())
                .create();
    }

    @Test
    void testComponentsBeingSent() {
        MessageEventReply reply = scenario.slash("test all").invoke();

        List<Component.Type> components = reply.components().stream().map(Component::getType).toList();

        assertEquals(3, components.size());
        assertTrue(components.contains(Component.Type.BUTTON));
        assertTrue(components.contains(Component.Type.STRING_SELECT));
        assertTrue(components.contains(Component.Type.USER_SELECT));
    }

    @Test
    void testComponentInvocation() {
        MessageEventReply reply = scenario.button("button").invoke();
        assertEquals("success", reply.content());

        reply = scenario.stringSelect("stringSelect").invoke();
        assertEquals("success", reply.content());

        reply = scenario.entitySelect("entitySelect").invoke();
        assertEquals("success", reply.content());
    }

    @Interaction
    public static class TestController {

        @Command("test all")
        public void testAll(CommandEvent event) {
            event.with().components("button").components("stringSelect").components("entitySelect").reply();
        }

        @Button("My Button")
        public void button(ComponentEvent event) {
            event.reply("success");
        }

        @MenuOption(label = "Sushi", value = "Sushi")
        @StringSelectMenu("What's your favourite food?")
        public void stringSelect(ComponentEvent event, List<String> selection) {
            event.reply("success");
        }

        @EntitySelectMenu(value = SelectTarget.USER, placeholder = "Select one")
        public void entitySelect(ComponentEvent event, Mentions mentions) {
            event.reply("success");
        }
    }
}
