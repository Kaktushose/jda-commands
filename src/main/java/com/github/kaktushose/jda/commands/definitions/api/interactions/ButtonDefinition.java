package com.github.kaktushose.jda.commands.definitions.api.interactions;

import com.github.kaktushose.jda.commands.definitions.api.features.JDAEntity;
import com.github.kaktushose.jda.commands.definitions.api.features.Replyable;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

public non-sealed interface ButtonDefinition extends JDAEntity<Button>, Replyable, PermissionsInteraction, CustomIdInteraction {

    String label();
    Emoji emoji();
    String link();
    ButtonStyle style();

}
