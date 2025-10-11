package definitions.interactions.component;

import com.github.kaktushose.jda.commands.annotations.interactions.*;
import com.github.kaktushose.jda.commands.definitions.interactions.MethodBuildContext;
import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.StringSelectMenuDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.StringSelectMenuDefinition.MenuOptionDefinition;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent;
import com.github.kaktushose.jda.commands.exceptions.InvalidDeclarationException;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import static definitions.TestHelpers.getBuildContext;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link StringSelectMenuDefinition}.
 */
class StringSelectMenuDefinitionTest {

    // -------------------------
    // error cases
    // -------------------------

    @Test
    @DisplayName("build() without @StringSelectMenu should throw NoSuchElementException")
    void method_withoutAnnotation_shouldThrowNoSuchElementException() {
        assertThrows(NoSuchElementException.class, () -> build("noAnnotation"));
    }

    @Test
    @DisplayName("build() with wrong signature should throw InvalidDeclarationException")
    void method_withWrongSignature_shouldThrowInvalidDeclarationException() {
        assertThrows(InvalidDeclarationException.class, () -> build("wrongSignature"));
    }

    // -------------------------
    // build + basic properties
    // -------------------------

    @Test
    @DisplayName("single @MenuOption should be collected and parsed correctly")
    void singleMenuOption_shouldBuildCorrectly() {
        StringSelectMenuDefinition def = build("singleOption");

        assertEquals("Choose one", def.placeholder());
        assertEquals(1, def.selectOptions().size());

        MenuOptionDefinition opt = def.selectOptions().iterator().next();
        assertEquals("v1", opt.value());
        assertEquals("Label 1", opt.label());
        assertEquals("desc", opt.description());
        assertEquals(Emoji.fromFormatted("😀"), opt.emoji());
        assertFalse(opt.isDefault());

        // toJDAEntity -> check placeholder, min/max and options converted
        var menu = def.toJDAEntity();
        assertEquals(def.placeholder(), menu.getPlaceholder());
        assertEquals(def.minValue(), menu.getMinValues());
        assertEquals(def.maxValue(), menu.getMaxValues());
        assertEquals(1, menu.getOptions().size());

        var jOpt = menu.getOptions().get(0);
        assertEquals(opt.value(), jOpt.getValue());
        assertEquals(opt.label(), jOpt.getLabel());
        assertEquals(opt.description(), jOpt.getDescription());
        assertEquals(opt.isDefault(), jOpt.isDefault());
        // emoji equality: may be null or equal
        if (opt.emoji() == null) {
            assertNull(jOpt.getEmoji());
        } else {
            assertEquals(opt.emoji(), jOpt.getEmoji());
        }
    }

    @Test
    @DisplayName("multiple options via @MenuOptionContainer should be added")
    void containerMenuOptions_shouldBuildAllOptions() {
        StringSelectMenuDefinition def = build("containerOptions");

        assertTrue(def.selectOptions().size() >= 2);

        Set<String> values = def.selectOptions().stream().map(MenuOptionDefinition::value).collect(Collectors.toSet());
        assertTrue(values.contains("a"));
        assertTrue(values.contains("b"));

        var menu = def.toJDAEntity();
        assertEquals(def.selectOptions().size(), menu.getOptions().size());
    }

    @Test
    @DisplayName("empty emoji string should result in null emoji in MenuOptionDefinition")
    void emptyEmojiString_shouldYieldNullEmoji() {
        StringSelectMenuDefinition def = build("emptyEmojiOption");
        assertEquals(1, def.selectOptions().size());
        MenuOptionDefinition opt = def.selectOptions().iterator().next();
        assertNull(opt.emoji());
    }

    // -------------------------
    // with(...) and createOptions behavior
    // -------------------------

