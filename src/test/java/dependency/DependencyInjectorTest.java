package dependency;

import com.github.kaktushose.jda.commands.dependency.DefaultDependencyInjector;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DependencyInjectorTest {

    private static DefaultDependencyInjector dependencyInjector;
    private static InjectableClass instance;
    private static List<Field> fields;

    @BeforeAll
    public static void setup() {
        instance = new InjectableClass();
        fields = Arrays.asList(instance.getClass().getDeclaredFields());
    }

    @BeforeEach
    public void cleanup() {
        dependencyInjector = new DefaultDependencyInjector();

    }

    @Test
    public void inject_withRegisteredDependency_ShouldBePresent() {
        dependencyInjector.registerDependencies(instance.getClass(), fields);
        dependencyInjector.registerProvider(new ProducingClass());

        dependencyInjector.inject(instance);

        assertEquals(Dependency.FOO, instance.getDependency().getString());
    }

    @Test
    public void inject_withoutRegisteredDependency_ShouldBeNull() {
        dependencyInjector.registerDependencies(instance.getClass(), fields);

        dependencyInjector.inject(instance);

        assertNull(instance.getDependency());
    }

    @Test
    public void registerProvider_withProducerMethodWithParameter_ShouldThrow() {
        dependencyInjector.registerDependencies(instance.getClass(), fields);
        dependencyInjector.registerProvider(new WrongProducingClass());

        dependencyInjector.inject(instance);

        assertNull(instance.getDependency());
    }
}
