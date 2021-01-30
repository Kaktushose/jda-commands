package com.github.kaktushose.jda.commands.internal;

import java.lang.reflect.AnnotatedType;
import java.util.Arrays;
import java.util.stream.Collectors;

public enum ParameterType {

    BYTE("java.lang.Byte"),
    SHORT("java.lang.Short"),
    INT("java.lang.Integer"),
    LONG("java.lang.Long"),
    FLOAT("java.lang.Float"),
    DOUBLE("java.lang.Double"),
    CHAR("java.lang.Char"),
    STRING("java.lang.String"),
    ARRAY("java.lang.String[]"),
    BOOLEAN("java.lang.Boolean"),
    MEMBER("net.dv8tion.jda.api.entities.Member"),
    USER("net.dv8tion.jda.api.entities.User"),
    ROLE("net.dv8tion.jda.api.entities.Role"),
    TEXTCHANNEL("net.dv8tion.jda.api.entities.TextChannel"),
    UNKNOWN("");

    public final String name;

    ParameterType(String name) {
        this.name = name;
    }

    public static boolean isValid(AnnotatedType annotatedType) {
        String typeName = annotatedType.getType().getTypeName();
        for (ParameterType type : values()) {
            if (type.name.equals(typeName)) {
                return true;
            }
        }
        Class<?> clazz;
        try {
            clazz = Class.forName(typeName);
        } catch (ClassNotFoundException ignored) {
            return false;
        }
        return Arrays.stream(clazz.getDeclaredConstructors()).anyMatch(constructor -> {
            if (constructor.getParameterCount() == 1) {
                return constructor.getParameterTypes()[0].getName().equals(String.class.getName());
            }
            return false;
        });
    }

    public static String wrap(String typeName) {
        try {
            ParameterType type = valueOf(typeName.toUpperCase());
            return type.name;
        } catch (IllegalArgumentException ignore) {
            return typeName;
        }
    }

    public static ParameterType getByName(String typeName) {
        return Arrays.stream(values()).filter(parameterType -> parameterType.name.equals(typeName)).collect(Collectors.toList()).stream().findFirst().orElse(UNKNOWN);
    }

    public static boolean isJDAEntity(String typeName) {
        return Arrays.stream(new ParameterType[]{MEMBER, USER, ROLE, TEXTCHANNEL}).anyMatch(parameterType -> parameterType.name.equals(typeName));
    }

}
