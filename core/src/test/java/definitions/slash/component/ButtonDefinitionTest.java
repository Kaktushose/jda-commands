package definitions.slash.component;

import com.github.kaktushose.jda.commands.annotations.interactions.Button;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.annotations.interactions.Permissions;
import com.github.kaktushose.jda.commands.definitions.interactions.CustomId;
import com.github.kaktushose.jda.commands.definitions.interactions.MethodBuildContext;
import com.github.kaktushose.jda.commands.definitions.interactions.component.ButtonDefinition;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent;
import com.github.kaktushose.jda.commands.exceptions.InvalidDeclarationException;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Set;

import static definitions.TestHelpers.getBuildContext;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Clean, comprehensive tests for {@link ButtonDefinition}.
 *
 * Note: we intentionally don't import net.dv8tion.jda.api.interactions.components.buttons.Button
 * to avoid a name clash with the {@code @Button} annotation. We use `var` for JDA Button instances.
 */
class ButtonDefinitionTest {

    // -------------------------
    // error cases
    // -------------------------

    @Test
    @DisplayName("build() without @Button should throw NoSuchElementException")
    void method_withoutAnnotation_shouldThrowNoSuchElementException() {
        assertThrows(NoSuchElementException.class, () -> build("noAnnotation"));
    }

    @Test
    @DisplayName("build() with no parameters should throw InvalidDeclarationException")
    void method_withoutArgs_shouldThrowInvalidDeclarationException() {
        assertThrows(InvalidDeclarationException.class, () -> build("noArgs"));
    }

    @Test
    @DisplayName("build() with wrong parameter type should throw InvalidDeclarationException")
    void method_withWrongEvent_shouldThrowInvalidDeclarationException() {
        assertThrows(InvalidDeclarationException.class, () -> build("wrongEvent"));
    }

    // -------------------------
    // basic property cases
    // -------------------------

    @Test
    @DisplayName("empty label should be preserved and displayName should fall back to definitionId")
    void button_withEmptyLabel_and_displayNameFallback() {
        ButtonDefinition def = build("emptyLabel");

        assertEquals("", def.label(), "label should be exactly the annotation value (empty string)");
        String display = def.displayName();
        assertNotNull(display);
        assertFalse(display.isEmpty(), "displayName must not be empty");
        assertNotEquals(def.label(), display, "when label is empty displayName must not equal the label");
    }

    @Test
    @DisplayName("label should be stored")
    void button_withLabel_shouldStoreLabel() {
        ButtonDefinition def = build("labeledButton");
        assertEquals("Press", def.label());
        assertEquals("Press", def.displayName(), "displayName should return label when present");
    }

    @Test
    @DisplayName("emoji string empty => emoji == null")
    void button_withEmptyEmoji_shouldResultInNullEmoji() {
        ButtonDefinition def = build("noEmoji");
        assertNull(def.emoji());
    }

    @Test
    @DisplayName("emoji string present => parsed Emoji")
    void button_withEmoji_shouldParseEmojiCorrectly() {
        ButtonDefinition def = build("withEmoji");
        assertNotNull(def.emoji());
        assertEquals(Emoji.fromFormatted("😀"), def.emoji());
    }

    @Test
    @DisplayName("link present => toJDAEntity() returns a link button (url present) with style LINK")
    void button_withLink_shouldUseLinkInsteadOfCustomId() {
        ButtonDefinition def = build("withLink");
        var button = def.toJDAEntity();

        // JDA Button returns nullable URL / id / emoji in your setup, so check for null
        assertNotNull(button.getUrl(), "button should expose a URL when link is provided");
        assertEquals("https://example.com", button.getUrl());
        assertEquals(ButtonStyle.LINK, button.getStyle());
        assertEquals(def.label(), button.getLabel());
    }

    @Test
    @DisplayName("no link => toJDAEntity() uses CustomId.independent(definitionId()) and sets id")
    void button_withoutLink_shouldUseCustomId() {
        ButtonDefinition def = build("normalButton");
        var button = def.toJDAEntity();

        assertNotNull(button.getId(), "button should have an id when no link is present");
        assertEquals(def.label(), button.getLabel());
        assertNull(button.getEmoji(), "normalButton has no emoji in test controller");
    }

