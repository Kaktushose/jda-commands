package com.github.kaktushose.jda.commands.message.emoji;

import net.dv8tion.jda.api.entities.emoji.ApplicationEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.fellbaum.jemoji.EmojiManager;

import java.util.*;
import java.util.stream.Collectors;

/// The [EmojiResolver] replaces emoji aliases in strings with their formatted value.
///
/// An emoji alias is just the discord alias (either Unicode oder app emoji)
/// enclosed by a colon `:`. For example the Unicode alias `:joy:` will be replaced by 😂 and the
/// app emoji `:app_emote:` by its uploaded file.
///
/// Normally it shouldn't be necessary to escape the colons in messages (that shouldn't be an emoji alias), but in case any troubles occur you can
/// just prefix it with `\` (in java `\\`) to escape it.
///
/// Supported are all discord emojis, their skin tone variants and the app emotes for this bot.
/// App emotes with the same name as a Unicode one will override later.
public class EmojiResolver {

    private final Map<String, Emoji> emojis;

    /// Constructs a new instance of [EmojiResolver] with the given application emojis and all Unicode emojis supported by discord.
    /// If one of the passed application emojis has the same alias as a Unicode emoji, the app emojis takes precedence.
    ///
    /// @param applicationEmojis a list of all application emojis of this bot
    public EmojiResolver(Collection<ApplicationEmoji> applicationEmojis) {
        Map<String, Emoji> emojis = new HashMap<>();

        for (net.fellbaum.jemoji.Emoji current : EmojiManager.getAllEmojis()) {
            for (String alias : current.getDiscordAliases()) {
                emojis.put(alias, Emoji.fromUnicode(current.getEmoji()));
            }
        }

        for (ApplicationEmoji current : applicationEmojis) {
            emojis.put(":" + current.getName() + ":", current);
        }

        this.emojis = Collections.unmodifiableMap(emojis);
    }

    /// Resolves the emoji aliases of a string according to the javadocs of this class.
    ///
    /// @param msg The string to be resolved
    ///
    /// @return the resolved string
    public String resolve(String msg) {
        return parse(msg)
                .stream()
                .map(component -> switch (component) {
                    case Component.EmojiReference(String name) -> {
                        Emoji found = emojis.get(name);
                        yield found == null
                                ? name
                                : found.getFormatted();
                    }
                    case Component.Literal(String value) -> value;
                })
                .collect(Collectors.joining());
    }


    private List<Component> parse(String msg) {
        List<Component> components = new ArrayList<>();

        int nextLiteralStart = 0;
        int referenceStart = -1;
        for (int i = 0; i < msg.length(); i++) {
            char c = msg.charAt(i);

            if (c == ' ' && referenceStart != -1) {
                referenceStart = -1;
            }

            outer: if (c == ':') {
                if (i >= 1 && msg.charAt(i - 1) == '\\') {
                    String literal = msg.substring(nextLiteralStart, i - 1);
                    components.add(new Component.Literal(literal));

                    nextLiteralStart = i;
                    continue;
                }

                if (referenceStart == -1) {
                    String literal = msg.substring(nextLiteralStart, i);
                    components.add(new Component.Literal(literal));

                    nextLiteralStart = referenceStart = i;
                } else {
                    // support for discord skin color settings, e.g. :woman_swimming::skin-tone-5:
                    if (i + 1 < msg.length() && msg.charAt(i + 1) == ':') {
                        i = i + 2;
                        break outer; // if new i is outside range of msg.length(), then rest is added as literal
                    }

                    String name = msg.substring(referenceStart, i + 1);
                    components.add(new Component.EmojiReference(name));
                    referenceStart = -1;

                    nextLiteralStart = i + 1;
                }
            }

            if (i >= msg.length() - 1) {
                String literal = msg.substring(nextLiteralStart);
                components.add(new Component.Literal(literal));
            }
        }

        return components;
    }
}
