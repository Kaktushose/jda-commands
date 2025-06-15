package internal;

import com.github.kaktushose.jda.commands.JDACBuilder;
import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.definitions.description.ClassFinder;
import com.github.kaktushose.jda.commands.definitions.interactions.InteractionDefinition;
import com.github.kaktushose.jda.commands.definitions.interactions.command.CommandDefinition;
import com.github.kaktushose.jda.commands.dispatching.instance.InteractionControllerInstantiator;
import internal.invocation.ButtonInvocation;
import internal.invocation.SlashCommandInvocation;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.hooks.InterfacedEventManager;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import org.jetbrains.annotations.NotNull;
import org.mockito.Mockito;

import java.util.*;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class TestScenario {

    private final IEventManager eventManager;
    private final List<CommandData> commands;

    public TestScenario(IEventManager eventManager, List<CommandData> commands) {
        this.eventManager = eventManager;
        this.commands = commands;
    }

    public static Builder with(Class<?> klass) {
        return new Builder(Mockito.mock(JDA.class), klass);
    }

    public static TestScenario create(Class<?> klass) {
        return new Builder(Mockito.mock(JDA.class), klass).create();
    }

    public SlashCommandInvocation slash(String command) {
        return new SlashCommandInvocation(eventManager, command);
    }

    public ButtonInvocation button(String customId) {
        return new ButtonInvocation(eventManager, customId);
    }

    public Collection<CommandData> commands() {
        return List.copyOf(commands);
    }

    public Optional<SlashCommandData> command(String command) {
        return commands.stream()
                .filter(SlashCommandData.class::isInstance)
                .map(SlashCommandData.class::cast)
                .filter(it -> it.getName().equals(command))
                .findFirst();
    }

    public static final class Builder {

        private final JDA jda;
        private final JDACBuilder jdacBuilder;
        private Consumer<JDACBuilder> consumer = (_) -> {};

        public Builder(@NotNull JDA jda, @NotNull Class<?> klass) {
            this.jda = jda;
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
            jdacBuilder.start();

            return new TestScenario(eventManager, commands);
        }
    }
}
