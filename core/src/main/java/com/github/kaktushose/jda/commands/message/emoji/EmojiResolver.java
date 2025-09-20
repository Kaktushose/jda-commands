package com.github.kaktushose.jda.commands.message.emoji;

import net.fellbaum.jemoji.Emoji;
import net.fellbaum.jemoji.EmojiManager;

import java.util.ArrayList;
import java.util.List;

public class EmojiResolver {

    public String resolve(String msg) {
        List<Component> components = parse(msg);
        List<Component.Literal> replaced = replaceEmojiAliases(components);
        return join(replaced);
    }

    private String join(List<Component.Literal> components) {
        StringBuilder builder = new StringBuilder();
        components.forEach(literal -> builder.append(literal.value()));
        return builder.toString();
    }

    private List<Component.Literal> replaceEmojiAliases(List<Component> components) {
        return components.stream()
                .map(component -> switch (component) {
                        case Component.EmojiReference(String name) -> {
                            String rendered = EmojiManager.getByDiscordAlias(name)
                                        .map(Emoji::getEmoji)
                                        .orElse(name);
                            yield new Component.Literal(rendered);
                        }
                        case Component.Literal l -> l;
                    })
                .toList();
    }


    private List<Component> parse(String msg) {
        List<Component> components = new ArrayList<>();

        int nextLiteralStart = 0;
        int referenceStart = -1;
        for (int i = 0; i < msg.length(); i++) {
            char c = msg.charAt(i);

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
