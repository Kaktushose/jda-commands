package definitions.interactions;

import com.github.kaktushose.jda.commands.annotations.interactions.AutoComplete;
import com.github.kaktushose.jda.commands.annotations.interactions.Command;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.definitions.interactions.AutoCompleteDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.MethodBuildContext;
import com.github.kaktushose.jda.commands.definitions.interactions.command.OptionDataDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.AutoCompleteEvent;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.exceptions.InvalidDeclarationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static definitions.TestHelpers.getBuildContext;

public class AutoCompleteDefinitionTest {

    @Test
    void testOnlyCommand_shouldWork(){
        AutoCompleteDefinition definition = Assertions.assertDoesNotThrow(() -> build("wholeCommand"));
        List<AutoCompleteDefinition.AutoCompleteRule> rules =  new ArrayList<>(definition.rules());
        Assertions.assertEquals("command", rules.getFirst().command());
        Assertions.assertEquals(Set.of(), rules.getFirst().options());
    }

    @Test
    void testCommandWithSubCommand_shouldWork(){
        AutoCompleteDefinition definition =Assertions.assertDoesNotThrow(() -> build("subCommand"));

        List<AutoCompleteDefinition.AutoCompleteRule> rules =  new ArrayList<>(definition.rules());
        Assertions.assertEquals("command sub", rules.getFirst().command());
        Assertions.assertEquals(Set.of(), rules.getFirst().options());
    }

    @Test
    void testWithOption_shouldWork(){
        AutoCompleteDefinition definition =Assertions.assertDoesNotThrow(() -> build("option"));

        List<AutoCompleteDefinition.AutoCompleteRule> rules =  new ArrayList<>(definition.rules());
        Assertions.assertEquals("command", rules.getFirst().command());
        Assertions.assertEquals(Set.of("name"), rules.getFirst().options());
    }

    @Test
    void testWithWrongEvent_shouldThrow(){
        Assertions.assertThrows(InvalidDeclarationException.class, () -> build("wrongEvent"));
    }

    @Test
    void testWithNoEvent_shouldThrow(){
        Assertions.assertThrows(InvalidDeclarationException.class, () -> build("noEvent"));
    }

    @Test
    void testWithoutOption_shouldBeRegisteredForAllOptions() {
        AutoCompleteDefinition definition = buildAutoComplete("someCommandAutoComplete", TestControllerWorking.class);
        SlashCommandDefinition slash = buildSlash("someCommand");
        for (OptionDataDefinition option : slash.commandOptions()) {
            Assertions.assertNotNull(option.autoComplete());
            Assertions.assertEquals(definition.rules(), option.autoComplete().rules());
        }
    }

    @Test
    void testWithoutOption_shouldBeRegisteredForFirstOption() {
        AutoCompleteDefinition definition = buildAutoComplete("someOtherCommandAutoComplete", TestControllerWorking.class);
        SlashCommandDefinition slash = buildSlash("someOtherCommand");
        var list = new ArrayList<>(slash.commandOptions());

        Assertions.assertNotNull(list.getFirst().autoComplete());
        Assertions.assertEquals(definition.rules(), list.getFirst().autoComplete().rules());

        Assertions.assertNull(list.getLast().autoComplete());
    }

    private AutoCompleteDefinition build(String method) {
        return buildAutoComplete(method, TestController.class);
    }

    private AutoCompleteDefinition buildAutoComplete(String method, Class<?> klass) {
        MethodBuildContext context = getBuildContext(klass, method);
        return AutoCompleteDefinition.build(context.clazz(), context.method());
    }

    private SlashCommandDefinition buildSlash(String method) {
        return SlashCommandDefinition.build(getBuildContext(TestControllerWorking.class, method));
    }

    @Interaction
    private static class TestController {

        @AutoComplete("command")
        public void wholeCommand(AutoCompleteEvent event) {
        }

        @AutoComplete("command sub")
        public void subCommand(AutoCompleteEvent event) {
        }

        @AutoComplete(value = "command", options = "name")
        public void option(AutoCompleteEvent event) {
        }

        @AutoComplete("command sub1")
        public void wrongEvent(CommandEvent event) {
        }

        @AutoComplete("command sub2")
        public void noEvent() {
        }
    }

    @Interaction
    private static class TestControllerWorking {

        @Command("some_command")
        public void someCommand(CommandEvent event, String arg1, String arg2) {
        }

        @AutoComplete("someCommand")
        public void someCommandAutoComplete(AutoCompleteEvent event) {
        }

        @Command("some_other_command")
        public void someOtherCommand(CommandEvent event, String arg1, String arg2) {
        }

        @AutoComplete(value = "someOtherCommand", options = "arg1")
        public void someOtherCommandAutoComplete(AutoCompleteEvent event) {
        }
    }
}
