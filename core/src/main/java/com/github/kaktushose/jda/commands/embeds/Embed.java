package com.github.kaktushose.jda.commands.embeds;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.exceptions.ParsingException;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Embed extends EmbedBuilder {

    public Embed(@NotNull EmbedBuilder embedBuilder) {
        super(embedBuilder);
    }

    public Embed(@NotNull DataObject object) {
        super(EmbedBuilder.fromData(object));
    }

    // TODO fields API

    public Embed placeholder(String placeholder, Object value) {
        String json = build().toData().toString().replaceAll(
                String.format(Pattern.quote("{%s}"), Matcher.quoteReplacement(placeholder)),
                quote(String.valueOf(value))
        );
        try {
            return new Embed(DataObject.fromJson(json));
        } catch (ParsingException exception) {
            throw new IllegalArgumentException("""
                    Cannot replace placeholder:
                        {%s}
                    with value:
                        %s
                    This produces invalid JSON, embeds cannot be constructed from! Reason: %s"""
                    .formatted(placeholder, value, exception.getCause().getMessage())
            );
        }

    }

    private String quote(String input) {
        StringBuilder escaped = new StringBuilder();
        for (char c : input.toCharArray()) {
            String append = switch (c) {
                case '"' -> "\\\"";
                case '\\' -> "\\\\";
                case '\b' -> "\\b";
                case '\f' -> "\\f";
                case '\n' -> "\\n";
                case '\r' -> "\\r";
                case '\t' -> "\\t";
                default -> c < 32 || c > 126 ? String.format("\\u%04x", (int) c) : String.valueOf(c);
            };
            escaped.append(append);
        }
        return escaped.toString();
    }

    public Embed placeholder(Map<String, Object> values) {
        values.forEach(this::placeholder);
        return this;
    }

    @NotNull
    public MessageCreateData toMessageCreateData() {
        return MessageCreateData.fromEmbeds(build());
    }

    @NotNull
    public MessageEditData toMessageEditData() {
        return MessageEditData.fromEmbeds(build());
    }

}
