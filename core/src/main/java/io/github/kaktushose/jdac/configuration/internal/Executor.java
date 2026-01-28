package io.github.kaktushose.jdac.configuration.internal;

import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.configuration.PropertyProvider;
import io.github.kaktushose.jdac.exceptions.ConfigurationException;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;

@ApiStatus.Internal
@SuppressWarnings("ClassCanBeRecord")
class Executor {
    private static final ScopedValue<SequencedMap<Property<?>, PropertyProvider<?>>> STACK = ScopedValue.newInstance();

    private final Resolver resolver;

    Executor(Resolver resolver) {
        this.resolver = resolver;
    }

    @Nullable <T> T applyProvider(PropertyProvider<T> provider) {
        SequencedMap<Property<?>, PropertyProvider<?>> stack = new LinkedHashMap<>(STACK.orElse(new LinkedHashMap<>()));

        checkCycling(stack, provider);

        stack.putLast(provider.type(), provider);
        return ScopedValue.where(STACK, stack)
                .call(() -> provider.supplier().apply(resolver::get));
    }

    private void checkCycling(SequencedMap<Property<?>, PropertyProvider<?>> stack, PropertyProvider<?> current) {
        Property<?> type = current.type();
        if (stack.containsKey(type)) {
            SequencedCollection<PropertyProvider<?>> callchain = stack.sequencedValues();

            if (callchain.getLast().type().equals(type)) {
                throw new ConfigurationException("cycling-calls-itself", entry("property", type.name()),
                        entry("class", current.referenceClass()));
            }

            String tree = formatTree(callchain, current);
            throw new ConfigurationException("cycling-tree", entry("property", type.name()),
                    entry("tree", tree));
        }
    }

    private String formatTree(SequencedCollection<PropertyProvider<?>> stack, PropertyProvider<?> current) {
        SequencedCollection<PropertyProvider<?>> shortStack = new ArrayList<>();
        for (PropertyProvider<?> p : stack.reversed()) {
            shortStack.add(p);
            if (p.type().equals(current.type())) break;
        }

        List<String> lines = shortStack.reversed().stream()
                .flatMap(frame -> Stream.of("↓ [requires]", "%s (provider in %s)".formatted(frame.type().name(), frame.referenceClass())))
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
