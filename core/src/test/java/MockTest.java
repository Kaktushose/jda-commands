import com.github.kaktushose.jda.commands.JDACommands;
import com.github.kaktushose.jda.commands.annotations.interactions.Command;
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.definitions.description.ClassFinder;
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent;
import com.github.kaktushose.jda.commands.dispatching.instance.InteractionControllerInstantiator;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.hooks.InterfacedEventManager;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MockTest {

    private static final String REPLY = "Hello World";
    @Mock
    private JDA jda;
    private IEventManager eventManager;
    @Mock
    private SnowflakeCacheView<Guild> snowflakeCacheView;
    @Mock
    private CommandListUpdateAction commandListUpdateAction;
    private List<CommandData> commands = new ArrayList<>();

    @BeforeEach
    void init() {
        eventManager = new InterfacedEventManager();
        jda.setEventManager(eventManager);
        when(snowflakeCacheView.iterator()).thenReturn(Collections.emptyIterator());
        when(jda.getGuildCache()).thenReturn(snowflakeCacheView);
        when(jda.updateCommands()).thenReturn(commandListUpdateAction);
        doAnswer(invocation -> {
            eventManager.register(invocation.getArgument(0));
            return null;
        }).when(jda).addEventListener(any());
        when(commandListUpdateAction.addCommands(anyCollection())).then(invocation -> {
            commands.addAll(invocation.getArgument(0));
            return invocation.getMock();
        });

        JDACommands.builder(jda, MockTest.class, "")
                .classFinders(ClassFinder.explicit(TestController.class))
                .instanceProvider(new InteractionControllerInstantiator() {
                    @Override
                    public <T> T instance(Class<T> clazz, Context context) {
                        return spy(clazz);
                    }
                }).start();
    }

    @Test
    void test() {
        var event = mock(SlashCommandInteractionEvent.class);
        var user = mock(User.class);
        var hook = mock(InteractionHook.class);
        CompletableFuture<MessageEditData> reply = new CompletableFuture<>();
        when(event.getFullCommandName()).thenReturn("test");
        when(event.getUser()).thenReturn(user);
        when(event.deferReply(anyBoolean())).thenReturn(mock(ReplyCallbackAction.class));
        when(event.getHook()).thenReturn(hook);
        when(hook.editOriginal(any(MessageEditData.class))).then(invocation -> {
            reply.complete(invocation.getArgument(0));
            return mock(WebhookMessageEditAction.class);
        });

        eventManager.handle(event);

        assertEquals(REPLY, reply.join().getContent());
    }

    @Interaction
    public static class TestController {

        @Command("test")
        public void onCommand(CommandEvent event) {
            event.reply(REPLY);
        }
    }
}
