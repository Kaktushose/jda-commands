package io.github.kaktushose.jdac;

import dev.goldmensch.fluava.Fluava;
import io.github.kaktushose.jdac.configuration.*;
import io.github.kaktushose.jdac.definitions.description.ClassFinder;
import io.github.kaktushose.jdac.definitions.description.Descriptor;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import io.github.kaktushose.jdac.definitions.interactions.InteractionRegistry;
import io.github.kaktushose.jdac.definitions.interactions.command.CommandDefinition;
import io.github.kaktushose.jdac.dispatching.FrameworkContext;
import io.github.kaktushose.jdac.dispatching.adapter.TypeAdapter;
import io.github.kaktushose.jdac.dispatching.adapter.internal.TypeAdapters;
import io.github.kaktushose.jdac.dispatching.expiration.ExpirationStrategy;
import io.github.kaktushose.jdac.dispatching.instance.InteractionControllerInstantiator;
import io.github.kaktushose.jdac.dispatching.middleware.Middleware;
import io.github.kaktushose.jdac.dispatching.middleware.Priority;
import io.github.kaktushose.jdac.dispatching.middleware.internal.Middlewares;
import io.github.kaktushose.jdac.dispatching.validation.Validator;
import io.github.kaktushose.jdac.dispatching.validation.internal.Validators;
import io.github.kaktushose.jdac.embeds.EmbedConfig;
import io.github.kaktushose.jdac.embeds.error.DefaultErrorMessageFactory;
import io.github.kaktushose.jdac.embeds.error.ErrorMessageFactory;
import io.github.kaktushose.jdac.embeds.internal.Embeds;
import io.github.kaktushose.jdac.exceptions.internal.JDACException;
import io.github.kaktushose.jdac.extension.Extension;
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
import io.github.kaktushose.proteus.type.Type;
import net.dv8tion.jda.api.entities.emoji.ApplicationEmoji;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static io.github.kaktushose.jdac.configuration.PropertyTypes.*;

public class JDACBuilder {
    public static final Logger log = LoggerFactory.getLogger(JDACBuilder.class);

    private final Map<PropertyType<?>, SortedSet<PropertyProvider<?>>> properties = new HashMap<>();

    JDACBuilder(JDAContext jdaContext) {
        // must be set
        addFallback(JDA_CONTEXT, _ -> jdaContext);

        // defaults
        addFallback(PACKAGES, _ -> List.of());

        addFallback(EXPIRATION_STRATEGY, _ -> ExpirationStrategy.AFTER_15_MINUTES);
        addFallback(GLOBAL_COMMAND_CONFIG, _ -> new CommandDefinition.CommandConfig());
        addFallback(SHUTDOWN_JDA, _ -> true);
        addFallback(LOCALIZE_COMMANDS, _ -> true);
        addFallback(LOCALIZER, _ -> new FluavaLocalizer(Fluava.create(Locale.ENGLISH)));
        addFallback(PERMISSION_PROVIDER, _ -> new DefaultPermissionsProvider());
        addFallback(ERROR_MESSAGE_FACTORY, ctx -> new DefaultErrorMessageFactory(ctx.get(EMBED_CONFIG).buildError()));
        addFallback(GUILD_SCOPE_PROVIDER, _ -> new DefaultGuildScopeProvider());
        addFallback(DESCRIPTOR, _ -> Descriptor.REFLECTIVE);

        addFallback(EMOJI_SOURCES, _ -> List.of(EmojiSource.reflective()));
        addFallback(CLASS_FINDER, ctx -> {
            String[] resources = ctx.get(PACKAGES).toArray(String[]::new);
            return List.of(ClassFinder.reflective(resources));
        });
        addFallback(EMBED_CONFIG, ctx -> new Embeds.Configuration(ctx.get(MESSAGE_RESOLVER)));

        // non settable services
        addFallback(EMBEDS, ctx -> ctx.get(EMBED_CONFIG).buildDefault());

        addFallback(I18N, ctx -> new I18n(ctx.get(DESCRIPTOR), ctx.get(LOCALIZER)));
        addFallback(MESSAGE_RESOLVER,
                ctx -> new MessageResolver(ctx.get(I18N), ctx.get(EMOJI_RESOLVER)));

        addFallback(EMOJI_RESOLVER, ctx -> {
            registerAppEmojis(ctx.get(JDA_CONTEXT), ctx.get(EMOJI_SOURCES));

            List<ApplicationEmoji> applicationEmojis = ctx.get(JDA_CONTEXT).applicationEmojis();
            return new EmojiResolver(applicationEmojis);
        });
    }

