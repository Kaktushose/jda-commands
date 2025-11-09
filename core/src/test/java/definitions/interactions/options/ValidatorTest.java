package definitions.interactions.options;

import io.github.kaktushose.jdac.annotations.constraints.Constraint;
import io.github.kaktushose.jdac.annotations.constraints.Max;
import io.github.kaktushose.jdac.annotations.constraints.Min;
import io.github.kaktushose.jdac.annotations.constraints.Perm;
import io.github.kaktushose.jdac.annotations.interactions.Command;
import io.github.kaktushose.jdac.annotations.interactions.Interaction;
import io.github.kaktushose.jdac.definitions.interactions.command.OptionDataDefinition;
import io.github.kaktushose.jdac.definitions.interactions.command.SlashCommandDefinition;
import io.github.kaktushose.jdac.dispatching.events.interactions.CommandEvent;
import io.github.kaktushose.jdac.dispatching.validation.impl.PermissionValidator;
import io.github.kaktushose.jdac.exceptions.ConfigurationException;
import io.github.kaktushose.jdac.exceptions.InvalidDeclarationException;
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
    void minMaxAnnotation_withWrongType_shouldThrowInvalidDeclarationException() {
        assertThrows(InvalidDeclarationException.class, () -> build("minMaxWrongType"));
    }

    @Test
    void constraint_withNoValidatorImplementation_shouldThrowConfigurationException() {
        assertThrows(ConfigurationException.class, () -> build("noValidator"));
    }

    @Test
    void constraint_withWrongConstraintType_shouldThrow() {
        assertThrows(InvalidDeclarationException.class, () -> build("wrongConstraintType"));
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

        @Command("minMaxWrongType")
        public void minMaxWrongType(CommandEvent event, @Min(1) String min, @Max(1) String max) {
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
