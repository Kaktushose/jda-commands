package io.github.kaktushose.jdac.testing.invocation.components;

import io.github.kaktushose.jdac.testing.TestScenario.Context;
import io.github.kaktushose.jdac.testing.invocation.internal.ComponentInvocation;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.Nullable;

public final class ButtonInvocation extends ComponentInvocation<ButtonInteractionEvent> {

    public ButtonInvocation(Context context, String customId, @Nullable MessageEditData lastMessage) {
        super(context, customId, lastMessage, ButtonInteractionEvent.class);
    }

}
