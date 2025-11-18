package io.github.kaktushose.jdac.configuration;

import io.github.kaktushose.jdac.definitions.description.ClassFinder;
import io.github.kaktushose.jdac.definitions.description.Descriptor;
import io.github.kaktushose.jdac.message.emoji.EmojiSource;

import java.util.Collection;

import static io.github.kaktushose.jdac.configuration.PropertyType.*;
import static io.github.kaktushose.jdac.configuration.PropertyType.FallbackBehaviour.ACCUMULATE;
import static io.github.kaktushose.jdac.configuration.PropertyType.FallbackBehaviour.OVERRIDE;

public class PropertyTypes {
    public static final PropertyType<Collection<String>> PACKAGES =
        new Enumeration<>(String.class, ACCUMULATE);

    public static final PropertyType<Collection<ClassFinder>> CLASS_FINDER =
            new Enumeration<>(ClassFinder.class, OVERRIDE);

    public static final PropertyType<Collection<EmojiSource>> EMOJI_SOURCES =
            new Enumeration<>(EmojiSource.class, ACCUMULATE);

    public static final PropertyType<Descriptor> DESCRIPTOR = new Instance<>(Descriptor.class);
}
