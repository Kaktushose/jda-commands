package com.github.kaktushose.jda.commands.embeds;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.kaktushose.jda.commands.i18n.I18n;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/// Subclass of [EmbedBuilder] that supports placeholders and easier manipulation of fields.
public class Embed extends EmbedBuilder {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final String name;
    private final Map<String, Object> placeholders;
    private final I18n i18n;
    private Locale locale;

    /// Constructs a new [Embed].
    ///
    /// @param embedBuilder the underlying [EmbedBuilder] to use
    /// @param name         the name of this embed used to identify it in [EmbedDataSource]s
    /// @param placeholders the global placeholders as defined in [Embeds]
    public Embed(@NotNull EmbedBuilder embedBuilder, @NotNull String name, @NotNull Map<String, Object> placeholders, @NotNull I18n i18n) {
        super(embedBuilder);
        this.name = name;
        this.placeholders = new HashMap<>(placeholders);
        this.i18n = i18n;
        locale = Locale.ENGLISH;
    }

    /// Constructs a new [Embed].
    ///
    /// @param object       the [DataObject] to construct the underlying [EmbedBuilder] from
    /// @param name         the name of this embed used to identify it in [EmbedDataSource]s
    /// @param placeholders the global placeholders as defined in [Embeds]
    public Embed(@NotNull DataObject object, @NotNull String name, @NotNull Map<String, Object> placeholders, @NotNull I18n i18n) {
        this(EmbedBuilder.fromData(object), name, placeholders, i18n);
    }

    public Embed locale(Locale locale) {
        this.locale = locale;
        return this;
    }


    /// Gets the name of this embed. This isn't a field that get displayed but only the name this embed is referenced
    /// by in the [EmbedDataSource].
    ///
    /// @return the name of this embed
    public String name() {
        return name;
    }


    /// Sets the Title of the embed.
    ///
    /// @param title the title of the embed
    @NotNull
    public Embed title(@Nullable String title) {
        setTitle(title);
        return this;
    }

    /// Sets the Title of the embed.
    ///
    /// @param title the title of the embed
    /// @param url   Makes the title into a hyperlink pointed at this url.
    @NotNull
    public Embed title(@Nullable String title, @Nullable String url) {
        setTitle(title, url);
        return this;
    }

    /// Sets the Description of the embed.
    @NotNull
    public Embed description(@Nullable CharSequence description) {
        setDescription(description);
        return this;
    }

    /// Sets the Color of the embed.
    @NotNull
    public Embed color(int color) {
        setColor(color);
        return this;
    }

    /// Sets the Color of the embed.
    @NotNull
    public Embed color(@Nullable Color color) {
        setColor(color);
        return this;
    }

    /// Sets the Timestamp of the embed.
    @NotNull
    public Embed timestamp(@Nullable TemporalAccessor accessor) {
        setTimestamp(accessor);
        return this;
    }

    /// Sets the Footer of the embed.
    @NotNull
    public Embed footer(@Nullable String footer) {
        setFooter(footer);
        return this;
    }

    /// Sets the Footer of the embed.
    @NotNull
    public Embed footer(@Nullable String footer, @Nullable String iconUrl) {
        setFooter(footer, iconUrl);
        return this;
    }

    /// Sets the Thumbnail of the embed.
    @NotNull
    public Embed thumbnail(@Nullable String url) {
        setThumbnail(url);
        return this;
    }

    /// Sets the Image of the embed.
    @NotNull
    public Embed image(@Nullable String url) {
        setImage(url);
        return this;
    }

    /// Sets the Author of the embed.
    @NotNull
    public Embed author(@Nullable String name) {
        setAuthor(name);
        return this;
    }

    /// Sets the Author of the embed.
    @NotNull
    public Embed author(@Nullable String name, @Nullable String url) {
        setAuthor(name, url);
        return this;
    }

    /// Sets the Author of the embed.
    @NotNull
    public Embed author(@Nullable String name, @Nullable String url, @Nullable String iconUrl) {
        setAuthor(name, url, iconUrl);
        return this;
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

    /// Adds all the provided placeholders to this embed instance. The values will be replaced when [#build()] is called.
    ///
    /// Existing entries with the same keys will be overwritten.
    ///
    /// @param placeholders a map of placeholder names to their corresponding values
    /// @return this instance for fluent interface
    @NotNull
    public Embed placeholders(@NotNull Map<String, Object> placeholders) {
        this.placeholders.putAll(placeholders);
        return this;
    }

    /// Adds all the provided [`placeholders`][I18n.Entry] to this embed instance. The values will be replaced when [#build()] is called.
    ///
    /// Existing entries with the same keys will be overwritten.
    ///
    /// @param placeholders the [`entries`][I18n.Entry] to add
    /// @return this instance for fluent interface
    @NotNull
    public Embed placeholders(@NotNull I18n.Entry... placeholders) {
        this.placeholders.putAll(Arrays.stream(placeholders).collect(Collectors.toUnmodifiableMap(I18n.Entry::name, I18n.Entry::value)));
        return this;
    }

    /// Returns a [MessageEmbed] just like [EmbedBuilder#build()], but will also localize this embed based on the
    /// [#locale(Locale)] and [`placeholders`][#placeholder(String, Object)] provided.
    ///
    /// @return the built, sendable [MessageEmbed]
    @NotNull
    @Override
    public MessageEmbed build() {
        String json = super.build().toData().toString();
        try {
            JsonNode node = localize(mapper.readTree(json));
            return EmbedBuilder.fromData(DataObject.fromJson(node.toString())).build();
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    private JsonNode localize(JsonNode node) {
        if (node instanceof ObjectNode objectNode) {
            Iterator<Map.Entry<String, JsonNode>> iterator = objectNode.fields();
            while (iterator.hasNext()) {
                Map.Entry<String, JsonNode> entry = iterator.next();
                JsonNode child = entry.getValue();
                JsonNode newChild = localize(child);
                if (newChild.isTextual()) {
                    objectNode.put(entry.getKey(), i18n.localize(locale, newChild.asText(), placeholders));
                } else {
                    objectNode.set(entry.getKey(), newChild);
                }
            }
        } else if (node instanceof ArrayNode arrayNode) {
            for (int i = 0; i < arrayNode.size(); i++) {
                JsonNode child = arrayNode.get(i);
                arrayNode.set(i, localize(child));
            }
        }
        return node;
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
}
