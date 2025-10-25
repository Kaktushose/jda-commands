package definitions.config;

import com.github.kaktushose.jda.commands.annotations.interactions.*;
import com.github.kaktushose.jda.commands.definitions.interactions.MethodBuildContext;
import com.github.kaktushose.jda.commands.definitions.interactions.command.CommandDefinition;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.ComponentEvent;
import com.github.kaktushose.jda.commands.internal.Helpers;
import definitions.TestHelpers;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandConfigTest {

    private static CommandDefinition.CommandConfig getCommandConfig(Class<?> clazz, String method) {
        return Helpers.commandConfig(TestHelpers.getBuildContext(clazz, method));
    }

    @Test
    void methodLevelConfig_withDefaultMethod_ShouldUseGlobalValues() {
        MethodBuildContext context = TestHelpers.getBuildContext(MethodLevelConfig.class, "defaultValues");
        var global = context.globalCommandConfig();

        var config = Helpers.commandConfig(context);

        assertions(global, config);
    }

    @Test
    void methodLevelConfig_withCustomMethod_ShouldUseMethodValues() {
        var config = getCommandConfig(MethodLevelConfig.class, "customValues");

        assertions(config);
    }

    @Test
    void classLevelConfig_withDefaultMethod_ShouldUseControllerValues() {
        var config = getCommandConfig(ClassLevelConfig.class, "defaultValues");

        assertions(config);
    }

    @Test
    void classLevelConfig_withSameCustomMethod_ShouldBeEquals() {
        var first = getCommandConfig(ClassLevelConfig.class, "defaultValues");
        var second = getCommandConfig(ClassLevelConfig.class, "sameValues");

        assertions(first, second);
    }

    @Test
    void classLevelConfig_withDifferentCustomMethod_ShouldUseMethodValues() {
        var config = getCommandConfig(ClassLevelConfig.class, "customValues");

        assertions(config);
    }

    private void assertions(CommandDefinition.CommandConfig config) {
        assertArrayEquals(new InteractionContextType[]{InteractionContextType.BOT_DM}, config.context());
        assertArrayEquals(new IntegrationType[]{IntegrationType.USER_INSTALL}, config.integration());
        assertEquals(CommandScope.GUILD, config.scope());
        assertTrue(config.isNSFW());
        assertArrayEquals(new Permission[]{Permission.ADMINISTRATOR}, config.enabledPermissions());
    }

    private void assertions(CommandDefinition.CommandConfig first, CommandDefinition.CommandConfig second) {
        assertArrayEquals(first.context(), second.context());
        assertArrayEquals(first.integration(), second.integration());
        assertEquals(first.scope(), second.scope());
        assertEquals(first.isNSFW(), second.isNSFW());
        assertArrayEquals(first.enabledPermissions(), second.enabledPermissions());
    }

    @Interaction
    private static class MethodLevelConfig {

        @Button
        public void defaultValues(ComponentEvent event) {
        }

        @Button
        @CommandConfig(
                context = InteractionContextType.BOT_DM,
                integration = IntegrationType.USER_INSTALL,
                scope = CommandScope.GUILD,
                isNSFW = true,
                enabledFor = Permission.ADMINISTRATOR
        )
        public void customValues(ComponentEvent event) {
        }
    }

    @Interaction
    @CommandConfig(
            context = InteractionContextType.BOT_DM,
            integration = IntegrationType.USER_INSTALL,
            scope = CommandScope.GUILD,
            isNSFW = true,
            enabledFor = Permission.ADMINISTRATOR
    )
    private static class ClassLevelConfig {

        @Button
        public void defaultValues(ComponentEvent event) {
        }

        @Button
        @CommandConfig(
                context = InteractionContextType.BOT_DM,
                integration = IntegrationType.USER_INSTALL,
                scope = CommandScope.GUILD,
                isNSFW = true,
                enabledFor = Permission.ADMINISTRATOR
        )
        public void sameValues(ComponentEvent event) {

        }

        @Button
        @ReplyConfig
        public void customValues(ComponentEvent event) {
        }
    }

}
