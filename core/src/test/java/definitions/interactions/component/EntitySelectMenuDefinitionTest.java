package definitions.interactions.component;

import io.github.kaktushose.jdac.annotations.interactions.EntitySelectMenu;
import io.github.kaktushose.jdac.annotations.interactions.Interaction;
import io.github.kaktushose.jdac.annotations.interactions.Permissions;
import io.github.kaktushose.jdac.definitions.interactions.MethodBuildContext;
import io.github.kaktushose.jdac.definitions.interactions.component.menu.EntitySelectMenuDefinition;
import io.github.kaktushose.jdac.dispatching.events.interactions.ComponentEvent;
import io.github.kaktushose.jdac.exceptions.InvalidDeclarationException;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu.DefaultValue;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu.SelectTarget;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static definitions.TestHelpers.getBuildContext;
import static org.junit.jupiter.api.Assertions.*;

class EntitySelectMenuDefinitionTest {

    @Test
    void method_withoutAnnotation_shouldThrowNoSuchElementException() {
        assertThrows(NoSuchElementException.class, () -> build("noAnnotation"));
    }

    @Test
    void method_withWrongSignature_shouldThrowInvalidDeclarationException() {
        assertThrows(InvalidDeclarationException.class, () -> build("wrongSignature"));
    }

    @Test
    void menu_withDefaults_ShouldBuild() {
        var definition = build("allDefaults");

        net.dv8tion.jda.api.components.selections.EntitySelectMenu menu = definition.toJDAEntity();

        assertEquals("Entity Select Menu: test", definition.displayName());
        assertEquals(EnumSet.of(SelectTarget.USER), menu.getEntityTypes());
        assertEquals("test", menu.getPlaceholder());
        assertTrue(menu.getDefaultValues().isEmpty());
        assertTrue(menu.getChannelTypes().isEmpty());
        assertEquals(1, menu.getMinValues());
        assertEquals(1, menu.getMaxValues());
    }

    @Test
    void menu_withExplicitValues_shouldBeSet() {
        var definition = build("explicit");

        net.dv8tion.jda.api.components.selections.EntitySelectMenu menu = definition.toJDAEntity();

        assertEquals(EnumSet.of(ChannelType.TEXT), menu.getChannelTypes());
        assertEquals(2, menu.getMinValues());
        assertEquals(3, menu.getMaxValues());
        assertEquals(3, menu.getDefaultValues().size());
        assertFalse(menu.getDefaultValues().stream().map(DefaultValue::getIdLong).anyMatch(it -> it == -1));
    }

    @Test
    void menu_withOverrides_shouldApplyOverridesCorrectly() {
        var base = build("allDefaults");

        var newTargets = Set.of(SelectTarget.USER);
        var newDefaults = Set.of(DefaultValue.user(42L));
        var newChannelTypes = Set.of(ChannelType.NEWS);

        var overridden = base.with(newTargets, newDefaults, newChannelTypes, "new placeholder", 2, 3, 1);

        assertTrue(overridden.selectTargets().contains(SelectTarget.USER));
        assertTrue(overridden.defaultValues().stream().anyMatch(d -> d.getIdLong() == 42L));
        assertTrue(overridden.channelTypes().contains(ChannelType.NEWS));
        assertEquals("new placeholder", overridden.placeholder());
        assertEquals(2, overridden.minValue());
        assertEquals(3, overridden.maxValue());
        assertEquals(1, overridden.uniqueId());
    }

    private EntitySelectMenuDefinition build(String method) {
        MethodBuildContext context = getBuildContext(TestController.class, method);
        return EntitySelectMenuDefinition.build(context);
    }

    @Interaction
    @Permissions("ADMIN")
    private static class TestController {

        public void noAnnotation() {
        }

        @EntitySelectMenu(value = SelectTarget.USER)
        public void wrongSignature(ComponentEvent event) {
        }

        @EntitySelectMenu(value = SelectTarget.USER, placeholder = "test")
        public void allDefaults(ComponentEvent event, Mentions mentions) {
        }

        @EntitySelectMenu(
                value = SelectTarget.CHANNEL,
                channelTypes = ChannelType.TEXT,
                minValue = 2,
                maxValue = 3,
                placeholder = "test",
                defaultChannels = {1, 2, 3, -1}
        )
        public void explicit(ComponentEvent event, Mentions mentions) {
        }
    }
}
