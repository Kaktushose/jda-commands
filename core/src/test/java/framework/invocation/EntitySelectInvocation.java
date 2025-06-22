package framework.invocation;

import framework.TestScenario;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectInteraction;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.Nullable;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

public final class EntitySelectInvocation extends ComponentInvocation<EntitySelectInteractionEvent> {

    private Mentions mentions;

    public EntitySelectInvocation(TestScenario.Context context, String customId, @Nullable MessageEditData lastMessage) {
        super(context, customId, lastMessage, EntitySelectInteractionEvent.class);

        mentions = mock(Mentions.class);
        lenient().when(event.getMentions()).thenReturn(mentions);

        EntitySelectInteraction interaction = mock(EntitySelectInteraction.class);
        lenient().when(event.getInteraction()).thenReturn(interaction);
        lenient().when(interaction.getMentions()).thenReturn(mentions);
    }

    /// This framework doesn't provide functionality for mirroring the entity selection. [Mentions] must be mocked on
    /// the fly when testing
    public EntitySelectInvocation mentions(Mentions mentions) {
        this.mentions = mentions;
        return this;
    }
}
