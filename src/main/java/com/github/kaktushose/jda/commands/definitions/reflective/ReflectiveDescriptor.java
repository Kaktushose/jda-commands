package com.github.kaktushose.jda.commands.definitions.reflective;

import com.github.kaktushose.jda.commands.definitions.description.ClassDescription;
import com.github.kaktushose.jda.commands.definitions.description.Descriptor;
import com.github.kaktushose.jda.commands.definitions.description.MethodDescription;
import com.github.kaktushose.jda.commands.definitions.description.ParameterDescription;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class ReflectiveDescriptor implements Descriptor {

    @Override
    public ClassDescription apply(Class<?> klass) {
        List<MethodDescription> methods = Arrays.stream(klass.getMethods())
                .map(this::method)
                .filter(Objects::nonNull)
                .toList();

        return new ClassDescription(
                klass,
                klass.getName(),
                toList(klass.getAnnotations()),
                methods
        );
    }

    private MethodDescription method(Method method) {
        if (!Modifier.isPublic(method.getModifiers())) return null;
        List<ParameterDescription> parameters = Arrays.stream(method.getParameters())
                .map(this::parameter)
                .toList();


        return new MethodDescription(
                method.getReturnType(),
                method.getName(),
                parameters,
                toList(method.getAnnotations()),
                (instance, arguments) -> method.invoke(instance, arguments.toArray())
        );
    }

    private ParameterDescription parameter(Parameter parameter) {
        return new ParameterDescription(
                parameter.getType(),
                toList(parameter.getAnnotations())
        );
    }

    private <T> Collection<T> toList(T[] array) {
        return Arrays.stream(array).toList();
    }
}
