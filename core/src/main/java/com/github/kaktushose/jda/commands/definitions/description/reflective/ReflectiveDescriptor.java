package com.github.kaktushose.jda.commands.definitions.description.reflective;

import com.github.kaktushose.jda.commands.definitions.description.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/// An [Descriptor] implementation that uses `java.lang.reflect` to create the [ClassDescription].
public class ReflectiveDescriptor implements Descriptor {

    @NotNull
    @Override
    public ClassDescription describe(@NotNull Class<?> clazz) {
        List<MethodDescription> methods = Arrays.stream(clazz.getMethods())
                .map(this::method)
                .filter(Objects::nonNull)
                .toList();

        return new ClassDescription(
                clazz,
                clazz.getName(),
                packageDescription(clazz.getPackage()),
                annotationList(clazz.getAnnotations()),
                methods
        );
    }

    private PackageDescription packageDescription(Package p) {
        return new PackageDescription(
                p.getName(),
                annotationList(p.getAnnotations())
        );
    }

    @Nullable
    private MethodDescription method(@NotNull Method method) {
        if (!Modifier.isPublic(method.getModifiers())) return null;
        List<ParameterDescription> parameters = Arrays.stream(method.getParameters())
                .map(this::parameter)
                .toList();


        return new MethodDescription(
                method.getDeclaringClass(),
                method.getReturnType(),
                method.getName(),
                parameters,
                annotationList(method.getAnnotations()),
                (instance, arguments) -> method.invoke(instance, arguments.toArray())
        );
    }

    @NotNull
    private ParameterDescription parameter(@NotNull Parameter parameter) {
        return new ParameterDescription(
                parameter.getType(),
                parameter.getName(),
                annotationList(parameter.getAnnotations())
        );
    }

    private List<AnnotationDescription<?>> annotationList(Annotation[] array) {
        return Arrays.stream(array)
                .map(this::annotation)
                .collect(Collectors.toUnmodifiableList());
    }

    // only add annotations one level deep
    private AnnotationDescription<?> annotation(@NotNull Annotation annotation) {
        return new AnnotationDescription<>(annotation, Arrays.stream(annotation.annotationType().getAnnotations())
                .map(ann -> new AnnotationDescription<>(ann, List.of()))
                .collect(Collectors.toUnmodifiableList()));
    }
}