    @Test
    @DisplayName("with() should accept runtime SelectOption set and mark defaults based on defaultValues")
    void with_shouldCreateOptionsAndApplyDefaults() {
        StringSelectMenuDefinition base = build("singleOption");

        // create runtime SelectOptions - one of them matches existing value v1, another new one vX
        SelectOption runtime1 = SelectOption.of("Label 1", "v1"); // existing
        SelectOption runtime2 = SelectOption.of("Label X", "vX");

        Set<SelectOption> runtimeSet = Set.of(runtime1, runtime2);
        // mark vX as default via defaultValues
        var newDef = base.with(runtimeSet, List.of("vX"), "New placeholder", 2, 3);

        // placeholder and min/max updated
        assertEquals("New placeholder", newDef.placeholder());
        assertEquals(2, newDef.minValue());
        assertEquals(3, newDef.maxValue());

        // new options include both v1 and vX
        Set<String> values = newDef.selectOptions().stream().map(MenuOptionDefinition::value).collect(Collectors.toSet());
        assertTrue(values.contains("v1"));
        assertTrue(values.contains("vX"));

        // check default marking: vX should be default, v1 not (unless original was default)
        MenuOptionDefinition vX = newDef.selectOptions().stream().filter(o -> o.value().equals("vX")).findFirst().orElseThrow();
        assertTrue(vX.isDefault());

        MenuOptionDefinition v1 = newDef.selectOptions().stream().filter(o -> o.value().equals("v1")).findFirst().orElseThrow();
        assertFalse(v1.isDefault());
    }

    // -------------------------
    // displayName + permissions
    // -------------------------

    @Test
    @DisplayName("displayName() returns formatted placeholder")
    void displayName_shouldReturnFormattedPlaceholder() {
        StringSelectMenuDefinition def = build("singleOption");
        assertEquals("Select Menu: Choose one", def.displayName());
    }

    @Nested
    @DisplayName("permissions merge behavior")
    class PermissionsTests {

        @Test
        @DisplayName("class-level permissions only")
        void classOnlyPermissions() {
            StringSelectMenuDefinition def = build("classOnlyPermission");
            assertEquals(Set.of("ADMIN"), Set.copyOf(def.permissions()));
        }

        @Test
        @DisplayName("class + method merge")
        void mergedPermissions() {
            StringSelectMenuDefinition def = build("mergedPermission");
            var perms = Set.copyOf(def.permissions());
            assertTrue(perms.contains("ADMIN"));
            assertTrue(perms.contains("MOD"));
            assertEquals(2, perms.size());
        }
    }

    // -------------------------
    // helpers
    // -------------------------

    private StringSelectMenuDefinition build(String method) {
        MethodBuildContext context = getBuildContext(TestController.class, method);
        return StringSelectMenuDefinition.build(context);
    }

    // -------------------------
    // Test controller
    // -------------------------

    @Interaction
    @Permissions("ADMIN")
    private static class TestController {

        public void noAnnotation() {
        }

        @StringSelectMenu("placeholder")
        public void wrongSignature(ComponentEvent event) {
            // missing List parameter -> should trigger InvalidDeclarationException
        }

        @StringSelectMenu(value = "Choose one", minValue = 1, maxValue = 1)
        @MenuOption(value = "v1", label = "Label 1", description = "desc", emoji = "😀", isDefault = false)
        public void singleOption(ComponentEvent event, List<String> values) {
        }

        @StringSelectMenu("Container")
        @MenuOptionContainer({
                @MenuOption(value = "a", label = "A"),
                @MenuOption(value = "b", label = "B")
        })
        public void containerOptions(ComponentEvent event, List<String> values) {
        }

        @StringSelectMenu("EmptyEmoji")
        @MenuOption(value = "e", label = "E", emoji = "")
        public void emptyEmojiOption(ComponentEvent event, List<String> values) {
        }

        @StringSelectMenu("perm")
        public void classOnlyPermission(ComponentEvent event, List<String> values) {
        }

        @StringSelectMenu("permMerged")
        @Permissions("MOD")
        public void mergedPermission(ComponentEvent event, List<String> values) {
        }
    }
}
