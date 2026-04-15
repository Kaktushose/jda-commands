package io.github.kaktushose.jdac.guice.internal.guice.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.kaktushose.jdac.definitions.description.ClassFinder;
import io.github.kaktushose.jdac.definitions.description.Descriptor;
import io.github.kaktushose.jdac.message.emoji.EmojiResolver;
import io.github.kaktushose.jdac.message.i18n.I18n;
import io.github.kaktushose.jdac.message.placeholder.PlaceholderResolver;
import io.github.kaktushose.jdac.message.resolver.MessageResolver;
import io.github.kaktushose.jdac.property.JDACIntrospection;
import io.github.kaktushose.jdac.property.JDACProperty;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ConfigurationScopeModule extends AbstractModule {

    // stage = CONFIGURATION
    protected final JDACIntrospection introspection;

    public ConfigurationScopeModule(JDACIntrospection introspection) {
        this.introspection = introspection;
    }

    @Provides
    public I18n i18n() {
        return introspection.get(JDACProperty.I18N);
    }

    @Provides
    public MessageResolver messageResolver() {
        return introspection.get(JDACProperty.MESSAGE_RESOLVER);
    }

    @Provides
    public EmojiResolver emojiResolver() {
        return introspection.get(JDACProperty.EMOJI_RESOLVER);
    }

    @Provides
    public PlaceholderResolver placeholderResolver() {
        return introspection.get(JDACProperty.PLACEHOLDER_RESOLVER);
    }

    @Provides
    public Descriptor descriptor() {
        return introspection.get(JDACProperty.DESCRIPTOR);
    }

    @Provides
    public ClassFinder mergedClassFinder() {
        return introspection.get(JDACProperty.MERGED_CLASS_FINDER);
    }

    @Provides
    public JDACIntrospection introspection() {
        return introspection;
    }
}
