package com.github.kaktushose.jda.commands.extension;

import com.github.kaktushose.jda.commands.JDACommandsBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/// Instances of this class are used to provide specific implementations of classes, that can be loaded by the [JDACommandsBuilder].
///
/// @param type the [Class] of the implemented interface
/// @param supplier the [java.util.function.Supplier] used to retrieve an instance of the specific implementation
public record ImplementationProvider<T>(
        @NotNull Class<T> type,
        @NotNull Function<JDACommandsCreationContext, ? extends T> supplier
) {

    T getValue(JDACommandsCreationContext builder) {
        checkCycling(builder);
        builder.alreadyCalled.add(this);

        T apply = supplier().apply(builder);

        builder.alreadyCalled.remove(this);
        return apply;
    }

    private void checkCycling(JDACommandsCreationContext builder) {
        boolean alreadyCalled = builder.alreadyCalled
                .stream()
                .anyMatch(provider -> provider.type.equals(type));

        if (alreadyCalled) {
            List<GraphEntry> stack = builder.alreadyCalled
                    .reversed()
                    .stream()
                    .map(provider -> {
                        var extension = builder.implementation(provider.type)
                                .stream()
                                .findAny()
                                .map(Map.Entry::getKey)
                                .orElseThrow()
                                .getClass();
                        return new GraphEntry(extension, provider.type);
                    })
                    .toList();

            throw new JDACommandsBuilder.ConfigurationException("Cycling dependencies while getting implementations of %s! \n%s"
                    .formatted(type, format(stack)));
        }
    }

    private record GraphEntry(
            Class<?> extension,
            Class<?> provides
    ) {}

    private String format(List<GraphEntry> stack) {
        if (stack.size() == 1) {
            GraphEntry entry = stack.getFirst();
            return "%s provides and needs %s, thus calls itself".formatted(entry.extension.getSimpleName(), entry.provides.getSimpleName());
        }

        List<String> lines = stack
                .stream()
                .flatMap(graphEntry -> Stream.of("↓", "%s defines %s".formatted(graphEntry.extension.getSimpleName(), graphEntry.provides.getSimpleName())))
                .skip(1)
                .collect(Collectors.toList());


        int intend = lines.stream().map(String::length).max(Integer::compare).orElseThrow() + 3;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            int missing = intend - line.length();
            String appendix;
            if (i == 0) {
                appendix = " ".repeat(missing) + "←--|";
            } else if (i == (lines.size() - 1)) {
                appendix = " ".repeat(missing) + "→--|";
            } else {
                appendix = " ".repeat(missing + 3) + "|";
            }
            lines.set(i, line + appendix);
        }
        return String.join(System.lineSeparator(), lines);
    }
}
