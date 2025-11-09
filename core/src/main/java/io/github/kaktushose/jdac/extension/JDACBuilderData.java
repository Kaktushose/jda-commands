package io.github.kaktushose.jdac.extension;

import io.github.kaktushose.jdac.JDACBuilder;
import io.github.kaktushose.jdac.JDACommands;
import io.github.kaktushose.jdac.JDAContext;
import io.github.kaktushose.jdac.definitions.description.ClassFinder;
import io.github.kaktushose.jdac.definitions.description.Descriptor;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import io.github.kaktushose.jdac.definitions.interactions.command.CommandDefinition.CommandConfig;
import io.github.kaktushose.jdac.dispatching.adapter.TypeAdapter;
import io.github.kaktushose.jdac.dispatching.expiration.ExpirationStrategy;
import io.github.kaktushose.jdac.dispatching.instance.InteractionControllerInstantiator;
import io.github.kaktushose.jdac.dispatching.middleware.Middleware;
import io.github.kaktushose.jdac.dispatching.middleware.Priority;
import io.github.kaktushose.jdac.dispatching.validation.Validator;
import io.github.kaktushose.jdac.embeds.error.DefaultErrorMessageFactory;
import io.github.kaktushose.jdac.embeds.error.ErrorMessageFactory;
import io.github.kaktushose.jdac.embeds.internal.Embeds;
import io.github.kaktushose.jdac.exceptions.ConfigurationException;
import io.github.kaktushose.jdac.extension.Implementation.ExtensionProvidable;
import io.github.kaktushose.jdac.extension.internal.ExtensionFilter;
import io.github.kaktushose.jdac.message.MessageResolver;
import io.github.kaktushose.jdac.message.emoji.EmojiResolver;
import io.github.kaktushose.jdac.message.emoji.EmojiSource;
import io.github.kaktushose.jdac.message.i18n.FluavaLocalizer;
import io.github.kaktushose.jdac.message.i18n.I18n;
import io.github.kaktushose.jdac.message.i18n.Localizer;
import io.github.kaktushose.jdac.permissions.DefaultPermissionsProvider;
import io.github.kaktushose.jdac.permissions.PermissionsProvider;
import io.github.kaktushose.jdac.scope.DefaultGuildScopeProvider;
import io.github.kaktushose.jdac.scope.GuildScopeProvider;
import dev.goldmensch.fluava.Fluava;
import io.github.kaktushose.proteus.type.Type;
import net.dv8tion.jda.api.entities.emoji.ApplicationEmoji;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.utils.Result;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static io.github.kaktushose.jdac.message.placeholder.Entry.entry;

/// Readonly view of a [JDACBuilder]. Acts as a snapshot of the current builder state during jda-commands startup.
///
/// @implNote This class is used to give implementations of [Extension] access to properties involved in the creation of [JDACommands].
///
/// Please note that this class only gives read not write access to the [JDACBuilder].
public sealed class JDACBuilderData permits JDACBuilder {
    public static final Logger log = LoggerFactory.getLogger(JDACBuilderData.class);

    // used for cycling dependency detection
    final List<Implementation<?>> alreadyCalled = new ArrayList<>();

    protected final Class<?> baseClass;
    protected final String[] packages;
    protected final JDAContext context;

    // extension stuff
    protected @Nullable Collection<Extension<Extension.Data>> loadedExtensions = null;
    protected final Map<Class<? extends Extension.Data>, Extension.Data> extensionData = new HashMap<>();
    protected ExtensionFilter extensionFilter = new ExtensionFilter(JDACBuilder.FilterStrategy.EXCLUDE, List.of());

    // loadable by extension
    protected @Nullable InteractionControllerInstantiator controllerInstantiator = null;

    protected ExpirationStrategy expirationStrategy = ExpirationStrategy.AFTER_15_MINUTES;

    protected @Nullable PermissionsProvider permissionsProvider = null;
    protected @Nullable ErrorMessageFactory errorMessageFactory = null;
    protected @Nullable GuildScopeProvider guildScopeProvider = null;
    protected @Nullable Descriptor descriptor = null;
    protected @Nullable Localizer localizer = null;

    // loadable by extensions (addition)
    protected Collection<EmojiSource> emojiSources;
    protected Collection<ClassFinder> classFinders;
    protected final Set<Entry<Priority, Middleware>> middlewares = new HashSet<>();
    protected final Map<Class<? extends Annotation>, Validator<?, ?>> validators = new HashMap<>();
    protected final Map<Map.Entry<Type<?>, Type<?>>, TypeAdapter<?, ?>> typeAdapters = new HashMap<>();

    // only user settable
    protected InteractionDefinition.ReplyConfig globalReplyConfig = new InteractionDefinition.ReplyConfig();
    protected CommandConfig globalCommandConfig = new CommandConfig();

    protected boolean shutdownJDA = true;
    protected boolean localizeCommands = true;

    protected @Nullable Embeds embeds = null;
    private @Nullable I18n i18n = null;
    private @Nullable MessageResolver messageResolver = null;

