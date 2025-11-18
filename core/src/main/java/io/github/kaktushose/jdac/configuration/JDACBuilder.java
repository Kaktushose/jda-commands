package io.github.kaktushose.jdac.configuration;

import dev.goldmensch.fluava.Fluava;
import io.github.kaktushose.jdac.JDAContext;
import io.github.kaktushose.jdac.definitions.description.ClassFinder;
import io.github.kaktushose.jdac.definitions.description.Descriptor;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import io.github.kaktushose.jdac.definitions.interactions.command.CommandDefinition;
import io.github.kaktushose.jdac.dispatching.adapter.TypeAdapter;
import io.github.kaktushose.jdac.dispatching.expiration.ExpirationStrategy;
import io.github.kaktushose.jdac.dispatching.instance.InteractionControllerInstantiator;
import io.github.kaktushose.jdac.dispatching.middleware.Middleware;
import io.github.kaktushose.jdac.dispatching.middleware.Priority;
import io.github.kaktushose.jdac.dispatching.validation.Validator;
import io.github.kaktushose.jdac.embeds.EmbedConfig;
import io.github.kaktushose.jdac.embeds.error.DefaultErrorMessageFactory;
import io.github.kaktushose.jdac.embeds.error.ErrorMessageFactory;
import io.github.kaktushose.jdac.embeds.internal.Embeds;
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
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.emoji.ApplicationEmoji;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.utils.Result;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static dev.goldmensch.fluava.Bundle.log;

public class JDACBuilder {
    private final Map<PropertyType<?>, SortedSet<PropertyProvider<?>>> properties = new HashMap<>();

