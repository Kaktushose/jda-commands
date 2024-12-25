package com.github.kaktushose.jda.commands.definitions.interactions.impl;

import com.github.kaktushose.jda.commands.definitions.Definition;
import com.github.kaktushose.jda.commands.definitions.features.JDAEntity;
import com.github.kaktushose.jda.commands.definitions.features.Replyable;
import com.github.kaktushose.jda.commands.definitions.interactions.CustomIdInteraction;
import com.github.kaktushose.jda.commands.definitions.interactions.PermissionsInteraction;
import com.github.kaktushose.jda.commands.definitions.reflect.interactions.ReplyConfig;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.SequencedCollection;
import java.util.Set;

public record SelectMenuDefinition(
        String displayName,
        Method method,
        SequencedCollection<Class<?>> parameters,
        Collection<String> permissions,
        ReplyConfig replyConfig,
        SelectTypes selectTypes,
        String placeholder,
        int minValue,
        int maxValue
) implements JDAEntity<SelectMenu>, Replyable, PermissionsInteraction, CustomIdInteraction {

    @Override
    public SelectMenu toJDAEntity() {
        return null;
    }

    sealed interface SelectTypes {
        record SelectOptionDefinition(String value, String label, String description, @Nullable Emoji emoji, boolean isDefault) implements Definition {
            @Override
            public String displayName() {
                return value;
            }
        }

        record StringSelectTypes(Set<SelectOptionDefinition> selectOptions) implements SelectTypes {}

        record EntitySelectTypes(Set<EntitySelectMenu.SelectTarget> selectTargets,
                                 Set<EntitySelectMenu.DefaultValue> defaultValues,
                                 Set<ChannelType> channelTypes) implements SelectTypes {}
    }

}
