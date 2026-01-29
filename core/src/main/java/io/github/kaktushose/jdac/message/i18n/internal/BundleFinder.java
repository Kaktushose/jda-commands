package io.github.kaktushose.jdac.message.i18n.internal;

import io.github.kaktushose.jdac.annotations.i18n.Bundle;
import io.github.kaktushose.jdac.definitions.description.ClassDescription;
import io.github.kaktushose.jdac.definitions.description.Description;
import io.github.kaktushose.jdac.definitions.description.Descriptor;
import io.github.kaktushose.jdac.message.i18n.I18n;
import org.apache.commons.collections4.map.LRUMap;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@ApiStatus.Internal
public class BundleFinder {
    // skipped classes during stack scanning (Class.getName().startWith(X))
    private static final List<String> SKIPPED = List.of(
            "io.github.kaktushose.jdac",
            "net.dv8tion.jda",
            "java."
    );

    // TODO make this configurable
    private final LRUMap<Class<?>, String> cache = new LRUMap<>(64);

    private final StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    private final Descriptor descriptor;

    public BundleFinder(Descriptor descriptor) {
        this.descriptor = descriptor;
    }

    public String findBundle() {
        // isn't intended for that, but who cares
        AtomicReference<@Nullable ClassDescription> last = new AtomicReference<>();

        return walker.walk(stream -> stream
                .map(frame -> checkFrame(frame, last))
                .filter(b -> !b.isEmpty())
                .findAny()
        ).orElseGet(() -> {
            String found = checkClass(last.get());
            return found.isEmpty()
                    ? I18n.DEFAULT_BUNDLE
                    : found;
        });
    }

    private String checkFrame(StackWalker.StackFrame frame, AtomicReference<@Nullable ClassDescription> last) {
        Class<?> klass = frame.getDeclaringClass();

        String name = klass.getName();

        // just some optimization
        if (SKIPPED.stream().anyMatch(name::startsWith)) {
            return "";
        }

        ClassDescription classDescription = descriptor.describe(klass);

        ClassDescription lastDes = last.get();
        if (lastDes != null && !lastDes.clazz().equals(classDescription.clazz())) {
            String found = checkClass(lastDes);
            if (!found.isEmpty()) {
                return found;
            }
        }

        last.set(classDescription);

        return classDescription.methods()
                .stream()
                .filter(method -> method.toMethodType().equals(frame.getMethodType()))
                .findFirst()
                .flatMap(this::readAnnotation)
                .orElse("");
    }

    public String checkClass(@Nullable ClassDescription classDescription) {
        if (classDescription == null) {
            return "";
        }
        return cache.computeIfAbsent(
                classDescription.clazz(), _ -> readAnnotation(classDescription)
                        .orElseGet(() -> readAnnotation(classDescription.packageDescription()).orElse(""))
        );
    }

    private Optional<String> readAnnotation(Description description) {
        return description.findAnnotation(Bundle.class)
                .map(Bundle::value);
    }
}
