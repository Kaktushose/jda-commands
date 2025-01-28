package com.github.kaktushose.jda.commands.extension;

import com.github.kaktushose.jda.commands.JDACommandsBuilder;
import com.github.kaktushose.jda.commands.definitions.description.ClassFinder;
import com.github.kaktushose.jda.commands.definitions.description.Descriptor;
import com.github.kaktushose.jda.commands.dispatching.instance.InteractionClassProvider;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.permissions.PermissionsProvider;
import com.github.kaktushose.jda.commands.scope.GuildScopeProvider;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/// Instances of this class are used to provide custom implementations of classes implementing [ExtensionImplementable].
/// Such instances are returned by [Extension#providedImplementations()] and used by the [JDACommandsBuilder].
///
/// If the value returned by [java.util.function.Supplier#get()] ([#supplier()]) is `null` then this implementation is discarded and thus treated as non-existent.
///
/// @param type the [Class] of the implemented interface
/// @param supplier the [java.util.function.Supplier] used to retrieve an instance of the custom implementation
public record Implementation<T extends Implementation.ExtensionImplementable>(
        @NotNull Class<T> type,
        @NotNull Function<ReadonlyJDACBuilder, T> supplier
) {

    public sealed interface ExtensionImplementable permits ClassFinder, Descriptor, InteractionClassProvider, ErrorMessageFactory, Pair, PermissionsProvider, GuildScopeProvider {}
    public record Pair<K, V extends ExtensionImplementable>(K key, V value) implements ExtensionImplementable {}

    T getValue(ReadonlyJDACBuilder builder) {
        checkCycling(builder);

        builder.alreadyCalled.add(this); // scope entry

        // other Implementation#getValue() could be called in here.
        // Scoping this will create a simple stack of already called methods, allowing checking for cycling dependencies (Implementation#type())
        T apply = supplier().apply(builder);

        builder.alreadyCalled.remove(this); // scope leave
        return apply;
    }

    private void checkCycling(ReadonlyJDACBuilder builder) {
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