    JDACBuilder(JDA jda) {
        // must be set
        addFallback(PropertyTypes.JDA_CONTEXT, _ -> new JDAContext(jda));

        // defaults
        addFallback(PropertyTypes.PACKAGES, _ -> List.of());

        addFallback(PropertyTypes.EXPIRATION_STRATEGY, _ -> ExpirationStrategy.AFTER_15_MINUTES);
        addFallback(PropertyTypes.GLOBAL_COMMAND_CONFIG, _ -> new CommandDefinition.CommandConfig());
        addFallback(PropertyTypes.SHUTDOWN_JDA, _ -> true);
        addFallback(PropertyTypes.LOCALIZE_COMMANDS, _ -> true);
        addFallback(PropertyTypes.LOCALIZER, _ -> new FluavaLocalizer(Fluava.create(Locale.ENGLISH)));
        addFallback(PropertyTypes.PERMISSION_PROVIDER, _ -> new DefaultPermissionsProvider());
        addFallback(PropertyTypes.ERROR_MESSAGE_FACTORY, ctx -> new DefaultErrorMessageFactory(ctx.get(PropertyTypes.EMBED_CONFIG).buildError()));
        addFallback(PropertyTypes.GUILD_SCOPE_PROVIDER, _ -> new DefaultGuildScopeProvider());
        addFallback(PropertyTypes.DESCRIPTOR, _ -> Descriptor.REFLECTIVE);

        addFallback(PropertyTypes.EMOJI_SOURCES, _ -> List.of(EmojiSource.reflective()));
        addFallback(PropertyTypes.CLASS_FINDER, ctx -> {
            String[] resources = ctx.get(PropertyTypes.PACKAGES).toArray(String[]::new);
            return List.of(ClassFinder.reflective(resources));
        });

        // non settable services
        addFallback(PropertyTypes.EMBEDS, ctx -> ctx.get(PropertyTypes.EMBED_CONFIG).buildDefault());

        addFallback(PropertyTypes.I18N, ctx -> new I18n(ctx.get(PropertyTypes.DESCRIPTOR), ctx.get(PropertyTypes.LOCALIZER)));
        addFallback(PropertyTypes.MESSAGE_RESOLVER,
                ctx -> new MessageResolver(ctx.get(PropertyTypes.I18N), ctx.get(PropertyTypes.EMOJI_RESOLVER)));

        addFallback(PropertyTypes.EMOJI_RESOLVER, ctx -> {
            JDAContext jdaContext = ctx.get(PropertyTypes.JDA_CONTEXT);
            registerAppEmojis(jdaContext, ctx.get(PropertyTypes.EMOJI_SOURCES));

            List<ApplicationEmoji> applicationEmojis = jdaContext.applicationEmojis();
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
        return addUserProperty(PropertyTypes.PACKAGES, _ -> List.of(packages));
    }

    public JDACBuilder classFinders(ClassFinder... classFinders) {
        return addUserProperty(PropertyTypes.CLASS_FINDER, _ -> List.of(classFinders));
    }

    public JDACBuilder descriptor(Descriptor descriptor) {
        return addUserProperty(PropertyTypes.DESCRIPTOR, _ -> descriptor);
    }

    public JDACBuilder embeds(Consumer<EmbedConfig> consumer) {
        return addUserProperty(PropertyTypes.EMBED_CONFIG, ctx -> {
            Embeds.Configuration embedConfig = new Embeds.Configuration(ctx.get(PropertyTypes.MESSAGE_RESOLVER));
            try {
                consumer.accept(embedConfig);
            } catch (Exception e) {
                if (ctx.get(PropertyTypes.SHUTDOWN_JDA)) ctx.get(PropertyTypes.JDA_CONTEXT).shutdown();
                throw e;
            }

            return embedConfig;
        });
    }

    public JDACBuilder localizer(Localizer localizer) {
        return addUserProperty(PropertyTypes.LOCALIZER, _ -> localizer);
    }

    public JDACBuilder instanceProvider(InteractionControllerInstantiator instanceProvider) {
        return addUserProperty(PropertyTypes.INTERACTION_CONTROLLER_INSTANTIATOR, _ -> instanceProvider);
    }

    public JDACBuilder expirationStrategy(ExpirationStrategy strategy) {
        return addUserProperty(PropertyTypes.EXPIRATION_STRATEGY, _ -> strategy);
    }

    public JDACBuilder middleware(Priority priority, Middleware middleware) {
        return addUserProperty(PropertyTypes.MIDDLEWARE, _ -> List.of(Map.entry(priority, middleware)));
    }

    public JDACBuilder adapter(Class<?> source, Class<?> target, TypeAdapter<?, ?> adapter) {
        return addUserProperty(PropertyTypes.TYPE_ADAPTER, _ -> Map.of(Map.entry(Type.of(source), Type.of(target)), adapter));
    }

    public JDACBuilder validator(Class<? extends Annotation> annotation, Validator<?, ?> validator) {
        return addUserProperty(PropertyTypes.VALIDATOR, _ -> Map.of(annotation, validator));
    }

    public JDACBuilder permissionsProvider(PermissionsProvider permissionsProvider) {
        return addUserProperty(PropertyTypes.PERMISSION_PROVIDER, _ -> permissionsProvider);
    }

    public JDACBuilder errorMessageFactory(ErrorMessageFactory errorMessageFactory) {
        return addUserProperty(PropertyTypes.ERROR_MESSAGE_FACTORY, _ -> errorMessageFactory);
    }

    public JDACBuilder guildScopeProvider(GuildScopeProvider guildScopeProvider) {
        return addUserProperty(PropertyTypes.GUILD_SCOPE_PROVIDER, _ -> guildScopeProvider);
    }

    public JDACBuilder globalReplyConfig(InteractionDefinition.ReplyConfig globalReplyConfig) {
        return addUserProperty(PropertyTypes.GLOBAL_REPLY_CONFIG, _ -> globalReplyConfig);
    }

    public JDACBuilder globalCommandConfig(CommandDefinition.CommandConfig config) {
        return addUserProperty(PropertyTypes.GLOBAL_COMMAND_CONFIG, _ -> config);
    }

    public JDACBuilder extensionData(Extension.Data... data) {
        return addUserProperty(PropertyTypes.EXTENSION_DATA, _ -> Arrays.asList(data));
    }

    public JDACBuilder shutdownJDA(boolean shutdown) {
        return addUserProperty(PropertyTypes.SHUTDOWN_JDA, _ -> shutdown);
    }

    public JDACBuilder localizeCommands(boolean localize) {
        return addUserProperty(PropertyTypes.LOCALIZE_COMMANDS, _ -> localize);
    }

    public JDACBuilder filterExtensions(io.github.kaktushose.jdac.JDACBuilder.FilterStrategy strategy, String... classes) {
        return addUserProperty(PropertyTypes.EXTENSION_FILTER, _ -> new ExtensionFilter(strategy, Arrays.asList(classes)));
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
