package com.github.kaktushose.jda.commands.definitions.api.interactions;

import com.github.kaktushose.jda.commands.definitions.api.Definition;
import com.github.kaktushose.jda.commands.definitions.api.features.JDAEntity;
import com.github.kaktushose.jda.commands.definitions.api.features.Replyable;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;

import javax.annotation.Nullable;
import java.util.Set;

public non-sealed interface SelectMenuDefinition extends JDAEntity<SelectMenu>, Replyable, PermissionsInteraction, CustomIdInteraction {

    SelectTypes selectTypes();

    String placeholder();

    int minValue();

    int maxValue();

    sealed interface SelectTypes {

        interface SelectOptionDefinition extends Definition {
            String value();

            String label();

            String description();

            @Nullable
            Emoji emoji();

            boolean isDefault();
        }

        record StringSelectTypes(Set<SelectOptionDefinition> selectOptions) implements SelectTypes {}

        record EntitySelectTypes(Set<EntitySelectMenu.SelectTarget> selectTargets,
                                 Set<EntitySelectMenu.DefaultValue> defaultValues,
                                 Set<ChannelType> channelTypes) implements SelectTypes {}
    }

}
