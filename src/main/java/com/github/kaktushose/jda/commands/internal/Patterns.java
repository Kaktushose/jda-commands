package com.github.kaktushose.jda.commands.internal;

import java.util.regex.Pattern;

public class Patterns {
    private static final Pattern JDAPermissionPattern = Pattern.compile("\\{@@([\\w]+)}");

    public static Pattern getJDAPermissionPattern() {
        return JDAPermissionPattern;
    }
}
