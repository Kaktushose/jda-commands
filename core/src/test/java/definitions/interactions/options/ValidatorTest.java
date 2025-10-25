package definitions.interactions.options;

import com.github.kaktushose.jda.commands.annotations.constraints.Constraint;
import com.github.kaktushose.jda.commands.annotations.constraints.Max;
import com.github.kaktushose.jda.commands.annotations.constraints.Min;
import com.github.kaktushose.jda.commands.annotations.constraints.Perm;
import com.github.kaktushose.jda.commands.annotations.interactions.Command;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.definitions.interactions.command.OptionDataDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.SlashCommandDefinition;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.validation.impl.PermissionValidator;
import com.github.kaktushose.jda.commands.exceptions.ConfigurationException;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static definitions.TestHelpers.getBuildContext;
import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {

    @Test
    void annotation_withNoConstraintAnnotation_shouldBeIgnored() {
        assertDoesNotThrow(() -> build("noConstraint"));
    }

    @Test
    void command_withMinMaxAnnotation_shouldPassWithoutValidatorImplementation() {
        assertDoesNotThrow(() -> build("minMax"));
    }

    @Test
    void constraint_withNoValidatorImplementation_shouldThrowConfigurationException() {
        assertThrows(ConfigurationException.class, () -> build("noValidator"));
    }

    @Test
    void constraint_withWrongConstraintType_shouldThrow() {
        assertThrows(ConfigurationException.class, () -> build("wrongConstraintType"));
    }

    @Test
    void command_withConstraint_shouldRegister() {
        OptionDataDefinition definition = optionData("constraint");

        assertEquals(1, definition.constraints().size());
        assertEquals(PermissionValidator.class, definition.constraints().stream().findAny().orElseThrow().validator().getClass());
    }

    private OptionDataDefinition optionData(String method) {
        return build(method).commandOptions().getFirst();
    }

    private SlashCommandDefinition build(String method) {
        return SlashCommandDefinition.build(getBuildContext(TestController.class, method));
    }

    @Interaction
    private static class TestController {

        @Command("noConstraint")
        public void noConstraint(CommandEvent event, @Nullable String argument) {
        }

        @Command("minMax")
        public void minMax(CommandEvent event, @Min(1) int min, @Max(1) int max) {
        }

        @Command("noValidator")
        public void noValidator(CommandEvent event, @NoValidator String argument) {
        }

        @Command("wrongConstraintType")
        public void wrongConstraintType(CommandEvent event, @Perm(Permission.ADMINISTRATOR) int argument) {
        }

        @Command("constraint")
        public void constraint(CommandEvent event, @Perm(Permission.ADMINISTRATOR) Member member) {
        }
    }

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(String.class)
    public @interface NoValidator {
    }

}
