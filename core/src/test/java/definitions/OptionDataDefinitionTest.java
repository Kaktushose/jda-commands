package definitions;

import com.github.kaktushose.jda.commands.annotations.constraints.Perm;
import com.github.kaktushose.jda.commands.definitions.description.ParameterDescription;
import com.github.kaktushose.jda.commands.definitions.interactions.command.OptionDataDefinition;
import com.github.kaktushose.jda.commands.dispatching.validation.internal.Validators;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OptionDataDefinitionTest {

    private static Class<?> controller;
    private static Validators validatorRegistry;

    @BeforeAll
    static void setup() {
        controller = OptionDataDefinitionTest.class;
        validatorRegistry = new Validators(Map.of());
    }

    @NotNull
    private ParameterDescription parameter(@NotNull Parameter parameter) {
        Class<?>[] arguments = {};
        if (parameter.getParameterizedType() instanceof ParameterizedType type) {
            arguments = Arrays.stream(type.getActualTypeArguments())
                    .map(it -> it instanceof ParameterizedType pT ? pT.getRawType() : it)
                    .map(it -> it instanceof Class<?> klass ? klass : null)
                    .toArray(Class[]::new);
        }

        return new ParameterDescription(
                parameter.getType(),
                arguments,
                parameter.getName(),
                toList(parameter.getAnnotations())
        );
    }

    private static <T> Collection<T> toList(T[] array) {
        return Arrays.stream(array).toList();
    }

    @Test
    void constraint_withMessage_ShouldWork() throws NoSuchMethodException {
        Method method = controller.getDeclaredMethod("constraintWithMessage", Member.class);
        OptionDataDefinition parameter = OptionDataDefinition.build(parameter(method.getParameters()[0]), null, validatorRegistry);
        var constraints = List.copyOf(parameter.constraints());

        assertEquals("error message", constraints.getFirst().message());
    }

    public void constraintWithMessage(@Perm(value = "10", message = "error message") Member member) {

    }

}
