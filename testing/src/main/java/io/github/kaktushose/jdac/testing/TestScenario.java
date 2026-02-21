package io.github.kaktushose.jdac.testing;

import io.github.kaktushose.jdac.JDACBuilder;
import io.github.kaktushose.jdac.JDACommands;
import io.github.kaktushose.jdac.definitions.description.ClassFinder;
import io.github.kaktushose.jdac.definitions.interactions.CustomId;
import io.github.kaktushose.jdac.definitions.interactions.InteractionDefinition;
import io.github.kaktushose.jdac.definitions.interactions.command.CommandDefinition;
import io.github.kaktushose.jdac.dispatching.instance.InteractionControllerInstantiator;
import io.github.kaktushose.jdac.introspection.Introspection;
import io.github.kaktushose.jdac.message.i18n.FluavaLocalizer;
import io.github.kaktushose.jdac.message.i18n.Localizer;
import dev.goldmensch.fluava.Fluava;
import io.github.kaktushose.jdac.testing.invocation.AutoCompleteInvocation;
import io.github.kaktushose.jdac.testing.invocation.commands.ContextCommandInvocation;
import io.github.kaktushose.jdac.testing.invocation.commands.SlashCommandInvocation;
import io.github.kaktushose.jdac.testing.invocation.components.ButtonInvocation;
import io.github.kaktushose.jdac.testing.invocation.components.EntitySelectInvocation;
import io.github.kaktushose.jdac.testing.invocation.components.StringSelectInvocation;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.hooks.InterfacedEventManager;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.*;

public class TestScenario {

    private static final Localizer localizer = FluavaLocalizer.create(Fluava.create(Locale.ENGLISH));
    private final Context context;

    private TestScenario(Context context) {
        this.context = context;
    }

    public static Builder with(Class<?> klass) {
        return new Builder(mock(JDA.class), klass);
    }

    public static TestScenario create(Class<?> klass) {
        return new Builder(mock(JDA.class), klass).create();
    }

    public SlashCommandInvocation slash(String command) {
        return new SlashCommandInvocation(context, command);
    }

    public ButtonInvocation button(String button) {
        return new ButtonInvocation(context, customId(button), null);
    }

    public StringSelectInvocation stringSelect(String menu) {
        return new StringSelectInvocation(context, customId(menu), null);
    }

    public EntitySelectInvocation entitySelect(String menu) {
        return new EntitySelectInvocation(context, customId(menu), null);
    }

    public Optional<SlashCommandData> command(String command) {
        return context.commands().stream()
                .filter(SlashCommandData.class::isInstance)
                .map(SlashCommandData.class::cast)
                .filter(it -> it.getName().equals(command))
                .findFirst();
    }

    public ContextCommandInvocation<User> context(String command, User target) {
        return new ContextCommandInvocation<>(context, command, target);
    }

    public ContextCommandInvocation<Message> context(String command, Message target) {
        return new ContextCommandInvocation<>(context, command, target);
    }

    public AutoCompleteInvocation autoComplete(String command, String option) {
        return new AutoCompleteInvocation(context, command, option);
    }

    private String customId(String component) {
        return new CustomId("independent", String.valueOf((context.klass().getName() + component).hashCode())).merged();
    }

    public static final class Builder {

        private final JDA jda;
        private final Class<?> klass;
        private final JDACBuilder jdacBuilder;
        private Consumer<JDACBuilder> consumer = (_) -> {
        };

        public Builder(@NotNull JDA jda, @NotNull Class<?> klass) {
            this.jda = jda;
            this.klass = klass;
            jdacBuilder = JDACommands.builder(jda)
                    .localizer(localizer)
                    .classFinders(ClassFinder.explicit(klass))
                    .instanceProvider(new InteractionControllerInstantiator() {
                        @Override
                        public <T> T instance(Class<T> clazz, Introspection context) {
                            return spy(clazz);
                        }
                    });
        }

        @NotNull
        public Builder replyConfig(@NotNull InteractionDefinition.ReplyConfig config) {
            jdacBuilder.globalReplyConfig(config);
            return this;
        }

        @NotNull
        public Builder commandConfig(@NotNull CommandDefinition.CommandConfig config) {
            jdacBuilder.globalCommandConfig(config);
            return this;
        }

        public Builder configure(Consumer<JDACBuilder> consumer) {
            this.consumer = consumer;
            return this;
        }

        @SuppressWarnings("unchecked")
        public TestScenario create() {
            // event manager
            IEventManager eventManager = new InterfacedEventManager();
            jda.setEventManager(eventManager);
            doAnswer(invocation -> {
                eventManager.register(invocation.getArgument(0));
                return null;
            }).when(jda).addEventListener(any());

            // make JDAContext functional
            SnowflakeCacheView<Guild> snowflakeCacheView = mock(SnowflakeCacheView.class);
            when(snowflakeCacheView.iterator()).thenReturn(Collections.emptyIterator());
            when(jda.getGuildCache()).thenReturn(snowflakeCacheView);
            when(jda.retrieveApplicationEmojis()).thenReturn(new AppEmojiRestAction());

            // cache commands passed to CommandUpdater
            List<CommandData> commands = new ArrayList<>();
            CommandListUpdateAction commandListUpdateAction = mock(CommandListUpdateAction.class);
            when(jda.updateCommands()).thenReturn(commandListUpdateAction);
            when(commandListUpdateAction.addCommands(anyCollection())).then(invocation -> {
                commands.addAll(invocation.getArgument(0));
                return invocation.getMock();
            });

            consumer.accept(jdacBuilder);
            JDACommands jdaCommands = jdacBuilder.start();

            return new TestScenario(new Context(eventManager, klass, jdaCommands, commands));
        }
    }

    public static class AppEmojiRestAction implements RestAction {
        @Override
        public @NotNull JDA getJDA() {
            return null;
        }

        @Override
        public @NotNull RestAction setCheck(@Nullable BooleanSupplier checks) {
            return null;
        }

        @Override
        public Object complete(boolean shouldQueue) {
            return List.of();
        }

        @Override
        public @NotNull CompletableFuture submit(boolean shouldQueue) {
            return null;
        }

        @Override
        public void queue(@Nullable Consumer success, @Nullable Consumer failure) {

        }
    }

    public record Context(IEventManager eventManager, Class<?> klass, JDACommands jdaCommands,
                          List<CommandData> commands) {

        public Context {
            commands = Collections.unmodifiableList(commands);
        }
    }
}
