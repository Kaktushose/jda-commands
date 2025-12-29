package definitions.interactions.component;

import io.github.kaktushose.jdac.annotations.interactions.*;
import io.github.kaktushose.jdac.definitions.interactions.CustomId;
import io.github.kaktushose.jdac.definitions.interactions.MethodBuildContext;
import io.github.kaktushose.jdac.definitions.interactions.component.menu.StringSelectMenuDefinition;
import io.github.kaktushose.jdac.definitions.interactions.component.menu.StringSelectMenuDefinition.MenuOptionDefinition;
import io.github.kaktushose.jdac.dispatching.events.interactions.ComponentEvent;
import io.github.kaktushose.jdac.exceptions.InvalidDeclarationException;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import static definitions.TestHelpers.getBuildContext;
import static org.junit.jupiter.api.Assertions.*;

class StringSelectMenuDefinitionTest {

    @Test
    void method_withoutAnnotation_shouldThrowNoSuchElementException() {
        assertThrows(NoSuchElementException.class, () -> build("noAnnotation"));
    }

    @Test
    void method_withWrongSignature_shouldThrowInvalidDeclarationException() {
        assertThrows(InvalidDeclarationException.class, () -> build("wrongSignature"));
    }

    @Test
    void singleMenuOption_shouldBuildCorrectly() {
        StringSelectMenuDefinition definition = build("singleOption");

        assertEquals("Choose one", definition.placeholder());
        assertEquals(1, definition.selectOptions().size());
        assertEquals(Set.of(new MenuOptionDefinition(
                "v1",
                "Label 1",
                "desc",
                Emoji.fromFormatted("👍"),
                false
        )), definition.selectOptions());

        var menu = definition.toJDAEntity(CustomId.independent(definition.definitionId(), 0));
        assertEquals(definition.minValue(), menu.getMinValues());
        assertEquals(definition.maxValue(), menu.getMaxValues());

        assertEquals(1, menu.getOptions().size());
        SelectOption option = menu.getOptions().getFirst();
        assertEquals("Label 1", option.getLabel());
        assertEquals("v1", option.getValue());
        assertEquals("desc", option.getDescription());
        assertEquals(Emoji.fromFormatted("👍"), option.getEmoji());
        assertFalse(option.isDefault());
    }

    @Test
    void containerMenuOptions_shouldBuildAllOptions() {
        StringSelectMenuDefinition definition = build("containerOptions");

        assertEquals(2, definition.selectOptions().size());
        assertEquals(definition.selectOptions().size(), definition.toJDAEntity(CustomId.independent(definition.definitionId(), 0)).getOptions().size());
    }

    @Test
    void with_shouldCreateOptionsAndApplyDefaults() {
        StringSelectMenuDefinition base = build("singleOption");

        SelectOption existingOption = SelectOption.of("Label 1", "v1");
        SelectOption newOption = SelectOption.of("Label 2", "v2");

        var overridden = base.with(Set.of(existingOption, newOption), List.of("v2"), "New placeholder", 2, 3, 1);

        assertEquals("New placeholder", overridden.placeholder());
        assertEquals(2, overridden.minValue());
        assertEquals(3, overridden.maxValue());
        assertEquals(1, overridden.uniqueId());

        Set<String> values = overridden.selectOptions().stream().map(MenuOptionDefinition::value).collect(Collectors.toSet());
        assertTrue(values.contains("v1"));
        assertTrue(values.contains("v2"));
    }

    @Test
    void displayName_shouldReturnFormattedPlaceholder() {
        StringSelectMenuDefinition def = build("singleOption");
        assertEquals("String Select Menu: Choose one", def.displayName());
    }

    private StringSelectMenuDefinition build(String method) {
        MethodBuildContext context = getBuildContext(TestController.class, method);
        return StringSelectMenuDefinition.build(context);
    }

    @Interaction
    @Permissions("ADMIN")
    private static class TestController {

        public void noAnnotation() {
        }

        @StringSelectMenu("placeholder")
        public void wrongSignature(ComponentEvent event) {
        }

        @StringSelectMenu("wrongList")
        public void wrongList(ComponentEvent event, List<Integer> values) {
        }

        @StringSelectMenu(value = "Choose one")
        @MenuOption(value = "v1", label = "Label 1", description = "desc", emoji = "👍")
        public void singleOption(ComponentEvent event, List<String> values) {
        }

        @StringSelectMenu("Container")
        @MenuOption(value = "a", label = "A")
        @MenuOption(value = "b", label = "B")
        public void containerOptions(ComponentEvent event, List<String> values) {
        }

        @StringSelectMenu("EmptyEmoji")
        @MenuOption(value = "e", label = "E")
        public void emptyEmojiOption(ComponentEvent event, List<String> values) {
        }
    }
}
