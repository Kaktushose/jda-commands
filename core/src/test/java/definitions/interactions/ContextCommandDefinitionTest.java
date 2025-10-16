package definitions.interactions;

import com.github.kaktushose.jda.commands.annotations.interactions.Command;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.annotations.interactions.Permissions;
import com.github.kaktushose.jda.commands.definitions.interactions.MethodBuildContext;
import com.github.kaktushose.jda.commands.definitions.interactions.command.ContextCommandDefinition;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.exceptions.InvalidDeclarationException;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.Command.Type;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Set;

import static definitions.TestHelpers.getBuildContext;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link ContextCommandDefinition}.
 */
class ContextCommandDefinitionTest {

    // -------------------------
    // error cases
    // -------------------------

    @Test
    @DisplayName("build() without @Command should throw NoSuchElementException")
    void method_withoutAnnotation_shouldThrowNoSuchElementException() {
        assertThrows(NoSuchElementException.class, () -> build("noAnnotation"));
    }

    @Test
    @DisplayName("build() with invalid command type should throw InvalidDeclarationException")
    void method_withInvalidType_shouldThrowInvalidDeclarationException() {
        assertThrows(InvalidDeclarationException.class, () -> build("invalidType"));
    }

    @Test
    @DisplayName("MESSAGE context with wrong signature should throw InvalidDeclarationException")
    void message_withWrongSignature_shouldThrowInvalidDeclarationException() {
        assertThrows(InvalidDeclarationException.class, () -> build("messageWrongSig"));
    }

    @Test
    @DisplayName("USER context with wrong signature should throw InvalidDeclarationException")
    void user_withWrongSignature_shouldThrowInvalidDeclarationException() {
        assertThrows(InvalidDeclarationException.class, () -> build("userWrongSig"));
    }

    // -------------------------
    // positive cases
    // -------------------------

    @Test
    @DisplayName("MESSAGE context with correct signature should build and produce CommandData with type MESSAGE")
    void message_withCorrectSignature_shouldBuild() {
        ContextCommandDefinition def = build("messageCorrect");
        var commandData = def.toJDAEntity();

        assertEquals("msg", commandData.getName());
        // check command type via fully qualified enum to avoid import clash with annotation
        assertEquals(Type.MESSAGE, commandData.getType());
        // command displayName should equal name
        assertEquals("msg", def.displayName());
    }

    @Test
    @DisplayName("USER context with Member parameter (typical user-context signature) should build and produce type USER")
    void user_withMemberParam_shouldBuild() {
        ContextCommandDefinition def = build("userWithMember");
        var commandData = def.toJDAEntity();

        assertEquals("usr", commandData.getName());
        assertEquals(Type.USER, commandData.getType());
        assertEquals("usr", def.displayName());
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
            ContextCommandDefinition def = build("classOnlyPermission");
            Set<String> perms = Set.copyOf(def.permissions());
            assertEquals(Set.of("ADMIN"), perms);
        }

        @Test
        @DisplayName("class + method merge")
        void mergedPermissions() {
            ContextCommandDefinition def = build("mergedPermission");
            Set<String> perms = Set.copyOf(def.permissions());
            assertTrue(perms.contains("ADMIN"));
            assertTrue(perms.contains("MOD"));
            assertEquals(2, perms.size());
        }
    }

    // -------------------------
    // helper
    // -------------------------

    private ContextCommandDefinition build(String method) {
        MethodBuildContext context = getBuildContext(TestController.class, method);
        return ContextCommandDefinition.build(context);
    }

    // -------------------------
    // Test controller
    // -------------------------
    @Interaction
    @Permissions("ADMIN") // class-level permission; ensure your @Permissions supports TYPE if you want this
    private static class TestController {

        public void noAnnotation() {
        }

        @Command("invalid")
        // use a type that ContextCommandDefinition doesn't accept (e.g. SLASH) to provoke invalid-type branch
        public void invalidType() {
        }

        @Command(value = "msg", type = Type.MESSAGE)
        // wrong signature: missing Message parameter
        public void messageWrongSig(CommandEvent event) {
        }

        @Command(value = "msg", type = Type.MESSAGE)
        // correct signature for MESSAGE context: (CommandEvent, Message)
        public void messageCorrect(CommandEvent event, Message message) {
        }

        @Command(value = "usr", type = Type.USER)
        // wrong signature for USER context (example): no second param -> expect InvalidDeclarationException
        public void userWrongSig(CommandEvent event) {
        }

        @Command(value = "usr", type = Type.USER)
        // plausible correct user signature: (CommandEvent, Member)
        public void userWithMember(CommandEvent event, Member member) {
        }

        @Command(value = "classOnlyPermission", type = Type.USER)
        public void classOnlyPermission(CommandEvent event, Member member) {
        }

        @Command(value = "mergedPermission", type = Type.USER)
        @Permissions("MOD")
        public void mergedPermission(CommandEvent event, Member member) {
        }
    }
}
