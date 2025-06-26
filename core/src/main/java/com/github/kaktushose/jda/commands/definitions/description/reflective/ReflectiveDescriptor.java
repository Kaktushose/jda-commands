package com.github.kaktushose.jda.commands.definitions.description.reflective;

import com.github.kaktushose.jda.commands.definitions.description.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/// An [Descriptor] implementation that uses `java.lang.reflect` to create the [ClassDescription].
public class ReflectiveDescriptor implements Descriptor {

    @NotNull
    @Override
    public ClassDescription describe(@NotNull Class<?> clazz) {
        List<MethodDescription> methods = Arrays.stream(clazz.getDeclaredMethods())
                .map(this::method)
                .toList();

        return new ClassDescription(
                clazz,
                clazz.getName(),
                packageDescription(clazz.getPackage()),
                toList(clazz.getAnnotations()),
                methods
        );
    }

    private PackageDescription packageDescription(Package p) {
        return new PackageDescription(
                p.getName(),
                toList(p.getAnnotations())
        );
    }


    private MethodDescription method(@NotNull Method method) {
        List<ParameterDescription> parameters = Arrays.stream(method.getParameters())
                .map(this::parameter)
                .toList();


        return new MethodDescription(
                method.getDeclaringClass(),
                method.getReturnType(),
                method.getName(),
                parameters,
                toList(method.getAnnotations()),
                (instance, arguments) -> method.invoke(instance, arguments.toArray())
        );
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

    @NotNull
    private <T> Collection<T> toList(@NotNull T[] array) {
        return Arrays.stream(array).toList();
    }
}
