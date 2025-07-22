package com.github.kaktushose.jda.commands.definitions.description.reflective;

import com.github.kaktushose.jda.commands.definitions.description.*;
import org.jspecify.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/// An [Descriptor] implementation that uses `java.lang.reflect` to create the [ClassDescription].
public class ReflectiveDescriptor implements Descriptor {

    @Override
    public ClassDescription describe(Class<?> clazz) {
        List<MethodDescription> methods = Arrays.stream(clazz.getDeclaredMethods())
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
    private MethodDescription method(Method method) {
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

    private ParameterDescription parameter(Parameter parameter) {
        @Nullable Class<?>[] arguments = {};
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
                annotationList(parameter.getAnnotations())
        );
    }

    private List<AnnotationDescription<?>> annotationList(Annotation[] array) {
        return Arrays.stream(array)
                .map(this::annotation)
                .collect(Collectors.toUnmodifiableList());
    }

    // only add annotations one level deep
    private AnnotationDescription<?> annotation(Annotation annotation) {
        return new AnnotationDescription<>(annotation, Arrays.stream(annotation.annotationType().getAnnotations())
                .map(ann -> new AnnotationDescription<>(ann, List.of()))
                .collect(Collectors.toUnmodifiableList()));
    }
}
