package com.github.kaktushose.jda.commands.embeds;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.exceptions.ParsingException;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Embed extends EmbedBuilder {

    private final List<Embeds.Placeholder<?>> placeholders;

    public Embed(@NotNull EmbedBuilder embedBuilder, Collection<Embeds.Placeholder<?>> placeholders) {
        super(embedBuilder);
        this.placeholders = new ArrayList<>(placeholders);
    }

    public Embed(@NotNull DataObject object, Collection<Embeds.Placeholder<?>> placeholders) {
        this(EmbedBuilder.fromData(object), placeholders);
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
        placeholders.add(new Embeds.Placeholder<>(placeholder, () -> value));
        return this;
    }

    @NotNull
    @Override
    public  MessageEmbed build() {
        String json = super.build().toData().toString();
        for (Embeds.Placeholder<?> placeholder : placeholders) {
            json = json.replaceAll(
                    String.format(Pattern.quote("{%s}"), Matcher.quoteReplacement(placeholder.key())),
                    Matcher.quoteReplacement(quote(String.valueOf(placeholder.value().get())))
            );
        }
        try {
            return EmbedBuilder.fromData(DataObject.fromJson(json)).build();
        } catch (ParsingException e) {
            throw new IllegalArgumentException(
                    "One of your placeholders produced invalid JSON! Reason:\n" + e.getCause().toString()
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
