package definitions.interactions.component;

import com.github.kaktushose.jda.commands.annotations.interactions.EntitySelectMenu;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.annotations.interactions.Permissions;
import com.github.kaktushose.jda.commands.definitions.interactions.MethodBuildContext;
import com.github.kaktushose.jda.commands.definitions.interactions.component.menu.EntitySelectMenuDefinition;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent;
import com.github.kaktushose.jda.commands.exceptions.InvalidDeclarationException;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu.SelectTarget;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Set;

import static definitions.TestHelpers.getBuildContext;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link EntitySelectMenuDefinition}.
 */
class EntitySelectMenuDefinitionTest {

    // -------------------------
    // error cases
    // -------------------------

    @Test
    @DisplayName("build() without @EntitySelectMenu should throw NoSuchElementException")
    void method_withoutAnnotation_shouldThrowNoSuchElementException() {
        assertThrows(NoSuchElementException.class, () -> build(WithClassPermsController.class, "noAnnotation"));
    }

    @Test
    @DisplayName("build() with wrong signature (missing Mentions) should throw InvalidDeclarationException")
    void method_withWrongSignature_shouldThrowInvalidDeclarationException() {
        assertThrows(InvalidDeclarationException.class, () -> build(WithClassPermsController.class, "wrongSignature"));
    }

    // -------------------------
    // build + parsing behavior
    // -------------------------

    @Test
    @DisplayName("default values (users/roles) are parsed and negative IDs ignored")
    void defaultValues_shouldBeParsedAndIgnoreNegatives() {
        var def = build(WithClassPermsController.class, "allDefaults");

        // expecting 2 positive defaults (one user, one role)
        assertEquals(2, def.defaultValues().size());

        var menu = def.toJDAEntity();
        assertNotNull(menu);
        assertEquals(def.placeholder(), menu.getPlaceholder());
    }

    @Test
    @DisplayName("selectTargets from annotation are applied")
    void selectTargets_shouldBeApplied() {
        var def = build(WithClassPermsController.class, "allDefaults");
        assertTrue(def.selectTargets().contains(SelectTarget.USER));
        assertTrue(def.selectTargets().contains(SelectTarget.ROLE));
    }

    @Test
    @DisplayName("channelTypes: UNKNOWN only -> resulting menu has no channelTypes set")
    void channelTypes_unknownOnly_shouldNotBeSet() {
        var def = build(WithClassPermsController.class, "unknownChannelTypes");
        var menu = def.toJDAEntity();
        assertTrue(menu.getChannelTypes().isEmpty(),
                "channelTypes should be empty when only UNKNOWN was provided");
    }

    @Test
    @DisplayName("channelTypes explicit -> applied to produced menu")
    void channelTypes_explicit_shouldBeSet() {
        var def = build(WithClassPermsController.class, "textChannelTypes");
        var menu = def.toJDAEntity();
        assertNotNull(menu.getChannelTypes());
        assertTrue(menu.getChannelTypes().contains(ChannelType.TEXT));
    }

    // -------------------------
    // with() overrides
    // -------------------------

    @Test
    @DisplayName("with() should override selectTargets/defaultValues/channelTypes/placeholder/min/max")
    void with_shouldOverrideValues() {
        var base = build(WithClassPermsController.class, "allDefaults");

        var newTargets = Set.of(SelectTarget.USER);
        var newDefaults = Set.of(net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu.DefaultValue.user(42L));
        var newChannelTypes = Set.of(ChannelType.NEWS);

        var overridden = base.with(newTargets, newDefaults, newChannelTypes, "new placeholder", 2, 3);

        assertTrue(overridden.selectTargets().contains(SelectTarget.USER));
        assertTrue(overridden.defaultValues().stream().anyMatch(d -> d.getIdLong() == 42L));
        assertTrue(overridden.channelTypes().contains(ChannelType.NEWS));
        assertEquals("new placeholder", overridden.placeholder());
        assertEquals(2, overridden.minValue());
        assertEquals(3, overridden.maxValue());
    }

    // -------------------------
    // displayName + permissions
    // -------------------------

    @Test
    @DisplayName("displayName returns formatted placeholder")
    void displayName_returnsFormattedPlaceholder() {
        var def = build(WithClassPermsController.class, "allDefaults");
        assertEquals("Select Menu: Example placeholder", def.displayName());
    }

    @Nested
    @DisplayName("permissions merge behavior")
    class PermissionsTests {

        @Test
        @DisplayName("class-level permissions only")
        void classOnlyPermissions() {
            var def = build(WithClassPermsController.class, "allDefaults");
            assertEquals(Set.of("ADMIN"), Set.copyOf(def.permissions()));
        }

        @Test
        @DisplayName("method-level permissions only (controller without class perms)")
        void methodOnlyPermissions() {
            var def = build(NoClassPermsController.class, "methodOnlyPermission");
            assertEquals(Set.of("USER"), Set.copyOf(def.permissions()));
        }

        @Test
        @DisplayName("class + method merge")
        void mergedPermissions() {
            var def = build(WithClassPermsController.class, "mergedPermission");
            var perms = Set.copyOf(def.permissions());
            assertTrue(perms.contains("ADMIN"));
            assertTrue(perms.contains("MOD"));
            assertEquals(2, perms.size());
        }
    }

    // -------------------------
    // helpers
    // -------------------------

    private EntitySelectMenuDefinition build(Class<?> controller, String method) {
        MethodBuildContext context = getBuildContext(controller, method);
        return EntitySelectMenuDefinition.build(context);
    }

    // -------------------------
    // Test controllers
    // -------------------------

    @Interaction
    @Permissions("ADMIN")
    private static class WithClassPermsController {

        public void noAnnotation() {
        }

        @EntitySelectMenu(value = { SelectTarget.USER }, placeholder = "badSig", minValue = 0, maxValue = 1)
        public void wrongSignature(ComponentEvent event) {
        }

        @EntitySelectMenu(
                value = { SelectTarget.USER, SelectTarget.ROLE },
                defaultUsers = {456L, -1L},
                defaultRoles = {789L},
                placeholder = "Example placeholder",
                minValue = 1,
                maxValue = 3
        )
        public void allDefaults(ComponentEvent event, Mentions mentions) {
        }

        @EntitySelectMenu(
                value = { SelectTarget.CHANNEL },
                channelTypes = { ChannelType.UNKNOWN },
                placeholder = "Unknown placeholder"
        )
        public void unknownChannelTypes(ComponentEvent event, Mentions mentions) {
        }

        @EntitySelectMenu(
                value = { SelectTarget.CHANNEL },
                channelTypes = { ChannelType.TEXT },
                placeholder = "Text placeholder"
        )
        public void textChannelTypes(ComponentEvent event, Mentions mentions) {
        }

        @EntitySelectMenu(
                value = { SelectTarget.USER },
                placeholder = "mergePerms"
        )
        @Permissions("MOD")
        public void mergedPermission(ComponentEvent event, Mentions mentions) {
        }
    }

    @Interaction
    private static class NoClassPermsController {

        @EntitySelectMenu(value = { SelectTarget.USER }, placeholder = "no class perm")
        @Permissions("USER")
        public void methodOnlyPermission(ComponentEvent event, Mentions mentions) {
        }
    }
}
