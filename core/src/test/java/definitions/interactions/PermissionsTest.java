package definitions.interactions;

import com.github.kaktushose.jda.commands.annotations.interactions.Command;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.annotations.interactions.Permissions;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.internal.Helpers;
import definitions.TestHelpers;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PermissionsTest {


    @Test
    void interaction_withMethodPermissions_shouldHaveMethodPermissions() {
        Set<String> permissions = Helpers.permissions(
                TestHelpers.getBuildContext(ClassPermissions.class, "onEvent")
        );

        assertEquals(Set.of("CLASS"), permissions);
    }

    @Test
    void interaction_withClassPermissions_shouldHaveClassPermissions() {
        Set<String> permissions = Helpers.permissions(
                TestHelpers.getBuildContext(MethodPermissions.class, "onEvent")
        );

        assertEquals(Set.of("METHOD"), permissions);
    }

    @Test
    void interaction_withClassAndMethodPermissions_shouldHaveMergedPermissions() {
        Set<String> permissions = Helpers.permissions(
                TestHelpers.getBuildContext(ClassPermissions.class, "onEvent1")
        );

        assertEquals(Set.of("CLASS", "METHOD"), permissions);
    }

    @Interaction
    private static class MethodPermissions {

        @Command("method")
        @Permissions("METHOD")
        public void onEvent(CommandEvent event) {
        }
    }

    @Interaction
    @Permissions("CLASS")
    private static class ClassPermissions {

        @Command("class")
        public void onEvent(CommandEvent event) {

        }

        @Command("merged")
        @Permissions("METHOD")
        public void onEvent1(CommandEvent event) {
        }
    }
}
