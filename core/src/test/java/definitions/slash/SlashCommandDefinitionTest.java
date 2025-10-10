package definitions.slash;

import com.github.kaktushose.jda.commands.annotations.interactions.Command;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.definitions.interactions.command.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.dispatching.events.ReplyableEvent;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.exceptions.InvalidDeclarationException;
import net.dv8tion.jda.api.interactions.commands.Command.Type;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static definitions.TestHelpers.getBuildContext;
import static org.junit.jupiter.api.Assertions.*;

class SlashCommandDefinitionTest {

    @Test
    void method_withoutAnnotation_ShouldThrowNoSuchElementException() {
        assertThrows(NoSuchElementException.class, () -> build("noAnnotation"));
    }

    @Test
    void method_withoutArgs_shouldThrowInvalidDeclarationException() {
        assertThrows(InvalidDeclarationException.class, () -> build("noArgs"));
    }

    @Test
    void method_withoutCommandEvent_shouldThrowInvalidDeclarationException() {
        assertThrows(InvalidDeclarationException.class, () -> build("noCommandEvent"));
    }

    @Test
    void method_withCommandEventNotAtIndex0_shouldThrowInvalidDeclarationException() {
        assertThrows(InvalidDeclarationException.class, () -> build("commandEventWrongIndex"));
    }

    @Test
    void method_withWrongEvent_ShouldThrowInvalidDeclarationException() {
        assertThrows(InvalidDeclarationException.class, () -> build("wrongEvent"));
    }

    @Test
    void command_withBlankName_shouldThrowInvalidDeclarationException() {
        assertThrows(InvalidDeclarationException.class, () -> build("blankName"));
    }

    @Test
    void command_withSpacesInName_shouldBeTrimmed() {
        assertEquals("too many spaces", build("spaces").name());
    }

    @Test
    void command_withMoreThanThreeLabels_shouldThrowInvalidDeclarationException() {
        assertThrows(InvalidDeclarationException.class, () -> build("tooLong"));
    }

    @Test
    void command_withCorrectDeclaration_shouldBuildCorrectSlashCommandData() {
        SlashCommandData command = build("correct").toJDAEntity();

        assertEquals("correct", command.getName());
        assertEquals("description", command.getDescription());
        assertEquals(1, command.getOptions().size());
        assertEquals(Type.SLASH, command.getType());

    }

    @Test
    void command_withCorrectDeclaration_shouldBuildCorrectSubcommandData() {
        final String name = "sub";

        SubcommandData command = build("correct").toSubcommandData(name);

        assertEquals(name, command.getName());
        assertEquals("description", command.getDescription());
        assertEquals(1, command.getOptions().size());
    }

    private SlashCommandDefinition build(String method) {
        return SlashCommandDefinition.build(getBuildContext(TestController.class, method));
    }

    @Interaction
    private static class TestController {

        public void noAnnotation() {
        }

        @Command("noArgs")
        public void noArgs() {
        }

        @Command("noCommandEvent")
        public void noCommandEvent(int i) {
        }

        @Command("commandEventWrongIndex")
        public void commandEventWrongIndex(int i, CommandEvent event) {
        }

        @Command("wrongEvent")
        public void wrongEvent(ReplyableEvent<?> event) {
        }

        @Command
        public void blankName() {
        }

        @Command("too  many       spaces")
        public void spaces(CommandEvent event) {
        }

        @Command("This is too long")
        public void tooLong() {

        }

        @Command(value = "correct", desc = "description")
        public void correct(CommandEvent event, String option) {
        }
    }
}
