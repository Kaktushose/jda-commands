package reply;

import io.github.kaktushose.jdac.annotations.interactions.*;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition.ReplyConfig;
import io.github.kaktushose.jdac.dispatching.events.interactions.CommandEvent;
import io.github.kaktushose.jdac.dispatching.events.interactions.ComponentEvent;
import io.github.kaktushose.jdac.testing.TestScenario;
import io.github.kaktushose.jdac.testing.reply.MessageEventReply;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu.SelectTarget;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Mentions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ComponentsV1Test {

    private static TestScenario scenario;

    @BeforeAll
    static void init() {
        scenario = TestScenario.with(TestController.class)
                .replyConfig(new ReplyConfig())
                .create();
    }

    @Test
    void testSingleComponentBeingSent() {
        MessageEventReply reply = scenario.slash("test single").invoke();

        List<Component.Type> components = reply.components().stream().map(Component::getType).toList();

        assertEquals(1, components.size());
        assertTrue(components.contains(Component.Type.BUTTON));
    }

    @Test
    void testMultipleComponentsBeingSent() {
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

    @Test
    void testKeepComponents() {
        MessageEventReply reply = scenario.slash("test all").invoke();

        assertEquals(3, reply.components().size());
        reply = reply.button("button").invoke();

        assertEquals(3, reply.components().size());
        assertNotNull(reply.lastMessage());
    }

    @Test
    void testKeepSelections() {
        MessageEventReply reply = scenario.slash("test select").invoke();

        Mentions mentions = mock(Mentions.class);
        Member member = mock(Member.class);
        when(mentions.getMembers()).thenAnswer(_ -> List.of(member));
        when(mentions.getChannels()).thenReturn(List.of());
        when(mentions.getRoles()).thenReturn(List.of());
        var entitySelectMenu = reply.findEntitySelect("entitySelect").orElseThrow();
        reply = reply.entitySelect("entitySelect").mentions(mentions).uniqueId(entitySelectMenu.getUniqueId()).invoke();

        entitySelectMenu = reply.findEntitySelect("entitySelect").orElseThrow();

        assertEquals(1, entitySelectMenu.getDefaultValues().size());
        assertEquals(SelectTarget.USER, entitySelectMenu.getDefaultValues().getFirst().getType());
    }

    @Test
    void testKeepComponentsFalse() {
        MessageEventReply reply = scenario.slash("false").invoke();

        assertEquals(3, reply.components().size());
        reply = reply.button("falseButton").invoke();

        assertEquals(0, reply.components().size());
        assertNull(reply.lastMessage());
    }

    @Interaction
    public static class TestController {

        @Command("test single")
        public void testSingle(CommandEvent event) {
            event.with().components("button").reply();
        }

        @Command("test all")
        public void testAll(CommandEvent event) {
            event.with().components("button").components("stringSelect").components("entitySelect").reply();
        }

        @Command("test select")
        public void testSelect(CommandEvent event) {
            event.with().components("entitySelect").reply();
        }

        @Button("My Button")
        public void button(ComponentEvent event) {
            event.reply("success");
        }

        @MenuOption(label = "Sushi", value = "Sushi")
        @StringMenu("What's your favourite food?")
        public void stringSelect(ComponentEvent event, List<String> selection) {
            event.reply("success");
        }

        @EntityMenu(value = SelectTarget.USER, placeholder = "Select one")
        public void entitySelect(ComponentEvent event, Mentions mentions) {
            event.reply("success");
        }

        @Command("false")
        public void keepAndEditFalse(CommandEvent event) {
            event.with().components("falseButton").components("stringSelect").components("entitySelect").reply();
        }

        @Button("My Button")
        public void falseButton(ComponentEvent event) {
            event.with().keepComponents(false).editReply(false).reply("success");
        }

        @Command("duplicate")
        public void duplicate(CommandEvent event) {
            event.with().components("button").components("button").reply();
        }

    }
}
