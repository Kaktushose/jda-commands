package com.github.kaktushose.jda.commands.definitions.interactions.impl;

import com.github.kaktushose.jda.commands.definitions.features.JDAEntity;
import com.github.kaktushose.jda.commands.definitions.features.Replyable;
import com.github.kaktushose.jda.commands.definitions.interactions.CustomIdInteraction;
import com.github.kaktushose.jda.commands.definitions.interactions.PermissionsInteraction;
import com.github.kaktushose.jda.commands.definitions.reflect.interactions.ReplyConfig;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.SequencedCollection;

public record ButtonDefinition(
        String displayName,
        Method method,
        SequencedCollection<Class<?>> parameters,
        Collection<String> permissions,
        ReplyConfig replyConfig,
        String label,
        Emoji emoji,
        String link,
        ButtonStyle style
) implements JDAEntity<Button>, Replyable, PermissionsInteraction, CustomIdInteraction {

    @Override
    public Button toJDAEntity() {
        return null;
    }

}