    protected JDACBuilderData(Class<?> baseClass, String[] packages, JDAContext context) {
        this.baseClass = baseClass;
        this.packages = packages;
        this.context = context;
        this.classFinders = List.of(ClassFinder.reflective(packages));
        this.emojiSources = List.of(EmojiSource.reflective());
    }

    @SuppressWarnings("unchecked")
    private Collection<Extension<Extension.Data>> extensions() {
        if (loadedExtensions == null) {
            loadedExtensions = ServiceLoader.load(Extension.class)
                    .stream()
                    .peek(provider -> log.debug("Found extension: {}", provider.type()))
                    .filter(extensionFilter)
                    .peek(provider -> log.debug("Using extension {}", provider.type()))
                    .map(ServiceLoader.Provider::get)
                    .map(extension -> (Extension<Extension.Data>) extension)
                    .peek(extension -> extension.init(extensionData.get(extension.dataType())))
                    .toList();
        }
        return loadedExtensions;
    }

    @SuppressWarnings("unchecked")
    <T extends ExtensionProvidable> SequencedCollection<Entry<Extension<Extension.Data>, T>> implementations(Class<T> type) {
        return extensions()
                .stream()
                .flatMap(extension -> extension.providedImplementations().stream()
                        .filter(implementation -> implementation.type().isAssignableFrom(type))
                        .flatMap(implementation -> implementation.implementations(this).stream())
                        .map(value -> Map.entry(extension, (T) value))
                ).toList();
    }

    private final Map<Class<?>, Supplier<Object>> defaults = Map.of(
            Localizer.class, () -> new FluavaLocalizer(Fluava.create(Locale.ENGLISH)),
            PermissionsProvider.class, DefaultPermissionsProvider::new,
            ErrorMessageFactory.class, () -> new DefaultErrorMessageFactory(embeds(messageResolver())),
            GuildScopeProvider.class, DefaultGuildScopeProvider::new,
            Descriptor.class, () -> Descriptor.REFLECTIVE
    );

    private final ClassValue<Object> loader = new ClassValue<>() {
        @SuppressWarnings("unchecked")
        @Override
        protected Object computeValue(Class<?> type) {
            var implementations = implementations((Class<? extends ExtensionProvidable>) type);

            if (implementations.isEmpty()) {
                if (!defaults.containsKey(type)) {
                    if (shutdownJDA()) context.shutdown();
                    throw new ConfigurationException("missing-implementation", entry("type", type.getName()));
                }

                return defaults.get(type).get();
            }

            if (implementations.size() == 1) {
                return implementations.getFirst().getValue();
            }

            String foundImplementations = implementations.stream()
                    .map(entry -> "extension %s -> %s".formatted(entry.getKey(), entry.getValue()))
                    .collect(Collectors.joining(System.lineSeparator()));

            if (shutdownJDA()) context.shutdown();

            throw new ConfigurationException("multiple-implementations", entry("type", type.getName()), entry("found", foundImplementations));
        }
    };


    @SuppressWarnings("unchecked")
    private <T extends ExtensionProvidable> T load(Class<T> type, @Nullable T setValue) {
        if (setValue != null) return setValue;
        return (T) loader.get(type);
    }

    /// the packages provided by the user for classpath scanning
    public String[] packages() {
        return packages;
    }

    /// the [JDAContext] to be used
    public JDAContext context() {
        return context;
    }

    /// the base class provided by the user for classpath scanning
    public Class<?> baseClass() {
        return baseClass;
    }

    /// @return the global [InteractionDefinition.ReplyConfig] provided by the user
    public InteractionDefinition.ReplyConfig globalReplyConfig() {
        return globalReplyConfig;
    }

    public CommandConfig globalCommandConfig() {
        return globalCommandConfig;
    }

    /// @return the [ExpirationStrategy] to be used
    public ExpirationStrategy expirationStrategy() {
        return expirationStrategy;
    }

    /// @return whether the JDA instance should be shutdown if the configuration/start of JDA-Commands fails or [JDACommands#shutdown()] is called.
    public boolean shutdownJDA() {
        return shutdownJDA;
    }

    /// @return whether JDA-Commands should use the [I18n] feature to localize commands.
    public boolean localizeCommands() {
        return localizeCommands;
    }

    // loadable - no defaults
    /// @return the [InteractionControllerInstantiator] to be used. Can be added via an [Extension]
    public InteractionControllerInstantiator controllerInstantiator() {
        return load(InteractionControllerInstantiator.class, controllerInstantiator);
    }

    // loadable - defaults
    /// @return the [Localizer] to be used. Can be added via an [Extension]
    public Localizer localizer() {
        return load(Localizer.class, localizer);
    }

    /// @return the [PermissionsProvider] to be used. Can be added via an [Extension]
    public PermissionsProvider permissionsProvider() {
        return load(PermissionsProvider.class, permissionsProvider);
    }

    /// @return the [ErrorMessageFactory] to be used. Can be added via an [Extension]
    public ErrorMessageFactory errorMessageFactory() {
        return load(ErrorMessageFactory.class, errorMessageFactory);
    }

