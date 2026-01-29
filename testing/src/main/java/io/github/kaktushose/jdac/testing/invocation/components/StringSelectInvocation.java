package io.github.kaktushose.jdac.testing.invocation.components;

import io.github.kaktushose.jdac.testing.TestScenario;
import io.github.kaktushose.jdac.testing.invocation.internal.ComponentInvocation;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.mockito.Mockito.lenient;

public final class StringSelectInvocation extends ComponentInvocation<StringSelectInteractionEvent> {

    private List<String> values;

    public StringSelectInvocation(
            TestScenario.Context context,
            String customId,
            @Nullable MessageEditData lastMessage
    ) {
        super(context, customId, lastMessage, StringSelectInteractionEvent.class);

        values = new ArrayList<>();
        lenient().when(event.getValues()).thenReturn(values);
    }

    public StringSelectInvocation values(Collection<String> values) {
        this.values = new ArrayList<>(values);
        return this;
    }

    public StringSelectInvocation values(String... values) {
        this.values.addAll(List.of(values));
        return this;
    }
}
