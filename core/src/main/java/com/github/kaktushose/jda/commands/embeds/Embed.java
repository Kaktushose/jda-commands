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

/// Subclass of [EmbedBuilder] that supports placeholders and easier manipulation of fields.
public class Embed extends EmbedBuilder {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(Embed.class);
    private final String name;
    private final List<Placeholder> placeholders;

    /// Constructs a new [Embed].
    ///
    /// @param embedBuilder the underlying [EmbedBuilder] to use
    /// @param name         the name of this embed used to identify it in [EmbedDataSource]s
    /// @param placeholders the global [Placeholder]s as defined in [Embeds]
    public Embed(@NotNull EmbedBuilder embedBuilder, @NotNull String name, @NotNull Collection<Placeholder> placeholders) {
        super(embedBuilder);
        this.name = name;
        this.placeholders = new ArrayList<>(placeholders);
    }

    /// Constructs a new [Embed].
    ///
    /// @param object       the [DataObject] to construct the underlying [EmbedBuilder] from
    /// @param name         the name of this embed used to identify it in [EmbedDataSource]s
    /// @param placeholders the global [Placeholder]s as defined in [Embeds]
    public Embed(@NotNull DataObject object, @NotNull String name, @NotNull Collection<Placeholder> placeholders) {
        this(EmbedBuilder.fromData(object), name, placeholders);
    }

    /// Used to modify the fields of this embed.
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

    /// Adds a Field to the embed that isn't inlined.
    ///
    /// @param name  the name of the Field, displayed in bold above the value.
    /// @param value the contents of the field.
    /// @return this instance for fluent interface
    @NotNull
    public Embed addField(@NotNull String name, @NotNull String value) {
        addField(name, value, false);
        return this;
    }

    /// Replace the placeholders of this embed with the values of the given map, where the key of the map represents
    /// the name of the placeholder and the value of the map the value to replace the placeholder with.
    ///
    /// @param values the [Map] to get the values from.
    /// @return this instance for fluent interface
    @NotNull
    public Embed placeholder(@NotNull Map<String, Object> values) {
        values.forEach(this::placeholder);
        return this;
    }

    /// Replace the placeholder with the given name with the given value.
    ///
    /// @param key   the key of the placeholder
    /// @param value the value to replace the placeholder with
    /// @return this instance for fluent interface
    @NotNull
    public Embed placeholder(@NotNull String key, @NotNull Object value) {
        placeholders.add(new Placeholder(key, value::toString));
        return this;
    }

    /// Returns a [MessageEmbed] just like [EmbedBuilder#build()], but will also replace the placeholders a value has
    /// been provided for.
    ///
    /// @return the built, sendable [MessageEmbed]
    @NotNull
    @Override
    public MessageEmbed build() {
        String json = super.build().toData().toString();
        for (Placeholder placeholder : placeholders) {
            String key = Pattern.quote(placeholder.key);
            json = json.replaceAll(String.format("\\{%s}|\\{o:%s}", key, key),
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

    /// Transforms this embed into [MessageCreateData].
    @NotNull
    public MessageCreateData toMessageCreateData() {
        return MessageCreateData.fromEmbeds(build());
    }

    /// Transforms this embed into [MessageEditData].
    @NotNull
    public MessageEditData toMessageEditData() {
        return MessageEditData.fromEmbeds(build());
    }

    /// Methods for manipulating the fields of an [Embed].
    public interface Fields {

        /// Removes all fields of this embed based on the given [Predicate].
        ///
        /// @param filter the [Predicate] to test the fields with
        /// @return this instance for fluent interface
        @NotNull
        Fields removeIf(@NotNull Predicate<MessageEmbed.Field> filter);

        /// Removes all fields with the given name of this embed based on the given [Predicate].
        ///
        /// @param name   the name of the fields to test
        /// @param filter the [Predicate] to test the fields with
        /// @return this instance for fluent interface
        @NotNull
        Fields removeIf(@NotNull String name, Predicate<MessageEmbed.Field> filter);

        /// Removes all fields with the given value.
        ///
        /// @param value the value of a field that should be removed
        /// @return this instance for fluent interface
        @NotNull
        default Fields remove(@NotNull String value) {
            return removeIf(field -> value.equals(field.getValue()));
        }

        /// Removes all fields with the given name.
        ///
        /// @param name the name of a field that should be removed
        /// @return this instance for fluent interface
        @NotNull
        default Fields removeByName(@NotNull String name) {
            return removeIf(field -> name.equals(field.getName()));
        }

        /// Removes all fields with the given name **and** value.
        ///
        /// @param name  the name of the field that should be removed
        /// @param value the value of the field that should be removed
        /// @return this instance for fluent interface
        @NotNull
        default Fields remove(@NotNull String name, @NotNull String value) {
            return removeIf(field -> name.equals(field.getName()) && value.equals(field.getValue()));
        }
    }

    /// Wrapper of an embed placeholder.
    ///
    /// @param key the key of the placeholder
    /// @param value the [Supplier] to get the value from the placeholder will be replaced with
    public record Placeholder(@NotNull String key, @NotNull Supplier<String> value) {}
}
