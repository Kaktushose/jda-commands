package io.github.kaktushose.jdac.testing.reply;

import com.github.kaktushose.jda.commands.definitions.interactions.CustomId;
import io.github.kaktushose.jdac.testing.TestScenario.Context;
import io.github.kaktushose.jdac.testing.invocation.components.ButtonInvocation;
import io.github.kaktushose.jdac.testing.invocation.components.EntitySelectInvocation;
import io.github.kaktushose.jdac.testing.invocation.internal.Invocation;
import io.github.kaktushose.jdac.testing.invocation.components.StringSelectInvocation;
import io.github.kaktushose.jdac.testing.invocation.internal.ReplyableInvocation;
import io.github.kaktushose.jdac.testing.reply.internal.EventReply;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class MessageEventReply extends EventReply {

    private final MessageData reply;
    private final List<ActionComponent> components;

    public MessageEventReply(ReplyableInvocation<?> invocation, Context context, MessageData data) {
        super(invocation, context);
        reply = data;
        components = reply.getComponents().stream()
                .map(LayoutComponent::getActionComponents)
                .flatMap(Collection::stream)
                .toList();
    }

    public String content() {
        return reply.getContent();
    }

    public List<MessageEmbed> embeds() {
        return reply.getEmbeds();
    }

    public ButtonInvocation button(String button) {
        return new ButtonInvocation(context, customId(button), invocation.lastMessage());
    }

    public StringSelectInvocation stringSelect(String menu) {
        return new StringSelectInvocation(context, customId(menu), invocation.lastMessage());
    }

    public EntitySelectInvocation entitySelect(String menu) {
        return new EntitySelectInvocation(context, customId(menu), invocation.lastMessage());
    }

    public List<ActionComponent> components() {
        return components;
    }

    public Optional<Button> findButton(String component) {
        return findComponent(component, Button.class);
    }

    public Optional<StringSelectMenu> findStringSelect(String component) {
        return findComponent(component, StringSelectMenu.class);
    }

    public Optional<EntitySelectMenu> findEntitySelect(String component) {
        return findComponent(component, EntitySelectMenu.class);
    }

    private  <T extends ActionComponent> Optional<T> findComponent(String component, Class<T> type) {
        String definitionId = definitionId(component);
        return components.stream()
                .filter(type::isInstance)
                .filter(it -> it.getId() != null)
                .filter(it -> CustomId.fromMerged(it.getId()).definitionId().equals(definitionId))
                .findAny()
                .map(type::cast);
    }

    public boolean isEdit() {
        return reply instanceof MessageEditData;
    }

    public MessageCreateData asCreateData() {
        if (reply instanceof MessageCreateData data) {
            return data;
        } else if (reply instanceof MessageEditData data) {
            return MessageCreateData.fromEditData(data);
        } else {
            throw new IllegalStateException();
        }
    }

    public MessageEditData asEditData() {
        if (reply instanceof MessageEditData data) {
            return data;
        } else if (reply instanceof MessageCreateData data) {
            return MessageEditData.fromCreateData(data);
        } else {
            throw new IllegalStateException();
        }
    }

    private String customId(String component) {
        return new CustomId(runtimeId(component), definitionId(component)).merged();
    }

    private String runtimeId(String component) {
        String definitionId = definitionId(component);
        return components.stream()
                .map(ActionComponent::getId)
                .filter(Objects::nonNull)
                .map(CustomId::fromMerged)
                .filter(it -> it.definitionId().equals(definitionId))
                .findAny()
                .map(CustomId::runtimeId)
                .orElseThrow();
    }

    private String definitionId(String component) {
        return String.valueOf((context.klass().getName() + component).hashCode());
    }
}