    private void registerAppEmojis(JDAContext context, Collection<EmojiSource> emojiSources) {
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

    private <T> void addFallback(PropertyType<T> type, Function<ConfigurationContext, T> supplier) {
        properties.computeIfAbsent(type, _ -> new TreeSet<>()).add(new PropertyProvider<>(type, PropertyProvider.FALLBACK_PRIORITY, supplier));
    }

    private <T> JDACBuilder addUserProperty(PropertyType<T> type, Function<ConfigurationContext, T> supplier) {
        properties.computeIfAbsent(type, _ -> new TreeSet<>()).add(new PropertyProvider<>(type, PropertyProvider.USER_PRIORITY, supplier));
        return this;
    }

    public JDACBuilder packages(String... packages) {
        return addUserProperty(PACKAGES, _ -> List.of(packages));
    }

    public JDACBuilder classFinders(ClassFinder... classFinders) {
        return addUserProperty(CLASS_FINDER, _ -> List.of(classFinders));
    }

    public JDACBuilder descriptor(Descriptor descriptor) {
        return addUserProperty(DESCRIPTOR, _ -> descriptor);
    }

    public JDACBuilder embeds(Consumer<EmbedConfig> consumer) {
        return addUserProperty(EMBED_CONFIG, ctx -> {
            Embeds.Configuration embedConfig = new Embeds.Configuration(ctx.get(MESSAGE_RESOLVER));
            try {
                consumer.accept(embedConfig);
            } catch (Exception e) {
                if (ctx.get(SHUTDOWN_JDA)) ctx.get(JDA_CONTEXT).shutdown();
                throw e;
            }

            return embedConfig;
        });
    }

    public JDACBuilder localizer(Localizer localizer) {
        return addUserProperty(LOCALIZER, _ -> localizer);
    }

    public JDACBuilder instanceProvider(InteractionControllerInstantiator instanceProvider) {
        return addUserProperty(INTERACTION_CONTROLLER_INSTANTIATOR, _ -> instanceProvider);
    }

    public JDACBuilder expirationStrategy(ExpirationStrategy strategy) {
        return addUserProperty(EXPIRATION_STRATEGY, _ -> strategy);
    }

    public JDACBuilder middleware(Priority priority, Middleware middleware) {
        return addUserProperty(MIDDLEWARE, _ -> List.of(Map.entry(priority, middleware)));
    }

    public JDACBuilder adapter(Class<?> source, Class<?> target, TypeAdapter<?, ?> adapter) {
        return addUserProperty(TYPE_ADAPTER, _ -> Map.of(Map.entry(Type.of(source), Type.of(target)), adapter));
    }

    public JDACBuilder validator(Class<? extends Annotation> annotation, Validator<?, ?> validator) {
        return addUserProperty(VALIDATOR, _ -> Map.of(annotation, validator));
    }

    public JDACBuilder permissionsProvider(PermissionsProvider permissionsProvider) {
        return addUserProperty(PERMISSION_PROVIDER, _ -> permissionsProvider);
    }

    public JDACBuilder errorMessageFactory(ErrorMessageFactory errorMessageFactory) {
        return addUserProperty(ERROR_MESSAGE_FACTORY, _ -> errorMessageFactory);
    }

    public JDACBuilder guildScopeProvider(GuildScopeProvider guildScopeProvider) {
        return addUserProperty(GUILD_SCOPE_PROVIDER, _ -> guildScopeProvider);
    }

    public JDACBuilder globalReplyConfig(InteractionDefinition.ReplyConfig globalReplyConfig) {
        return addUserProperty(GLOBAL_REPLY_CONFIG, _ -> globalReplyConfig);
    }

    public JDACBuilder globalCommandConfig(CommandDefinition.CommandConfig config) {
        return addUserProperty(GLOBAL_COMMAND_CONFIG, _ -> config);
    }

    public JDACBuilder extensionData(Extension.Data... data) {
        return addUserProperty(EXTENSION_DATA, _ -> Arrays.asList(data));
    }

    public JDACBuilder shutdownJDA(boolean shutdown) {
        return addUserProperty(SHUTDOWN_JDA, _ -> shutdown);
    }

    public JDACBuilder localizeCommands(boolean localize) {
        return addUserProperty(LOCALIZE_COMMANDS, _ -> localize);
    }

    public JDACBuilder filterExtensions(FilterStrategy strategy, String... classes) {
        return addUserProperty(EXTENSION_FILTER, _ -> new ExtensionFilter(strategy, Arrays.asList(classes)));
    }

    public JDACommands start() {
        Loader loader = new Loader(properties);

        try {

            log.info("Starting JDA-Commands...");

            FrameworkContext frameworkContext = new FrameworkContext(
                    new Middlewares(loader.get(MIDDLEWARE), loader.get(ERROR_MESSAGE_FACTORY), loader.get(PERMISSION_PROVIDER)),
                    loader.get(ERROR_MESSAGE_FACTORY),
                    new InteractionRegistry(
                            new Validators(loader.get(VALIDATOR)),
                            loader.get(I18N),
                            loader.get(LOCALIZE_COMMANDS) ? loader.get(I18N).localizationFunction() : (_) -> Map.of(),
                            loader.get(DESCRIPTOR)
                    ),
                    new TypeAdapters(loader.get(TYPE_ADAPTER), loader.get(I18N)),
                    loader.get(EXPIRATION_STRATEGY),
                    loader.get(INTERACTION_CONTROLLER_INSTANTIATOR),
                    loader.get(EMBEDS),
                    loader.get(I18N),
                    loader.get(MESSAGE_RESOLVER),
                    loader.get(GLOBAL_REPLY_CONFIG),
                    loader.get(GLOBAL_COMMAND_CONFIG)
            );

            JDACommands jdaCommands = new JDACommands(
                    frameworkContext,
                    loader.get(JDA_CONTEXT),
                    loader.get(GUILD_SCOPE_PROVIDER),
                    loader.get(SHUTDOWN_JDA)
            );

            ClassFinder merged = annotationClass -> loader.get(CLASS_FINDER)
                    .stream()
                    .map(classFinder -> classFinder.search(annotationClass))
                    .flatMap(Collection::stream)
                    .toList();

            jdaCommands.start(merged);
            return jdaCommands;
        } catch (JDACException e) {
            if (loader.get(SHUTDOWN_JDA)) {
                loader.get(JDA_CONTEXT).shutdown();
            }
            throw e;
        }
    }

    /// The two available filter strategies
    public enum FilterStrategy {
        /// includes the defined classes
        INCLUDE,
        /// excludes the defined classes
        EXCLUDE
    }

    static void main() {
        JDACBuilder builder = new JDACBuilder(null);
        builder.packages("my package");
//        builder.classFinders(ClassFinder.explicit(String.class));

        Loader loader = new Loader(builder.properties);
        var value = loader.get(PropertyTypes.CLASS_FINDER);
        System.out.println(value);
        loader.get(PropertyTypes.CLASS_FINDER);
    }
}
