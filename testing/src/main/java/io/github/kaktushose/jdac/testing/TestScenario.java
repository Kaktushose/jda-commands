package io.github.kaktushose.jdac.testing;

import com.github.kaktushose.jda.commands.JDACBuilder;
import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.definitions.description.ClassFinder;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.CommandDefinition;
import com.github.kaktushose.jda.commands.dispatching.instance.InteractionControllerInstantiator;
import io.github.kaktushose.jdac.testing.invocation.commands.SlashCommandInvocation;
import io.github.kaktushose.jdac.testing.invocation.components.ButtonInvocation;
import io.github.kaktushose.jdac.testing.invocation.components.EntitySelectInvocation;
import io.github.kaktushose.jdac.testing.invocation.components.StringSelectInvocation;
import io.github.kaktushose.jdac.testing.invocation.internal.Invocation;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.hooks.InterfacedEventManager;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.*;

public class TestScenario {

    private final Context context;

    public TestScenario(Context context) {
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
        return new ButtonInvocation(context, button, null);
    }

    public StringSelectInvocation stringSelect(String menu) {
        return new StringSelectInvocation(context, menu, null);
    }

    public EntitySelectInvocation entitySelect(String menu) {
        return new EntitySelectInvocation(context, menu, null);
    }

    public Optional<SlashCommandData> command(String command) {
        return context.commands().stream()
                .filter(SlashCommandData.class::isInstance)
                .map(SlashCommandData.class::cast)
                .filter(it -> it.getName().equals(command))
                .findFirst();
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
            jdacBuilder = JDACommands.builder(jda, klass)
                    .classFinders(ClassFinder.explicit(klass))
                    .instanceProvider(new InteractionControllerInstantiator() {
                        @Override
                        public <T> T instance(Class<T> clazz, Context context) {
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

            // cache commands passed to SlashCommandUpdater
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

    public record Context(IEventManager eventManager, Class<?> klass, JDACommands jdaCommands,
                          List<CommandData> commands) {

        public Context {
            commands = Collections.unmodifiableList(commands);
        }
    }
}
