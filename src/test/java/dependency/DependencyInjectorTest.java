package dependency;

import com.github.kaktushose.jda.commands.dependency.DependencyInjector;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DependencyInjectorTest {

    private static DependencyInjector dependencyInjector;
    private static InjectableClass instance;
    private static List<Field> fields;

    @BeforeAll
    public static void setup() {
        instance = new InjectableClass();
        fields = Arrays.asList(instance.getClass().getDeclaredFields());
    }

    @BeforeEach
    public void cleanup() {
        dependencyInjector = new DependencyInjector();

    }

    @Test
    public void inject_withRegisteredDependency_ShouldBePresent() {
        dependencyInjector.registerDependencies(instance, fields);
        dependencyInjector.registerProvider(new ProducingClass());

        dependencyInjector.inject();

        assertEquals(Dependency.FOO, instance.getDependency().getString());
    }

    @Test
    public void inject_withoutRegisteredDependency_ShouldBeNull() {
        dependencyInjector.registerDependencies(instance, fields);

        dependencyInjector.inject();

        assertNull(instance.getDependency());
    }

    @Test
    public void registerProvider_withProducerMethodWithParameter_ShouldThrow() {
        dependencyInjector.registerDependencies(instance, fields);
        dependencyInjector.registerProvider(new WrongProducingClass());

        dependencyInjector.inject();

        assertNull(instance.getDependency());
    }
}
