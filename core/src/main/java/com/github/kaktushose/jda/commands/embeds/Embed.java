package com.github.kaktushose.jda.commands.embeds;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.exceptions.ParsingException;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Embed extends EmbedBuilder {

    public Embed(@NotNull EmbedBuilder embedBuilder) {
        super(embedBuilder);
    }

    public Embed(@NotNull DataObject object) {
        this(EmbedBuilder.fromData(object));
    }

    @NotNull
    public Fields fields() {
        return new Fields() {

            @NotNull
            @Override
            public Fields removeIf(@NotNull Predicate<MessageEmbed.Field> filter) {
                getFields().removeIf(filter);
                return this;
            }

            @Override
            public @NotNull Fields removeIf(@NotNull String name, Predicate<MessageEmbed.Field> filter) {
                getFields().removeIf(filter.and(field -> name.equals(field.getName())));
                return this;
            }
        };
    }

    public interface Fields {

        @NotNull
        Fields removeIf(@NotNull Predicate<MessageEmbed.Field> filter);

        @NotNull
        Fields removeIf(@NotNull String name, Predicate<MessageEmbed.Field> filter);

        @NotNull
        default Fields remove(@NotNull String value) {
            return removeIf(field -> value.equals(field.getValue()));
        }

        @NotNull
        default Fields removeByName(@NotNull String name) {
            return removeIf(field -> name.equals(field.getName()));
        }

        @NotNull
        default Fields remove(@NotNull String name, @NotNull String value) {
            return removeIf(field -> name.equals(field.getName()) && value.equals(field.getValue()));
        }
    }

    public Embed placeholder(Map<String, Object> values) {
        values.forEach(this::placeholder);
        return this;
    }

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

    @NotNull
    public MessageCreateData toMessageCreateData() {
        return MessageCreateData.fromEmbeds(build());
    }

    @NotNull
    public MessageEditData toMessageEditData() {
        return MessageEditData.fromEmbeds(build());
    }

}
