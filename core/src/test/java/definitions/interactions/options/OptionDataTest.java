package definitions.interactions.options;

import io.github.kaktushose.jdac.annotations.interactions.Choices;
import io.github.kaktushose.jdac.annotations.interactions.Command;
import io.github.kaktushose.jdac.annotations.interactions.Interaction;
import io.github.kaktushose.jdac.annotations.interactions.Param;
import io.github.kaktushose.jdac.definitions.description.ParameterDescription;
import io.github.kaktushose.jdac.definitions.interactions.command.OptionDataDefinition;
import io.github.kaktushose.jdac.definitions.interactions.command.SlashCommandDefinition;
import io.github.kaktushose.jdac.dispatching.events.interactions.CommandEvent;
import io.github.kaktushose.jdac.dispatching.events.interactions.ComponentEvent;
import io.github.kaktushose.jdac.dispatching.validation.internal.Validators;
import io.github.kaktushose.jdac.exceptions.InvalidDeclarationException;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static definitions.TestHelpers.I18N;
import static definitions.TestHelpers.getBuildContext;
import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.*;

class OptionDataTest {

    private static final Map<Class<?>, OptionType> CLASS_TO_OPTION_TYPE = Map.ofEntries(
            entry(Boolean.class, OptionType.BOOLEAN),
            entry(Short.class, OptionType.INTEGER),
            entry(Integer.class, OptionType.INTEGER),
            entry(Long.class, OptionType.INTEGER),
            entry(Float.class, OptionType.NUMBER),
            entry(Double.class, OptionType.NUMBER),
            entry(User.class, OptionType.USER),
            entry(Member.class, OptionType.USER),
            entry(Role.class, OptionType.ROLE),
            entry(IMentionable.class, OptionType.MENTIONABLE),
            entry(GuildChannelUnion.class, OptionType.CHANNEL),
            entry(GuildChannel.class, OptionType.CHANNEL),
            entry(AudioChannel.class, OptionType.CHANNEL),
            entry(GuildMessageChannel.class, OptionType.CHANNEL),
            entry(NewsChannel.class, OptionType.CHANNEL),
            entry(StageChannel.class, OptionType.CHANNEL),
            entry(TextChannel.class, OptionType.CHANNEL),
            entry(ThreadChannel.class, OptionType.CHANNEL),
            entry(VoiceChannel.class, OptionType.CHANNEL),
            entry(Message.Attachment.class, OptionType.ATTACHMENT)
    );

    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_BOXED = Map.of(
            int.class, Integer.class,
            long.class, Long.class,
            char.class, Character.class,
            byte.class, Byte.class,
            double.class, Double.class,
            float.class, Float.class,
            boolean.class, Boolean.class
    );

    @Test
    void command_withEventAsOption_shouldThrowInvalidDeclarationException() {
        assertThrows(InvalidDeclarationException.class, () -> build("eventType"));
    }

    @Test
    void optionData_withoutAnnotation_shouldResolveNameReflectively() {
        assertEquals("reflective", optionData("notAnnotated").name());
    }

    @Test
    void optionData_withAnnotation_shouldUseAnnotatedName() {
        assertEquals("annotation", optionData("annotatedParameter").name());
    }

    @Test
    void optionData_withoutAnnotation_shouldUseFallbackAnnotation() {
        assertEquals("no description", optionData("notAnnotated").description());
    }

    @Test
    void optionData_withAnnotation_shouldUseAnnotatedDescription() {
        assertEquals("custom description", optionData("annotatedParameter").description());
    }

    @Test
    void optionData_withoutAnnotation_shouldNotBeOptional() {
        assertFalse(optionData("notAnnotated").optional());
    }

    @Test
    void optionData_withAnnotatedOptional_shouldBeOptional() {
        assertTrue(optionData("annotatedParameter").optional());
    }

