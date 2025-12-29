package definitions.interactions.component;

import io.github.kaktushose.jdac.annotations.interactions.Button;
import io.github.kaktushose.jdac.annotations.interactions.Interaction;
import io.github.kaktushose.jdac.definitions.interactions.CustomId;
import io.github.kaktushose.jdac.definitions.interactions.MethodBuildContext;
import io.github.kaktushose.jdac.definitions.interactions.component.ButtonDefinition;
import io.github.kaktushose.jdac.dispatching.events.interactions.ComponentEvent;
import io.github.kaktushose.jdac.exceptions.InvalidDeclarationException;
import net.dv8tion.jda.api.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static definitions.TestHelpers.getBuildContext;
import static org.junit.jupiter.api.Assertions.*;

class ButtonDefinitionTest {

    @Test
    void method_withoutAnnotation_shouldThrowNoSuchElementException() {
        assertThrows(NoSuchElementException.class, () -> build("noAnnotation"));
    }

    @Test
    void method_withoutArgs_shouldThrowInvalidDeclarationException() {
        assertThrows(InvalidDeclarationException.class, () -> build("noArgs"));
    }

    @Test
    void method_withWrongEvent_shouldThrowInvalidDeclarationException() {
        assertThrows(InvalidDeclarationException.class, () -> build("wrongEvent"));
    }

    @Test
    void method_withAdditionalArguments_shouldThrowInvalidDeclarationException() {
        assertThrows(InvalidDeclarationException.class, () -> build("additionalArgs"));
    }

    @Test
    void button_withEmptyLabel_shouldUseIdForDisplay() {
        ButtonDefinition definition = build("emptyLabel");

        assertEquals("", definition.label());
        assertEquals("Button: %s".formatted(definition.definitionId()), definition.displayName());
    }

    @Test
    void button_withLabel_shouldBuild() {
        ButtonDefinition definition = build("labeledButton");

        assertEquals("Press", definition.label());
    }

    @Test
    void button_withEmptyEmoji_shouldReturnNullEmoji() {
        ButtonDefinition definition = build("noEmoji");

        assertNull(definition.emoji());
    }

    @Test
    void button_withEmoji_shouldParseEmojiCorrectly() {
        ButtonDefinition definition = build("withEmoji");

        assertEquals(Emoji.fromFormatted("👍"), definition.emoji());
    }

    @Test
    void button_withLink_shouldNotHaveId() {
        ButtonDefinition definition = build("withLink");

        var button = definition.toJDAEntity(CustomId.independent(definition.definitionId(), 0));

        assertNull(button.getCustomId());
        assertEquals("https://example.com", button.getUrl());
        assertEquals(ButtonStyle.LINK, button.getStyle());
    }

    @Test
    void button_withoutLink_shouldHaveId() {
        ButtonDefinition definition = build("normalButton");

        var button = definition.toJDAEntity(CustomId.independent(definition.definitionId(), 0));

        assertNotNull(button.getCustomId());
        assertNull(button.getUrl());
    }

    @Test
    void button_withCustomId_shouldUseGivenId() {
        ButtonDefinition definition = build("normalButton");
        CustomId customId = CustomId.independent("test-id-123", 0);

        var button = definition.toJDAEntity(customId);

        assertNotNull(button.getCustomId());
        assertEquals(customId.merged(), button.getCustomId());
    }

    @Test
    void button_withOverrides_shouldApplyOverridesCorrectly() {
        ButtonDefinition base = build("normalButton");

        ButtonDefinition override = base.with(
                "New Label", Emoji.fromFormatted("👎"), "https://new.link", ButtonStyle.DANGER, 1
        );

        assertEquals("New Label", override.label());
        assertEquals(Emoji.fromFormatted("👎"), override.emoji());
        assertEquals("https://new.link", override.link());
        assertEquals(ButtonStyle.DANGER, override.style());
        assertEquals(1, override.uniqueId());

        ButtonDefinition keep = override.with(null, null, null, null, override.uniqueId());
        assertEquals("New Label", keep.label());
        assertEquals(Emoji.fromFormatted("👎"), keep.emoji());
        assertEquals("https://new.link", keep.link());
        assertEquals(ButtonStyle.DANGER, keep.style());
    }

    private ButtonDefinition build(String method) {
        MethodBuildContext context = getBuildContext(TestController.class, method);
        return ButtonDefinition.build(context);
    }

    @Interaction
    private static class TestController {

        public void noAnnotation() {
        }

        @Button("noArgs")
        public void noArgs() {
        }

        @Button("wrongEvent")
        public void wrongEvent(String notAnEvent) {
        }

        @Button("additional")
        public void additionalArgs(ComponentEvent event, int i) {
        }

        @Button
        public void emptyLabel(ComponentEvent event) {
        }

        @Button("Press")
        public void labeledButton(ComponentEvent event) {
        }

        @Button(value = "withEmoji", emoji = "👍")
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
    }
}
