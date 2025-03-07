package com.github.kaktushose.jda.commands.embeds;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.exceptions.ParsingException;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Embed extends EmbedBuilder {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(Embed.class);
    private final String name;
    private final List<Placeholder> placeholders;

    public Embed(@NotNull EmbedBuilder embedBuilder, @NotNull String name, @NotNull Collection<Placeholder> placeholders) {
        super(embedBuilder);
        this.name = name;
        this.placeholders = new ArrayList<>(placeholders);
    }

    public Embed(@NotNull DataObject object, @NotNull String name, @NotNull Collection<Placeholder> placeholders) {
        this(EmbedBuilder.fromData(object), name, placeholders);
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

    @NotNull
    public Embed addField(@NotNull String name, @NotNull String value) {
        addField(name, value, false);
        return this;
    }

    @NotNull
    public Embed placeholder(@NotNull Map<String, Object> values) {
        values.forEach(this::placeholder);
        return this;
    }

    @NotNull
    public Embed placeholder(@NotNull String placeholder, @NotNull Object value) {
        placeholders.add(new Placeholder(placeholder, value::toString));
        return this;
    }

    @NotNull
    @Override
    public MessageEmbed build() {
        String json = super.build().toData().toString();
        for (Placeholder placeholder : placeholders) {
            json = json.replaceAll(
                    String.format(Pattern.quote("{%s}"), Matcher.quoteReplacement(placeholder.key())),
                    Matcher.quoteReplacement(quote(String.valueOf(placeholder.value().get())))
            );
        }
        try {
            checkPlaceholders(mapper.readTree(json));
            return EmbedBuilder.fromData(DataObject.fromJson(json)).build();
        } catch (ParsingException | JsonProcessingException e) {
            throw new IllegalArgumentException("One of your placeholders produced invalid JSON! Reason:\n" + e);
        }
    }

    private void checkPlaceholders(JsonNode node) {
        if (node.isObject()) {
            node.forEach(this::checkPlaceholders);
        } else if (node.isArray()) {
            for (int i = 0; i < node.size(); i++) {
                checkPlaceholders(node.get(i));
            }
        } else {
            String value = node.asText();
            if (value.matches("\\{([^{}]*)}") && !value.matches("\\{o:([^{}]*)}")) {
                log.error("Placeholder '{}' in embed '{}' didn't get replaced with a value!", value, name);
            }
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

    public record Placeholder(@NotNull String key, @NotNull Supplier<String> value) {}
}
