package definitions.interactions;

import io.github.kaktushose.jdac.annotations.interactions.Command;
import io.github.kaktushose.jdac.annotations.interactions.CommandConfig;
import io.github.kaktushose.jdac.annotations.interactions.Interaction;
import io.github.kaktushose.jdac.definitions.interactions.MethodBuildContext;
import io.github.kaktushose.jdac.definitions.interactions.command.ContextCommandDefinition;
import io.github.kaktushose.jdac.dispatching.events.interactions.CommandEvent;
import io.github.kaktushose.jdac.exceptions.InvalidDeclarationException;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.Command.Type;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static definitions.TestHelpers.getBuildContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ContextCommandDefinitionTest {

    @Test
    void method_withoutAnnotation_shouldThrowNoSuchElementException() {
        assertThrows(NoSuchElementException.class, () -> build("noAnnotation"));
    }

    @Test
    void method_withInvalidType_shouldThrowInvalidDeclarationException() {
        assertThrows(InvalidDeclarationException.class, () -> build("invalidType"));
    }

    @Test
    void message_withWrongSignature_shouldThrowInvalidDeclarationException() {
        assertThrows(InvalidDeclarationException.class, () -> build("messageWrongSig"));
    }

    @Test
    void user_withWrongSignature_shouldThrowInvalidDeclarationException() {
        assertThrows(InvalidDeclarationException.class, () -> build("userWrongSig"));
    }

    @Test
    void member_withWrongContextType_shouldThrowInvalidDeclarationException() {
        assertThrows(InvalidDeclarationException.class, () -> build("userWithMemberWrongContext"));
    }

    @Test
    void message_withCorrectSignature_shouldBuild() {
        ContextCommandDefinition definition = build("messageCorrect");

        var commandData = definition.toJDAEntity(0);

        assertEquals("message", commandData.getName());
        assertEquals(Type.MESSAGE, commandData.getType());
    }

    @Test
    void user_withMemberParam_shouldBuild() {
        ContextCommandDefinition definition = build("userWithMember");

        var commandData = definition.toJDAEntity(0);

        assertEquals("user", commandData.getName());
        assertEquals(Type.USER, commandData.getType());
    }

    private ContextCommandDefinition build(String method) {
        MethodBuildContext context = getBuildContext(TestController.class, method);
        return ContextCommandDefinition.build(context);
    }

    @Interaction
    private static class TestController {

        public void noAnnotation() {
        }

        @Command("invalid")
        public void invalidType() {
        }

        @Command(value = "message", type = Type.MESSAGE)
        public void messageWrongSig(CommandEvent event) {
        }

        @Command(value = "message", type = Type.MESSAGE)
        public void messageCorrect(CommandEvent event, Message message) {
        }

        @Command(value = "user", type = Type.USER)
        public void userWrongSig(CommandEvent event) {
        }

        @Command(value = "user", type = Type.USER)
        @CommandConfig(context = InteractionContextType.BOT_DM)
        public void userWithMemberWrongContext(CommandEvent event, Member member) {
        }

        @Command(value = "user", type = Type.USER)
        public void userWithMember(CommandEvent event, Member member) {
        }
    }
}