    /// @return the [GuildScopeProvider] to be used. Can be added via an [Extension]
    public GuildScopeProvider guildScopeProvider() {
        return load(GuildScopeProvider.class, guildScopeProvider);
    }

    /// @return the [Descriptor] to be used. Can be added via an [Extension]
    public Descriptor descriptor() {
        return load(Descriptor.class, descriptor);
    }

    /// @return the [ClassFinder]s to be used. Can be added via an [Extension]
    public Collection<ClassFinder> classFinders() {
        Collection<ClassFinder> all = implementations(ClassFinder.class)
                .stream()
                .map(Entry::getValue)
                .collect(Collectors.toSet());
        all.addAll(classFinders);
        return all;
    }

    /// @return the [EmojiSource]s to be used. Can be added via an [Extension]
    public Collection<EmojiSource> emojiSources() {
        Collection<EmojiSource> all = implementations(EmojiSource.class)
                .stream()
                .map(Entry::getValue)
                .collect(Collectors.toSet());
        all.addAll(emojiSources);
        return all;
    }

    /// @return a [ClassFinder] that searches in all [ClassFinder]s returned by [#classFinders()]
    public ClassFinder mergedClassFinder() {
        Collection<ClassFinder> classFinders = classFinders();

        return annotationClass -> classFinders
                .stream()
                .map(classFinder -> classFinder.search(annotationClass))
                .flatMap(Collection::stream)
                .toList();

    }

    /// @return the [Middleware]s to be used. Can be added via an [Extension]
    public Collection<Entry<Priority, Middleware>> middlewares() {
        Collection<Entry<Priority, Middleware>> all = implementations(Implementation.MiddlewareContainer.class)
                .stream()
                .map(Entry::getValue)
                .map(container -> Map.entry(container.priority(), container.middleware()))
                .collect(Collectors.toSet());
        all.addAll(middlewares);
        return all;
    }

    /// @return the [Validator]s to be used. Can be added via an [Extension]
    public Map<Class<? extends Annotation>, Validator<?, ?>> validators() {
        Map<Class<? extends Annotation>, Validator<?, ?>> all = implementations(Implementation.ValidatorContainer.class)
                .stream()
                .map(Entry::getValue)
                .collect(Collectors.toMap(Implementation.ValidatorContainer::annotation, Implementation.ValidatorContainer::validator));
        all.putAll(validators);
        return all;
    }

    /// @return the [TypeAdapter]s to be used. Can be added via an [Extension]
    @SuppressWarnings("unchecked")
    public Map<Entry<Type<?>, Type<?>>, TypeAdapter<?, ?>> typeAdapters() {
        Map<Entry<Type<?>, Type<?>>, TypeAdapter<?, ?>> all = implementations(Implementation.TypeAdapterContainer.class)
                .stream()
                .map(Entry::getValue)
                .collect(Collectors.toMap((it -> Map.entry(it.source(), it.target())), Implementation.TypeAdapterContainer::adapter));
        all.putAll(typeAdapters);
        return all;
    }

    protected Embeds embeds(MessageResolver messageResolver) {
        return embeds != null ? embeds : (embeds = new Embeds(Collections.emptyList(), Collections.emptyMap(), messageResolver));
    }

    /// @return the used [I18n] instance
    public I18n i18n() {
        return i18n != null ? i18n : (i18n = new I18n(descriptor(), localizer()));
    }

    /// @return the used [MessageResolver] instance
    protected MessageResolver messageResolver() {
        if (messageResolver == null) {
            registerAppEmojis();

            List<ApplicationEmoji> applicationEmojis = context().applicationEmojis();
            messageResolver = new MessageResolver(i18n(), new EmojiResolver(applicationEmojis));
        }

        return messageResolver;
    }

    private void registerAppEmojis() {
        context.performTask(jda -> emojiSources.stream()
                .map(EmojiSource::get)
                .map(Map::entrySet)
                .flatMap(Set::stream)
                .forEach(entry -> {
                    Result<ApplicationEmoji> result = jda.createApplicationEmoji(entry.getKey(), entry.getValue())
                            .mapToResult()
                            .complete();

                    if (result.isSuccess()) {
                        log.debug("Registered new application emoji with name {}", entry.getKey());
                        return;
                    }

                    if (result.isFailure() && result.getFailure() instanceof ErrorResponseException e) {
                        List<String> codes = e.getSchemaErrors()
                                .stream()
                                .map(ErrorResponseException.SchemaError::getErrors)
                                .flatMap(List::stream)
                                .map(ErrorResponseException.ErrorCode::getCode)
                                .toList();

                        if (codes.size() == 1 && codes.contains("APPLICATION_EMOJI_NAME_ALREADY_TAKEN")) {
                            log.debug("Application emoji with name {} already registered", entry.getKey());
                            return;
                        }
                    }

                    log.error("Couldn't register emoji with name {}", entry.getKey(), result.getFailure());
                }), true);
    }

}
