package com.github.kaktushose.jda.commands.extension;

import com.github.kaktushose.jda.commands.JDACommandsBuilder;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record ImplementationProvider<T>(
        Class<T> type,
        Function<ReadOnlyJDACommandsBuilder, ? extends T> supplier
) {
    private static final StackWalker STACK_WALKER = StackWalker.getInstance(Set.of(StackWalker.Option.RETAIN_CLASS_REFERENCE,
            StackWalker.Option.SHOW_HIDDEN_FRAMES), 8);

    public T getValue(ReadOnlyJDACommandsBuilder builder) {
        checkCycling(builder);
        return supplier().apply(builder);
    }

    @Override
    public Function<ReadOnlyJDACommandsBuilder,? extends T> supplier() {
        return supplier;
    }

    private void checkCycling(ReadOnlyJDACommandsBuilder builder) {
        List<StackWalker.StackFrame> relevantFrames = new ArrayList<>();
        Boolean alreadyCalled = STACK_WALKER.walk(stream -> stream
                .peek(stackFrame -> {
                    if (Extension.class.isAssignableFrom(stackFrame.getDeclaringClass())
                            || ImplementationProvider.class.isAssignableFrom(stackFrame.getDeclaringClass())
                            || Function.class.isAssignableFrom(stackFrame.getDeclaringClass())
                    ) {
                        relevantFrames.add(stackFrame);
                    }
                })
                .anyMatch(stackFrame -> stackFrame.getDeclaringClass().equals(supplier.getClass())));

        if (alreadyCalled) {
            throw new JDACommandsBuilder.ConfigurationException("Cycling dependencies while getting implementations of %s! \n%s"
                    .formatted(type, format(beautify(relevantFrames, builder))));
        }
    }

    private String format(List<StackEntry> stack) {
        if (stack.size() == 1) {
            StackEntry entry = stack.getFirst();
            return "%s provides and needs %s, thus calls itself".formatted(entry.extension.getSimpleName(), entry.provides.getSimpleName());
        }

        List<String> lines = stack
                .stream()
                .flatMap(stackEntry -> Stream.of("↓", "%s defines %s".formatted(stackEntry.extension.getSimpleName(), stackEntry.provides.getSimpleName())))
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

    record StackEntry(
            Class<?> extension,
            Class<?> provides
    ) {}

    // never touch this!! If this ever breaks, just remove the cycling graph. The "anyMatch" check in checkCycling above is enough for cycling checking.
    private List<StackEntry> beautify(List<StackWalker.StackFrame> stack, ReadOnlyJDACommandsBuilder builder) {
        List<StackEntry> entries = new ArrayList<>();
        Iterator<StackWalker.StackFrame> iterator = stack.iterator();

        while (iterator.hasNext()) {
            StackWalker.StackFrame frame = iterator.next();
            while (!frame.getDeclaringClass().equals(ImplementationProvider.class) && !frame.getMethodName().equals("getValue") && iterator.hasNext()) frame = iterator.next();
            while (frame.getMethodType().returnType().equals(Object.class) || frame.getMethodType().returnType().equals(void.class) && iterator.hasNext()) frame = iterator.next();
            Class<?> declaringClass = frame.getDeclaringClass();

            Class<?> provides = frame.getMethodType().returnType();
            Class<?> extension;
            if (Extension.class.isAssignableFrom(declaringClass)) {
                extension = declaringClass;
            } else {
                extension = builder.implementation(provides)
                        .stream()
                        .filter(entry -> entry.getValue().supplier().getClass().equals(declaringClass))
                        .findAny()
                        .map(Map.Entry::getKey)
                        .orElseThrow()
                        .getClass();
            }
            entries.add(new StackEntry(extension, provides));
        }
        return entries;
    }
}
