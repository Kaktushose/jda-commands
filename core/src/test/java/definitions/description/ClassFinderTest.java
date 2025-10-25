package definitions.description;

import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.definitions.description.ClassFinder;
import com.github.kaktushose.jda.commands.guice.Implementation;
import definitions.description.environment.BaseClass;
import definitions.description.environment.SubClass;
import definitions.description.environment.nested.NestedClass;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClassFinderTest {

    @Test
    void classFinder_withExplicit_shouldReturnStatedClasses() {
        ClassFinder classFinder = ClassFinder.explicit(BaseClass.class);

        assertEquals(List.of(BaseClass.class), classFinder.search(Interaction.class));
    }

    @Test
    void classFinder_withReflective_shouldReturnAllClasses() {
        ClassFinder classFinder = ClassFinder.reflective();

        assertEquals(List.of(BaseClass.class, BaseClass.BaserInnerClass.class, NestedClass.class), classFinder.search(Interaction.class));
    }

    @Test
    void reflective_withPackageLimitation_shouldOnlyReturnClassesInPackage() {
        ClassFinder classFinder = ClassFinder.reflective("definitions.description.environment.nested");

        assertEquals(List.of(NestedClass.class), classFinder.search(Interaction.class));
    }

    @Test
    void reflective_withSuperclassSearch_shouldOnlyReturnSubclass() {
        ClassFinder classFinder = ClassFinder.reflective();

        assertEquals(List.of(SubClass.class), classFinder.search(Implementation.class, BaseClass.class));
    }
}
