package io.github.kaktushose.jdac.definitions.description.reflective;

import io.github.kaktushose.jdac.definitions.description.ClassFinder;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.Annotation;
import java.util.SequencedCollection;

@ApiStatus.Internal
public class ReflectiveClassFinder implements ClassFinder {

    private final String[] packages;

    public ReflectiveClassFinder(String[] packages) {
        this.packages = packages;
    }

    @Override
    public SequencedCollection<Class<?>> search(Class<? extends Annotation> annotationClass) {
        try(ScanResult result = new ClassGraph()
                .acceptPackages(packages)
                .enableAnnotationInfo()
                .enableClassInfo()
                .scan()) {
            return result.getClassesWithAnnotation(annotationClass)
                    .loadClasses();
        }
    }
}