    @Test
    void optionData_withWildcardOptional_shouldThrowInvalidDeclarationException() {
        assertThrows(InvalidDeclarationException.class, () -> build("wildCardOptional"));
    }

    @Test
    void optionData_withOptionalType_shouldBeOptionalAndUnwrapType() {
        OptionDataDefinition definition = optionData("optional");

        assertTrue(definition.optional());
        assertEquals(Optional.class, definition.declaredType());
        assertEquals(String.class, definition.resolvedType());
    }

    @Test
    void optionData_withNoExplicitType_shouldInferCorrectType() {
        Validators validators = new Validators(Map.of());
        ParameterDescription description = build("notAnnotated").methodDescription().parameters().getLast();

        for (Class<?> type : CLASS_TO_OPTION_TYPE.keySet()) {
            OptionDataDefinition build = OptionDataDefinition.build(modify(type, description), null, I18N, validators);
            assertEquals(CLASS_TO_OPTION_TYPE.get(type), build.optionType());
        }
    }

    @Test
    void optionData_withPrimitiveType_shouldBoxCorrectly() {
        Validators validators = new Validators(Map.of());
        ParameterDescription description = build("notAnnotated").methodDescription().parameters().getLast();

        for (Class<?> type : PRIMITIVE_TO_BOXED.keySet()) {
            OptionDataDefinition build = OptionDataDefinition.build(modify(type, description), null, I18N, validators);
            assertEquals(PRIMITIVE_TO_BOXED.get(type), build.resolvedType());
        }
    }

    @Test
    void optionData_withOptionTypeInAnnotation_shouldNotHaveDefaultType() {
        OptionDataDefinition definition = optionData("annotatedParameter");

        assertEquals(OptionType.INTEGER,  definition.optionType());
    }

    @Test
    void optionData_withChoices_shouldWork() {
        OptionDataDefinition definition = optionData("choices");

        assertEquals(2, definition.choices().size());
        assertEquals(new Choice("one", "one"), definition.choices().getFirst());
        assertEquals(new Choice("two", "two"), definition.choices().getLast());
    }

    @Test
    void optionData_withValueChoices_shouldWork() {
        OptionDataDefinition definition = optionData("valueChoices");

        assertEquals(2, definition.choices().size());
        assertEquals(new Choice("one", "1"), definition.choices().getFirst());
        assertEquals(new Choice("two", "2"), definition.choices().getLast());
    }

    @Test
    void name_withCamelCase_shouldBeConvertedToSnakeCase() {
        assertEquals("camel_case", optionData("camelCase").name());
    }

    private ParameterDescription modify(Class<?> type, ParameterDescription description) {
        return new ParameterDescription(type, description.typeArguments(), description.name(), description.annotations());
    }

    private OptionDataDefinition optionData(String method) {
        return build(method).commandOptions().getFirst();
    }

    private SlashCommandDefinition build(String method) {
        return SlashCommandDefinition.build(getBuildContext(TestController.class, method));
    }

    @Interaction
    private static class TestController {

        @Command("eventType")
        public void eventType(CommandEvent event, ComponentEvent componentEvent) {
        }

        @Command("notAnnotated")
        public void notAnnotated(CommandEvent event, String reflective) {
        }

        @Command("eventType")
        public void annotatedParameter(CommandEvent event, @Param(name = "annotation",
                value = "custom description", optional = true, type = OptionType.INTEGER) String name) {
        }

        @Command("wildCardOptional")
        public void wildCardOptional(CommandEvent event, Optional<?> optional) {
        }

        @Command("optional")
        public void optional(CommandEvent event, Optional<String> optional) {
        }

        @Command("camelCase")
        public void camelCase(CommandEvent event, String camelCase) {
        }

        @Command("choices")
        public void choices(CommandEvent event, @Choices({"one", "two"}) String argument) {
        }

        @Command("valueChoices")
        public void valueChoices(CommandEvent event, @Choices({"one:1", "two:2"}) String argument) {
        }
    }
}
