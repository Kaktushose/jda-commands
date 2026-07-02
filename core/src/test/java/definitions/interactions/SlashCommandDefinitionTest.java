package definitions.interactions;

import io.github.kaktushose.jdac.annotations.interactions.Command;
import io.github.kaktushose.jdac.annotations.interactions.Interaction;
import io.github.kaktushose.jdac.definitions.description.Descriptor;
import io.github.kaktushose.jdac.definitions.interactions.InteractionRegistry;
import io.github.kaktushose.jdac.definitions.interactions.command.SlashCommandDefinition;
import io.github.kaktushose.jdac.dispatching.events.ReplyableEvent;
import io.github.kaktushose.jdac.dispatching.events.interactions.CommandEvent;
import io.github.kaktushose.jdac.exceptions.InvalidDeclarationException;
import io.github.kaktushose.jdac.message.i18n.internal.BundleFinder;
import io.github.kaktushose.jdac.message.i18n.internal.JDACLocalizationFunction;
import io.github.kaktushose.jdac.message.resolver.MessageResolver;
import io.github.kaktushose.jdac.property.Definitions;
import io.github.kaktushose.jdac.property.JDACProperty;
import io.github.kaktushose.jdac.property.JDACScope;
import io.github.kaktushose.jdac.property.internal.JDACInternalProperties;
import io.github.kaktushose.jdac.property.internal.JDACIntrospectionImpl;
import net.dv8tion.jda.api.interactions.commands.Command.Type;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import definitions.TestHelpers;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.junit.jupiter.api.Test;

import java.util.List;
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

    @Test
    public void test() {
        JDACIntrospectionImpl jdacIntrospection = JDACIntrospectionImpl
                .create(JDACScope.INITIALIZED)
                .addFallback(JDACInternalProperties.BUNDLE_FINDER, _ -> new BundleFinder(Descriptor.REFLECTIVE))
                .addFallback(JDACProperty.DEFINITIONS, _ -> new InteractionRegistry(null, null, null))
                .addFallback(JDACProperty.MESSAGE_RESOLVER, _ -> new MessageResolver(List.of()))
                .build();
        jdacIntrospection.scoped().run(() -> {
            SlashCommandData command = build("noDescription").toJDAEntity();
            command.setLocalizationFunction(JDACLocalizationFunction.PROVIDER_FUNC.apply(jdacIntrospection));

            DataObject data = command.toData();

            assertEquals("no description", data.getString("description"));
            assertTrue(data.getObject("description_localizations").keys().isEmpty());
        });
    }

    private SlashCommandDefinition build(String method) {
        return ScopedValue.where(JDACIntrospectionImpl.INTROSPECTION, TestHelpers.INTROSPECTION).call(() ->
                SlashCommandDefinition.build(getBuildContext(TestController.class, method))
        );
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

        @Command(value = "nodescription")
        public void noDescription(CommandEvent event) {
        }

    }
}
