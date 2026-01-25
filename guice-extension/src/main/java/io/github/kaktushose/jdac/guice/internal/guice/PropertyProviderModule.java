package io.github.kaktushose.jdac.guice.internal.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.kaktushose.jdac.configuration.Property;
import io.github.kaktushose.jdac.definitions.description.ClassFinder;
import io.github.kaktushose.jdac.definitions.description.Descriptor;
import io.github.kaktushose.jdac.introspection.Introspection;
import io.github.kaktushose.jdac.message.emoji.EmojiResolver;
import io.github.kaktushose.jdac.message.i18n.I18n;
import io.github.kaktushose.jdac.message.placeholder.PlaceholderResolver;
import io.github.kaktushose.jdac.message.resolver.MessageResolver;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class PropertyProviderModule extends AbstractModule {

    protected final Introspection introspection;

    public PropertyProviderModule(Introspection introspection) {
        this.introspection = introspection;
    }

    @Provides
    public I18n i18n() {
        return introspection.get(Property.I18N);
    }

    @Provides
    public MessageResolver messageResolver() {
        return introspection.get(Property.MESSAGE_RESOLVER);
    }

    @Provides
    public EmojiResolver emojiResolver() {
        return introspection.get(Property.EMOJI_RESOLVER);
    }

    @Provides
    public PlaceholderResolver placeholderResolver() {
        return introspection.get(Property.PLACEHOLDER_RESOLVER);
    }

    @Provides
    public Descriptor descriptor() {
        return introspection.get(Property.DESCRIPTOR);
    }

    @Provides
    public ClassFinder mergedClassFinder() {
        return introspection.get(Property.MERGED_CLASS_FINDER);
    }

    @Provides
    public Introspection introspection() {
        return introspection;
    }
}