    @Test
    @DisplayName("toJDAEntity(customId) should use the provided custom id merged value")
    void button_withCustomId_shouldUseGivenId() {
        ButtonDefinition def = build("normalButton");
        CustomId customId = CustomId.independent("test-id-123");
        var button = def.toJDAEntity(customId);

        assertNotNull(button.getId());
        assertEquals(customId.merged(), button.getId());
    }

    // -------------------------
    // permissions
    // -------------------------

    @Nested
    @DisplayName("permissions merge behavior")
    class PermissionsTests {

        @Test
        @DisplayName("class-level permissions only")
        void classOnlyPermissions() {
            ButtonDefinition def = build("classOnlyPermissionButton");
            Set<String> perms = Set.copyOf(def.permissions());
            assertEquals(Set.of("ADMIN"), perms);
        }

        @Test
        @DisplayName("class + method merge")
        void mergedPermissions() {
            ButtonDefinition def = build("mergedPermissionButton");
            Set<String> perms = Set.copyOf(def.permissions());
            assertTrue(perms.contains("ADMIN"));
            assertTrue(perms.contains("MOD"));
            assertEquals(2, perms.size());
        }
    }

    // -------------------------
    // override behaviour
    // -------------------------

    @Test
    @DisplayName("with() should override non-null values and keep others")
    void button_withOverrides_shouldApplyOverridesCorrectly() {
        ButtonDefinition base = build("normalButton");

        ButtonDefinition override = base.with(
                "New Label", Emoji.fromFormatted("🔥"), "https://new.link", ButtonStyle.DANGER
        );

        assertEquals("New Label", override.label());
        assertEquals(Emoji.fromFormatted("🔥"), override.emoji());
        assertEquals("https://new.link", override.link());
        assertEquals(ButtonStyle.DANGER, override.style());

        // null overrides should keep previous values
        ButtonDefinition keep = override.with(null, null, null, null);
        assertEquals("New Label", keep.label());
        assertEquals(Emoji.fromFormatted("🔥"), keep.emoji());
        assertEquals("https://new.link", keep.link());
        assertEquals(ButtonStyle.DANGER, keep.style());
    }

    // -------------------------
    // helpers
    // -------------------------

    private ButtonDefinition build(String method) {
        MethodBuildContext context = getBuildContext(TestController.class, method);
        return ButtonDefinition.build(context);
    }

    // -------------------------
    // test controller
    // -------------------------

    @Interaction
    @Permissions("ADMIN") // class-level permission (your annotation must allow TYPE and METHOD)
    private static class TestController {

        public void noAnnotation() {
        }

        @Button("noArgs")
        public void noArgs() {
        }

        @Button("wrongEvent")
        public void wrongEvent(String notAnEvent) {
        }

        @Button("") // empty label
        public void emptyLabel(ComponentEvent event) {
        }

        @Button("Press")
        public void labeledButton(ComponentEvent event) {
        }

        @Button(value = "withEmoji", emoji = "😀")
        public void withEmoji(ComponentEvent event) {
        }

        @Button(value = "noEmoji", emoji = "")
        public void noEmoji(ComponentEvent event) {
        }

        @Button(value = "withLink", style = ButtonStyle.LINK, link = "https://example.com")
        public void withLink(ComponentEvent event) {
        }

        @Button(value = "normalButton", style = ButtonStyle.PRIMARY)
        public void normalButton(ComponentEvent event) {
        }

        @Button("classOnlyPermissionButton")
        public void classOnlyPermissionButton(ComponentEvent event) {
        }

        @Button("methodOnlyPermissionButton")
        @Permissions("USER")
        public void methodOnlyPermissionButton(ComponentEvent event) {
        }

        @Button("mergedPermissionButton")
        @Permissions("MOD")
        public void mergedPermissionButton(ComponentEvent event) {
        }

        @Button("noPermissionsButton")
        public void noPermissionsButton(ComponentEvent event) {
        }
    }
}
