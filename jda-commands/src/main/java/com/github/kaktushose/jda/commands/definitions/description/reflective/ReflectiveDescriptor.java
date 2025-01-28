package com.github.kaktushose.jda.commands.definitions.description.reflective;

import com.github.kaktushose.jda.commands.definitions.description.ClassDescription;
import com.github.kaktushose.jda.commands.definitions.description.Descriptor;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.definitions.description.ParameterDescription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
                toList(clazz.getAnnotations()),
                methods
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
                toList(method.getAnnotations()),
                (instance, arguments) -> method.invoke(instance, arguments.toArray())
        );
    }

    @NotNull
    private ParameterDescription parameter(@NotNull Parameter parameter) {
        return new ParameterDescription(
                parameter.getType(),
                parameter.getName(),
                toList(parameter.getAnnotations())
        );
    }

    @NotNull
    private <T> Collection<T> toList(@NotNull T[] array) {
        return Arrays.stream(array).toList();
    }
}
